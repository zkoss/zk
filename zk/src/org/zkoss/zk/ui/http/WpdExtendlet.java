/* WpdExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct  6 10:47:11     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.util.Iterator;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Exceptions;
import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.Loader;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.Extendlet;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletLoader;

import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.WidgetDefinition;

/**
 * The extendlet to handle WPD (Widget Package Descriptor).
 *
 * <p>Note: it assumes all JavaScript files are encoded in UTF-8.
 *
 * @author tomyeh
 * @since 5.0.0
 */
/*package*/ class WpdExtendlet implements Extendlet {
	private static final Log log = Log.lookup(WpdExtendlet.class);

	private ExtendletContext _webctx;
	/** DSP Interpretation cache. */
	private ResourceCache _cache;
	private Boolean _debugJS;
	private ThreadLocal _req = new ThreadLocal();

	public void init(ExtendletConfig config) {
		_webctx = config.getExtendletContext();
		final WpdLoader loader = new WpdLoader();
		_cache = new ResourceCache(loader, 16);
		_cache.setMaxSize(1024);
		_cache.setLifetime(60*60*1000); //1hr
		final int checkPeriod = loader.getCheckPeriod();
		_cache.setCheckPeriod(checkPeriod >= 0 ? checkPeriod: 60*60*1000); //1hr
	}
	public boolean getFeature(int feature) {
		return feature == ALLOW_DIRECT_INCLUDE;
	}
	public void service(HttpServletRequest request,
	HttpServletResponse response, String path, String extra)
	throws ServletException, IOException {
		byte[] data;
		_req.set(request);
		try {
			data = (byte[])_cache.get(path);
		} finally {
			_req.set(null);
		}
		if (data == null) {
			if (Servlets.isIncluded(request)) log.error("Failed to load the resource: "+path);
				//It might be eaten, so log the error
			response.sendError(response.SC_NOT_FOUND, path);
			return;
		}

		response.setContentType("text/javascript;charset=UTF-8");
		if (data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}
		response.setContentLength(data.length);
		final OutputStream os = response.getOutputStream();
		os.write(data);
		response.flushBuffer();
	}
	private boolean isDebugJS() {
		if (_debugJS == null) {
			final WebManager wm = WebManager.getWebManager(_webctx.getServletContext());
			if (wm == null) return true; //just in case
			_debugJS = Boolean.valueOf(wm.getWebApp().getConfiguration().isDebugJS());
		}
		return _debugJS.booleanValue();

	}
	private Object parse(InputStream is, String path) throws Exception {
		final Element root = new SAXBuilder(true, false, true).build(is).getRootElement();
		final String name = IDOMs.getRequiredAttributeValue(root, "name");
		final String lang = IDOMs.getRequiredAttributeValue(root, "language");
		final LanguageDefinition langdef = LanguageDefinition.lookup(lang);
		final boolean cacheable = !"false".equals(root.getAttributeValue("cacheable"));

		final ByteArrayOutputStream out = new ByteArrayOutputStream(1024*8);
		write(out, "_z='");
		write(out, name);
		write(out, "';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);\n");

		final String pathpref = path.substring(0, path.lastIndexOf('/') + 1);
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String elnm = el.getName();
			if ("widget".equals(elnm)) {
				final String wgtnm = IDOMs.getRequiredAttributeValue(el, "name");
				final String jspath = wgtnm + ".js"; //eg: /js/zul/wgt/Div.js
				if (writeResource(out, jspath, pathpref, false)) {
					final String wgtflnm = name + "." + wgtnm;
					write(out, "(_zkwg=_zkpk.");
					write(out, wgtnm);
					write(out, ").prototype.className='");
					write(out, wgtflnm);
					write(out, "';");
					if (langdef.hasWidgetDefinition(wgtflnm))
						writeMolds(out, langdef, wgtflnm, pathpref);
						//Note: widget defiition not available if it is a base type (such as zul.Widget)
				} else
					log.error("Failed to load widget "+wgtnm+": "+jspath+" not found, "+el.getLocator());
			} else if ("script".equals(elnm)) {
				String jspath = el.getAttributeValue("src");
				if (jspath != null && jspath.length() > 0) {
					if (!writeResource(out, jspath, pathpref, true))
						log.error("Failed to load script "+jspath+", "+el.getLocator());
				}

				String s = el.getText(true);
				if (s != null && s.length() > 0) {
					write(out, s);
					write(out, "\n;"); //might terminate with //, or not with ;
				}
			} else {
				log.warning("Unknown element "+elnm+", "+el.getLocator());
			}
		}
		write(out, "\n}finally{zPkg.end(_z);}}");

		final byte[] bs = out.toByteArray();
		if (cacheable) return bs;
		return new Loader.Resource(bs, false);
	}
	private void writeMolds(OutputStream out, LanguageDefinition langdef,
	String wgtflnm, String pathpref) {
		try {
			WidgetDefinition wgtdef = langdef.getWidgetDefinition(wgtflnm);
			write(out, "_zkmd={};");
			for (Iterator it = wgtdef.getMoldNames().iterator(); it.hasNext();) {
				final String mold = (String)it.next();
				final String uri = wgtdef.getMoldURI(mold);
				if (uri != null) {
					write(out, "_zkmd['");
					write(out, mold);
					write(out, "']=");
					if (!writeResource(out, uri, pathpref, true)) {
						write(out, "zk.$void;zk.error('");
						write(out, uri);
						write(out, " not found')");
						log.error("Failed to load mold "+mold+" for widget "+wgtflnm+". Cause: "+uri+" not found");
					}
					write(out, ";\n");
				}
			}
			write(out, "zkmld(_zkwg,_zkmd);");
		} catch (Throwable ex) {
			log.error("Failed to load molds for widget "+wgtflnm+".\nCause: "+Exceptions.getMessage(ex));
		}
	}
	private boolean writeResource(OutputStream out, String path,
	String pathpref, boolean locate)
	throws IOException, ServletException {
		if (path.startsWith("~./")) path = path.substring(2);
		else if (path.charAt(0) != '/')
			path = Files.normalize(pathpref, path);

		final InputStream is = _webctx.getResourceAsStream(
			locate ?
				Servlets.locate(_webctx.getServletContext(),
					(HttpServletRequest)_req.get(), path, _webctx.getLocator()):
				path);

		if (is == null) {
			final boolean debugJS = isDebugJS();
			if (debugJS) write(out, "zk.debug(");
			write(out, "'Failed to load ");
			write(out, path);
			write(out, '\'');
			if (debugJS) write(out, ')');
			write(out, ';');
			return false;
		}

		Files.copy(out, is);
		write(out, "\n;"); //might terminate with //, or not with ;
		return true;
	}
	private void write(OutputStream out, String s) throws IOException {
		final byte[] bs = s.getBytes("UTF-8");
		out.write(bs, 0, bs.length);
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

	private class WpdLoader extends ExtendletLoader {
		private WpdLoader() {
		}

		//-- super --//
		protected Object parse(InputStream is, String path) throws Exception {
			return WpdExtendlet.this.parse(is, path);
		}
		protected ExtendletContext getExtendletContext() {
			return _webctx;
		}
	}
}
