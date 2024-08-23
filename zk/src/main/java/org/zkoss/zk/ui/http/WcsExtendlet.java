/* WcsExtendlet.java

	Purpose:

	Description:

	History:
		Mon Jun 29 16:25:12     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.http;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.encoder.Encode;

import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.lang.Library;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.HttpBufferedResponse;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.ExtendletConfig;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletLoader;
import org.zkoss.zk.fn.JspFns;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * The extendlet to handle WCS (widget CSS).
 * Each language (such as zul) must have exactly one of WCS file to
 * generate all CSS for all widgets of a language.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class WcsExtendlet extends AbstractExtendlet<WcsInfo> {
	public void init(ExtendletConfig config) {
		init(config, new WcsLoader());
		config.addCompressExtension("wcs");
	}

	public void service(HttpServletRequest request, HttpServletResponse response, String path)
			throws ServletException, java.io.IOException {

		String resourceCache = Library.getProperty("org.zkoss.zk.WCS.cache");
		if (resourceCache != null && "false".equalsIgnoreCase(resourceCache))
			_cache.clear();

		final WcsInfo wi = _cache.get(path);
		if (wi == null) {
			if (Servlets.isIncluded(request)) {
				log.error("Failed to load the resource: " + path);
				//It might be eaten, so log the error
				throw new java.io.FileNotFoundException("Failed to load the resource: " + path);
				//have the includer to handle it
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					Encode.forJavaScript(Encode.forHtml(path)));
			return;
		}

		final StringWriter sw = new StringWriter();
		final Execution exec = new ExecutionImpl(getServletContext(), request, response, null, null),
				olde = Executions.getCurrent();
		ExecutionsCtrl.setCurrent(exec);
		((ExecutionCtrl) exec).onActivate();
		try {
			int hours = 8760;
			final ThemeProvider tp = getWebApp().getConfiguration().getThemeProvider();
			if (tp != null) {
				final String p = "~." + path;
				if (tp.beforeWCS(exec, p) == null) {
					response.setContentType("text/css;charset=UTF-8");
					return; //skip the whole file
				}
				hours = tp.getWCSCacheControl(exec, p);
			}
			if (hours > 0) {
				boolean isNotModified = JspFns.setCacheControl(getServletContext(), request, response, "org.zkoss.web.classWebResource.cache",
						hours);
				if (isNotModified)
					return;
			}

			for (int j = 0; j < wi.items.length; ++j) {
				final Object o = wi.items[j];
				if (o instanceof String) {
					String uri = (String) o;
					if (tp != null) {
						uri = tp.beforeWidgetCSS(exec, uri);
						if (uri == null)
							continue; //skip it
					}

					try {
						_webctx.include(request, HttpBufferedResponse.getInstance(response, sw), uri, null);
					} catch (Throwable ex) {
						log.error("Unable to load " + wi.items[j], ex);
					}
				} else { //static method
					sw.write(invoke(new RequestContext(this, request, response), (MethodInfo) o));
				}
				sw.write('\n');
			}

			for (Iterator it = wi.langdef.getCSSURIs().iterator(); it.hasNext();) {
				String uri = (String) it.next();
				if (tp != null) {
					uri = tp.beforeWidgetCSS(exec, uri);
					if (uri == null)
						continue; //skip it
				}
				try {
					_webctx.include(request, HttpBufferedResponse.getInstance(response, sw), uri, null);
				} catch (Throwable ex) {
					log.error("Unable to load " + uri, ex);
				}
			}

			// @since 9.0.1 support footer.less
			String footerUri = "~./zul/css/footer.css.dsp";
			try {
				if (tp != null)
					footerUri = tp.beforeWidgetCSS(exec, footerUri);
				if (footerUri != null && getWebApp().getResource(footerUri) != null)
					_webctx.include(request, HttpBufferedResponse.getInstance(response, sw), footerUri, null);
			} catch (Throwable ex) {
				log.error("Unable to load " + footerUri, ex);
			}
		} finally {
			((ExecutionCtrl) exec).onDeactivate();
			ExecutionsCtrl.setCurrent(olde);
		}

		response.setContentType("text/css;charset=UTF-8");
		byte[] data = sw.getBuffer().toString().getBytes("UTF-8");
		if (_webctx.shallCompress(request, "wcs") && data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null)
				data = bs; //yes, browser support compress
		}
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
	}

	private WcsInfo parse(InputStream is, String path) throws Exception {
		final Element root = new SAXBuilder(true, false, true).build(is).getRootElement();
		final String lang = IDOMs.getRequiredAttributeValue(root, "language");
		if (lang.length() == 0)
			throw new UiException("The language attribute must be specified, " + root.getLocator() + ", " + path);

		final List<Object> items = new LinkedList<Object>();
		for (Element el : root.getElements()) {
			final String elnm = el.getName();
			if ("stylesheet".equals(elnm)) {
				final String href = IDOMs.getRequiredAttributeValue(el, "href");
				if (href.length() != 0)
					items.add(href);
				else
					log.warn("Ingored stylesheet: href required, " + el.getLocator() + ", " + path);
			} else if ("function".equals(elnm)) {
				final MethodInfo mtd = getMethodInfo(el);
				if (mtd != null)
					items.add(mtd);
			} else
				log.warn("Ignored unknown element, " + el.getLocator() + ", " + path);
		}
		return new WcsInfo(lang, items);
	}

	private class WcsLoader extends ExtendletLoader<WcsInfo> {
		private WcsLoader() {
		}

		//-- super --//
		protected WcsInfo parse(InputStream is, String path, String orgpath) throws Exception {
			return WcsExtendlet.this.parse(is, path);
		}

		protected ExtendletContext getExtendletContext() {
			return _webctx;
		}

		protected String getRealPath(String path) {
			if (path.length() > 1 && path.substring(1).startsWith(Attributes.INJECT_URI_PREFIX)) {
				final int j = path.indexOf('/', Attributes.INJECT_URI_PREFIX.length() + 1);
				if (j > 0)
					return path.substring(j);
			}
			return path;
		}
	}
}
