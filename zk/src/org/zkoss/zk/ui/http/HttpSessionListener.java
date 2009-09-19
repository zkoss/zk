/* HttpSessionListener.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 21 19:22:15     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import javax.servlet.*;
import javax.servlet.http.*;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.impl.Attributes;

/**
 * Used to clean up desktops that a session owns.
 *
 * @author tomyeh
 */
public class HttpSessionListener implements
javax.servlet.http.HttpSessionListener, ServletRequestAttributeListener,
HttpSessionAttributeListener, ServletContextAttributeListener {
	//HttpSessionListener//
	public void sessionCreated(HttpSessionEvent evt) {
	}
	public void sessionDestroyed(HttpSessionEvent evt) {
		//Note: Session Fixation Protection (such as Spring Security)
		//might invalidate HTTP session and restore with a new one.
		//Thus, we use an attribute to denote this case and avoid the callback
		final HttpSession hsess = evt.getSession();
		if (hsess.getAttribute(Attributes.RENEW_NATIVE_SESSION) == null)
			WebManager.sessionDestroyed(hsess);
	}

	//HttpSessionAttributeListener//
	public void attributeAdded(HttpSessionBindingEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final Session sess = Sessions.getCurrent();
			if (sess instanceof SimpleSession
			&& evt.getSession().equals(sess.getNativeSession()))
				((SimpleSession)sess).getScopeListeners()
					.notifyAdded(name, evt.getValue());
		}
	}
	public void attributeRemoved(HttpSessionBindingEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final Session sess = Sessions.getCurrent();
			if (sess instanceof SimpleSession
			&& evt.getSession().equals(sess.getNativeSession()))
				((SimpleSession)sess).getScopeListeners()
					.notifyRemoved(name);
		}
	}
	public void attributeReplaced(HttpSessionBindingEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final Session sess = Sessions.getCurrent();
			if (sess instanceof SimpleSession
			&& evt.getSession().equals(sess.getNativeSession()))
				((SimpleSession)sess).getScopeListeners()
					.notifyReplaced(name, evt.getValue());
		}
	}

	//ServletContextAttributeListener//
	public void attributeAdded(ServletContextAttributeEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final WebApp wapp = WebManager.getWebAppIfAny(evt.getServletContext());
			if (wapp instanceof SimpleWebApp)
				((SimpleWebApp)wapp).getScopeListeners()
					.notifyAdded(name, evt.getValue());
		}
	}
	public void attributeRemoved(ServletContextAttributeEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final WebApp wapp = WebManager.getWebAppIfAny(evt.getServletContext());
			if (wapp instanceof SimpleWebApp)
				((SimpleWebApp)wapp).getScopeListeners()
					.notifyRemoved(name);
		}
	}
	public void attributeReplaced(ServletContextAttributeEvent evt) {
		final String name = evt.getName();
		if (!shallIgnore(name)) {
			final WebApp wapp = WebManager.getWebAppIfAny(evt.getServletContext());
			if (wapp instanceof SimpleWebApp)
				((SimpleWebApp)wapp).getScopeListeners()
					.notifyReplaced(name, evt.getValue());
		}
	}

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
	private static boolean shallIgnore(String name) {
		return name.startsWith("javax.zkoss") || name.startsWith("org.zkoss");
	}
}
