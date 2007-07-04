/* ZUMLResourcelet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  4 17:35:14     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.io.Writer;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.Loader;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.Resourcelet;
import org.zkoss.web.util.resource.ExtendedWebContext;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.impl.RequestInfoImpl;

/**
 * The ZUML resource processor used to parse the ZUML files
 * loaded from the classpath.
 *
 * @author tomyeh
 * @since 2.4.1
 */
/*package*/ class ZUMLResourcelet implements Resourcelet {
	private static final Log log = Log.lookup(ZUMLResourcelet.class);
	private ExtendedWebContext _webctx;
	/** PageDefinition cache. */
	private ResourceCache _cache;
	private boolean _compress = true;

	private ServletContext getServletContext() {
		return _webctx.getServletContext();
	}
	private WebManager getWebManager() {
		return WebManager.getWebManager(getServletContext());
	}
	private WebApp getWebApp() {
		return getWebManager().getWebApp();
	}

	//Resourcelet//
	public void init(ExtendedWebContext webctx) {
		_webctx = webctx;
		_cache = new ResourceCache(new ZUMLLoader(), 17);
		_cache.setMaxSize(200).setLifetime(60*60*1000); //1hr
		_cache.setCheckPeriod(60*60*1000); //1hr
	}
	public void service(HttpServletRequest request,
	HttpServletResponse response, String path, String extra)
	throws ServletException, IOException {
		if (extra != null)
			log.warning("extra is not supported by ZUMLResourcelet: "+extra);

		final Session sess = WebManager.getSession(getServletContext(), request);
		final PageDefinition pagedef = (PageDefinition)_cache.get(path);
		if (pagedef == null) {
			//FUTURE: support the error page (from Configuration)
			Utils.handleError(getServletContext(), request, response, path, null);
			return;
		}

		//mimic DHtmlLayoutServlet to process PageDefinition
		final Object old = I18Ns.setup(sess, request, response,
			sess.getWebApp().getConfiguration().getResponseCharset());
		try {
			process(sess, request, response, pagedef, path);
		} catch (Throwable ex) {
			Utils.handleError(getServletContext(), request, response, path, ex);
		} finally {
			I18Ns.cleanup(request, old);
		}
	}
	//-- private --//
	/**
	 * Process the request.
	 * @return false if the page is not found.
	 */
	private void process(Session sess,
	HttpServletRequest request, HttpServletResponse response,
	PageDefinition pagedef, String path) throws ServletException, IOException {
		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;

		final Desktop desktop = getWebManager().getDesktop(sess, request, path, true);
		final RequestInfo ri = new RequestInfoImpl(
			wapp, sess, desktop, request, PageDefinitions.getLocator(wapp, path));

		boolean compress = _compress && !Servlets.isIncluded(request);
		final Page page = wappc.getUiFactory().newPage(ri, pagedef, path);
		final Execution exec = new ExecutionImpl(
			getServletContext(), request, response, desktop, page);
		final Writer out = compress ? (Writer)new StringWriter(): response.getWriter();
		wappc.getUiEngine().execNewPage(exec, pagedef, page, out);

		if (compress) {
			byte[] bs = ((StringWriter)out).toString().getBytes("UTF-8");
			byte[] data;
			if (bs.length > 200) {
				data = Https.gzip(request, response, null, bs);
				if (data == null) //browser doesn't support compress
					data = bs;
				else
					bs = null; //free it
			} else {
				data = bs;
			}

			response.setContentLength(data.length);
			response.getOutputStream().write(data);
			response.flushBuffer();
		}
	}

	/** Helper class. */
	private class ZUMLLoader implements Loader {
		private ZUMLLoader() {
		}

		//-- super --//
		public boolean shallCheck(Object src, long expiredMillis) {
			return expiredMillis > 0;
		}
		/** Returns the last modified time.
		 */
		public long getLastModified(Object src) {
			return 1; //any value (because it is packed in jar)
		}
		public Object load(Object src) throws Exception {
//			if (D.ON && log.debugable()) log.debug("Parse "+src);
			final String path = (String)src;
			final InputStream is = _webctx.getResourceAsStream(path);
			if (is == null)
				return null;

			try {
				return parse0(is, Servlets.getExtension(path));
			} catch (Exception ex) {
				if (log.debugable())
					log.realCauseBriefly("Failed to parse "+path, ex);
				else
					log.error("Failed to parse "+path
					+"\nCause: "+ex.getClass().getName()+" "+Exceptions.getMessage(ex)
					+"\n"+Exceptions.getBriefStackTrace(ex));
				return null; //as non-existent
			}
		}
		private Object parse0(InputStream is, String ext) throws Exception {
			return PageDefinitions.getPageDefinitionDirectly(
				getWebApp(), _webctx.getLocator(),
				new java.io.InputStreamReader(is, "UTF-8"), ext);
		}
	}
}
