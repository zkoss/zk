/* ZumlExtendlet.java

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
import java.io.OutputStream;
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
import org.zkoss.web.util.resource.Extendlet;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.web.util.resource.ExtendletConfig;

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
/*package*/ class ZumlExtendlet implements Extendlet {
	private static final Log log = Log.lookup(ZumlExtendlet.class);
	private ExtendletContext _webctx;
	/** PageDefinition cache. */
	private ResourceCache _cache;

	private ServletContext getServletContext() {
		return _webctx.getServletContext();
	}
	private WebManager getWebManager() {
		return WebManager.getWebManager(getServletContext());
	}
	private WebApp getWebApp() {
		return getWebManager().getWebApp();
	}

	//Extendlet//
	public void init(ExtendletConfig config) {
		_webctx = config.getExtendletContext();
		_cache = new ResourceCache(new ZUMLLoader(), 17);
		_cache.setMaxSize(512);
		_cache.setLifetime(60*60*1000); //1hr
		_cache.setCheckPeriod(60*60*1000); //1hr
	}
	public void service(HttpServletRequest request,
	HttpServletResponse response, String path, String extra)
	throws ServletException, IOException {
		if (extra != null)
			log.warning("extra is not supported by ZumlExtendlet: "+extra);

		final Session sess = WebManager.getSession(getServletContext(), request);
		final PageDefinition pagedef = (PageDefinition)_cache.get(path);
		if (pagedef == null) {
			//FUTURE: support the error page (from Configuration)
			handleError(sess, request, response, path, null);
			return;
		}

		//mimic DHtmlLayoutServlet to process PageDefinition
		final Object old = I18Ns.setup(sess, request, response,
			sess.getWebApp().getConfiguration().getResponseCharset());
		try {
			process(sess, request, response, pagedef, path);
		} catch (Throwable ex) {
			handleError(sess, request, response, path, ex);
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

		final boolean compress = !Servlets.isIncluded(request);
		final Page page = wappc.getUiFactory().newPage(ri, pagedef, path);
		final Execution exec = new ExecutionImpl(
			getServletContext(), request, response, desktop, page);
		final Writer out = compress ? (Writer)new StringWriter(): response.getWriter();
		wappc.getUiEngine().execNewPage(exec, pagedef, page, out);

		if (compress) {
			final String result = ((StringWriter)out).toString();

			try {
				final OutputStream os = response.getOutputStream();
					//Call it first to ensure getWrite() is not called yet

				byte[] data = result.getBytes("UTF-8");
				if (data.length > 200) {
					byte[] bs = Https.gzip(request, response, null, data);
					if (bs != null) data = bs; //yes, browser support compress
				}

				response.setContentLength(data.length);
				os.write(data);
				response.flushBuffer();
			} catch (IllegalStateException ex) { //getWriter is called
				response.getWriter().write(result);
			}
		}
	}
	/** Handles exception being thrown when rendering a page.
	 * @param err the exception being throw. If null, it means the page
	 * is not found.
	 */
	private void handleError(Session sess, HttpServletRequest request,
	HttpServletResponse response, String path, Throwable err)
	throws ServletException, IOException {
		//Note: if not included, it is handled by Web container
		if (err != null && Servlets.isIncluded(request)) {
			//Bug 1802487 and 1714094
			final String errpg = sess.getWebApp().getConfiguration()
				.getErrorPage(sess.getDeviceType(), err);
			if (errpg != null) {
				try {
					request.setAttribute("javax.servlet.error.message", Exceptions.getMessage(err));
					request.setAttribute("javax.servlet.error.exception", err);
					request.setAttribute("javax.servlet.error.exception_type", err.getClass());
					request.setAttribute("javax.servlet.error.status_code", new Integer(500));
					Servlets.forward(getServletContext(), request, response, errpg, null, 0);
					return; //done
				} catch (IOException ex) { //eat it (connection off)
				} catch (Throwable ex) {
					log.warning("Failed to load the error page: "+errpg, ex);
				}
			}
		}

		Utils.handleError(getServletContext(), request, response, path, err);
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
