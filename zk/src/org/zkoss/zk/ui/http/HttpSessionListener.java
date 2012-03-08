/* HttpSessionListener.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 21 19:22:15     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import javax.servlet.*;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;

/**
 * Used to clean up desktops that a session owns.
 *
 * <p>This listener works only with Servlet 2.4 or later.
 * For servers that support only Servlet 2.3, please use
 * {@link HttpSessionListener23} instead.
 * @author tomyeh
 */
public class HttpSessionListener extends HttpSessionListener23
implements ServletRequestAttributeListener, ServletContextListener {
	private static WebManager _webman;
	// ServletRequestAttributeListener//
	public void attributeAdded(ServletRequestAttributeEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final Execution exec = Executions.getCurrent();
			if (exec instanceof ExecutionImpl
			&& evt.getServletRequest().equals(exec.getNativeRequest()))
				((ExecutionImpl)exec).getScopeListeners()
					.notifyAdded(name, evt.getValue());
		}
	}
	public void attributeRemoved(ServletRequestAttributeEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final Execution exec = Executions.getCurrent();
			if (exec instanceof ExecutionImpl
			&& evt.getServletRequest().equals(exec.getNativeRequest()))
				((ExecutionImpl)exec).getScopeListeners()
					.notifyRemoved(name);
		}
	}
	public void attributeReplaced(ServletRequestAttributeEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final Execution exec = Executions.getCurrent();
			if (exec instanceof ExecutionImpl
			&& evt.getServletRequest().equals(exec.getNativeRequest()))
				((ExecutionImpl)exec).getScopeListeners()
					.notifyReplaced(name, evt.getValue());
		}
	}
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
	}
	@Override
	public void contextInitialized(ServletContextEvent event) {
		if (Servlets.isServlet3() && _webman == null) {
			try {
				final ServletContext ctx = event.getServletContext();
				if (ctx.getInitParameter("DHtmlLayoutServlet.update-uri") != null) {
					String updateURI = Utils.checkUpdateURI(
							ctx.getInitParameter("DHtmlLayoutServlet.update-uri"), "The update-uri parameter");
	
					_webman = new WebManager(ctx, updateURI);
				} else
					throw new ServletException("update-uri required!");
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}
	}
	public static WebManager getWebManager () {
		return _webman;
	}
}
