/* WpdExtendlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct  6 10:47:11     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Iterator;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.Extendlet;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletLoader;

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

	public void init(ExtendletConfig config) {
		_webctx = config.getExtendletContext();
		final WpdLoader loader = new WpdLoader();
		_cache = new ResourceCache(loader, 16);
		_cache.setMaxSize(1024);
		_cache.setLifetime(60*60*1000); //1hr
		final int checkPeriod = loader.getCheckPeriod();
		_cache.setCheckPeriod(checkPeriod >= 0 ? checkPeriod: 60*60*1000); //1hr
	}
	public void service(HttpServletRequest request,
	HttpServletResponse response, String path, String extra)
	throws ServletException, IOException {
		byte[] data = (byte[])_cache.get(path);
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
	private byte[] parse(InputStream is, String path) throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(1024*8);
		final Element root = new SAXBuilder(true, false, true).build(is).getRootElement();
		final String name = IDOMs.getRequiredAttributeValue(root, "name");
		final String lang = IDOMs.getRequiredAttributeValue(root, "language");

		final StringBuffer sb = new StringBuffer(256);
		sb.append("_z='").append(name)
			.append("';if(!zk.$import(_z)){zk.$package(_z);\n");
		write(out, sb);

		sb.append(";zkPkg.end('")
			.append(lang).append("',_z,[");
		final String pathpref = path.substring(0, path.lastIndexOf('/') + 1);
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String elnm = el.getName();
			if ("widget".equals(elnm)) {
				final String wgtnm = IDOMs.getRequiredAttributeValue(el, "name");
				final String jspath = pathpref + wgtnm + ".js"; //eg: /js/zul/wgt/Div.js
				if (!writeResource(out, jspath))
					log.error("Failed to load widget "+wgtnm+": "+jspath+" not found, "+el.getLocator());
				sb.append('\'').append(wgtnm).append("',");
			} else if ("script".equals(elnm)) {
				String jspath = el.getAttributeValue("src");
				if (jspath != null && jspath.length() > 0) {
					if (jspath.startsWith("~./")) jspath = jspath.substring(2);
					else if (jspath.charAt(0) != '/') {
						jspath = new File(pathpref, jspath).getCanonicalPath();
					}
					if (!writeResource(out, jspath))
						log.error("Failed to load script "+jspath+", "+el.getLocator());
				}

				String s = el.getText(true);
				if (s != null) {
					write(out, s);
					writeln(out);
				}
			} else {
				log.warning("Unknown element "+elnm+", "+el.getLocator());
			}
		}
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		sb.append("]);}");
		write(out, sb);
		return out.toByteArray();
	}
	private boolean writeResource(OutputStream out, String path)
	throws IOException {
		final InputStream is = _webctx.getResourceAsStream(path);
		if (is == null)
			return false;

		Files.copy(out, is);
		writeln(out);
		return true;
	}
	private void write(OutputStream out, String s) throws IOException {
		final byte[] bs = s.getBytes("UTF-8");
		out.write(bs, 0, bs.length);
	}
	private static void write(OutputStream out, StringBuffer sb)
	throws IOException {
		final byte[] bs = sb.toString().getBytes("UTF-8");
		out.write(bs, 0, bs.length);
		sb.setLength(0);
	}
	private void writeln(OutputStream out) throws IOException {
		final byte[] lf = {'\n'};
		out.write(lf, 0, 1);
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
