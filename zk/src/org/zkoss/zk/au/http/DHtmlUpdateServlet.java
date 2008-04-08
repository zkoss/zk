/* DHtmlUpdateServlet.java

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
package org.zkoss.zk.au.http;

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

import org.zkoss.mesg.Messages;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.util.resource.ClassWebResource;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.http.I18Ns;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuObsolete;
import org.zkoss.zk.au.AuAlert;
import org.zkoss.zk.au.AuSendRedirect;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.CommandNotFoundException;
import org.zkoss.zk.device.Devices;

/**
 * Used to receive command from the server and send result back to client.
 * Though it is called
 * DHtmlUpdateServlet, it is used to serve all kind of HTTP-based clients,
 * including ajax (HTML+Ajax), mil (Mobile Interactive Language),
 * and others (see {@link Desktop#getDeviceType}.
 *
 * <p>Design decision: it is better to use independent servlets for
 * /web, /upload and /view. However, to simplify the configuration,
 * we choose not to.
 *
 * @author tomyeh
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
		&& pi.indexOf('*') < 0 //language independent
		&& !Servlets.isIncluded(request)) {
			//If a resource processor is registered for the extension,
			//we assume the content is dynamic
			final String ext = Servlets.getExtension(pi);
			if (ext == null
			|| getClassWebResource().getExtendlet(ext) == null) {
				if (_lastModified == 0) _lastModified = new Date().getTime();
					//Hard to know when it is modified, so cheat it..
				return _lastModified;
			}
		}
		return -1;
	}
	private ClassWebResource getClassWebResource() {
		return WebManager.getWebManager(_ctx).getClassWebResource();
	}
	protected
	void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final Session sess = WebManager.getSession(_ctx, request);
		final Object old = I18Ns.setup(sess, request, response, "UTF-8");
		try {
			final String pi = Https.getThisPathInfo(request);
			if (pi != null && pi.length() != 0) {
				//if (log.finerable()) log.finer("Path info: "+pi);
				if (pi.startsWith(ClassWebResource.PATH_PREFIX)) {
					getClassWebResource().service(request, response);
				} else if (pi.startsWith("/upload")) {
					Uploads.process(sess, _ctx, request, response);
				} else if (pi.startsWith("/view")) {
					DynaMedias.process(
						sess, _ctx, request, response, pi.substring(5));
				} else {
					log.warning("Unknown path info: "+pi);
				}
				return;
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
		final String errClient = request.getHeader("ZK-Error-Report");
		if (errClient != null)
			if (log.debugable()) log.debug("Error found at client: "+errClient+"\n"+getDetail(request));

		//parse desktop ID
		final String dtid = request.getParameter("dtid");
		if (dtid == null) {
			//Bug 1929139: incomplete request (IE only)
			final boolean ie = Servlets.isExplorer(request);
			if (!ie || log.debugable()) {
				final String msg = "Incomplete request\n"+getDetail(request);
				if (ie) log.debug(msg);
				else log.warning(msg); //impossible, so warning
			}

			response.sendError(467, "Incomplete request");
			return;
		}

		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		Desktop desktop = wappc.getDesktopCache(sess).getDesktopIfAny(dtid);
		if (desktop == null) {
			final String scmd = request.getParameter("cmd.0");
			if (!"rmDesktop".equals(scmd))
				desktop = recover(sess, request, response, wappc, dtid);

			if (desktop == null) {
				sessionTimeout(request, response, dtid, scmd);
				return;
			}
		}
		WebManager.setDesktop(request, desktop);
			//reason: a new page might be created (such as include)

		final String sid = request.getHeader("ZK-SID");
		if (sid != null) //Mobile client doesn't have ZK-SID
			response.setHeader("ZK-SID", sid);

		//parse commands
		final List aureqs = new LinkedList();
		try {
			for (int j = 0;; ++j) {
				final String scmd = request.getParameter("cmd."+j);
				if (scmd == null)
					break;

				final Command cmd = AuRequest.getCommand(scmd);
				final String uuid = request.getParameter("uuid."+j);
				final String[] data = request.getParameterValues("data."+j);
				if (data != null) {
					for (int k = data.length; --k >= 0;)
						if ("_z~nil".equals(data[k]))
							data[k] = null;
				}
				if (uuid == null || uuid.length() == 0) {
					aureqs.add(new AuRequest(desktop, cmd, data));
				} else {
					aureqs.add(new AuRequest(desktop, uuid, cmd, data));
				}
			}
		} catch (Throwable ex) {
			final String errmsg = Exceptions.getMessage(ex);
			if (ex instanceof CommandNotFoundException) log.debug(errmsg);
			else log.warningBriefly(ex);
			responseError(request, response, errmsg);
			return;
		}

		if (aureqs.isEmpty()) {
			final String errmsg = "Illegal request: cmd required";
			log.debug(errmsg);
			responseError(request, response, errmsg);
			return;
		}

//		if (log.debugable()) log.debug("AU request: "+aureqs);
		final Execution exec = 
			new ExecutionImpl(_ctx, request, response, desktop, null);
		if (sid != null)
			((ExecutionCtrl)exec).setRequestId(sid);
		final AuWriter out = new SmartAuWriter().open(request, response, 900);
		wappc.getUiEngine().execUpdate(exec, aureqs, out);

		out.close(request, response);
	}
	private void sessionTimeout(HttpServletRequest request,
	HttpServletResponse response, String dtid, String scmd)
	throws ServletException, IOException {
		final String sid = request.getHeader("ZK-SID");
		if (sid != null)
			response.setHeader("ZK-SID", sid);

		final AuWriter out = new SmartAuWriter().open(request, response, 0);

		if (!"rmDesktop".equals(scmd) && !Events.ON_RENDER.equals(scmd)
		&& !Events.ON_TIMER.equals(scmd) && !"dummy".equals(scmd)) {//possible in FF due to cache
			String uri = Devices.getTimeoutURI(
				Servlets.isMilDevice(request) ? "mil": "ajax");
			final AuResponse resp;
			if (uri != null) {
				if (uri.length() != 0)
					uri = Encodes.encodeURL(_ctx, request, response, uri);
				resp = new AuSendRedirect(uri, null);
			} else {
				resp = new AuObsolete(
					dtid, Messages.get(MZk.UPDATE_OBSOLETE_PAGE, dtid));
			}
			out.write(resp);
		}

		out.close(request, response);
	}

	/** Recovers the desktop if possible.
	 */
	private Desktop recover(Session sess,
	HttpServletRequest request, HttpServletResponse response,
	WebAppCtrl wappc, String dtid) {
		final FailoverManager failover = wappc.getFailoverManager();
		if (failover != null) {
			Desktop desktop = null;
			try {
				if (failover.isRecoverable(sess, dtid)) {
					desktop = WebManager.getWebManager(_ctx)
						.getDesktop(sess, request, null, true);
					wappc.getUiEngine().execRecover(
						new ExecutionImpl(_ctx, request, response, desktop, null),
						failover);
					return desktop; //success
				}
			} catch (Throwable ex) {
				log.error("Unable to recover "+dtid, ex);
				if (desktop != null)
					((DesktopCtrl)desktop).recoverDidFail(ex);
			}
		}
		return null;
	}

	/** Generates a response for an error message.
	 */
	private static void responseError(HttpServletRequest request,
	HttpServletResponse response, String errmsg) throws IOException {
		//Don't use sendError because Browser cannot handle UTF-8
		final AuWriter out = new SmartAuWriter().open(request, response, 0);
		out.write(new AuAlert(errmsg));
		out.close(request, response);
	}


	/** Returns the request details.
	 */
	private static String getDetail(HttpServletRequest request) {
		final StringBuffer sb = new StringBuffer(128);
		sb.append(" sid: ").append(request.getHeader("ZK-SID")).append('\n');
		addHeaderInfo(sb, request, "user-agent");
		addHeaderInfo(sb, request, "content-length");
//		addHeaderInfo(sb, request, "content-type");
		sb.append(" ip: ").append(request.getRemoteAddr());
//		sb.append(" method: ").append(request.getMethod());
		return sb.toString();
	}
	private static void addHeaderInfo(StringBuffer sb,
	HttpServletRequest request, String header) {
		sb.append(' ')
			.append(header).append(": ").append(request.getHeader(header))
			.append('\n');
	}
}
