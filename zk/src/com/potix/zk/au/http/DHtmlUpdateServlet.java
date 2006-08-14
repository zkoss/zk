/* DHtmlUpdateServlet.java

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
package com.potix.zk.au.http;

import java.util.List;
import java.util.LinkedList;
import java.util.Date;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import com.potix.mesg.Messages;
import com.potix.lang.Exceptions;
import com.potix.util.logging.Log;

import com.potix.web.servlet.Servlets;
import com.potix.web.servlet.http.Https;
import com.potix.web.servlet.http.Encodes;
import com.potix.web.servlet.auth.Authens;
import com.potix.web.util.resource.ClassWebResource;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.ComponentNotFoundException;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.ui.http.ExecutionImpl;
import com.potix.zk.ui.http.WebManager;
import com.potix.zk.ui.http.I18Ns;
import com.potix.zk.au.AuRequest;
import com.potix.zk.au.AuResponse;
import com.potix.zk.au.AuObsolete;
import com.potix.zk.au.AuAlert;
import com.potix.zk.au.AuSendRedirect;
import com.potix.zk.au.CommandNotFoundException;

/**
 * Used to receive command from the server and send result back to client.
 *
 * <p>Design decision: it is better to use independent servlets for
 * /web, /upload and /view. However, to simplify the configuration,
 * we choose not to.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class DHtmlUpdateServlet extends HttpServlet {
	private static final Log log = Log.lookup(DHtmlUpdateServlet.class);

	private ServletContext _ctx;
	private long _lastModified;

	public void init(ServletConfig config) throws ServletException {
		if (log.debugable()) log.debug("Starting DHtmlUpdateServlet at "+config.getServletContext());
		_ctx = config.getServletContext();
	}
	public ServletContext getServletContext() {
		return _ctx;
	}

	//-- super --//
	protected long getLastModified(HttpServletRequest request) {
		final String pi = Https.getThisPathInfo(request);
		if (pi != null && pi.startsWith(ClassWebResource.PATH_PREFIX)
		&& !pi.endsWith(".dsp") && !Servlets.isIncluded(request)) {
			if (_lastModified == 0) _lastModified = new Date().getTime();
				//Hard to know when it is modified, so cheat it..
			return _lastModified;
		}
		return -1;
	}
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		Authens.isAuthenticated(request);
		//Enforce Web container to authenticate again
		//CONSIDER: we might use login="required" to define pages

		final Session sess = WebManager.getSession(_ctx, request);
		final Object old = I18Ns.setup(sess, request, response);
		try {
			final String pi = Https.getThisPathInfo(request);
			if (log.finerable()) log.finer("Path info: "+pi);
			if (pi != null) {
				if (pi.startsWith(ClassWebResource.PATH_PREFIX)) {
					WebManager.getWebManager(_ctx).
						getClassWebResource().doGet(request, response);
					return;
				}
				if (pi.startsWith("/upload")) {
					Uploads.process(sess, _ctx, request, response);
					return;
				}
				if (pi.startsWith("/view")) {
					Views.process(
						sess, _ctx, request, response, pi.substring(5));
					return;
				}
			}

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

	//-- ASYNC-UPDATE --//
	/** Process update requests from the client. */
	private void process(Session sess,
	HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final UiEngine uieng = wappc.getUiEngine();
		final List aureqs = new LinkedList();

		//parse desktop ID
		final String dtid = request.getParameter("dtid");
		if (dtid == null) {
			log.error("dtid not found: QS="+request.getQueryString()+", params="+request.getParameterMap().keySet());
			responseError(uieng, response, "Illegal request: dtid is required");
			return;
		}
		final Desktop desktop;
		try {
			desktop = wappc.getDesktopCache(sess).getDesktop(dtid);
			WebManager.setDesktop(request, desktop);
				//reason: a new page might be created (such as include)
		} catch (ComponentNotFoundException ex) {
			final StringWriter out = getXmlWriter();

			final String scmd = request.getParameter("cmd.0");
			if (!"rmDesktop".equals(scmd) && !"onRender".equals(scmd)
			&& !"onTimer".equals(scmd)) {//possible in FF due to cache
				String uri = wapp.getConfiguration().getTimeoutURI();
				final AuResponse resp;
				if (uri != null) {
					if (uri.length() != 0)
						uri = Encodes.encodeURL(_ctx, request, response, uri);
					resp = new AuSendRedirect(uri, null);
				} else {
					resp = new AuObsolete(
						dtid, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, dtid));
				}
				uieng.response(resp, out);
			}

			flushXmlWriter(response, out);
			return;
		}

		//parse commands
		try {
			for (int j = 0;; ++j) {
				final String scmd = request.getParameter("cmd."+j);
				if (scmd == null)
					break;

				final AuRequest.Command cmd = AuRequest.getCommand(scmd);
				final String uuid = request.getParameter("uuid."+j);
				final String[] data = request.getParameterValues("data."+j);
				if (data != null) {
					for (int k = data.length; --k >= 0;)
						if ("zk_null~q".equals(data[k]))
							data[k] = null;
				}
				if (uuid == null || uuid.length() == 0) {
					aureqs.add(new AuRequest(desktop, cmd, data));
				} else {
					aureqs.add(new AuRequest(desktop, uuid, cmd, data));
				}
			}
			if (aureqs.isEmpty()) {
				responseError(uieng, response, "Illegal request: cmd is required");
				return;
			}
		} catch (CommandNotFoundException ex) {
			responseError(uieng, response, Exceptions.getMessage(ex));
			return;
		}

		if (log.debugable()) log.debug("AU request: "+aureqs);
		final StringWriter out = getXmlWriter();

		final Execution exec = new ExecutionImpl(
			_ctx, request, response, desktop, null);
		uieng.execUpdate(exec, aureqs, out);

		flushXmlWriter(response, out);
	}

	/** Returns the writer for output XML.
	 * @param withrs whether to output <rs> first.
	 */
	private static StringWriter getXmlWriter() {
		final StringWriter out = new StringWriter();
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rs>\n");
		return out;
	}
	/** Flushes all content in out to the response.
	 * Don't write the response thereafter.
	 * @param withrs whether to output </rs> first.
	 */
	private static final
	void flushXmlWriter(HttpServletResponse response, StringWriter out)
	throws IOException {
		out.write("\n</rs>");

		//Use OutputStream due to Bug 1528592 (Jetty 6)
		final byte[] bs = out.toString().getBytes("UTF-8");
		response.setContentType("text/xml;charset=UTF-8");
		response.setContentLength(bs.length);
		response.getOutputStream().write(bs);
		response.flushBuffer();
	}

	/** Generates a response for an error message.
	 */
	private static
	void responseError(UiEngine uieng, HttpServletResponse response,
	String errmsg) throws IOException {
		log.debug(errmsg);

		//Don't use sendError because Browser cannot handle UTF-8
		final StringWriter out = getXmlWriter();
		uieng.response(new AuAlert(errmsg), out);
		flushXmlWriter(response, out);
	}
}
