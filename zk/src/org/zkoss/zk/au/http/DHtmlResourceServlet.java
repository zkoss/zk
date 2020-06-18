/* DHtmlResourceServlet.java

	Purpose:
		
	Description:
		
	History:
		 Thu May 14 14:33:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.util.resource.ClassWebResource;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.I18Ns;
import org.zkoss.zk.ui.http.SessionResolverImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.sys.SessionsCtrl;

/**
 * Used to receive ZK resource from the server
 * <p>Init parameters:
 * <dl>
 * <dt>compress</dt>
 * <dd>It specifies whether to compress the output if the browser supports the compression (Accept-Encoding).</dd>
 * </dl>
 * @author tomyeh
 * @author jameschu
 * @since 9.5.0
 */
public class DHtmlResourceServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(DHtmlResourceServlet.class);
	private long _lastModified;
	private boolean _compress = true;

	//Servlet//
	public void init() throws ServletException {
		final ServletConfig config = getServletConfig();
		final ServletContext ctx = getServletContext();
		final WebManager webman = WebManager.getWebManager(ctx);
		String param = config.getInitParameter("compress");
		_compress = param == null || param.length() == 0 || "true".equals(param);
		if (!_compress)
			webman.getClassWebResource().setCompress(null); //disable all
	}

	/*
	 * Returns whether to compress the output.
	 */
	public boolean isCompress() {
		return _compress;
	}

	/*
	 * Set whether to compress the output or not.
	 */
	public void setCompress(boolean compress) {
		_compress = compress;
	}

	//-- super --//
	protected long getLastModified(HttpServletRequest request) {
		final String pi = Https.getThisPathInfo(request);
		if (pi != null && pi.startsWith(ClassWebResource.PATH_PREFIX) && pi.indexOf('*') < 0 //language independent
				&& !Servlets.isIncluded(request)) {
			//If a resource extension is registered for the extension, we assume the content is dynamic
			final String ext = Servlets.getExtension(pi, false);
			if (ext == null || getClassWebResource().getExtendlet(ext) == null) {
				if (_lastModified == 0)
					_lastModified = new Date().getTime();
				//Hard to know when it is modified, so cheat it..
				return _lastModified;
			}
		}
		return -1;
	}

	private ClassWebResource getClassWebResource() {
		return WebManager.getWebManager(getServletContext()).getClassWebResource();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet0(request, response, getServletContext(), getClassWebResource());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private static boolean shallSession(ClassWebResource cwr, String pi) {
		//Optimize the access of static resources (for GAE)
		return cwr.getExtendlet(Servlets.getExtension(pi, false)) != null || (pi != null && pi.indexOf('*') >= 0);
	}

	static boolean doGet0(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
			ClassWebResource cwr) throws ServletException, IOException {
		final String pi = Https.getThisPathInfo(request);

		final boolean withpi = pi != null && pi.length() != 0 && !(pi.startsWith("/_/") || "/_".equals(pi));
		if (withpi && pi.startsWith(ClassWebResource.PATH_PREFIX)) {
			//use HttpSession to avoid loading SerializableSession in GAE and don't retrieve session if possible
			final HttpSession hsess = shallSession(cwr, pi) ? request.getSession(false) : null;
			Object oldsess = null;
			if (hsess == null) {
				oldsess = SessionsCtrl.getRawCurrent();
				SessionsCtrl.setCurrent(new SessionResolverImpl(ctx, request));
				//it might be created later
			}
			WebApp wapp;
			Session sess;
			final Object old = hsess != null
					? (wapp = WebManager.getWebAppIfAny(ctx)) != null
							&& (sess = SessionsCtrl.getSession(wapp, hsess)) != null
									? I18Ns.setup(sess, request, response, "UTF-8")
									: I18Ns.setup(hsess, request, response, "UTF-8")
					: Charsets.setup(null, request, response, "UTF-8");
			try {
				cwr.service(request, response, pi.substring(ClassWebResource.PATH_PREFIX.length()));
			} finally {
				if (hsess != null)
					I18Ns.cleanup(request, old);
				else {
					Charsets.cleanup(request, old);
					SessionsCtrl.setRawCurrent(oldsess);
				}
			}
			return true; //done
		}
		return false;
	}
}
