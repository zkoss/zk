/* WebManager.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 15 13:28:19     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.resource.Labels;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.XMLResourcesLocator;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.util.resource.ClassWebResource;
import org.zkoss.web.util.resource.Extendlet;
import org.zkoss.web.util.resource.ServletContextLocator;
import org.zkoss.web.util.resource.ServletLabelLocator;
import org.zkoss.web.util.resource.ServletRequestResolver;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.impl.Utils;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.ConfigParser;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.WebAppFactory;
import org.zkoss.zk.ui.sys.WebAppsCtrl;
import org.zkoss.zk.ui.util.Configuration;

/**
 * A bridge between Web server and ZK.
 *
 * <p>Each Web application that uses ZK will have an independent instance
 * of {@link WebManager}.
 *
 * @author tomyeh
 */
public class WebManager {
	private static final Logger log = LoggerFactory.getLogger(WebManager.class);

	/** A context attribute for storing an instance of this class. */
	/*package*/ static final String ATTR_WEB_MANAGER = "javax.zkoss.zk.ui.WebManager";
	/** A request attribute to store the current desktop.
	 * Because we store this portlet request, we have to name it
	 * with javax such that it is visible to other servlets and portlets.
	 */
	/*package*/ static final String ATTR_DESKTOP = "javax.zkoss.zk.ui.desktop";

	/** Map(ServletContext, List(WebManagerActivationListener)). */
	private static final Map<ServletContext, List<WebManagerActivationListener>> _actListeners = new HashMap<ServletContext, List<WebManagerActivationListener>>();

	private final ServletContext _ctx;
	private final WebApp _wapp;
	private String _updateURI;
	private String _resourceURI;
	private final ClassWebResource _cwr;

	/**
	 * Creates the Web manager. It is singleton in a Web application
	 * and it is created automatically by {@link DHtmlLayoutServlet},
	 * so you rarely need to create it manually.
	 * @since 3.6.0
	 */
	public WebManager(ServletContext ctx, String updateURI) {
		this(ctx, updateURI, updateURI);
	}
	/**
	 * Creates the Web manager. It is singleton in a Web application
	 * and it is created automatically by {@link DHtmlLayoutServlet},
	 * so you rarely need to create it manually.
	 * @param ctx the servlet context
	 * @param updateURI the URI for asynchronous update
	 * @param resourceURI the URI for ZK resource.
	 * @since 9.5.0
	 */
	@SuppressWarnings("deprecation")
	public WebManager(ServletContext ctx, String updateURI, String resourceURI) {
		if (log.isDebugEnabled())
			log.debug("Starting WebManager at " + ctx);

		if (ctx == null || updateURI == null)
			throw new IllegalArgumentException("null");
		if (getWebManagerIfAny(ctx) != null)
			throw new UiException("Only one Web manager is allowed in one context: " + ctx);

		log.info("Starting ZK " + org.zkoss.zk.Version.RELEASE + ' ' + WebApps.getEdition() + " (build: "
				+ org.zkoss.zk.ui.impl.AbstractWebApp.loadBuild() + ')');

		_ctx = ctx;
		_updateURI = updateURI;
		_resourceURI = resourceURI;
		_ctx.setAttribute(ATTR_WEB_MANAGER, this);

		Servlets.setClientIdentifier(new ClientIdentifier());
		//plugin device-dependent browser identifier

		//load config as soon as possible since it might set some system props
		final Configuration config = new Configuration();
		final ConfigParser parser = new ConfigParser();

		//load metainfo/zk/config.xml
		try {
			parser.parseConfigXml(config);
		} catch (Throwable ex) {
			log.error("Unable to load metainfo/zk/config.xml", ex);
		}

		//load metainfo/zk/zk.xml
		String xml = "metainfo/zk/zk.xml";
		try {
			final XMLResourcesLocator loc = Utils.getXMLResourcesLocator();

			// B65-ZK-1671: ThemeProvider specified in metainfo/zk/zk.xml may get overridden by default
			//   Also need to enforce configuration loading order (zul -> zkex -> zkmax) so that correct
			//   versions of StandardThemeProvider, ThemeRegistry, and ThemeResolver are configured
			final List<XMLResourcesLocator.Resource> xmls = loc.getDependentXMLResources(xml, "config-name", "depends");
			for (XMLResourcesLocator.Resource res : xmls) {
				final URL cfgUrl = res.url;
				try {
					parser.parse(cfgUrl, config, loc);
				} catch (Throwable ex) {
					log.error("Unable to load " + cfgUrl, ex);
				}
			}
		} catch (Throwable ex) {
			log.error("Unable to load " + xml, ex);
		}

		//load /WEB-INF/zk.xml
		xml = "/WEB-INF/zk.xml";
		try {
			final URL cfgUrl = _ctx.getResource(xml);
			if (cfgUrl != null)
				parser.parse(cfgUrl, config, new ServletContextLocator(_ctx, true));
			//accept URL, so zk.xml could contain URL
		} catch (Throwable ex) {
			log.error("Unable to load " + xml, ex);
		}

		//load additional configuration file
		xml = Library.getProperty("org.zkoss.zk.config.path");
		if (xml != null && xml.length() > 0) {
			log.info("Parsing " + xml);
			InputStream is = null;
			try {
				is = Servlets.getResourceAsStream(_ctx, xml);
				if (is != null)
					parser.parse(is, config, new ServletContextLocator(_ctx, true));
				else
					log.error("File not found: " + xml);
			} catch (Throwable ex) {
				log.error("Unable to load " + xml, ex);
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (Throwable ignored) {
					}
			}
		}

		//after zk.xml is loaded since it depends on the configuration
		Classes.configureContextClassLoader();
		_cwr = ClassWebResource.getInstance(_ctx, _resourceURI);
		_cwr.setCompress(new String[] { "js", "css", "html", "xml" });
		String s = Library.getProperty("org.zkoss.web.util.resource.dir");
		if (s != null && s.length() > 0) {
			if (s.charAt(0) != '/')
				s = '/' + s;
			_cwr.setExtraLocator(new ServletContextLocator(_ctx, null, s, false, ClassWebResource.PATH_PREFIX)); //for safety, not accept URL
		}

		String[] labellocs = config.getLabelLocations();
		if (labellocs.length == 0)
			Labels.register(new ServletLabelLocator(_ctx));
		else
			for (int j = 0; j < labellocs.length; ++j)
				Labels.register(new ServletLabelLocator(_ctx, labellocs[j]));
		Labels.setVariableResolver(new ServletRequestResolver());

		//create a WebApp instance
		Class cls = config.getWebAppFactoryClass();
		if (cls != null) {
			try {
				_wapp = ((WebAppFactory) cls.newInstance()).newWebApp(_ctx, config);
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
			}
		} else {
			cls = config.getWebAppClass();
			if (cls != null) {
				try {
					_wapp = (WebApp) cls.newInstance();
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex, "Unable to construct " + cls);
				}
			} else {
				_wapp = new SimpleWebApp();
			}
		}
		WebAppsCtrl.setCurrent(_wapp);
		((WebAppCtrl) _wapp).init(_ctx, config);

		_cwr.setEncodeURLPrefix(getCWRURLPrefix());
		boolean isDebugJS = config.isDebugJS();
		_cwr.setDebugJS(isDebugJS);
		Extendlet wpdExtendlet = new WpdExtendlet();
		checkAndAddExtendlet("wpd", wpdExtendlet); //add after _cwr.setDebugJS (since it calls back)
		if (isDebugJS) //only handle sourcemap in debugJS mode
			checkAndAddExtendlet("map", wpdExtendlet);
		checkAndAddExtendlet("wcs", new WcsExtendlet());

		//Register resource processors for each extension
		//FUTURE: Extendlet can be specified in zk.xml
		//Note: getAll loads config.xml, which must be processed before zk.xml
		ZumlExtendlet extlet = null;
		for (Iterator<LanguageDefinition> it = LanguageDefinition.getAll().iterator(); it.hasNext();) {
			final LanguageDefinition langdef = it.next();
			final List<String> exts = langdef.getExtensions();
			if (!exts.isEmpty()) {
				if (extlet == null)
					extlet = new ZumlExtendlet();
				checkAndAddExtendlet(exts.get(0), extlet);
				//Add to the first extension only (the main one)
			}
		}

		final List<WebManagerActivationListener> listeners = _actListeners.remove(_ctx); //called and drop
		if (listeners != null) {
			for (Iterator<WebManagerActivationListener> it = CollectionsX.comodifiableIterator(listeners); it
					.hasNext();) {
				try {
					it.next().didActivate(this);
				} catch (Throwable ex) {
					log.error("", ex);
				}
			}
		}
	}

	private void checkAndAddExtendlet(String ext, Extendlet extlet) {
		if (_cwr.getExtendlet(ext, false) == null)
			_cwr.addExtendlet(ext, extlet);
	}

	/** Returns the prefix of URL to represent this build. */
	private String getCWRURLPrefix() {
		final String verInfoEnabled = Library.getProperty("org.zkoss.zk.ui.versionInfo.enabled", "true");
		String build = _wapp.getBuild();
		if (!"true".equals(verInfoEnabled)) {
			return org.zkoss.zk.ui.http.Utils.obfuscateHashWithSalt(build, verInfoEnabled);
		}
		int code = _wapp.getVersion().hashCode() ^ build.hashCode() ^ WebApps.getEdition().hashCode();

		for (Iterator<LanguageDefinition> it = LanguageDefinition.getAll().iterator(); it.hasNext();) {
			final LanguageDefinition langdef = it.next();
			for (Iterator e = langdef.getJavaScriptModules().entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry) e.next();
				code ^= Objects.hashCode(me.getKey()) + Objects.hashCode(me.getValue());
			}
			for (Iterator e = langdef.getMergedJavaScriptPackages("zk").iterator(); e.hasNext();) {
				code ^= Objects.hashCode(e.next());
			}
		}

		return Integer.toHexString(code);
		//FF 8-char boundary: http://code.google.com/intl/de/speed/page-speed/docs/caching.html
	}

	public void destroy() {
		try {
			((WebAppCtrl) _wapp).destroy();
		} finally {
			_ctx.removeAttribute(ATTR_WEB_MANAGER);
			WebAppsCtrl.setCurrent(null);
		}
	}

	/** Returns the handler to retrieve resource from class path,
	 * under /web.
	 */
	public final ClassWebResource getClassWebResource() {
		return _cwr;
	}

	/** Returns the URI for asynchronous update.
	 * <p>Notice that the returned URI is not encoded, i.e., it doesn't
	 * proceed with the servlet context prefix.
	 * @see Desktop#getUpdateURI
	 * @see WebApp#getUpdateURI
	 * @since 3.6.2
	 */
	public String getUpdateURI() {
		return _updateURI;
	}

	/** Returns the URI for ZK resource
	 * <p>Notice that the returned URI is not encoded, i.e., it doesn't
	 * proceed with the servlet context prefix.
	 * @see Desktop#getResourceURI
	 * @see WebApp#getResourceURI
	 * @since 9.5.0
	 */
	public String getResourceURI() {
		return _resourceURI;
	}

	/** Returns the Web application.
	 * Notice: a Web application is allocated for each servlet.
	 */
	public final WebApp getWebApp() {
		return _wapp;
	}

	/** Returns the Web application of the specified context.
	 * @exception UiException if not found (i.e., not initialized
	 * properly)
	 * @since 5.0.0
	 */
	public static final WebApp getWebApp(ServletContext ctx) {
		final WebManager webman = getWebManagerIfAny(ctx);
		final WebApp wapp = webman != null ? webman.getWebApp() : null;
		if (wapp == null)
			throw new UiException("The Web application not found. Make sure <load-on-startup> is specified for "
					+ DHtmlLayoutServlet.class.getName());
		return wapp;
	}

	/** Called by DHtmlLayoutServlet#init when WebManager is created
	 * by HttpSessionListener#contextInitialized
	 * 
	 * @param updateURI the URI for asynchronous update.
	 */
	/*package*/ void setUpdateUri(String updateURI) {
		_updateURI = updateURI;
	}

	/** Called by DHtmlLayoutServlet#init when WebManager is created
	 * by HttpSessionListener#contextInitialized
	 *
	 * @param resourceURI the URI for ZK resource.
	 */
	/*package*/ void setResourceUri(String resourceURI) {
		_resourceURI = resourceURI;
		_cwr.setMappingURI(resourceURI);
	}

	//-- static --//
	/** Register a listener to the specified context such that
	 * it will be invoked if the corresponding {@link WebManager} is created.
	 *
	 * <p>Note: if the Web manager is created already, {@link WebManagerActivationListener#didActivate}
	 * will be invoked immediately before this method returns.
	 *
	 * @since 2.4.0
	 */
	public static final void addActivationListener(ServletContext ctx, WebManagerActivationListener listener) {
		if (ctx == null || listener == null)
			throw new IllegalArgumentException("null");

		final WebManager webman = getWebManagerIfAny(ctx);
		if (webman != null) {
			listener.didActivate(webman);
		} else {
			synchronized (WebManager.class) {
				List<WebManagerActivationListener> l = _actListeners.get(ctx);
				if (l == null)
					_actListeners.put(ctx, l = new LinkedList<WebManagerActivationListener>());
				l.add(listener);
			}
		}
	}

	/** Returns the Web manager of the specified context.
	 * @exception UiException if not found (i.e., not initialized
	 * properly)
	 */
	public static final WebManager getWebManager(ServletContext ctx) {
		final WebManager webman = getWebManagerIfAny(ctx);
		if (webman == null)
			throw new UiException("The Web manager not found. Make sure <load-on-startup> is specified for "
					+ DHtmlLayoutServlet.class.getName());
		return webman;
	}

	/** Returns the Web manager of the specified {@link WebApp}.
	 * @since 3.0.4
	 */
	public static final WebManager getWebManager(WebApp wapp) {
		return getWebManager(wapp.getServletContext());
	}

	/** Returns the Web manager of the give context, or null if not found.
	 * @since 5.0.5
	 */
	public static final WebManager getWebManagerIfAny(ServletContext ctx) {
		return (WebManager) ctx.getAttribute(ATTR_WEB_MANAGER);
	}

	/** Returns the Web manager of the specified {@link WebApp},
	 * or null if not found.
	 * @since 5.0.5
	 */
	public static final WebManager getWebManagerIfAny(WebApp wapp) {
		return getWebManagerIfAny(wapp.getServletContext());
	}

	/** Returns the Web application of the specified context, or null
	 * if not available.
	 * @since 5.0.3
	 */
	public static final WebApp getWebAppIfAny(ServletContext ctx) {
		final WebManager webman = getWebManagerIfAny(ctx);
		return webman != null ? webman.getWebApp() : null;
	}

	/**  Returns the session associated with the specified request request.
	 * If the request does not have a session, creates one.
	 */
	public static final Session getSession(ServletContext ctx, HttpServletRequest request) {
		return getSession(ctx, request.getSession(), request);
	}

	/**Returns the current HttpSession  associated with this request or,
	 * if there is no current session and create is true, returns a new session.
	 *
	 * @param create true to create a new session for this request if necessary;
	 * false to return null  if there's no current session
	 * @since 3.0.1
	 */
	public static final Session getSession(ServletContext ctx, HttpServletRequest request, boolean create) {
		final HttpSession hsess = request.getSession(create);
		return hsess != null ? getSession(ctx, hsess, request) : null;
	}

	private static final Session getSession(ServletContext ctx, HttpSession hsess, HttpServletRequest request) {
		final WebApp wapp = getWebManager(ctx).getWebApp();
		final Session sess = SessionsCtrl.getSession(wapp, hsess);
		return sess != null ? sess : SessionsCtrl.newSession(wapp, hsess, request);
	}

	/** Called when a HTTP session listener is notified.
	 * <p>Once called the session is cleaned. All desktops are dropped.
	 */
	/*package*/ static final void sessionDestroyed(HttpSession hsess) {
		//Under JBoss, the servlet might be destroyed before this callback
		final WebManager webman = getWebManagerIfAny(hsess.getServletContext());
		if (webman != null) {
			final WebApp wapp = webman.getWebApp();
			final Session sess = SessionsCtrl.getSession(wapp, hsess);
			if (sess != null)
				((WebAppCtrl) wapp).sessionDestroyed(sess);
		}
	}

	/** Returns the desktop of the specified request, or null
	 * if not found and autocreate is false, or it has been redirect
	 * or forward to other page.
	 * @param path the path of the ZUML page.
	 * @param autocreate whether to create one if not found
	 * @since 3.0.0
	 */
	public Desktop getDesktop(Session sess, ServletRequest request, ServletResponse response, String path,
			boolean autocreate) {
		Desktop desktop = (Desktop) request.getAttribute(ATTR_DESKTOP);
		if (desktop == null && autocreate) {
			if (log.isDebugEnabled())
				log.debug("Create desktop for " + path);
			request.setAttribute(ATTR_DESKTOP, desktop = newDesktop(sess, request, response, path));
		}
		return desktop;
	}

	/** Creates an desktop. */
	private Desktop newDesktop(Session sess, ServletRequest request, ServletResponse response, String path) {
		final Locator loc = PageDefinitions.getLocator(_wapp, path);
		//we have to retrieve locator before setting execution,
		//since it assumes exec.getDesktop not null
		//which is true except this moment (before desktop is created)

		final Execution exec = ExecutionsCtrl.getCurrent();
		final TemporaryExecution de = new TemporaryExecution(_ctx, (HttpServletRequest) request,
				(HttpServletResponse) response, null);
		ExecutionsCtrl.setCurrent(de);
		try {
			Desktop desktop = ((WebAppCtrl) _wapp).getUiFactory()
					.newDesktop(new RequestInfoImpl(_wapp, sess, null, request, loc), _updateURI, _resourceURI, path);
			return !de.isVoided() ? desktop : null;
		} finally {
			ExecutionsCtrl.setCurrent(exec);
		}
	}

	/** Sets the desktop to the specified request.
	 * Used internally for implementation only.
	 */
	public static void setDesktop(HttpServletRequest request, Desktop desktop) {
		request.setAttribute(ATTR_DESKTOP, desktop);
	}

	/** Creates a page.
	 * It invokes {@link UiFactory#newPage}. However, it prepares
	 * {@link ExecutionsCtrl#getCurrent} for {@link org.zkoss.zk.ui.sys.IdGenerator#nextPageUuid}
	 *
	 * <p>Note: Use this method to create a page, rather than invoking
	 * {@link UiFactory#newPage} directly.
	 * @since 3.6.0
	 */
	public static Page newPage(UiFactory uf, RequestInfo ri, PageDefinition pagedef, ServletResponse response,
			String path) {
		final DesktopCtrl desktopCtrl = (DesktopCtrl) ri.getDesktop();
		final Execution exec = ExecutionsCtrl.getCurrent();
		TemporaryExecution de = new TemporaryExecution(ri.getWebApp().getServletContext(),
				(HttpServletRequest) ri.getNativeRequest(), (HttpServletResponse) response, ri.getDesktop());
		desktopCtrl.setExecution(de);
		ExecutionsCtrl.setCurrent(de);
		try {
			return uf.newPage(ri, pagedef, path);
			//de won't be voided since no DesktopInit-like plugin
		} finally {
			ExecutionsCtrl.setCurrent(exec);
			desktopCtrl.setExecution(exec);
		}
	}

	/** Creates a page.
	 * It invokes {@link UiFactory#newPage}. However, it prepares
	 * {@link ExecutionsCtrl#getCurrent} for {@link org.zkoss.zk.ui.sys.IdGenerator#nextPageUuid}
	 *
	 * <p>Note: Use this method to create a page, rather than invoking
	 * {@link UiFactory#newPage} directly.
	 * @since 3.6.0
	 */
	public static Page newPage(UiFactory uf, RequestInfo ri, Richlet richlet, ServletResponse response, String path) {
		final DesktopCtrl desktopCtrl = (DesktopCtrl) ri.getDesktop();
		final Execution exec = ExecutionsCtrl.getCurrent();
		TemporaryExecution de = new TemporaryExecution(ri.getWebApp().getServletContext(),
				(HttpServletRequest) ri.getNativeRequest(), (HttpServletResponse) response, ri.getDesktop());
		desktopCtrl.setExecution(de);
		ExecutionsCtrl.setCurrent(de);
		try {
			return uf.newPage(ri, richlet, path);
			//de won't be voided since no DesktopInit-like plugin
		} finally {
			ExecutionsCtrl.setCurrent(exec);
			desktopCtrl.setExecution(exec);
		}
	}

	private static class ClientIdentifier implements Servlets.ClientIdentifier {
		private final String _name;
		private final double _version;

		private ClientIdentifier() {
			this(null, 0);
		}

		private ClientIdentifier(String name, double version) {
			_name = name;
			_version = version;
		}

		public ClientIdentifier matches(String userAgent) {
			Object[] inf = org.zkoss.zk.device.Devices.matches(userAgent);
			if (inf != null)
				return new ClientIdentifier((String) inf[0], (Double) inf[1]);
			return null;
		}

		public String getName() {
			return _name;
		}

		public double getVersion() {
			return _version;
		}
	}
}
