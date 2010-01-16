/* WcsExtendlet.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 29 16:25:12     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
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
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletLoader;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.util.ThemeProvider;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.fn.JspFns;

/**
 * The extendlet to handle WCS (widget CSS).
 * Each language (such as zul) must have exactly one of WCS file to
 * generate all CSS for all widgets of a langauge.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class WcsExtendlet extends AbstractExtendlet {
	public void init(ExtendletConfig config) {
		init(config, new WcsLoader());
		config.addCompressExtension("wcs");
	}
	public void service(HttpServletRequest request,
	HttpServletResponse response, String path)
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
		final Execution exec = new ExecutionImpl(
				getServletContext(), request, response, null, null),
			olde = Executions.getCurrent();
		ExecutionsCtrl.setCurrent(exec);
 		((ExecutionCtrl)exec).onActivate();
		try {
			int hours = 8760;
			final ThemeProvider tp = getWebApp().getConfiguration().getThemeProvider();
			if (tp != null) {
				try {
					final String p = "~." + path;
					if (tp.beforeWCS(exec, p) == null) {
						response.setContentType("text/css;charset=UTF-8");
						return; //skip the whole file
					}
					hours = tp.getWCSCacheControl(exec, p);
				} catch (AbstractMethodError ex) { //ignore it (backward compatible)
				}
			}
			if (hours > 0)
				JspFns.setCacheControl(response,
					"org.zkoss.web.classWebResource.cache", hours);

			for (int j = 0; j < wi.items.length; ++j) {
				final Object o = wi.items[j];
				if (o instanceof String) {
					String uri = (String)o;
					if (tp != null) {
						try {
							uri = tp.beforeWidgetCSS(exec, uri);
							if (uri == null)
								continue; //skip it
						} catch (AbstractMethodError ex) { //ignore it (backward compatible)
						}
					}

					try {
						_webctx.include(request, HttpBufferedResponse.getInstance(response, sw), uri, null);
					} catch (Throwable ex) {
						log.realCauseBriefly("Unable to load "+wi.items[j], ex);
					}
				} else { //static method
					sw.write(invoke((MethodInfo)o));
				}
				sw.write('\n');
			}

			for (Iterator it = wi.langdef.getCSSURIs().iterator(); it.hasNext();) {
				String uri = (String)it.next();
				if (tp != null) {
					try {
						uri = tp.beforeWidgetCSS(exec, uri);
						if (uri == null)
							continue; //skip it
					} catch (AbstractMethodError ex) { //ignore it (backward compatible)
					}
				}
				try {
					_webctx.include(request, HttpBufferedResponse.getInstance(response, sw), uri, null);
				} catch (Throwable ex) {
					log.realCauseBriefly("Unable to load "+uri, ex);
				}
			}
		} finally {
			((ExecutionCtrl)exec).onDeactivate();
			ExecutionsCtrl.setCurrent(olde);
		}

		response.setContentType("text/css;charset=UTF-8");
		byte[] data = sw.getBuffer().toString().getBytes("UTF-8");
		if (_webctx.shallCompress(request, "wcs") && data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
	}
	/*package*/ Object parse(InputStream is, String path)
	throws Exception {
		final Element root = new SAXBuilder(true, false, true).build(is).getRootElement();
		final String lang = IDOMs.getRequiredAttributeValue(root, "language");
		if (lang.length() == 0)
			throw new UiException("The language attribute must be specified, "+root.getLocator()+", "+path);

		final List items = new LinkedList();
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String elnm = el.getName();
			if ("stylesheet".equals(elnm)) {
				final String href = IDOMs.getRequiredAttributeValue(el, "href");
				if (href.length() != 0)
					items.add(href);
				else
					log.warning("Ingored stylesheet: href required, " + el.getLocator()+", "+path);
			} else if ("function".equals(elnm)) {
				final MethodInfo mtd = getMethodInfo(el);
				if (mtd != null) items.add(mtd);
			} else
				log.warning("Ignored unknown element, " + el.getLocator()+", "+path);
		}
		return new WcsInfo(lang, items);
	}

	private class WcsLoader extends ExtendletLoader {
		private WcsLoader() {
		}

		//-- super --//
		protected Object parse(InputStream is, String path, String orgpath)
		throws Exception {
			return WcsExtendlet.this.parse(is, path);
		}
		protected ExtendletContext getExtendletContext() {
			return _webctx;
		}
		protected String getRealPath(String path) {
			if (path.length() > 1
			&& path.substring(1).startsWith(Attributes.INJECT_URI_PREFIX)) {
				final int j = path.indexOf('/', Attributes.INJECT_URI_PREFIX.length() + 1);
				if (j > 0)
					return path.substring(j);
			}
			return path;
		}
	}
	/*package*/ static class WcsInfo {
		/*package*/ final LanguageDefinition langdef;
		/** A list of URI or static method. */
		/*package*/ final Object[] items;
		private WcsInfo(String lang, List items) {
			this.langdef = LanguageDefinition.lookup(lang);
			this.items = (Object[])items.toArray(new Object[items.size()]);
		}
	}
}
