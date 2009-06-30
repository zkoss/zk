/* WcsExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 29 16:25:12     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.io.InputStream;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.HttpBufferedResponse;
import org.zkoss.web.util.resource.Extendlet;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletLoader;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * The extendlet to handle WCS (widget CSS).
 * Each language (such as zul) must have exactly one of WCS file to
 * generate all CSS for all widgets of a langauge.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class WcsExtendlet implements Extendlet {
	private static final Log log = Log.lookup(WcsExtendlet.class);

	private ExtendletContext _webctx;
	private ResourceCache _cache;

	public void init(ExtendletConfig config) {
		_webctx = config.getExtendletContext();
		final WcsLoader loader = new WcsLoader();
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
	throws ServletException, java.io.IOException {
		final WcsInfo wi = (WcsInfo)_cache.get(path);
		if (wi == null) {
			if (Servlets.isIncluded(request))
				log.error("Failed to load the resource: "+path);
				//It might be eaten, so log the error
			response.sendError(response.SC_NOT_FOUND, path);
			return;
		}

		final StringWriter sw = new StringWriter();
		for (int j = 0; j < wi.uris.length; ++j) {
			_webctx.include(request, HttpBufferedResponse.getInstance(response, sw), wi.uris[j], null);
		}
		for (Iterator it = wi.langdef.getCSSURIs().iterator(); it.hasNext();) {
			final String uri = (String)it.next();
			_webctx.include(request, HttpBufferedResponse.getInstance(response, sw), uri, null);
		}

		response.setContentType("text/css;charset=UTF-8");
		byte[] data = sw.getBuffer().toString().getBytes("UTF-8");
		if (data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
	}

	private Object parse(InputStream is, String path) throws Exception {
		final Element root = new SAXBuilder(true, false, true).build(is).getRootElement();
		final String lang = IDOMs.getRequiredAttributeValue(root, "language");
		if (lang.length() == 0)
			throw new UiException("The language attribute must be specified, "+root.getLocator());

		final List uris = new LinkedList();
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			if ("stylesheet".equals(el.getName())) {
				final String href = IDOMs.getRequiredAttributeValue(el, "href");
				if (href.length() != 0)
					uris.add(href);
				else
					log.warning("Ingored stylesheet: href required, " + el.getLocator());
			} else
				log.warning("Ignored unknown element, " + el.getLocator());
		}
		return new WcsInfo(lang, uris);
	}

	private class WcsLoader extends ExtendletLoader {
		private WcsLoader() {
		}

		//-- super --//
		protected Object parse(InputStream is, String path) throws Exception {
			return WcsExtendlet.this.parse(is, path);
		}
		protected ExtendletContext getExtendletContext() {
			return _webctx;
		}
	}
	private static class WcsInfo {
		private final LanguageDefinition langdef;
		private final String[] uris;
		private WcsInfo(String lang, List uris) {
			this.langdef = LanguageDefinition.lookup(lang);
			this.uris = (String[])uris.toArray(new String[uris.size()]);
		}
	}
}
