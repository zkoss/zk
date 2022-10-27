/* DispatcherRichletFilter.java

	Purpose:

	Description:

	History:
		4:56 PM 2021/11/22, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui.http;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.util.ActionParameterResolver;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.stateless.action.data.ActionData;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.zk.au.AuDecoder;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriters;
import org.zkoss.zk.au.RequestOutOfSequenceException;
import org.zkoss.zk.au.http.AuMultipartUploader;
import org.zkoss.zk.ui.ActivationTimeoutException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.http.DesktopRecycles;
import org.zkoss.zk.ui.http.RichletFilter;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Dispatches Http requests to {@link StatelessRichlet} mapping handlers
 * @author jumperchen
 */
public class DispatcherRichletFilter extends RichletFilter {
	/**
	 * The base packages of Richlet initial config for scanning {@link RichletMapping} annotation.
	 */
	public static final String BASE_PACKAGES = "basePackages";

	/**
	 * The cloud mode flag of Richlet initial config for destroying desktop per request.
	 */
	public static final String CLOUD_MODE = "cloudMode";

	private static final Logger log = LoggerFactory.getLogger(
			DispatcherRichletFilter.class);

	private final Map<String, Method> _urlMappings = new HashMap<>();

	/**
	 * Lookups a richlet mapping with the given path
	 * @param path a request path.
	 */
	public Method lookup(String path) {
		return _urlMappings.get(path);
	}

	private boolean _isCloudMode = false;

	/**
	 * Returns whether the mode in cloud.
	 * @return
	 */
	public boolean isCloudMode() {
		return _isCloudMode;
	}

	public void init(FilterConfig config) throws ServletException {
		super.init(config);
		_isCloudMode = "true".equalsIgnoreCase(config.getInitParameter(CLOUD_MODE));

		// hook a ID Generator to avoid duplication uuid issue
		if (_isCloudMode) {
			WebAppCtrl webAppCtrl = (WebAppCtrl) WebApps.getCurrent();
			webAppCtrl.setIdGenerator(new CloudBasedIdGenerator(webAppCtrl.getIdGenerator()));
		}

		String basePackages = config.getInitParameter(BASE_PACKAGES);
		if (!Strings.isBlank(basePackages)) {
			Reflections reflections = new Reflections(basePackages,
					Scanners.TypesAnnotated, Scanners.MethodsAnnotated);
			// TypesAnnotated
			Map<Class<?>, Set<Object>> richletMappings = new HashMap<>();
			Set<Class<?>> typesAnnotatedWith =
					reflections.getTypesAnnotatedWith(RichletMapping.class);
			for (Class c : typesAnnotatedWith) {
				Set<Object> objects = richletMappings.computeIfAbsent(c,
						(e) -> new HashSet<>());
				objects.add(c);
			}

			// MethodsAnnotated
			Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(
					RichletMapping.class);

			for (Method m : methodsAnnotatedWith) {
				Set<Object> objects = richletMappings.computeIfAbsent(m.getDeclaringClass(),
						(e) -> new HashSet<>());
				objects.add(m);
			}

			Configuration configuration = _webman.getWebApp()
					.getConfiguration();

			for (Map.Entry<Class<?>, Set<Object>> me : richletMappings.entrySet()) {
				Class<?> richlet = me.getKey();
				String richletName = richlet.getName();
				RichletMapping annotation = me.getKey()
						.getDeclaredAnnotation(RichletMapping.class);
				for (Object value : me.getValue()) {
					if (value instanceof Class) {
						if (me.getValue().size() == 1) {
							throw new UiException("Missing a method entry point for [" + richletName + "]");
						}
						configuration.addRichlet(richletName, (Class) value, null);
						configuration.addRichletMapping(richletName, annotation.value());
					} else if (value instanceof Method) {
						final String prefix = annotation != null ? annotation.value() : "";
						RichletMapping declaredAnnotation = ((Method) value).getDeclaredAnnotation(
								RichletMapping.class);
						configuration.addRichlet(richletName, (Class<?>) richlet, null);
						configuration.addRichletMapping(richletName, prefix + declaredAnnotation.value());
						final String url = prefix + declaredAnnotation.value();
						if (Strings.isBlank(url)) {
							throw new UiException("Not allowed a blank url for [" + richletName + "]");
						}
						_urlMappings.put(prefix + declaredAnnotation.value(), (Method) value);
					}
				}
			}
			_webman.getWebApp().setAttribute(DispatcherRichletFilter.class.getName(), this);
		} else {
			throw new UiException("The " + BASE_PACKAGES + " initial config is unset.");
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			final String uri = ((HttpServletRequest) request).getRequestURI();
			// ignore file mapping
			if ((uri.startsWith(_webman.getUpdateURI())) || uri.startsWith(
					_webman.getResourceURI()) ||
					// ignore server push
					(uri.contains("comet") && ((HttpServletRequest) request).getQueryString().startsWith("dtid="))
					|| hasFileExtension(uri)) {
				chain.doFilter(request, response);
			} else {
				super.doFilter(request, response, chain);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	private static Pattern FILE_EXTENSION = Pattern.compile("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+");
	private static boolean hasFileExtension(String url) {
		if (!Strings.isBlank(url)) {
			int filenamePos = url.lastIndexOf('/');
			String filename = 0 <= filenamePos ? url.substring(filenamePos + 1) : url;
			if (filename.length() > 0 &&
					FILE_EXTENSION.matcher(filename).matches()) {
				int dotPos = filename.lastIndexOf('.');
				if (0 <= dotPos) {
					return !Strings.isBlank(filename.substring(dotPos + 1));
				}
			}
		}
		return false;
	}

	protected boolean process(Session sess,
			javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response, String path,
			boolean bRichlet) throws ServletException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			return processUpdate(sess, request, response, path, bRichlet);
		} else {
			final boolean result = super.process(sess, request, response, path, bRichlet);
			// drop the current desktop for Richlet case to release all IStubcomponents.
			// only if it's richlet case
			if (_isCloudMode && RICHLET_FLAG.equals(request.getAttribute(RICHLET_FLAG))) {
				Desktop desktop = _webman.getDesktop(sess, request, response,
						path, false);
				if (desktop != null) {
					((WebAppCtrl) _webman.getWebApp()).getDesktopCache(desktop.getSession()).removeDesktop(desktop);
				}
			}
			return result;
		}
	}

	protected boolean processUpdate(Session sess, HttpServletRequest request, HttpServletResponse response, String path,
			boolean bRichlet) throws ServletException, IOException {
		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl) wapp;
		final Configuration config = wapp.getConfiguration();

		final ServletContext ctx = _config.getServletContext();
		AuDecoder audec = getAuDecoder(wapp);

		boolean multipartContent = ServletFileUpload.isMultipartContent(request);
		if (multipartContent) {
			audec = AuMultipartUploader.parseRequest(request, audec);
		}

		final String dtid = audec.getDesktopId(request);

		if (dtid == null) {
			//Bug 1929139: incomplete request (IE only)
			if (log.isDebugEnabled()) {
				final String msg = "Incomplete request\n" + Servlets.getDetail(request);
				log.debug(msg);
			}

			response.sendError(467, "Incomplete request");
			return true;
		}
		Desktop desktop = getDesktop(sess, dtid);

		boolean shallRecovering = false;
		// cluster env. case
		if (desktop == null) {
			desktop = _webman.getDesktop(sess, request, response, path, true);
			if (desktop == null) // forward or redirect
				return true;
			shallRecovering = true;
		}
		WebManager.setDesktop(request, desktop);
		//reason: a new page might be created (such as include)

		final String sid = request.getHeader("ZK-SID");
		if (sid != null) { //Some client might not have ZK-SID
			//B65-ZK-2464 : Possible XSS Vulnerability in HTTP Header
			try {
				Integer.parseInt(sid);
			} catch (NumberFormatException e) {
				return false; // do not process if sid does not exist
			}
			response.setHeader("ZK-SID", sid);
		}
		//parse commands
		final List<AuRequest> aureqs = audec.decode(request, desktop);

		if (aureqs.isEmpty()) {
			return false;  // do not process unidentified request
		}

		final RequestInfo ri = new RequestInfoImpl(wapp, sess, desktop, request,
				PageDefinitions.getLocator(wapp, path));
		((SessionCtrl) sess).notifyClientRequest(true);

		final UiFactory uf = wappc.getUiFactory();
		if (uf.isRichlet(ri, bRichlet)) {
			final Richlet richlet = uf.getRichlet(ri, path);
			if (richlet == null)
				return false; // not found

			final Page page = desktop.getFirstPage() == null ? WebManager.newPage(uf, ri, richlet, response, path)
					: desktop.getFirstPage();
			final AsyncUpdateExecution exec = new AsyncUpdateExecution(ctx, request, response, desktop, page);
			if (sid != null)
				((ExecutionCtrl) exec).setRequestId(sid);

			byte[] _result = null;
			try {
				UiEngine uiEngine = wappc.getUiEngine();
				Object updctx = uiEngine.startUpdate(exec);


				// restore desktop here
				if (shallRecovering) {
					try {
						exec.enableRecovering();
						((DesktopCtrl) desktop).setId(dtid);
					} finally {
						exec.disableRecovering();
					}
				}
				ExecutionsCtrl.setCurrent(exec);
				// init page if not init yet
				if (page.getDesktop() == null) {
					((PageCtrl)page).preInit();
				}
				final List<Throwable> errs = new LinkedList<Throwable>();
				try {
					for (AuRequest auRequest : aureqs) {
						Map requestData = auRequest.getData();
						String mtd = (String) requestData.get(ActionData.METHOD);
						if (mtd != null) {
							Method targetMethod = Arrays.stream(
											richlet.getClass().getDeclaredMethods())
									.filter(method -> Objects.equals(mtd, method.getName())).findAny().get();
							Object[] resolveParameters = ActionParameterResolver.resolve(
									auRequest.getUuid(), requestData,
									targetMethod.getParameterTypes());
							targetMethod.invoke(richlet, resolveParameters);
						} else {
							// same as UIEngineImpl.java
							((DesktopCtrl) desktop).service(auRequest, !errs.isEmpty());
						}
					}
				} catch (Throwable ex) {
					errs.add(ex);
				} finally {
					JSONObject output = new JSONObject();
					JSONArray jsonArray = uiEngine.finishUpdate(updctx, errs);
					output.put("rs", jsonArray);
					output.put("rid", "0");
					_result = output.toString().getBytes("UTF-8");

					uiEngine.closeUpdate(updctx);
					updctx = null;

					// cleanup the desktop
					if (_isCloudMode) {
						DesktopRecycles.removeDesktop(exec);
					}
					ExecutionsCtrl.setCurrent(null);
				}
			} catch (ActivationTimeoutException ex) {
				log.warn(ex.getMessage());
				response.setHeader("ZK-SID", sid);
				response.setIntHeader("ZK-Error", AuResponse.SC_ACTIVATION_TIMEOUT);
			} catch (RequestOutOfSequenceException ex) {
				log.warn(ex.getMessage());
				response.setHeader("ZK-SID", sid);
				response.setIntHeader("ZK-Error", AuResponse.SC_OUT_OF_SEQUENCE);
			}

			final HttpServletRequest hreq = request;
			final HttpServletResponse hres = response;
			if (_compress && _result.length > 200) {
				final byte[] bs = Https.gzip(hreq, hres, null, _result);
				if (bs != null)
					_result = bs; //yes, browser support compress
			}
			hres.setContentType(AuWriters.CONTENT_TYPE);
			//we have to set content-type again. otherwise, tomcat might
			//fail to preserve what is set in open()
			hres.setContentLength(_result.length);
			hres.getOutputStream().write(_result);
			//Use OutputStream due to Bug 1528592 (Jetty 6)
			hres.flushBuffer();
		} else {
			return false; // not found
		}
		return true; // success
	}


	private static void responseError(HttpServletRequest request, HttpServletResponse response, String errmsg)
			throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, errmsg);
	}

	protected Desktop getDesktop(Session sess, String dtid) {
		return ((WebAppCtrl) sess.getWebApp()).getDesktopCache(sess).getDesktopIfAny(dtid);
	}

	private static final AuDecoder getAuDecoder(WebApp wapp) {
		AuDecoder audec = wapp != null ? ((WebAppCtrl) wapp).getAuDecoder() : null;
		return audec != null ? audec : _audec;
	}

	private static final AuDecoder _audec = new AuDecoder() {
		public String getDesktopId(Object request) {
			return ((HttpServletRequest) request).getParameter("dtid");
		}

		public String getFirstCommand(Object request) {
			return ((HttpServletRequest) request).getParameter("cmd_0");
		}

		@SuppressWarnings("unchecked")
		public List<AuRequest> decode(Object request, Desktop desktop) {
			final List<AuRequest> aureqs = new LinkedList<AuRequest>();
			final HttpServletRequest hreq = (HttpServletRequest) request;
			for (int j = 0;; ++j) {
				final String cmdId = hreq.getParameter("cmd_" + j);
				if (cmdId == null)
					break;

				final String uuid = hreq.getParameter("uuid_" + j);
				final String data = hreq.getParameter("data_" + j);
				final Map<String, Object> decdata = (Map) JSONValue.parse(data);
				aureqs.add(uuid == null || uuid.length() == 0 ? new AuRequest(desktop, cmdId, decdata)
						: new AuRequest(desktop, uuid, cmdId, decdata));
			}
			return aureqs;
		}

		public boolean isIgnorable(Object request, WebApp wapp) {
			final HttpServletRequest hreq = (HttpServletRequest) request;
			for (int j = 0;; ++j) {
				if (hreq.getParameter("cmd_" + j) == null)
					break;

				final String opt = hreq.getParameter("opt_" + j);
				if (opt == null || opt.indexOf("i") < 0)
					return false; //not ignorable
			}
			return true;
		}
	};
}
