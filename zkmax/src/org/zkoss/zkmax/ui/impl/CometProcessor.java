/* CometProcessor.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  6 16:02:22     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.impl;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Desktop;
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
 * @since 3.1.0
 */
/*package*/ class CometProcessor implements AuProcessor {
	private CometProcessor() {
	}

	/** Initializes {@link CometProcess} for the specified application.
	 * Notice: the second invocation with the same application has no effect at all.
	 */
	/*package*/ static void init(WebApp wapp) {
		final String STARTED = "org.zkoss.zkmax.ui.impl.CometStarted";
		if (wapp.getAttribute(STARTED) == null) {
			DHtmlUpdateServlet
				.addAuProcessor(wapp, "/comet", new CometProcessor());
			wapp.setAttribute(STARTED, Boolean.TRUE);
		}
	}

	//AuProcessor//
	public void process(Session sess, ServletContext ctx,
	HttpServletRequest request, HttpServletResponse response, String pi)
	throws ServletException, IOException {
		final String dtid = (String)request.getParameter("dtid");
		if (dtid == null || dtid.length() == 0) {
			response.sendError(response.SC_BAD_REQUEST, "dtid is required");
			return;
		}

		final Desktop desktop = ((WebAppCtrl)sess.getWebApp())
			.getDesktopCache(sess).getDesktopIfAny(dtid);
		if (desktop == null) {
			response.sendError(response.SC_GONE, "Desktop not found, "+dtid);
			return;
		}

		final ServerPush sp = ((DesktopCtrl)desktop).getServerPush();
		if (!(sp instanceof CometServerPush)) {
			response.sendError(response.SC_GONE, "Server push disabled");
			return;
		}

		((CometServerPush)sp).process(
			new ExecutionImpl(
				(ServletContext)desktop.getWebApp().getNativeContext(),
				request, response, desktop, null),
			new CometAuWriter());
	}
}
