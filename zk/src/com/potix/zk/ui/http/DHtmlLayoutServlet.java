/* DHtmlLayoutServlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:11:28     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.http;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import com.potix.mesg.Messages;
import com.potix.lang.D;
import com.potix.util.logging.Log;

import com.potix.web.Attributes;
import com.potix.web.servlet.Servlets;
import com.potix.web.servlet.http.Https;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.metainfo.PageDefinitions;
import com.potix.zk.ui.sys.UiFactory;
import com.potix.zk.ui.sys.RequestInfo;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.impl.RequestInfoImpl;

/**
 * Used to process the request for a ZUML page.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class DHtmlLayoutServlet extends HttpServlet {
	private static final Log log = Log.lookup(WebManager.class);
	private ServletContext _ctx;
	private WebManager _webman;

	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
			//Note callback super to avoid saving config

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
		final Session sess = WebManager.getSession(getServletContext(), request);
		final Object old = I18Ns.setup(sess, request, response);
		try {
			process(sess, request, response);
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
	private void process(Session sess,
	HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final String path = Https.getThisServletPath(request);
		if (D.ON && log.debugable()) log.debug("Creates from "+path);

		final WebApp wapp = _webman.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final Desktop desktop = _webman.getDesktop(sess, request, path);
		final RequestInfo ri = new RequestInfoImpl(
			wapp, sess, desktop, request,
			PageDefinitions.getLocator(wapp, path));
		final UiFactory uf = wappc.getUiFactory();
		final PageDefinition pagedef = uf.getPageDefinition(ri, path);
		if (pagedef == null) {
			if (Servlets.isIncluded(request)) {
				final String msg = Messages.get(MZk.PAGE_NOT_FOUND,
					new Object[] {path});
				final Map attrs = new HashMap();
				attrs.put(Attributes.ALERT_TYPE, "error");
				attrs.put(Attributes.ALERT, msg);
				Servlets.include(_ctx, request, response,
					"~./html/alert.dsp", attrs, Servlets.PASS_THRU_ATTR);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			return;
		}

		final Page page = uf.newPage(ri, pagedef, path);
		final Execution exec = new ExecutionImpl(
			_ctx, request, response, desktop, page);
		wappc.getUiEngine()
			.execNewPage(exec, pagedef, page, response.getWriter());
	}
}
