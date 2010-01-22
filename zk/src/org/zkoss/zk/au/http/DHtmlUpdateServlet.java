/* DHtmlUpdateServlet.java

	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:11:28     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
import javax.servlet.http.HttpSession;

import org.zkoss.mesg.Messages;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.json.JSONValue;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.Charsets;
import org.zkoss.web.servlet.http.Https;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.util.resource.ClassWebResource;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.URIInfo;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.http.SessionResolverImpl;
import org.zkoss.zk.ui.http.I18Ns;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;
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
 * <dt>compress</dt>
 * <dd>It specifies whether to compress the output if the browser supports the compression (Accept-Encoding).</dd>
 * <dt>extension0, extension1...</dt>
 * <dd>It specifies an AU extension ({@link AuExtension}).
 * The <code>extension0</code> parameter specifies
 * the first AU extension, the <code>extension1</code> parameter the second AU extension,
 * and so on.<br/>
 * The syntax of the value is<br/>
 * <code>/prefix=class</code>
 * </dd>
 * </dl>
 *
 * <p>By default: there are two extensions are associated with
 * "/upload" and "/view" (see {@link #addAuExtension}.
 * Also, "/web" is reserved. Don't associate to any AU extension.
 *
 * @author tomyeh
 */
public class DHtmlUpdateServlet extends HttpServlet {
	private static final Log log = Log.lookup(DHtmlUpdateServlet.class);
	private static final String ATTR_UPDATE_SERVLET
		= "org.zkoss.zk.au.http.updateServlet";
	private static final String ATTR_AU_PROCESSORS
		= "org.zkoss.zk.au.http.auProcessors";

	private ServletContext _ctx;
	private long _lastModified;
	/** (String name, AuExtension). */
	private Map _aues = new HashMap(8);
	private boolean _compress = true;

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
		_ctx.setAttribute(ATTR_UPDATE_SERVLET, this);

		final WebManager webman = WebManager.getWebManager(_ctx);
		String param = config.getInitParameter("compress");
		_compress = param == null || param.length() == 0 || "true".equals(param);
		if (!_compress)
			webman.getClassWebResource().setCompress(null); //disable all

		//Copies au extensions defined before DHtmlUpdateServlet is started
		final WebApp wapp = webman.getWebApp();
		final Map aues = (Map)wapp.getAttribute(ATTR_AU_PROCESSORS);
		if (aues != null) {
			for (Iterator it = aues.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				addAuExtension((String)me.getKey(), (AuExtension)me.getValue());
			}
			wapp.removeAttribute(ATTR_AU_PROCESSORS);
		}

		//ZK 5: extension defined in init-param has the higher priority
		for (int j = 0;;) {
			param = config.getInitParameter("extension" + j++);
			if (param == null) {
				param = config.getInitParameter("processor" + j++); //backward compatible
				if (param == null) break;
			}
			final int k = param.indexOf('=');
			if (k < 0) {
				log.warning("Ignore init-param: illegal format, "+param);
				continue;
			}

			final String prefix = param.substring(0, k).trim();
			final String clsnm = param.substring(k + 1).trim();
			try {
				addAuExtension(prefix,
					(AuExtension)Classes.newInstanceByThread(clsnm));
			} catch (ClassNotFoundException ex) {
				log.warning("Ignore init-param: class not found, "+clsnm);
			} catch (ClassCastException ex) {
				log.warning("Ignore: "+clsnm+" not implement "+AuExtension.class);
			} catch (Throwable ex) {
				log.warning("Ignore init-param: failed to add an AU extension, "+param,
					ex);
			}
		}

		if (getAuExtension("/upload") == null) {
			try {
				addAuExtension("/upload", new AuUploader());
			} catch (Throwable ex) {
				final String msg =
					"Make sure commons-fileupload.jar is installed.";
				log.warningBriefly("Failed to configure fileupload. "+msg, ex);

				//still add /upload to generate exception when fileupload is used
				addAuExtension("/upload",
					new AuExtension() {
						public void init(DHtmlUpdateServlet servlet) {
						}
						public void destroy() {
						}
						public void service(HttpServletRequest request, HttpServletResponse response, String pi)
						throws ServletException, IOException {
							if (Sessions.getCurrent(false) != null)
								throw new ServletException("Failed to upload. "+msg);
						}
					});
			}
		}

		if (getAuExtension("/view") == null)
			addAuExtension("/view", new AuDynaMediar());
	}
	public void destroy() {
		for (Iterator it = _aues.values().iterator(); it.hasNext();) {
			final AuExtension aue = (AuExtension)it.next();
			try {
				aue.destroy();
			} catch (Throwable ex) {
				log.warningBriefly("Unable to stop "+aue, ex);
			}
		}
	}
	public ServletContext getServletContext() {
		return _ctx;
	}

	/* Returns whether to compress the output.
	 * @since 5.0.0
	 */
	public boolean isCompress() {
		return _compress;
	}

	/** Returns the AU extension that is associated the specified prefix.
	 * @since 5.0.0
	 */
	public static final AuExtension getAuExtension(WebApp wapp, String prefix) {
		DHtmlUpdateServlet upsv = DHtmlUpdateServlet.getUpdateServlet(wapp);
		return upsv != null ? upsv.getAuExtension(prefix): null;
	}
	/** Adds an AU extension and associates it with the specified prefix,
	 * even before {@link DHtmlUpdateServlet} is started.
	 * <p>Unlike {@link #addAuExtension(String, AuExtension)}, it can be called
	 * even if the update servlet is not loaded yet ({@link #getUpdateServlet}
	 * returns null).
	 *
	 * <p>If there was an AU extension associated with the same name, the
	 * the old AU extension will be replaced.
	 * @since 5.0.0
	 */
	public static final AuExtension
	addAuExtension(WebApp wapp, String prefix, AuExtension extension)
	throws ServletException {
		DHtmlUpdateServlet upsv = DHtmlUpdateServlet.getUpdateServlet(wapp);
		if (upsv == null) {
			synchronized (DHtmlUpdateServlet.class) {
				upsv = DHtmlUpdateServlet.getUpdateServlet(wapp);
				if (upsv == null) {
					checkAuExtension(prefix, extension);
					Map aues = (Map)wapp.getAttribute(ATTR_AU_PROCESSORS);
					if (aues == null)
						wapp.setAttribute(ATTR_AU_PROCESSORS, aues = new HashMap(4));
					return (AuExtension)aues.put(prefix, extension);
				}
			}
		}

		return upsv.addAuExtension(prefix, extension);
	}
	/** Adds an AU extension and associates it with the specified prefix.
	 *
	 * <p>If there was an AU extension associated with the same name, the
	 * the old AU extension will be replaced.
	 *
	 * <p>If you want to add an Au extension, even before DHtmlUpdateServlet
	 * is started, use {@link #addAuExtension(WebApp, String, AuExtension)}
	 * instead.
	 *
	 * @param prefix the prefix. It must start with "/", but it cannot be
	 * "/" nor "/web" (which are reserved).
	 * @param extension the AU extension (never null).
	 * @return the previous AU extension associated with the specified prefix,
	 * or null if the prefix was not associated before.
	 * @see #addAuExtension(WebApp,String,AuExtension)
	 * @since 5.0.0
	 */
	public AuExtension addAuExtension(String prefix, AuExtension extension)
	throws ServletException {
		checkAuExtension(prefix, extension);

		if (_aues.get(prefix) ==  extension) //speed up to avoid sync
			return extension; //nothing changed

		extension.init(this);

		//To avoid using sync in doGet(), we make a copy here
		final AuExtension old;
		synchronized (this) {
			final Map ps = new HashMap(_aues);
			old = (AuExtension)ps.put(prefix, extension);
			_aues = ps;
		}
		if (old != null)
			try {
				old.destroy();
			} catch (Throwable ex) {
				log.warningBriefly("Unable to stop "+old, ex);
			}
		return old;
	}
	private static void checkAuExtension(String prefix, AuExtension extension) {
		if (prefix == null || !prefix.startsWith("/") || prefix.length() < 2
		|| extension == null)
			throw new IllegalArgumentException();
		if (ClassWebResource.PATH_PREFIX.equalsIgnoreCase(prefix))
			throw new IllegalArgumentException(
				ClassWebResource.PATH_PREFIX + " is reserved");
	}
	/** Returns the AU extension associated with the specified prefix,
	 * or null if no AU extension associated.
	 * @since 5.0.0
	 */
	public AuExtension getAuExtension(String prefix) {
		return (AuExtension)_aues.get(prefix);
	}
	/** Returns the first AU extension matches the specified path,
	 * or null if not found.
	 */
	private AuExtension getAuExtensionByPath(String path) {
		for (Iterator it = _aues.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			if (path.startsWith((String)me.getKey()))
				return (AuExtension)me.getValue();
		}
		return null;
	}

	//-- super --//
	protected long getLastModified(HttpServletRequest request) {
		final String pi = Https.getThisPathInfo(request);
		if (pi != null && pi.startsWith(ClassWebResource.PATH_PREFIX)
		&& pi.indexOf('*') < 0 //language independent
		&& !Servlets.isIncluded(request)) {
			//If a resource extension is registered for the extension,
			//we assume the content is dynamic
			final String ext = Servlets.getExtension(pi, false);
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

		final boolean withpi = pi != null && pi.length() != 0;
		if (withpi && pi.startsWith(ClassWebResource.PATH_PREFIX)) {
			//use HttpSession to avoid loading SerializableSession in GAE
			//and don't retrieve session if possible
			final ClassWebResource cwr = getClassWebResource();
			final HttpSession sess =
				shallSession(cwr, pi) ? request.getSession(false): null;
			Object oldsess = null;
			if (sess == null) {
				oldsess = SessionsCtrl.getRawCurrent();
				SessionsCtrl.setCurrent(new SessionResolverImpl(_ctx, request));
				//it might be created later
			}

			final Object old = sess != null?
				I18Ns.setup(sess, request, response, "UTF-8"):
				Charsets.setup(null, request, response, "UTF-8");
			try {
				cwr.service(request, response,
						pi.substring(ClassWebResource.PATH_PREFIX.length()));
			} finally {
				if (sess != null) I18Ns.cleanup(request, old);
				else {
					Charsets.cleanup(request, old);
					SessionsCtrl.setRawCurrent(oldsess);
				}
			}
			return; //done
		}

		final Session sess = WebManager.getSession(_ctx, request, false);
		if (withpi) {
			final AuExtension aue = getAuExtensionByPath(pi);
			if (aue == null) {
				response.setIntHeader("ZK-Error", response.SC_NOT_FOUND);
					//Don't use sendError since browser might handle it
				log.warning("Unknown path info: "+pi);
				return;
			}

			Object oldsess = null;
			if (sess == null) {
				oldsess = SessionsCtrl.getRawCurrent();
				SessionsCtrl.setCurrent(new SessionResolverImpl(_ctx, request));
				//it might be created later
			}

			final Object old = sess != null?
				I18Ns.setup(sess, request, response, "UTF-8"):
				Charsets.setup(null, request, response, "UTF-8");
			try {
				aue.service(request, response, pi);
			} finally {
				if (sess != null) I18Ns.cleanup(request, old);
				else {
					Charsets.cleanup(request, old);
					SessionsCtrl.setRawCurrent(oldsess);
				}
			}
			return; //done
		}

		//AU
		if (sess == null) {
			response.setIntHeader("ZK-Error", response.SC_GONE); //denote timeout
			//Bug 1849088: rmDesktop might be sent after invalidate
			//Bug 1859776: need send response to client for redirect or others
			final String dtid = request.getParameter("dtid");
			if (dtid != null)
				sessionTimeout(request, response,
					WebManager.getWebManager(_ctx).getWebApp().getConfiguration(),
					dtid);
			return;
		}

		final Object old = I18Ns.setup(sess, request, response, "UTF-8");
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
	private static boolean shallSession(ClassWebResource cwr, String pi) {
		return cwr.getExtendlet(Servlets.getExtension(pi, false)) != null;
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
			if (log.debugable()) {
				final String msg = "Incomplete request\n"+Servlets.getDetail(request);
				log.debug(msg);
			}

			response.sendError(467, "Incomplete request");
			return;
		}

		final WebApp wapp = sess.getWebApp();
		final WebAppCtrl wappc = (WebAppCtrl)wapp;
		final Configuration config = wapp.getConfiguration();
		Desktop desktop = wappc.getDesktopCache(sess).getDesktopIfAny(dtid);
		if (desktop == null) {
			final String cmdId = request.getParameter("cmd.0");
			if (!"rmDesktop".equals(cmdId))
				desktop = recover(sess, request, response, wappc, dtid);

			if (desktop == null) {
				sessionTimeout(request, response, config, dtid);
				return;
			}
		}
		WebManager.setDesktop(request, desktop);
			//reason: a new page might be created (such as include)

		final String sid = request.getHeader("ZK-SID");
		if (sid != null) //Some client might not have ZK-SID
			response.setHeader("ZK-SID", sid);

		//parse commands
		final List aureqs = new LinkedList();
		final boolean timerKeepAlive = config.isTimerKeepAlive();
		boolean keepAlive = false;
		try {
			for (int j = 0;; ++j) {
				final String cmdId = request.getParameter("cmd_"+j);
				if (cmdId == null)
					break;

				keepAlive = keepAlive
					|| (!(!timerKeepAlive && Events.ON_TIMER.equals(cmdId))
						&& !"dummy".equals(cmdId));
					//dummy is used for PollingServerPush for piggyback

				final String uuid = request.getParameter("uuid_"+j);
				final String data = request.getParameter("data_"+j);
				final Map decdata = (Map)JSONValue.parse(data);
				aureqs.add(uuid == null || uuid.length() == 0 ? 
					new AuRequest(desktop, cmdId, decdata):
					new AuRequest(desktop, uuid, cmdId, decdata));
			}
		} catch (Throwable ex) {
			log.warningBriefly(ex);
			responseError(request, response, Exceptions.getMessage(ex));
			return;
		}

		if (aureqs.isEmpty()) {
			final String errmsg = "Illegal request: cmd required";
			log.debug(errmsg);
			responseError(request, response, errmsg);
			return;
		}

		sess.setAttribute(Attributes.GAE_FIX, new Integer(0)); //enforce GAE to write session
		((SessionCtrl)sess).notifyClientRequest(keepAlive);

//		if (log.debugable()) log.debug("AU request: "+aureqs);
		final Execution exec = 
			new ExecutionImpl(_ctx, request, response, desktop, null);
		if (sid != null)
			((ExecutionCtrl)exec).setRequestId(sid);
		final AuWriter out = AuWriters.newInstance();
		out.setCompress(_compress);
		out.open(request, response,
			desktop.getDevice().isSupported(Device.RESEND) ?
				getProcessTimeout(config.getResendDelay()): 0);
				//Note: getResendDelay() might return nonpositive
		wappc.getUiEngine().execUpdate(exec, aureqs, out);

		out.close(request, response);
	}
	private static int getProcessTimeout(int resendDelay) {
		if (resendDelay > 0) {
			resendDelay = (resendDelay * 3) >> 2;
			if (resendDelay <= 0) resendDelay = 1;
		}
		return resendDelay;
	}
	private void sessionTimeout(HttpServletRequest request,
	HttpServletResponse response, Configuration config, String dtid)
	throws ServletException, IOException {
		final String sid = request.getHeader("ZK-SID");
		if (sid != null)
			response.setHeader("ZK-SID", sid);

		final AuWriter out =
			AuWriters.newInstance().open(request, response, 0);

		for (int j = 0;; ++j) {
			final String cmdId = request.getParameter("cmd_"+j);
			if (cmdId == null)
				break;

			if (!"rmDesktop".equals(cmdId) && !Events.ON_RENDER.equals(cmdId)
			&& !Events.ON_TIMER.equals(cmdId)
			&& !Events.ON_CLIENT_INFO.equals(cmdId)
			&& !Events.ON_MOVE.equals(cmdId)
			&& !Events.ON_SIZE.equals(cmdId)
			&& !Events.ON_Z_INDEX.equals(cmdId)
			&& !("dummy".equals(cmdId)
				&& !isDummyTimeout(request.getParameter("data_"+j)))) {
				final String deviceType = getDeviceType(request);
				URIInfo ui = (URIInfo)config.getTimeoutURI(getDeviceType(request));
				String uri = ui != null ? ui.uri: null;
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
				break; //found
			}
		}

		out.close(request, response);
	}
	/** Tests if the dummy command is used to trigger the timeout. */
	private static boolean isDummyTimeout(String data) {
		if (data != null && data.length() > 0) {
			try {
				return ((Map)JSONValue.parse(data)).containsKey("timeout");
			} catch (Throwable ex) {
				log.warning("Illegal dummy command: "+data);
			}
		}
		return false;
	}

	private static String getDeviceType(HttpServletRequest request) {
		final String agt = request.getHeader("user-agent");
		if (agt != null && agt.length() > 0) {
			try {
				return Devices.getDeviceByClient(agt).getType();
			} catch (Throwable ex) {
				log.warning("Unknown device for "+agt);
			}
		}
		return "ajax";
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
	HttpServletResponse response, String errmsg)
	throws IOException {
		//Don't use sendError because Browser cannot handle UTF-8
		AuWriter out = AuWriters.newInstance().open(request, response, 0);
		out.write(new AuAlert(errmsg));
		out.close(request, response);
	}
}
