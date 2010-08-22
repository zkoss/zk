/* WpdExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct  6 10:47:11     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Strings;
import org.zkoss.lang.Exceptions;
import org.zkoss.io.Files;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONArray;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletLoader;

import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.WidgetDefinition;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.URIInfo;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.device.Device;

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
public class WpdExtendlet extends AbstractExtendlet {
	public void init(ExtendletConfig config) {
		init(config, new WpdLoader());
		config.addCompressExtension("wpd");
	}

	public void service(HttpServletRequest request,
	HttpServletResponse response, String path)
	throws ServletException, IOException {
		byte[] data = retrieve(request, response, path);
		if (data == null)
			return;

		response.setContentType("text/javascript;charset=UTF-8");
		if (_webctx.shallCompress(request, "wpd") && data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
	}
	private byte[] retrieve(HttpServletRequest request,
	HttpServletResponse response, String path)
	throws ServletException, IOException {
		byte[] data;
		boolean zkpkg = false;
		setProvider(new Provider(request, response));
		try {
			final Object rawdata = _cache.get(path);
			if (rawdata == null) {
				if (Servlets.isIncluded(request))
					log.error("Failed to load the resource: "+path);
					//It might be eaten, so log the error
				response.sendError(response.SC_NOT_FOUND, path);
				return null;
			}

			final boolean cacheable;
			if (rawdata instanceof ByteContent) {
				final ByteContent bc = (ByteContent)rawdata;
				data = bc.content;
				cacheable = bc.cacheable;
			} else {
				final WpdContent wc = (WpdContent)rawdata;
				data = wc.toByteArray(request);
				cacheable = wc.cacheable;
				zkpkg = wc.zkpkg;
			}
			if (cacheable)
				org.zkoss.zk.fn.JspFns.setCacheControl(getServletContext(),
						request, response, "org.zkoss.web.classWebResource.cache", 8760);
		} finally {
			setProvider(null);
		}

		return zkpkg ? mergeJavaScript(request, response, data): data;
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
	 * {@link LanguageDefinition#getMergeJavaScriptPackages}.
	 * @since 5.0.4.
	 */
	protected byte[] mergeJavaScript(HttpServletRequest request,
	HttpServletResponse response, byte[] data)
	throws ServletException, IOException {
		ByteArrayOutputStream out = null;
		Device device = null;
		final String deviceType = getDeviceType();
		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();) {
			for (Iterator it2 = ((LanguageDefinition)it.next())
			.getMergeJavaScriptPackages().iterator(); it2.hasNext();) {
				final String pkg = (String)it2.next();
				if (out == null) {
					out = new ByteArrayOutputStream(1024*100);
					out.write(data);

					device = Devices.getDevice(deviceType);
				}

				final String path = device.packageToPath(pkg);
				data = retrieve(request, response, path);
				if (data != null)
					out.write(data);
				else
					log.error("Failed to load the resource: "+path);
			}
		}
		return out != null ? out.toByteArray(): data;
	}

	/*package*/ Object parse(InputStream is, String path)
	throws Exception {
		final Element root = new SAXBuilder(true, false, true).build(is).getRootElement();
		final String name = IDOMs.getRequiredAttributeValue(root, "name");
		if (name.length() == 0)
			throw new UiException("The name attribute must be specified, "+root.getLocator()+", "+path);
		final boolean zk = "zk".equals(name),
			aaas = "zk.aaas".equals(name);
		final String lang = root.getAttributeValue("language");
		final LanguageDefinition langdef = //optional
			lang != null ? LanguageDefinition.lookup(lang): null;
		final String dir = path.substring(0, path.lastIndexOf('/') + 1);
		final boolean cacheable = !"false".equals(root.getAttributeValue("cacheable"));

		final WpdContent wc =
			zk || aaas || !cacheable || isWpdContentRequired(root) ?
				new WpdContent(dir, zk, cacheable): null;

		final Provider provider = getProvider();
		final ByteArrayOutputStream out = new ByteArrayOutputStream(1024*16);
		String depends = null;
		if (zk) {
			write(out, "//ZK, Copyright 2009 Potix Corporation. Distributed under LGPL 3.0\n"
				+ "//jQuery, Copyright 2009 John Resig\n"
				+ "function $eval(s){return eval(s);}"
					//jq.globalEval() seems have memory leak problem, so use eval()
				+ "if(!window.zk){");
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
			if (provider != null && provider.getResource(dir + "wv/zk.wpd") != null)
				write(out, ",true");
			write(out, "))try{");
		}

		final Map moldInfos = new HashMap();
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String elnm = el.getName();
			if ("widget".equals(elnm)) {
				final String wgtnm = IDOMs.getRequiredAttributeValue(el, "name");
				final String jspath = wgtnm + ".js"; //eg: /js/zul/wgt/Div.js
				if (!writeResource(out, jspath, dir, false)) {
					log.error("Widget "+wgtnm+": "+jspath+" not found, "+el.getLocator()+", "+path);
					continue;
				}

				final String wgtflnm = name + "." + wgtnm;
				write(out, "zkreg('");
				write(out, wgtflnm);
				write(out, '\'');
				WidgetDefinition wgtdef = langdef != null ? langdef.getWidgetDefinitionIfAny(wgtflnm): null;
				if (wgtdef != null && wgtdef.isBlankPreserved())
					write(out, ",true");
				write(out, ");");
				if (wgtdef == null)
					continue;

				try {
					boolean first = true;
					for (Iterator e = wgtdef.getMoldNames().iterator(); e.hasNext();) {
						final String mold = (String)e.next();
						final String uri = wgtdef.getMoldURI(mold);
						if (uri == null) continue;

						if (first) {
							first = false;
							write(out, "zk._m={};\n");
						}
							
						write(out, "zk._m['");
						write(out, mold);
						write(out, "']=");

						String[] info = (String[])moldInfos.get(uri);
						if (info != null) { //reuse
							write(out, "[zk._p.p.");
							write(out, info[0]);
							write(out, ",'");
							write(out, info[1]);
							write(out, "'];");
						} else {
							moldInfos.put(uri, new String[] {wgtnm, mold});
							if (!writeResource(out, uri, dir, true)) {
								write(out, "zk.$void;zk.error('");
								write(out, uri);
								write(out, " not found')");
								log.error("Failed to load mold "+mold+" for widget "+wgtflnm+": "+uri+" not found");
							}
							write(out, ';');
						}
					}
					if (!first) {
						write(out, "zkmld(");
						write(out, zk ? "zk.": "zk._p.p.");
						write(out, wgtnm);
						write(out, ",zk._m);");
					}
				} catch (Throwable ex) {
					log.error("Failed to load molds for widget "+wgtflnm+".\nCause: "+Exceptions.getMessage(ex));
				}
			} else if ("script".equals(elnm)) {
				String browser = el.getAttributeValue("browser");
				String jspath = el.getAttributeValue("src");
				if (jspath != null && jspath.length() > 0) {
					if (wc != null
					&& (browser != null || jspath.indexOf('*') >= 0)) {
						move(wc, out);
						wc.add(jspath, browser);
					} else {
						if (browser != null && provider != null
						&& !Servlets.isBrowser(provider.request, browser))
							continue;
						if (!writeResource(out, jspath, dir, true))
							log.error(jspath+" not found, "+el.getLocator()+", "+path);
					}
				}

				String s = el.getText(true);
				if (s != null && s.length() > 0) {
					write(out, s);
					write(out, '\n'); //might terminate with //
				}
			} else if ("function".equals(elnm)) {
				final MethodInfo mtd = getMethodInfo(el);
				if (mtd != null)
					if (wc != null) {
						move(wc, out);
						wc.add(mtd);
					} else {
						write(out, mtd);
					}
			} else {
				log.warning("Unknown element "+elnm+", "+el.getLocator()+", "+path);
			}
		}
		if (zk) {
			final WebApp wapp = getWebApp();
			if (wapp != null)
				writeAppInfo(out, wapp);
			 write(out, '}'); //end of if(window.zk)

			writeHost(wc, out, wapp);
		} else if (aaas) {
			writeHost(wc, out, getWebApp());
		} else {
			write(out, "\n}finally{zk.setLoaded(zk._p.n);}");
			if (depends != null) {
				write(out, "});zk.setLoaded('");
				write(out, name);
				write(out, "',1);");
			} else
				write(out, "})();");
		}

		if (wc != null) {
			move(wc, out);
			return wc;
		}
		return new ByteContent(out.toByteArray(), cacheable);
	}
	private static boolean isWpdContentRequired(Element root) {
		for (Iterator it = root.getElements("script").iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
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
	private boolean writeResource(OutputStream out, String path,
	String dir, boolean locate)
	throws IOException, ServletException {
		if (path.startsWith("~./")) path = path.substring(2);
		else if (path.charAt(0) != '/')
			path = Files.normalize(dir, path);

		final InputStream is =
			getProvider().getResourceAsStream(path, locate);
		if (is == null) {
			write(out, "zk.log('");
			write(out, path);
			write(out, " not found');");
			return false;
		}

		Files.copy(out, is);
		Files.close(is);
		write(out, '\n'); //might terminate with //
		return true;
	}
	private void write(OutputStream out, String s) throws IOException {
		if (s != null) {
			final byte[] bs = s.getBytes("UTF-8");
			out.write(bs, 0, bs.length);
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
	private void write(OutputStream out, char cc) throws IOException {
		assert cc < 128;
		final byte[] bs = new byte[] {(byte)cc};
		out.write(bs, 0, 1);
	}
	private void write(OutputStream out, MethodInfo mtd) throws IOException {
		try {
			write(out, invoke(mtd));
		} catch (IOException ex) {
			throw ex;
		}
	}
	private void move(WpdContent wc, ByteArrayOutputStream out) {
		final byte[] bs = out.toByteArray();
		if (bs.length > 0) {
			wc.add(bs);
			out.reset();
		}
	}

	private void writeAppInfo(OutputStream out, WebApp wapp)
	throws IOException, ServletException {
		final StringBuffer sb = new StringBuffer(256);
		sb.append("\nzkver('").append(wapp.getVersion())
			.append("','").append(wapp.getBuild());
		final Provider provider = getProvider();
		if (provider != null) {
			final ServletContext ctx = getServletContext();
			String s = Encodes.encodeURL(ctx, provider.request, provider.response, "/");
			int j = s.lastIndexOf('/'); //might have jsessionid=...
			if (j >= 0) s = s.substring(0, j) + s.substring(j + 1);

			sb.append("','")
				.append(s)
				.append("','")
				.append(Encodes.encodeURL(ctx, provider.request, provider.response,
					wapp.getUpdateURI(false)));
		} else
			sb.append("','','");

		sb.append("',{");
		for (Iterator it = LanguageDefinition.getByDeviceType("ajax").iterator();
		it.hasNext();) {
			final LanguageDefinition langdef = (LanguageDefinition)it.next();
			for (Iterator e = langdef.getJavaScriptModules().entrySet().iterator();
			e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				sb.append('\'').append(me.getKey())
				  .append("':'").append(me.getValue()).append("',");
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
		if (v != 900) sb.append("pd:").append(v).append(',');
		v = config.getTooltipDelay();
		if (v != 800) sb.append("td:").append(v).append(',');
		v = config.getResendDelay();
		if (v > 0) sb.append("rd:").append(v).append(',');
		v = config.getClickFilterDelay();
		if (v >= 0) sb.append("cd:").append(v).append(',');
		if (config.isTimerKeepAlive())
			sb.append("ta:1,");
		if (config.isDebugJS()) sb.append("dj:1,");
		if (config.isKeepDesktopAcrossVisits())
			sb.append("kd:1,");
		if (config.getPerformanceMeter() != null)
			sb.append("pf:1,");

		Object[][] infs = config.getClientErrorReloads("ajax", null);
		if (infs != null) {
			sb.append("eu:{");
			outErrReloads(config, sb, infs);
			sb.append("},");
		}
		infs = config.getClientErrorReloads("ajax", "server-push");
		if (infs != null) {
			sb.append("eup:{");
			outErrReloads(config, sb, infs);
			sb.append("},");
		}

		removeLast(sb, ',');

		sb.append("});");
		write(out, sb.toString());
	}
	private void outErrReloads(Configuration config, StringBuffer sb,
	Object[][] infs) {
		for (int j = 0; j < infs.length; ++j) {
			if (j > 0) sb.append(',');
			sb.append('\'').append(infs[j][0]).append("':'");

			String uri = ((URIInfo)infs[j][1]).uri;
			if (uri.length() > 0)
				try {
					final Provider provider = getProvider();
					uri = Encodes.encodeURL(getServletContext(),
						provider.request, provider.response, uri);
				} catch (javax.servlet.ServletException ex) {
					throw new UiException("Unable to encode "+uri, ex);
				}
			sb.append(Strings.escape(uri, "'\\")).append('\'');
		}
	}
	private static void removeLast(StringBuffer sb, char cc) {
		if (sb.charAt(sb.length() - 1) == cc)
			sb.setLength(sb.length() - 1); //remove last comma
	}

	private static String outMain(String main, Map params) {
		final StringBuffer sb = new StringBuffer("\nzkamn('");
		final int j = main.lastIndexOf('.');
		if (j >= 0)
			sb.append(main.substring(0, j));

		sb.append("',function(){\n").append(main).append(".main(");

		final Map ms = new LinkedHashMap();
		for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String nm = (String)me.getKey();
			if (!"main".equals(nm)) {
				final String[] vals = (String[])me.getValue();
				ms.put(nm, vals.length == 0 ? null:
					vals.length == 1 ? (Object)vals[0]: (Object)vals);
			}
		}

		sb.append(JSONObject.toJSONString(ms)).append(")\n})");
		return sb.toString();
	}
	private static String outHost(HttpServletRequest request,
	WebApp wapp, String clientPackages) {
		final StringBuffer sb = new StringBuffer()
			.append("zk.setHost('").append(request.getScheme())
			.append("://").append(request.getServerName());
		if (request.getServerPort() != 80)
			sb.append(':').append(request.getServerPort());

		String uri = request.getContextPath();
		if (uri != null && uri.length() > 0) {
			if (uri.charAt(0) != '/') sb.append('/');
			sb.append(uri);
			removeLast(sb, '/');
		}
		return sb.append("','").append(wapp.getUpdateURI(false)).append("',")
			.append(clientPackages).append(");").toString();
	}

	private class WpdLoader extends ExtendletLoader {
		private WpdLoader() {
		}

		//-- super --//
		protected Object parse(InputStream is, String path, String orgpath)
		throws Exception {
			return WpdExtendlet.this.parse(is, path);
		}
		protected ExtendletContext getExtendletContext() {
			return _webctx;
		}
		protected String getRealPath(String path) {
			final int j = path.lastIndexOf(".wpd");
			return path.substring(0, j).replace('.', '/') + "/zk" + path.substring(j);
		}
	}
	/*package*/ static class ByteContent {
		/*package*/ final byte[] content;
		/*package*/ final boolean cacheable;

		private ByteContent(byte[] cnt, boolean cacheable) {
			this.content = cnt;
			this.cacheable = cacheable;
		}
	}
			
	/*package*/ class WpdContent {
		private final String _dir;
		private final List _cnt = new LinkedList();
		private final boolean zkpkg;
		private final boolean cacheable;

		/**
		 * @param zkpkg whether it is the zk package.
		 */
		private WpdContent(String dir, boolean zkpkg, boolean cacheable) {
			_dir = dir;
			this.zkpkg = zkpkg;
			this.cacheable = cacheable;
		}
		private void add(byte[] bs) {
			_cnt.add(bs);
		}
		private void add(MethodInfo mtd) {
			_cnt.add(mtd);
		}
		private void add(String jspath, String browser) {
			_cnt.add(new String[] {jspath, browser});
		}
		private void addHost(WebApp wapp, String clientPackages) {
			_cnt.add(new Object[] {wapp, clientPackages});
		}
		/*package*/ byte[] toByteArray(HttpServletRequest request) throws ServletException, IOException {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final String main = request != null ? request.getParameter("main"): null;
			for (Iterator it = _cnt.iterator(); it.hasNext();) {
				final Object o = it.next();
				if (o instanceof byte[]) {
					out.write((byte[])o);
				} else if (o instanceof MethodInfo) {
					write(out, (MethodInfo)o);
				} else if (o instanceof String[]) {
					final String[] inf = (String[])o;
					if (inf[1] != null) {
						final Provider provider = getProvider();
						if (provider != null && !Servlets.isBrowser(provider.request, inf[1]))
							continue;
					}
					if (!writeResource(out, inf[0], _dir, true))
						log.error(inf[0] + " not found");
				} else if (o instanceof Object[]) { //host
					if (main != null) {
						final Object[] inf = (Object[])o;
						write(out, outHost(request, (WebApp)inf[0], (String)inf[1]));
					}
				}
			}

			if (main != null && main.length() > 0)
				write(out, outMain(main, request.getParameterMap()));
			return out.toByteArray();
		}
	}
}
