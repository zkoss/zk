/* CometProcessor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  6 16:02:22     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.comet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ServerPush;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zk.au.http.AuProcessor;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;

/**
 * Used with {@link CometServerPush} to handle the comet requests
 * sent from the client.
 *
 * <p>Note: there is only one instance of {@link CometProcessor} in
 * one application, while {@link CometServerPush} is one instance
 * for each desktop (if turned on).
 *
 * @author tomyeh
 * @since 3.5.0
 */
/*package*/ class CometProcessor implements AuProcessor {
	private CometProcessor() {
	}

	/** Initializes {@link CometProcess} for the specified application.
	 * Notice: the second invocation with the same application has no effect at all.
	 */
	/*package*/ static void init(WebApp wapp) {
		if (DHtmlUpdateServlet.getAuProcessor(wapp, "/comet") == null)
			DHtmlUpdateServlet
				.addAuProcessor(wapp, "/comet", new CometProcessor());
	}

	//AuProcessor//
	public void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, String pi)
	throws ServletException, IOException {
		if (sess == null) {
			response.setIntHeader("ZK-Error", response.SC_GONE); //denote timeout
				//Not use response.sendError to avoid being processed by browser
			return;
		}

		final SessionCtrl sessCtrl = (SessionCtrl)sess;
		sessCtrl.notifyClientRequest(false);
			//note: it won't invalidate it now but set a flag

		process0(sess, request, response);

		if (sessCtrl.isInvalidated())
			sessCtrl.invalidateNow();
	}
	private void process0(Session sess,
	HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		//Note: we don't use sendError since server will convert it
		//to HTML to show the error (which is meaningless to Client Engine)
		final String dtid = (String)request.getParameter("dtid");
		if (dtid == null || dtid.length() == 0) {
			response.setHeader("ZK-Comet-Error", "dtid is required");
			return;
		}

		final Desktop desktop = ((WebAppCtrl)sess.getWebApp())
			.getDesktopCache(sess).getDesktopIfAny(dtid);
		if (desktop == null) {
			response.setHeader("ZK-Comet-Error", "Desktop not found, "+dtid);
			return;
		}

		final DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
		final ServerPush sp = desktopCtrl.getServerPush();
		if (!(sp instanceof CometServerPush)) {
			response.setHeader("ZK-Comet-Error", "Disabled");
			return;
		}

		final CometServerPush cmsp = (CometServerPush)sp;
		if (cmsp.setBusy()) {
			response.setHeader("ZK-Comet-Error", "Busy");
			return;
		}

		final Execution exec = new ExecutionImpl(
			(ServletContext)desktop.getWebApp().getNativeContext(),
			request, response, desktop, null);

		final String sid = request.getHeader("ZK-SID");
		if (sid != null) { //Mobile client doesn't have ZK-SID
			response.setHeader("ZK-SID", sid);
			((ExecutionCtrl)exec).setRequestId(sid);
		}

		final CometAuWriter out = new CometAuWriter();
		out.open(request, response, -1);
		cmsp.process(exec, out);
		out.close(request, response);
	}
}
