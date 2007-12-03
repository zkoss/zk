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

import java.util.Iterator;
import java.util.Collection;
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
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.PerformanceMeter;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.http.I18Ns;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.CommandNotFoundException;
import org.zkoss.zk.au.out.AuObsolete;
import org.zkoss.zk.au.out.AuAlert;
import org.zkoss.zk.au.out.AuSendRedirect;
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
					getClassWebResource()
						.service(request, response,
							pi.substring(ClassWebResource.PATH_PREFIX.length()));
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
	/** Process update requests from the client.
	 * @since 3.0.0
	 */
	protected void process(Session sess,
	HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final UiEngine uieng = wappc.getUiEngine();
		final List aureqs = new LinkedList();

		//parse desktop ID
		final String dtid = request.getParameter("dtid");
		if (dtid == null) {
			//log.warning("dtid not found: CP="+request.getContextPath()+" and "+Https.getThisContextPath(request)
			//	+", SP="+request.getServletPath()+" and "+Https.getThisServletPath(request)
			//	+", QS="+request.getQueryString()+" and "+Https.getThisQueryString(request)
			//	+", params="+request.getParameterMap().keySet());
			//responseError(response, "Illegal request: dtid is required");
			//Tom M. Yeh: 20060922: Unknown reason to get here but it is annoying
			//to response it back to users
			return;
		}

		Desktop desktop = wappc.getDesktopCache(sess).getDesktopIfAny(dtid);
		if (desktop == null) {
			final String scmd = request.getParameter("cmd.0");
			if (!"rmDesktop".equals(scmd))
				desktop = recover(sess, request, response, wappc, dtid);

			if (desktop == null) {
				final AuWriter out =
					new HttpAuWriter().open(request, response, 0);

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
				return;
			}
		}
		WebManager.setDesktop(request, desktop);
			//reason: a new page might be created (such as include)

		//parse commands
		final Configuration config = wapp.getConfiguration();
		final boolean timerKeepAlive = config.isTimerKeepAlive();
		boolean keepAlive = false;
		try {
			for (int j = 0;; ++j) {
				final String scmd = request.getParameter("cmd."+j);
				if (scmd == null)
					break;

				keepAlive = keepAlive
					|| (!(!timerKeepAlive && Events.ON_TIMER.equals(scmd))
						&& !"dummy".equals(scmd));
					//dummy is used for PollingServerPush for piggyback

				final Command cmd = AuRequest.getCommand(scmd);
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
		} catch (CommandNotFoundException ex) {
			responseError(request, response, Exceptions.getMessage(ex));
			return;
		}

		if (aureqs.isEmpty()) {
			responseError(request, response, "Illegal request: cmd is required");
			return;
		}

		((SessionCtrl)sess).notifyClientRequest(keepAlive);

		//if (log.debugable()) log.debug("AU request: "+aureqs);
		final AuWriter out = new HttpAuWriter()
			.open(request, response, config.getResendDelay() / 2 - 1000);
		final PerformanceMeter pfmeter = config.getPerformanceMeter();

		final Execution exec = 
			new ExecutionImpl(_ctx, request, response, desktop, null);
		final Collection reqIds = uieng.execUpdate(exec, aureqs,
			pfmeter != null ? meterStart(pfmeter, request, exec): null,
			out);

		if (reqIds != null && pfmeter != null)
			meterComplete(pfmeter, response, reqIds, exec);

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
						.getDesktop(sess, request, response, null, true);
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
		log.debug(errmsg);

		//Don't use sendError because Browser cannot handle UTF-8
		final AuWriter out = new HttpAuWriter().open(request, response, 0);
		out.write(new AuAlert(errmsg));
		out.close(request, response);
	}

	/** Handles the start of request.
	 *
	 * @return the request ID from the ZK-Client-Start header,
	 * or null if not found.
	 */
	private static String meterStart(PerformanceMeter pfmeter,
	HttpServletRequest request, Execution exec) {
		//Format of ZK-Client-Complete:
		//	request-id1 request-id2=time1,request-id3=time2
		String hdr = request.getHeader("ZK-Client-Complete");
		if (hdr != null) {
			for (int j = 0;;) {
				int k = hdr.indexOf(',', j);
				String ids = k >= 0 ? hdr.substring(j, k):
					j == 0 ? hdr: hdr.substring(j);

				int x = ids.lastIndexOf('=');
				if (x > 0) {
					try {
						long time = Long.parseLong(ids.substring(x + 1));

						ids = ids.substring(0, x);
						for (int y = 0;;) {
							int z = ids.indexOf(' ', y);
							String reqId = z >= 0 ? ids.substring(y, z):
								y == 0 ? ids: ids.substring(y);
							pfmeter.requestCompleteAtClient(reqId, exec, time);

							if (z < 0) break; //done
							y = z + 1;
						}
					} catch (NumberFormatException ex) {
						log.error("Unable to parse "+ids);
					}
				}

				if (k < 0) break; //done
				j = k + 1;
			}
		}

		//Format of ZK-Client-Start:
		//	request-id=time
		hdr = request.getHeader("ZK-Client-Start");
		if (hdr != null) {
			final int j = hdr.lastIndexOf('=');
			if (j > 0) {
				try {
					final String reqId = hdr.substring(0, j);
					pfmeter.requestStartAtClient(reqId, exec,
						Long.parseLong(hdr.substring(j + 1)));
					pfmeter.requestStartAtServer(reqId, exec,
						System.currentTimeMillis());
					return reqId;
				} catch (NumberFormatException ex) {
					log.error("Unable to parse "+hdr);
				}
			}
		}
		return null;
	}
	/** Handles the complete of the request.
	 * It sets the ZK-Client-Complete header.
	 */
	private static void meterComplete(PerformanceMeter pfmeter,
	HttpServletResponse response, Collection reqIds, Execution exec) {
		final StringBuffer sb = new StringBuffer(256);
		long time = System.currentTimeMillis();
		for (Iterator it = reqIds.iterator(); it.hasNext();) {
			final String reqId = (String)it.next();
			if (sb.length() > 0) sb.append(' ');
			sb.append(reqId);
			pfmeter.requestCompleteAtServer(reqId, exec, time);
		}

		response.setHeader("ZK-Client-Complete", sb.toString());
			//tell the client what are completed
	}
}
