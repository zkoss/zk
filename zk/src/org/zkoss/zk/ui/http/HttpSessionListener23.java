/* HttpSessionListener23.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan  5 15:48:14 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.http;

import javax.servlet.*;
import javax.servlet.http.*;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.Attributes;

/**
 * The listener works with Servlet 2.3.
 * For servers that support Servlet 2.4 or later, please use
 * {@link HttpSessionListener} instead.
 *
 * <p>With this listener, there is no way to detect the modification of
 * the request attributes.
 * @author tomyeh
 * @since 5.0.0
 */
public class HttpSessionListener23 implements
javax.servlet.http.HttpSessionListener, 
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

	/*package*/ static boolean shallIgnore(String name) {
		return name.startsWith("javax.zkoss") || name.startsWith("org.zkoss");
	}
}

