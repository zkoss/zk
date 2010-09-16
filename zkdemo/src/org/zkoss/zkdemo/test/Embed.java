/* Embed.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 16 16:33:21 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkdemo.test;

import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.zkoss.json.JSONArray;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zkplus.embed.Bridge;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;


/**
 * Used with embed.jsp to demostrate how to start an execution in a foreign
 * Ajax channel.
 *
 * @author tomyeh
 * @since 5.0.5
 */
public class Embed extends HttpServlet {
	private int cnt = 0;
	public void service(HttpServletRequest request, HttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {
		final Desktop desktop = getDesktop(request);
		Bridge bridge = Bridge.start(getServletContext(), request, response, desktop);
		try {
			final Listbox listbox = (Listbox) ((Page)desktop.getPages().iterator().next()).getRoots().iterator().next();
			listbox.appendChild(new Listitem("Ajax " + ++cnt));
			response.getWriter().write(bridge.getResult());
		} finally {
			bridge.close();
		}
	}
	private Desktop getDesktop(HttpServletRequest request) {
		return (Desktop)((Map)request.getSession().getAttribute("desktops"))
			.get(request.getParameter("desktop"));
	}
}
