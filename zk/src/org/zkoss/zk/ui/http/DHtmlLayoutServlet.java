/* DHtmlLayoutServlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:11:28     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.io.Writer;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.impl.RequestInfoImpl;

/**
 * Used to process the request for a ZUML page. Though it is called
 * DHtmlLayoutServlet, it is used to serve all kind of HTTP-based clients,
 * including ajax (HTML+Ajax), mil (Mobile Interactive Language)
 * and others (see {@link Desktop#getDeviceType}.
 *
 * <p>Init parameters:
 *
 * <dl>
 * <dt>update-uri</dt>
 * <dd>It specifies the URI which the ZK AU engine is mapped to.</dd>
 * <dt>compress</dt>
 * <dd>It specifies whether to compress the output if the browser supports the compression (Accept-Encoding) and this Servlet is not included by other Servlets.</dd>
 * <dt>log-level</dt>
 * <dd>It specifies the default log level for org.zkoss. If not specified, the system default (usually INFO) is used.</dd>
 * </dl>
 * @author tomyeh
 */
public class DHtmlLayoutServlet extends HttpServlet {
	private static final Log log = Log.lookup(DHtmlLayoutServlet.class);
	private static final String ATTR_LAYOUT_SERVLET
		= "org.zkoss.zk.ui.http.LayoutServlet";

	private ServletContext _ctx;
	private WebManager _webman;
	private boolean _compress = true;

	/** @deprecated As of release 3.6.0, removed in favor of 
	 * {@link WebManager#getWebManager}.
	 */
	public static DHtmlLayoutServlet getLayoutServlet(WebApp wapp) {
		return (DHtmlLayoutServlet)
			((ServletContext)wapp.getNativeContext())
				.getAttribute(ATTR_LAYOUT_SERVLET);
	}

	//Servlet//
	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
			//Note callback super to avoid saving config

		String param = config.getInitParameter("log-level");
		if (param != null && param.length() > 0) {
			final Level level = Log.getLevel(param);
			if (level != null) Log.lookup("org.zkoss").setLevel(level);
			else log.error("Unknown log-level: "+param);
		}

		param = config.getInitParameter("compress");
		_compress = param == null || param.length() == 0 || "true".equals(param);

		_ctx = config.getServletContext();
		_webman = WebManager.getWebManagerIfAny(_ctx);
		if (_webman != null) {
			log.info("Web Manager was created before ZK loader");
		} else {
			String updateURI = config.getInitParameter("update-uri");
			if (updateURI == null
			|| (updateURI = updateURI.trim()).length() == 0
			|| updateURI.charAt(0) != '/')
				throw new ServletException("The update-uri parameter must be specified and starts with /");
			if (updateURI.indexOf(';') >= 0 || updateURI.indexOf('?') >= 0)
				throw new ServletException("The update-uri parameter cannot contain ';' or '?'");
				//Jetty will encode URL by appending ';jsess..' and we have to
				//remove it under certain situations, so not alow it
			if (updateURI.charAt(updateURI.length() - 1) == '\\') {
				if (updateURI.length() == 1)
					throw new ServletException("The update-uri parameter cannot contain only '/'");
				updateURI = updateURI.substring(0, updateURI.length() - 1);
					//remove the trailing '\\' if any
			}
			_webman = new WebManager(_ctx, updateURI);
		}

		_ctx.setAttribute(ATTR_LAYOUT_SERVLET, this);
	}
	public void destroy() {
		if (_webman != null) _webman.destroy();
	}
	public ServletContext getServletContext() {
		return _ctx;
	}

	//-- super --//
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String path = Https.getThisPathInfo(request);
		final boolean bRichlet = path != null && path.length() > 0;
		if (!bRichlet)
			path = Https.getThisServletPath(request);
//		if (D.ON && log.finerable()) log.finer("Creates from "+path);

		final Session sess = WebManager.getSession(getServletContext(), request);
		if (!SessionsCtrl.requestEnter(sess)) {
			response.sendError(response.SC_SERVICE_UNAVAILABLE,
				Messages.get(MZk.TOO_MANY_REQUESTS));
			return;
		}
		try {
			final Object old = I18Ns.setup(sess, request, response,
				sess.getWebApp().getConfiguration().getResponseCharset());
			try {
				if (!process(sess, request, response, path, bRichlet))
					handleError(sess, request, response, path, null);
			} catch (Throwable ex) {
				handleError(sess, request, response, path, ex);
			} finally {
				I18Ns.cleanup(request, old);
			}
		} finally {
			SessionsCtrl.requestExit(sess);
		}
	}
	protected
	void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		doGet(request, response);
	}

	//-- private --//
	/**
	 * Process the request.
	 * @return false if the page is not found.
	 * @since 3.0.0
	 */
	protected boolean process(Session sess,
	HttpServletRequest request, HttpServletResponse response, String path,
	boolean bRichlet)
	throws ServletException, IOException {
		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;

		final Desktop desktop =
			_webman.getDesktop(sess, request, response, path, true);
		if (desktop == null) //forward or redirect
			return true;

		final RequestInfo ri = new RequestInfoImpl(
			wapp, sess, desktop, request,
			PageDefinitions.getLocator(wapp, path));
		sess.setAttribute("_z_gae_fix", new Integer(0));
		((SessionCtrl)sess).notifyClientRequest(true);

		final boolean compress = _compress && !Servlets.isIncluded(request);
		final Writer out;
		final UiFactory uf = wappc.getUiFactory();
		if (uf.isRichlet(ri, bRichlet)) {
			final Richlet richlet = uf.getRichlet(ri, path);
			if (richlet == null)
				return false; //not found

			final Page page = WebManager.newPage(uf, ri, richlet, response, path);
			final Execution exec = new ExecutionImpl(
				_ctx, request, response, desktop, page);
			out = compress ? (Writer)new StringWriter(): response.getWriter();
			wappc.getUiEngine().execNewPage(exec, richlet, page, out);
				//no need to set device type here, since UiEngine will do it later
		} else {
			final PageDefinition pagedef = uf.getPageDefinition(ri, path);
			if (pagedef == null)
				return false; //not found

			final Page page = WebManager.newPage(uf, ri, pagedef, response, path);
			final Execution exec = new ExecutionImpl(
				_ctx, request, response, desktop, page);
			out = compress ? (Writer)new StringWriter(): response.getWriter();
			wappc.getUiEngine().execNewPage(exec, pagedef, page, out);
		}

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
		return true; //success
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
			//Bug 1714094: we have to handle err, because Web container
			//didn't allow developer to intercept errors caused by inclusion
			final String errpg = sess.getWebApp().getConfiguration()
				.getErrorPage(sess.getDeviceType(), err);
			if (errpg != null) {
				try {
					request.setAttribute("javax.servlet.error.message", Exceptions.getMessage(err));
					request.setAttribute("javax.servlet.error.exception", err);
					request.setAttribute("javax.servlet.error.exception_type", err.getClass());
					request.setAttribute("javax.servlet.error.status_code", new Integer(500));
					if (process(sess, request, response, errpg, false))
						return; //done

					log.warning("The error page not found: "+errpg);
				} catch (IOException ex) { //eat it (connection off)
				} catch (Throwable ex) {
					log.warning("Failed to load the error page: "+errpg, ex);
				}
			}
		}

		Utils.handleError(_ctx, request, response, path, err);
	}
}
