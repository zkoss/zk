/* DHtmlLayoutServlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:11:28     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
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

import org.zkoss.web.Attributes;
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
import org.zkoss.zk.ui.impl.RequestInfoImpl;

/**
 * Used to process the request for a ZUML page. Though it is called
 * DHtmlLayoutServlet, it is used to serve all kind of HTTP-based clients,
 * including html, mul and others (see {@link Desktop#getClientType}.
 *
 * @author tomyeh
 */
public class DHtmlLayoutServlet extends HttpServlet {
	private static final Log log = Log.lookup(DHtmlLayoutServlet.class);
	private ServletContext _ctx;
	private WebManager _webman;

	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
			//Note callback super to avoid saving config

		final String loglevel = config.getInitParameter("log-level");
		if (loglevel != null && loglevel.length() > 0) {
			final Level level = Log.getLevel(loglevel);
			if (level != null) Log.lookup("org.zkoss").setLevel(level);
			else log.error("Unknown log-level: "+loglevel);
		}

		_ctx = config.getServletContext();
		if (WebManager.getWebManagerIfAny(_ctx) != null)
			throw new ServletException("Only one layout servlet is allowed in one context: "+_ctx);

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
		if (D.ON && log.finerable()) log.finer("Creates from "+path);

		final Session sess = WebManager.getSession(getServletContext(), request);
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
	 */
	private boolean process(Session sess,
	HttpServletRequest request, HttpServletResponse response, String path,
	boolean bRichlet)
	throws ServletException, IOException {
		final WebApp wapp = _webman.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;

		final Desktop desktop = _webman.getDesktop(sess, request, path, true);
		final RequestInfo ri = new RequestInfoImpl(
			wapp, sess, desktop, request,
			PageDefinitions.getLocator(wapp, path));

		final UiFactory uf = wappc.getUiFactory();
		if (uf.isRichlet(ri, bRichlet)) {
			final Richlet richlet = uf.getRichlet(ri, path);
			if (richlet == null)
				return false; //not found

			final Page page = uf.newPage(ri, richlet, path);
			final Execution exec = new ExecutionImpl(
				_ctx, request, response, desktop, page);
			wappc.getUiEngine().execNewPage(exec, richlet, page, response.getWriter());
				//no need to set client type here, since UiEngine will do it later
		} else {
			final PageDefinition pagedef = uf.getPageDefinition(ri, path);
			if (pagedef == null)
				return false; //not found

			final Page page = uf.newPage(ri, pagedef, path);
			final Execution exec = new ExecutionImpl(
				_ctx, request, response, desktop, page);
			wappc.getUiEngine()
				.execNewPage(exec, pagedef, page, response.getWriter());
		}
		return true; //success
	}
	/** Handles exception being thrown when rendering a page.
	 * @param ex the exception being throw. If null, it means the page
	 * is not found.
	 */
	private void handleError(Session sess, HttpServletRequest request,
	HttpServletResponse response, String path, Throwable err)
	throws ServletException, IOException {
		if (Servlets.isIncluded(request)) {
			final String msg;
			if (err != null) {
			//Bug 1714094: we have to handle err, because Web container
			//didn't allow developer to intercept errors caused by inclusion
				final String errpg =
					sess.getWebApp().getConfiguration().getErrorPage(err);
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

				msg = Messages.get(MZk.PAGE_FAILED,
					new Object[] {path, Exceptions.getMessage(err),
						Exceptions.formatStackTrace(null, err, null, 6)});
			} else {
				msg = Messages.get(MZk.PAGE_NOT_FOUND, new Object[] {path});
			}

			final Map attrs = new HashMap();
			attrs.put(Attributes.ALERT_TYPE, "error");
			attrs.put(Attributes.ALERT, msg);
			Servlets.include(_ctx, request, response,
				"~./html/alert.dsp", attrs, Servlets.PASS_THRU_ATTR);
		} else {
			//If not included, let the Web container handle it
			if (err != null) {
				if (err instanceof ServletException)
					throw (ServletException)err;
				else if (err instanceof IOException)
					throw (IOException)err;
				else
					throw UiException.Aide.wrap(err);
			}
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
