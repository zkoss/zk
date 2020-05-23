/* WpdExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct  6 10:47:11     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.io.Files;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletLoader;
import org.zkoss.zk.device.Device;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.WidgetDefinition;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.URIInfo;

/**
 * The extendlet to handle WPD (Widget Package Descriptor).
 *
 * <p>Note: it assumes all JavaScript files are encoded in UTF-8.
 *
 * <h3>Bootstrapping</h3>
 *
 * <p>In additions to loading, WPD allows to bootstrap a JavaScript codes
 * by specifying a parameter called main. For example, the following link
 *
 *<pre><code>&lt;script type="text/javascript" src="/zkdemo/zkau/web/js/zk.wpd?main=foo.Go&what=12&more=xy" charset="UTF-8">
&lt;/script></code></pre>
 *
 * will cause the following to be executed
 *
 *<pre><code>zk.load('foo', function() {foo.Go.main({what: '123', more: 'xy'})});</code></pre>
 *
 * In other words, it loads the package called <code>foo</code>, and then
 * invoke the <code>main</code> method of the <code>foo.Go</code> class.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class WpdExtendlet extends AbstractExtendlet<Object> {
	//source map
	private Boolean _sourceMapEnabled;
	private static final String HANDLE_SOURCE_MAPPING_URL = "zk$handlesourcemappingurl";
	private static final String SOURCE_MAP_PREFIX = "zk$sourcemap$";
	private static final String SOURCE_MAP_SUPPORTED = "zk$sourcemapsupported";
	//check closure compiler existence
	private Boolean CLOSURE_COMPILER_AVAILABLE;

	public void init(ExtendletConfig config) {
		init(config, new WpdLoader());
		if (!isDebugJS()) {
			config.addCompressExtension("wpd");
		} else if (isSourceMapEnabled() && CLOSURE_COMPILER_AVAILABLE == null) {
			CLOSURE_COMPILER_AVAILABLE = Classes.existsByThread("com.google.debugging.sourcemap.SourceMapGeneratorV3");
			if (!CLOSURE_COMPILER_AVAILABLE)
				log.warn("Closure compiler is not available.");
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		// handle sourcemapping URL
		if (isDebugJS() && path.endsWith("wpd"))
			request.getSession().setAttribute(HANDLE_SOURCE_MAPPING_URL, true);
		byte[] data = retrieve(request, response, path);
		if (data == null)
			return;

		response.setContentType("text/javascript;charset=UTF-8");
		if (_webctx.shallCompress(request, "wpd") && data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null)
				data = bs; //yes, browser support compress
		}
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
	}

	/** Retrieves the content of the given path.
	 * @since 5.0.4
	 */
	protected byte[] retrieve(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, IOException {
		byte[] data;
		String pkg = null;
		boolean isSourceMapSupported = false;
		Boolean shouldHandleSourceMappingURL = null;
		if (isDebugJS()) {
			String userAgent = request.getHeader("user-agent");
			isSourceMapSupported = Objects.equals(CLOSURE_COMPILER_AVAILABLE, true)
					&& (Servlets.isBrowser(userAgent, "chrome") || Servlets.isBrowser(userAgent, "ff")
					|| Servlets.isBrowser(userAgent, "ie11") || Servlets.isBrowser(userAgent, "safari"));
			if (isSourceMapSupported) {
				HttpSession session = request.getSession();
				if (path.endsWith("map")) {
					String name = path.substring(path.lastIndexOf("/") + 1).replaceAll(".map", "");
					SourceMapManager sourceMapManager = (SourceMapManager) session.getAttribute(SOURCE_MAP_PREFIX + name);
					if (sourceMapManager == null) {
						log.warn("Failed to load the source map resource: " + path);
						response.sendError(HttpServletResponse.SC_NOT_FOUND, path);
						return null;
					}
					String sourceMapContent = sourceMapManager.getSourceMapContent();
					return sourceMapContent.getBytes();
				}
				shouldHandleSourceMappingURL = (Boolean) session.getAttribute(HANDLE_SOURCE_MAPPING_URL);
				if (shouldHandleSourceMappingURL != null)
					session.removeAttribute(HANDLE_SOURCE_MAPPING_URL);
				request.setAttribute(SOURCE_MAP_SUPPORTED, true);
			}
		}
		/* 2011/4/27 Tony:
		 * Here we don't use "org.zkoss.web.classWebResource.cache" directly,
		 * since some of our users might want to clear the cache for their css.dsp ,
		 * and still cache the wpd js resource.
		 * 
		 * So we add on a new config to clear the widget source .
		 * @see bug 2898413
		 */
		String resourceCache = Library.getProperty("org.zkoss.zk.WPD.cache");
		if (resourceCache != null && "false".equalsIgnoreCase(resourceCache))
			_cache.clear();

		final Content content = (Content) _cache.get(path);
		if (content == null) {
			if (Servlets.isIncluded(request)) {
				log.error("Failed to load the resource: " + path);
				//It might be eaten, so log the error
				throw new java.io.FileNotFoundException("Failed to load the resource: " + path);
				//have the includer to handle it
			}
			response.sendError(response.SC_NOT_FOUND, path);
			return null;
		}

		final boolean cacheable;
		final RequestContext reqctx = new RequestContext(this, request, response);
		synchronized (content) {
			final Object rawdata = content.parse(reqctx);
			if (rawdata instanceof ByteContent) {
				final ByteContent bc = (ByteContent) rawdata;
				data = bc.content;
				cacheable = bc.cacheable;
			} else {
				final WpdContent wc = (WpdContent) rawdata;
				data = wc.toByteArray(reqctx);
				pkg = wc.name;
				cacheable = wc.cacheable;
			}
		}
		if (cacheable) {
			boolean isNotModified = org.zkoss.zk.fn.JspFns.setCacheControl(getServletContext(), request, response,
					"org.zkoss.web.classWebResource.cache", 8760);
			if (isNotModified)
				return null;
		}

		if (pkg != null)
			data = mergeJavaScript(request, response, pkg, data);

		if (isSourceMapSupported && shouldHandleSourceMappingURL != null) {
			if (pkg == null)
				pkg = path.substring(path.lastIndexOf("/") + 1).replaceAll(".wpd", "");
			pkg = SOURCE_MAP_PREFIX + pkg; //key
			SourceMapManager sourceMapManager = (SourceMapManager) request.getAttribute(pkg);
			if (sourceMapManager != null) {
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 100);
				out.write(data);
				out.write(("//# sourceMappingURL=" + sourceMapManager.getSourceMappingURL() + "\n").getBytes());
				data = out.toByteArray();
				request.getSession().setAttribute(pkg, sourceMapManager);
			}
		}
		return data;
	}

	/** Returns the device type for this WpdExtendlet.
	 * <p>Default: ajax.
	 * The derived class might override it to implement a Wpd extendlet
	 * for other devices.
	 * @since 5.0.4
	 */
	protected String getDeviceType() {
		return "ajax";
	}

	/** Merges the JavaScript code of the mergeable packages defined in
	 * {@link LanguageDefinition#getMergedJavaScriptPackages}.
	 * @since 5.0.4.
	 */
	protected byte[] mergeJavaScript(HttpServletRequest request, HttpServletResponse response, String pkgTo,
			byte[] data) throws ServletException, IOException {
		ByteArrayOutputStream out = null;
		Device device = null;
		final String deviceType = getDeviceType();
		SourceMapManager sourceMapManagerPkgTo = (SourceMapManager) request.getAttribute(SOURCE_MAP_PREFIX + pkgTo);
		for (LanguageDefinition langdef : LanguageDefinition.getByDeviceType(deviceType)) {
			for (String pkg : langdef.getMergedJavaScriptPackages(pkgTo)) {
				if (out == null) {
					out = new ByteArrayOutputStream(1024 * 100);
					out.write(data);

					device = Devices.getDevice(deviceType);
				}
				final String path = device.packageToPath(pkg);
				data = retrieve(request, response, path);
				if (data != null) {
					out.write(data);
					if (request.getAttribute(SOURCE_MAP_SUPPORTED) != null) {
						//merge source map
						SourceMapManager sourceMapManager = (SourceMapManager) request
								.getAttribute(SOURCE_MAP_PREFIX + pkg);
						if (sourceMapManager == null)
							log.warn("Failed to merge the source map: " + path);
						else
							sourceMapManagerPkgTo.appendSourceMap(sourceMapManager);
					}
				} else
					log.error("Failed to load the resource: " + path);
			}
		}
		return out != null ? out.toByteArray() : data;
	}

	private Object parse(RequestContext reqctx, InputStream is, String path) throws Exception {
		final Element root = new SAXBuilder(true, false, true).build(is).getRootElement();
		final String name = IDOMs.getRequiredAttributeValue(root, "name");
		if (name.length() == 0)
			throw new UiException("The name attribute must be specified, " + root.getLocator() + ", " + path);
		final boolean zk = "zk".equals(name), aaas = "zk.aaas".equals(name);
		final String lang = root.getAttributeValue("language");
		final LanguageDefinition langdef = //optional
				lang != null ? LanguageDefinition.lookup(lang) : null;
		final String dir = path.substring(0, path.lastIndexOf('/') + 1);
		final boolean cacheable = !"false".equals(root.getAttributeValue("cacheable"));

		final WpdContent wc = zk || aaas || !cacheable || isWpdContentRequired(name, root)
				? new WpdContent(name, dir, cacheable) : null;

		final ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 16);
		String depends = null;
		SourceMapManager sourceMapManager = null;
		String requestURI = reqctx.request.getRequestURI();
		if (isDebugJS() && reqctx.request.getAttribute(SOURCE_MAP_SUPPORTED) != null) {
			String sourceRoot = requestURI.substring(0, requestURI.indexOf("js/"));
			sourceMapManager = new SourceMapManager(name, sourceRoot, reqctx.request.getSession().getId());
		}
		if (zk) {
			write(out, "if(!window.zk){\n");
			if (sourceMapManager != null)
				sourceMapManager.appendEmptySourceMap(1);
			//may be loaded multiple times because specified in lang.xml
		} else if (!aaas) {
			depends = root.getAttributeValue("depends");
			if (depends != null && depends.length() == 0)
				depends = null;
			if (depends != null) {
				write(out, "zk.load('");
				write(out, depends);
				write(out, "',");
			} else
				write(out, '(');
			write(out, "function(){if(zk._p=zkpi('");
			write(out, name);
			write(out, '\'');
			if (reqctx.getResource(dir + "wv/zk.wpd") != null)
				write(out, ",true");
			write(out, "))try{\n");
			if (sourceMapManager != null)
				sourceMapManager.appendEmptySourceMap(1);
		}

		final Map<String, String[]> moldInfos = new HashMap<String, String[]>();
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			final String elnm = el.getName();
			if ("widget".equals(elnm)) {
				final String wgtnm = IDOMs.getRequiredAttributeValue(el, "name");
				final String jspath = wgtnm + ".js"; //eg: /js/zul/wgt/Div.js
				if (!writeResource(reqctx, out, jspath, dir, false, sourceMapManager)) {
					log.error("Widget " + wgtnm + ": " + jspath + " not found, " + el.getLocator() + ", " + path);
					continue;
				}

				final String wgtflnm = name + "." + wgtnm;
				write(out, "zkreg('");
				write(out, wgtflnm);
				write(out, '\'');
				WidgetDefinition wgtdef = langdef != null ? langdef.getWidgetDefinitionIfAny(wgtflnm) : null;
				if (wgtdef != null && wgtdef.isBlankPreserved())
					write(out, ",true");
				write(out, ");\n");
				if (sourceMapManager != null)
					sourceMapManager.appendEmptySourceMap(1);
				if (wgtdef == null)
					continue;

				try {
					boolean first = true;
					for (Iterator e = wgtdef.getMoldNames().iterator(); e.hasNext();) {
						final String mold = (String) e.next();
						final String uri = wgtdef.getMoldURI(mold);
						if (uri == null)
							continue;

						if (first) {
							first = false;
							write(out, "zk._m={};\n");
							if (sourceMapManager != null)
								sourceMapManager.appendEmptySourceMap(1);
						}

						write(out, "zk._m['");
						write(out, mold);
						write(out, "']=");

						String[] info = (String[]) moldInfos.get(uri);
						if (info != null) { //reuse
							write(out, "[zk._p.p.");
							write(out, info[0]);
							write(out, ",'");
							write(out, info[1]);
							write(out, "'];\n");
							if (sourceMapManager != null)
								sourceMapManager.appendEmptySourceMap(1);
						} else {
							moldInfos.put(uri, new String[] { wgtnm, mold });
							if (!writeResource(reqctx, out, uri, dir, true, sourceMapManager)) {
								write(out, "zk.$void;zk.error('");
								write(out, uri);
								write(out, " not found')");
								log.error("Failed to load mold " + mold + " for widget " + wgtflnm + ": " + uri
										+ " not found");
							}
							write(out, ';');
						}
					}
					if (!first) {
						write(out, "zkmld(");
						write(out, zk ? "zk." : "zk._p.p.");
						write(out, wgtnm);
						write(out, ",zk._m);\n");
						if (sourceMapManager != null)
							sourceMapManager.appendEmptySourceMap(1);
					}
				} catch (Throwable ex) {
					log.error("Failed to load molds for widget " + wgtflnm + ".\nCause: " + Exceptions.getMessage(ex));
				}
			} else if ("script".equals(elnm)) {
				String browser = el.getAttributeValue("browser");
				String jspath = el.getAttributeValue("src");
				if (jspath != null && jspath.length() > 0) {
					if (wc != null && (browser != null || jspath.indexOf('*') >= 0)) {
						move(wc, out);
						wc.add(jspath, browser);
						if (sourceMapManager != null)
							sourceMapManager.addNonResolvedSourceMap();
					} else {
						if (browser != null && (!Servlets.isBrowser(reqctx.request, browser)
								// F70-ZK-1956: Check whether the script should be loaded or ignored.
								|| getScriptManager().isScriptIgnored(reqctx.request, jspath)))
							continue;
						if (!writeResource(reqctx, out, jspath, dir, true, sourceMapManager))
							log.error(jspath + " not found, " + el.getLocator() + ", " + path);
					}
				}

				String s = el.getText(true);
				if (s != null && s.length() > 0) {
					write(out, s);
					write(out, '\n'); //might terminate with //
					if (sourceMapManager != null)
						sourceMapManager.appendEmptySourceMap(countLines(s) + 1);
				}
			} else if ("function".equals(elnm)) {
				final MethodInfo mtd = getMethodInfo(el);
				if (mtd != null)
					if (wc != null) {
						move(wc, out);
						wc.add(mtd);
						if (sourceMapManager != null)
							sourceMapManager.addNonResolvedSourceMap();
					} else {
						String js = write(reqctx, out, mtd);
						if (sourceMapManager != null)
							sourceMapManager.appendEmptySourceMap(countLines(js));
					}
			} else {
				log.warn("Unknown element " + elnm + ", " + el.getLocator() + ", " + path);
			}
		}
		if (zk) {
			final WebApp wapp = getWebApp();
			if (wapp != null)
				writeAppInfo(reqctx, out, wapp, sourceMapManager);
			write(out, '}'); //end of if(window.zk)

			writeHost(wc, out, wapp);
			if (sourceMapManager != null)
				sourceMapManager.addNonResolvedSourceMap();
		} else if (aaas) {
			writeHost(wc, out, getWebApp());
			if (sourceMapManager != null)
				sourceMapManager.addNonResolvedSourceMap();
		} else {
			if (sourceMapManager != null)
				sourceMapManager.appendEmptySourceMap(1);
			write(out, "\n}finally{zk.setLoaded(zk._p.n);}");
			if (depends != null) {
				write(out, "});zk.setLoaded('");
				write(out, name);
				write(out, "',1);");
			} else
				write(out, "})();");
			if (sourceMapManager != null)
				sourceMapManager.appendEmptySourceMap(1);
		}

		if (sourceMapManager != null) {
			reqctx.request.setAttribute(SOURCE_MAP_PREFIX + name, sourceMapManager);
		}
		if (wc != null) {
			move(wc, out);
			return wc;
		}
		return new ByteContent(out.toByteArray(), cacheable);
	}

	private ScriptManager getScriptManager() {
		if (_smanager == null) {
			synchronized (this) {
				if (_smanager == null) {
					String clsnm = Library.getProperty("org.zkoss.zk.ui.http.ScriptManager.class");
					if (clsnm != null) {
						try {
							_smanager = (ScriptManager) Classes.newInstanceByThread(clsnm);
						} catch (Throwable ex) {
							log.error("Unable to instantiate " + clsnm, ex);
						}
					}
					if (_smanager == null)
						_smanager = new ScriptManagerImpl();
				}
			}
		}
		return _smanager;
	}

	private static volatile ScriptManager _smanager;

	private boolean isWpdContentRequired(String pkg, Element root) {
		for (LanguageDefinition langdef : LanguageDefinition.getByDeviceType(getDeviceType()))
			if (langdef.getJavaScriptPackagesWithMerges().contains(pkg))
				return true;

		for (Iterator it = root.getElements("script").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			if (el.getAttributeValue("browser") != null)
				return true;
		}
		return false;
	}

	private void writeHost(WpdContent wc, ByteArrayOutputStream out, WebApp wapp) {
		if (wapp != null) {
			final String[] pkgs = wapp.getConfiguration().getClientPackages();
			if (pkgs.length > 0) {
				move(wc, out);
				wc.addHost(wapp, JSONArray.toJSONString(pkgs));
			}
		}
	}

	private boolean writeResource(RequestContext reqctx, OutputStream out, String path, String dir, boolean locate,
			SourceMapManager sourceMapManager) throws IOException, ServletException {
		return writeResource(reqctx, out, path, dir, locate, sourceMapManager, -1);
	}

	private boolean writeResource(RequestContext reqctx, OutputStream out, String path, String dir, boolean locate,
			SourceMapManager sourceMapManager, int index) throws IOException, ServletException {
		if (path.startsWith("~./"))
			path = path.substring(2);
		else if (path.charAt(0) != '/')
			path = Files.normalize(dir, path);

		//source map browser issue and check source map file is available or not
		InputStream isSourceMap = null;
		String originalPath = "";
		if (isDebugJS()) {
			if (sourceMapManager != null)
				isSourceMap = reqctx.getResourceAsStream(path + ".map", locate);
			if ("js".equals(Servlets.getExtension(path)) && !path.endsWith(".src.js")
					&& (isSourceMap == null || sourceMapManager == null)) {
				originalPath = path; //if .src.js not exist -> roll back
				path = path.substring(0, path.length() - 3) + ".src.js";
			}
		}

		InputStream is = reqctx.getResourceAsStream(path, locate);
		while (is == null) {
			if (Strings.isEmpty(originalPath)) {
				write(out, "zk.log('");
				write(out, path);
				write(out, " not found');");
				return false;
			} else {
				path = originalPath;
				is = reqctx.getResourceAsStream(path, locate);
				originalPath = null;
			}
		}

		if (sourceMapManager != null) {
			String sourceMapContent = "";
			final InputStream isCopy = reqctx.getResourceAsStream(path, locate);
			String jsContent = IOUtils.toString(isCopy);
			if (isSourceMap != null)
				sourceMapContent = IOUtils.toString(isSourceMap);
			String sourceMapSourcePath = path;
			if (!sourceMapSourcePath.endsWith(".src.js")) {
				String basePath = sourceMapSourcePath.substring(0, sourceMapSourcePath.length() - 3);
				sourceMapSourcePath = reqctx.getResource(basePath + ".ts") != null // source might be .ts
						? basePath + ".ts"
						: basePath + ".src.js";
			}
			sourceMapManager.insertSourceMap(index, sourceMapContent, countLines(jsContent), sourceMapSourcePath.substring(1)); //skip first "/"
			sourceMapManager.insertEmptySourceMap((index == -1) ? -1 : index + 1, 1); //for \n
			if (index != -1) {
				sourceMapManager.getSourceMapInfoList().remove(index + 2);
				sourceMapManager.getSourceMapInfoList().remove(index + 2);
			}
			Files.close(isCopy);
			Files.close(isSourceMap);
		}

		Files.copy(out, is);
		Files.close(is);
		write(out, '\n'); //might terminate with //
		return true;
	}

	private int countLines(String js) {
		int line = 0;
		for (int i = 0; i < js.length(); i++) {
			if (js.charAt(i) == '\n') {
				line++;
			}
		}
		return line;
	}

	private void write(OutputStream out, String s) throws IOException {
		if (s != null) {
			final byte[] bs = s.getBytes("UTF-8");
			out.write(bs, 0, bs.length);
		}
	}

	private void write(OutputStream out, char cc) throws IOException {
		assert cc < 128;
		final byte[] bs = new byte[] { (byte) cc };
		out.write(bs, 0, 1);
	}

	//return result, only for sourcemap
	private String write(RequestContext reqctx, OutputStream out, MethodInfo mtd) throws IOException {
		try {
			String result = invoke(reqctx, mtd);
			write(out, result);
			return result;
		} catch (IOException ex) {
			throw ex;
		}
	}

	/*private static void write(OutputStream out, StringBuffer sb)
	throws IOException {
		final byte[] bs = sb.toString().getBytes("UTF-8");
		out.write(bs, 0, bs.length);
		sb.setLength(0);
	}*/
	private void writeln(OutputStream out) throws IOException {
		write(out, '\n');
	}

	private void move(WpdContent wc, ByteArrayOutputStream out) {
		final byte[] bs = out.toByteArray();
		if (bs.length > 0) {
			wc.add(bs);
			out.reset();
		}
	}

	private void writeAppInfo(RequestContext reqctx, OutputStream out, WebApp wapp, SourceMapManager sourceMapManager)
			throws IOException, ServletException {
		final String verInfoEnabled = Library.getProperty("org.zkoss.zk.ui.versionInfo.enabled", "true");
		final boolean exposeVer = "true".equals(verInfoEnabled);
		final StringBuffer sb = new StringBuffer(256);
		if (exposeVer)
			sb.append("\nzkver('").append(wapp.getVersion()).append("','");
		else
			sb.append("\nzkver('','");

		sb.append(obfuscateVer(exposeVer, wapp.getBuild(), verInfoEnabled));

		final ServletContext ctx = getServletContext();
		String s = Encodes.encodeURL(ctx, reqctx.request, reqctx.response, "/");
		int j = s.lastIndexOf('/'); //might have jsessionid=...
		if (j >= 0)
			s = s.substring(0, j) + s.substring(j + 1);

		sb.append("','").append(s).append("','")
				.append(Encodes.encodeURL(ctx, reqctx.request, reqctx.response, wapp.getUpdateURI(false)));

		sb.append("',{");
		for (Iterator it = LanguageDefinition.getByDeviceType(getDeviceType()).iterator(); it.hasNext();) {
			final LanguageDefinition langdef = (LanguageDefinition) it.next();
			for (Iterator e = langdef.getJavaScriptModules().entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry) e.next();
				sb.append('\'').append(me.getKey()).append("':'")
						.append(obfuscateVer(exposeVer, me.getValue(), verInfoEnabled)).append("',");
			}
			removeLast(sb, ',');
		}

		sb.append("},{");
		if (WebApps.getFeature("ee"))
			sb.append("ed:'e',");
		else if (WebApps.getFeature("pe"))
			sb.append("ed:'p',");

		final Configuration config = wapp.getConfiguration();
		int v = config.getProcessingPromptDelay();
		if (v != 900)
			sb.append("pd:").append(v).append(',');
		v = config.getTooltipDelay();
		if (v != 800)
			sb.append("td:").append(v).append(',');
		v = config.getAutoResendTimeout();
		if (v != 200)
			sb.append("art:").append(v).append(',');
		if (config.isTimerKeepAlive())
			sb.append("ta:1,");
		if (config.isDebugJS())
			sb.append("dj:1,");
		if (config.isKeepDesktopAcrossVisits())
			sb.append("kd:1,");
		if (config.getPerformanceMeter() != null)
			sb.append("pf:1,");
		if (!config.isHistoryStateEnabled())
			sb.append("hs:0,");

		final String deviceType = getDeviceType();
		Object[][] infs = config.getClientErrorReloads(deviceType, null);
		if (infs != null) {
			sb.append("eu:{");
			outErrReloads(reqctx, config, sb, infs);
			sb.append("},");
		}
		infs = config.getClientErrorReloads(deviceType, "server-push");
		if (infs != null) {
			sb.append("eup:{");
			outErrReloads(reqctx, config, sb, infs);
			sb.append("},");
		}

		//ZK-4564
		sb.append("resURI:'").append(WebManager.getWebManager(ctx).getResourceURI()).append("'");
		sb.append("});");
		write(out, sb.toString());
		if (sourceMapManager != null)
			sourceMapManager.appendEmptySourceMap(1);
	}

	private Object obfuscateVer(boolean exposeVersion, Object ver, String salt) {
		return exposeVersion ? ver : Integer.toHexString(37 * ver.hashCode() + salt.hashCode());
	}

	private void outErrReloads(RequestContext reqctx, Configuration config, StringBuffer sb, Object[][] infs) {
		for (int j = 0; j < infs.length; ++j) {
			if (j > 0)
				sb.append(',');
			sb.append('\'').append(infs[j][0]).append("':'");

			String uri = ((URIInfo) infs[j][1]).uri;
			if (uri.length() > 0)
				try {
					uri = Encodes.encodeURL(getServletContext(), reqctx.request, reqctx.response, uri);
				} catch (javax.servlet.ServletException ex) {
					throw new UiException("Unable to encode " + uri, ex);
				}
			sb.append(Strings.escape(uri, "'\\")).append('\'');
		}
	}

	private static void removeLast(StringBuffer sb, char cc) {
		if (sb.charAt(sb.length() - 1) == cc)
			sb.setLength(sb.length() - 1); //remove last comma
	}

	private static String outMain(String main, Map<String, String[]> params) {
		final StringBuffer sb = new StringBuffer("\nzkamn('");
		final int j = main.lastIndexOf('.');
		if (j >= 0)
			sb.append(main.substring(0, j));

		sb.append("',function(){\n").append(main).append(".main(");

		final Map<String, Object> ms = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, String[]> me : params.entrySet()) {
			final String nm = me.getKey();
			if (!"main".equals(nm)) {
				final String[] vals = me.getValue();
				ms.put(nm, vals.length == 0 ? null : vals.length == 1 ? vals[0] : vals);
			}
		}

		sb.append(JSONObject.toJSONString(ms)).append(")\n})");
		return sb.toString();
	}

	private static String outHost(HttpServletRequest request, WebApp wapp, String clientPackages) {
		final StringBuffer sb = new StringBuffer().append("zk.setHost('").append(request.getScheme()).append("://")
				.append(request.getServerName());
		if (request.getServerPort() != 80)
			sb.append(':').append(request.getServerPort());

		String uri = request.getContextPath();
		if (uri != null && uri.length() > 0) {
			if (uri.charAt(0) != '/')
				sb.append('/');
			sb.append(uri);
			removeLast(sb, '/');
		}
		return sb.append("','").append(wapp.getResourceURI(false)).append("',").append(clientPackages).append(");")
				.toString();
	}

	/**  Loader used with ResourceCache. */
	private class WpdLoader extends ExtendletLoader<Object> {
		private WpdLoader() {
		}

		//-- super --//
		protected Object parse(InputStream is, String path, String orgpath) throws Exception {
			return new Content(new SourceInfo(is, path));
		}

		protected ExtendletContext getExtendletContext() {
			return _webctx;
		}

		protected String getRealPath(String path) {
			final int j = path.lastIndexOf(".wpd");
			return path.substring(0, j).replace('.', '/') + "/zk" + path.substring(j);
		}
	}

	private static class Content {
		private Object _cnt;

		private Content(SourceInfo si) {
			_cnt = si;
		}

		private Object parse(RequestContext reqctx) throws ServletException, IOException {
			if (_cnt instanceof SourceInfo)
				try {
					_cnt = ((SourceInfo) _cnt).parse(reqctx);
				} catch (IOException ex) {
					throw ex;
				} catch (ServletException ex) {
					throw ex;
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex);
				}
			return _cnt;
		}
	}

	private class SourceInfo {
		private final byte[] _raw;
		private final String _path;

		private SourceInfo(InputStream input, String path) throws IOException {
			_raw = Files.readAll(input);
			_path = path;
		}

		private Object parse(RequestContext reqctx) throws Exception {
			return WpdExtendlet.this.parse(reqctx, new ByteArrayInputStream(_raw), _path);
		}
	}

	private static class ByteContent {
		private final byte[] content;
		private final boolean cacheable;

		private ByteContent(byte[] cnt, boolean cacheable) {
			this.content = cnt;
			this.cacheable = cacheable;
		}
	}

	private class WpdContent {
		/** The package name*/
		private final String name;
		private final String _dir;
		private final List<Object> _cnt = new LinkedList<Object>();
		/** Whether it is cacheable. */
		private final boolean cacheable;

		private WpdContent(String name, String dir, boolean cacheable) {
			this.name = name;
			_dir = dir;
			this.cacheable = cacheable;
		}

		private void add(byte[] bs) {
			_cnt.add(bs);
		}

		private void add(MethodInfo mtd) {
			_cnt.add(mtd);
		}

		private void add(String jspath, String browser) {
			_cnt.add(new String[] { jspath, browser });
		}

		private void addHost(WebApp wapp, String clientPackages) {
			_cnt.add(new Object[] { wapp, clientPackages });
		}

		@SuppressWarnings("unchecked")
		private byte[] toByteArray(RequestContext reqctx) throws ServletException, IOException {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final HttpServletRequest request = reqctx.request;
			final String main = request != null ? request.getParameter("main") : null;

			//for source map
			SourceMapManager sourceMapManager = null;
			List<String> resultList = new ArrayList<String>(16);
			List<Integer> unresolvedList = null;
			if (isDebugJS()) {
				sourceMapManager = (SourceMapManager) reqctx.request.getAttribute(SOURCE_MAP_PREFIX + name);
				if (sourceMapManager != null)
					unresolvedList = sourceMapManager.getUnresolvedSourceMapInfoIndexList();
			}
			for (Object o : _cnt) {
				String result = "";
				if (o instanceof byte[]) {
					out.write((byte[]) o);
				} else if (o instanceof MethodInfo) {
					result = write(reqctx, out, (MethodInfo) o);
					resultList.add(result);
				} else if (o instanceof String[]) {
					resultList.add("$skipped$");
					final String[] inf = (String[]) o;
					if (inf[1] != null && request != null) {
						if (!Servlets.isBrowser(request, inf[1])
								// F70-ZK-1956: Check whether the script should be loaded or ignored.
								|| getScriptManager().isScriptIgnored(reqctx.request, inf[0]))
							continue;
					}
					int sourceMapInfoIndex = unresolvedList != null ? unresolvedList.get(resultList.size() - 1) : -1;
					if (!writeResource(reqctx, out, inf[0], _dir, true, sourceMapManager, sourceMapInfoIndex))
						log.error(inf[0] + " not found");
				} else if (o instanceof Object[]) { //host
					if (main != null) {
						final Object[] inf = (Object[]) o;
						result = outHost(request, (WebApp) inf[0], (String) inf[1]);
						resultList.add(result);
						write(out, result);
					}
				}
			}

			if (sourceMapManager != null && resultList.size() != 0) {
				for (int i = 0; i < resultList.size(); i++) {
					String result = resultList.get(i);
					if ("$skipped$".equals(result)) {
						continue;
					} else
						sourceMapManager.resolveSourceMapByIndex(unresolvedList.get(i), countLines(result));
				}
			}

			if (main != null && main.length() > 0) {
				String result = outMain(main, request.getParameterMap());
				if (sourceMapManager != null && result.length() != 0)
					sourceMapManager.appendEmptySourceMap(countLines(result));
				write(out, result);
			}
			return out.toByteArray();
		}
	}

	/** Returns whether to use source map to debug. */
	private boolean isSourceMapEnabled() {
		if (_sourceMapEnabled == null) {
			final WebApp wapp = getWebApp();
			if (wapp == null)
				return false; //zk lighter
			_sourceMapEnabled = Boolean.valueOf(wapp.getConfiguration().isSourceMapEnabled());
		}
		return _sourceMapEnabled.booleanValue();
	}
}
