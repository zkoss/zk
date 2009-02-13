/* DHtmlUpdateServlet.java

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
package org.zkoss.zk.au.http;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
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
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.util.resource.ClassWebResource;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.PerformanceMeter;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.http.I18Ns;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.CommandNotFoundException;
import org.zkoss.zk.au.out.AuObsolete;
import org.zkoss.zk.au.out.AuAlert;
import org.zkoss.zk.au.out.AuSendRedirect;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.device.Device;

/**
 * Used to receive command from the server and send result back to client.
 * Though it is called
 * DHtmlUpdateServlet, it is used to serve all kind of HTTP-based clients,
 * including ajax (HTML+Ajax), mil (Mobile Interactive Language),
 * and others (see {@link Desktop#getDeviceType}.
 *
 * <p>Init parameters:
 *
 * <dl>
 * <dt>processor0, processor1...</dt>
 * <dd>It specifies an AU processor.
 * The first processor must be specified with the name called processor0,
 * second processor1 and so on.<br/>
 * The syntax of the value is<br/>
 * <code>/prefix=class</code>
 * </dd>
 * </dl>
 *
 * <p>By default: there are two processors are associated with
 * "/upload" and "/view" (see {@link #addAuProcessor}.
 * Also, "/web" is reserved. Don't associate to any AU processor.
 *
 * @author tomyeh
 */
public class DHtmlUpdateServlet extends HttpServlet {
	private static final Log log = Log.lookup(DHtmlUpdateServlet.class);
	private static final String ATTR_UPDATE_SERVLET
		= "org.zkoss.zk.au.http.UpdateServlet";

	private ServletContext _ctx;
	private long _lastModified;
	/** (String name, AuProcessor). */
	private Map _procs = new HashMap(4);

	/** Returns the update servlet of the specified application, or
	 * null if not loaded yet.
	 * Note: if the update servlet is not loaded, it returns null.
	 * @since 3.0.2
	 */
	public static DHtmlUpdateServlet getUpdateServlet(WebApp wapp) {
		return (DHtmlUpdateServlet)
			((ServletContext)wapp.getNativeContext())
				.getAttribute(ATTR_UPDATE_SERVLET);
	}

	//Servlet//
	public void init(ServletConfig config) throws ServletException {
		//super.init(config);
			//Note callback super to avoid saving config

//		if (log.debugable()) log.debug("Starting DHtmlUpdateServlet at "+config.getServletContext());
		_ctx = config.getServletContext();

		for (int j = 0;;) {
			String param = config.getInitParameter("processor" + j++);
			if (param == null) break;
			final int k = param.indexOf('=');
			if (k < 0) {
				log.warning("Ignore init-param: illegal format, "+param);
				continue;
			}

			final String prefix = param.substring(0, k).trim();
			final String clsnm = param.substring(k + 1).trim();
			try {
				addAuProcessor(prefix,
					(AuProcessor)Classes.newInstanceByThread(clsnm));
			} catch (ClassNotFoundException ex) {
				log.warning("Ignore init-param: class not found, "+clsnm);
			} catch (ClassCastException ex) {
				log.warning("Ignore: "+clsnm+" not implement "+AuProcessor.class);
			} catch (Throwable ex) {
				log.warning("Ignore init-param: failed to add an AU processor, "+param,
					ex);
			}
		}

		if (getAuProcessor("/upload") == null) {
			try {
				addAuProcessor("/upload", new AuUploader());
			} catch (Throwable ex) {
				final String msg =
					" Make sure commons-fileupload.jar is installed.";
				log.warningBriefly("Failed to configure fileupload."+msg, ex);

				//still add /upload to generate exception when fileupload is used
				addAuProcessor("/upload",
					new AuProcessor() {
						public void process(Session sess, ServletContext ctx,
						HttpServletRequest request, HttpServletResponse response, String pi)
						throws ServletException, IOException {
							throw new ServletException("Failed to upload."+msg);
						}
					});
			}
		}

		if (getAuProcessor("/view") == null)
			addAuProcessor("/view", new AuDynaMediar());

		_ctx.setAttribute(ATTR_UPDATE_SERVLET, this);
	}
	public ServletContext getServletContext() {
		return _ctx;
	}

	/** Adds an AU processor and associates it with the specified prefix.
	 *
	 * <p>If there was an AU processor associated with the same name, the
	 * the old AU processor will be replaced.
	 *
	 * @param prefix the prefix. It must start with "/", but it cannot be
	 * "/" nor "/web" (which are reserved).
	 * @param processor the AU processor (never null).
	 * @return the previous AU processor associated with the specified prefix,
	 * or null if the prefix was not associated before.
	 * @since 3.0.2
	 */
	public AuProcessor addAuProcessor(String prefix, AuProcessor processor) {
		if (prefix == null || !prefix.startsWith("/") || prefix.length() < 2
		|| processor == null)
			throw new IllegalArgumentException();
		if (ClassWebResource.PATH_PREFIX.equalsIgnoreCase(prefix))
			throw new IllegalArgumentException(
				ClassWebResource.PATH_PREFIX + " is reserved");

		if (_procs.get(prefix) ==  processor) //speed up to avoid sync
			return processor; //nothing changed

		//To avoid using sync in doGet(), we make a copy here
		final AuProcessor old;
		synchronized (this) {
			final Map ps = new HashMap(_procs);
			old = (AuProcessor)ps.put(prefix, processor);
			_procs = ps;
		}
		return old;
	}
	/** Returns the AU processor associated with the specified prefix,
	 * or null if no AU processor associated.
	 * @since 3.0.2
	 */
	public AuProcessor getAuProcessor(String prefix) {
		return (AuProcessor)_procs.get(prefix);
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
		final String pi = Https.getThisPathInfo(request);
//		if (log.finerable()) log.finer("Path info: "+pi);

		final Session sess = WebManager.getSession(_ctx, request, false);
		final boolean withpi = pi != null && pi.length() != 0;
		if (withpi && pi.startsWith(ClassWebResource.PATH_PREFIX)) {
			final Object old = sess != null?
				I18Ns.setup(sess, request, response, "UTF-8"):
				Charsets.setup(request, response, "UTF-8");
			try {
				getClassWebResource()
					.service(request, response,
						pi.substring(ClassWebResource.PATH_PREFIX.length()));
			} finally {
				if (sess != null) I18Ns.cleanup(request, old);
				else Charsets.cleanup(request, old);
			}
			return; //done
		}

		if (sess == null) {
			response.setIntHeader("ZK-Error", response.SC_GONE); //denote timeout
			if (!withpi) { //AU request
				//Bug 1849088: rmDesktop might be sent after invalidate
				//Bug 1859776: need send response to client for redirect or others
				final String dtid = request.getParameter("dtid");
				if (dtid != null)
					sessionTimeout(request, response,
						dtid, request.getParameter("cmd.0"));
			}
			return;
		}

		final Object old = I18Ns.setup(sess, request, response, "UTF-8");
		try {
			if (withpi) {
				for (Iterator it = _procs.entrySet().iterator(); it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					if (pi.startsWith((String)me.getKey())) {
						((AuProcessor)me.getValue())
							.process(sess, _ctx, request, response, pi);
						return; //done
					}
				}
				response.setIntHeader("ZK-Error", response.SC_NOT_FOUND);
					//Don't use sendError since browser might handle it
				log.warning("Unknown path info: "+pi);
			} else {
				process(sess, request, response);
			}
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
		final String errClient = request.getHeader("ZK-Error-Report");
		if (errClient != null)
			if (log.debugable()) log.debug("Error found at client: "+errClient+"\n"+Servlets.getDetail(request));

		//parse desktop ID
		final String dtid = request.getParameter("dtid");
		if (dtid == null) {
			//Bug 1929139: incomplete request (IE only)
			final boolean ie = Servlets.isExplorer(request);
			if (!ie || log.debugable()) {
				final String msg = "Incomplete request\n"+Servlets.getDetail(request);
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
			final String cmdId = request.getParameter("cmd.0");
			if (!"rmDesktop".equals(cmdId))
				desktop = recover(sess, request, response, wappc, dtid);

			if (desktop == null) {
				sessionTimeout(request, response, dtid, cmdId);
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
		final Configuration config = wapp.getConfiguration();
		final boolean timerKeepAlive = config.isTimerKeepAlive();
		boolean keepAlive = false;
		try {
			for (int j = 0;; ++j) {
				final String cmdId = request.getParameter("cmd."+j);
				if (cmdId == null)
					break;

				keepAlive = keepAlive
					|| (!(!timerKeepAlive && Events.ON_TIMER.equals(cmdId))
						&& !"dummy".equals(cmdId));
					//dummy is used for PollingServerPush for piggyback

				final String uuid = request.getParameter("uuid."+j);
				final String[] data = request.getParameterValues("data."+j);
				if (data != null) {
					for (int k = data.length; --k >= 0;)
						if ("_z~nil".equals(data[k]))
							data[k] = null;
				}
				aureqs.add(uuid == null || uuid.length() == 0 ? 
					new AuRequest(desktop, AuRequest.getCommand(cmdId), data):
					new AuRequest(desktop, uuid, cmdId, data));
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

		((SessionCtrl)sess).notifyClientRequest(keepAlive);

//		if (log.debugable()) log.debug("AU request: "+aureqs);
		final PerformanceMeter pfmeter = config.getPerformanceMeter();

		final Execution exec = 
			new ExecutionImpl(_ctx, request, response, desktop, null);
		if (sid != null)
			((ExecutionCtrl)exec).setRequestId(sid);
		final AuWriter out = AuWriters.newInstance()
			.open(request, response,
				desktop.getDevice().isSupported(Device.RESEND) ?
					config.getResendDelay() / 2 - 500: 0);
				//Note: getResendDelay() might return nonpositive
		final Collection pfrqids = wappc.getUiEngine().execUpdate(exec, aureqs,
			pfmeter != null ? meterAuStart(pfmeter, request, exec): null,
			out);

		if (pfrqids != null && pfmeter != null)
			meterAuComplete(pfmeter, response, pfrqids, exec);

		out.close(request, response);
	}
	private void sessionTimeout(HttpServletRequest request,
	HttpServletResponse response, String dtid, String cmdId)
	throws ServletException, IOException {
		final String sid = request.getHeader("ZK-SID");
		if (sid != null)
			response.setHeader("ZK-SID", sid);

		final AuWriter out = AuWriters.newInstance().open(request, response, 0);

		if (!"rmDesktop".equals(cmdId) && !Events.ON_RENDER.equals(cmdId)
		&& !Events.ON_TIMER.equals(cmdId) && !"dummy".equals(cmdId)) {//possible in FF due to cache
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
						.getDesktop(sess, request, response, null, true);
					if (desktop == null) //forward or redirect
						throw new IllegalStateException("sendRediect or forward not allowed in recovering");

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
		final AuWriter out = AuWriters.newInstance().open(request, response, 0);
		out.write(new AuAlert(errmsg));
		out.close(request, response);
	}

	/** Handles the start of AU request for performance measurement.
	 *
	 * @return the request ID from the ZK-Client-Start header,
	 * or null if not found.
	 */
	private static String meterAuStart(PerformanceMeter pfmeter,
	HttpServletRequest request, Execution exec) {
		//Format of ZK-Client-Complete and ZK-Client-Receive:
		//	request-id1=time1,request-id2=time2
		String hdr = request.getHeader("ZK-Client-Receive");
		if (hdr != null) meterAuClient(pfmeter, exec, hdr, false);
		hdr = request.getHeader("ZK-Client-Complete");
		if (hdr != null) meterAuClient(pfmeter, exec, hdr, true);

		//Format of ZK-Client-Start:
		//	request-id=time
		hdr = request.getHeader("ZK-Client-Start");
		if (hdr != null) {
			final int j = hdr.lastIndexOf('=');
			if (j > 0) {
				final String pfrqid = hdr.substring(0, j);
				try {
					pfmeter.requestStartAtClient(pfrqid, exec,
						Long.parseLong(hdr.substring(j + 1)));
					pfmeter.requestStartAtServer(pfrqid, exec,
						System.currentTimeMillis());
				} catch (NumberFormatException ex) {
					log.warning("Ingored: failed to parse ZK-Client-Start, "+hdr);
				} catch (Throwable ex) {
					log.warning("Ingored: failed to invoke "+pfmeter, ex);
				}
				return pfrqid;
			}
		}
		return null;
	}
	private static void meterAuClient(PerformanceMeter pfmeter,
	Execution exec, String hdr, boolean complete) {
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
						String pfReqId = z >= 0 ? ids.substring(y, z):
							y == 0 ? ids: ids.substring(y);
						if (complete)
							pfmeter.requestCompleteAtClient(pfReqId, exec, time);
						else
							pfmeter.requestReceiveAtClient(pfReqId, exec, time);

						if (z < 0) break; //done
						y = z + 1;
					}
				} catch (NumberFormatException ex) {
					log.warning("Ingored: unable to parse "+ids);
				} catch (Throwable ex) {
					//backward compatibile: requestReceiveAtClient added since 3.0.8
					if (complete || !(ex instanceof AbstractMethodError))
						log.warning("Ingored: failed to invoke "+pfmeter, ex);
				}
			}

			if (k < 0) break; //done
			j = k + 1;
		}
	}
	/** Handles the complete of the AU request for the performance measurement.
	 * It sets the ZK-Client-Complete header.
	 *
	 * @param pfrqids a collection of request IDs that are processed
	 * at the server
	 */
	private static void meterAuComplete(PerformanceMeter pfmeter,
	HttpServletResponse response, Collection pfrqids, Execution exec) {
		final StringBuffer sb = new StringBuffer(256);
		long time = System.currentTimeMillis();
		for (Iterator it = pfrqids.iterator(); it.hasNext();) {
			final String pfrqid = (String)it.next();
			if (sb.length() > 0) sb.append(' ');
			sb.append(pfrqid);

			try {
				pfmeter.requestCompleteAtServer(pfrqid, exec, time);
			} catch (Throwable ex) {
				log.warning("Ingored: failed to invoke "+pfmeter, ex);
			}
		}

		response.setHeader("ZK-Client-Complete", sb.toString());
			//tell the client what are completed
	}
}
