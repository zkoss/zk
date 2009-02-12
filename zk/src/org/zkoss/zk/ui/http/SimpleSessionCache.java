/* SimpleSessionCache.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 21 11:29:27     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import javax.servlet.http.HttpSession;
import javax.portlet.PortletSession;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionCache;

/**
 * A simple implementation of {@link SessionCache}.
 * @author tomyeh
 * @since 3.0.5
 */
public class SimpleSessionCache implements SessionCache {
	/** A session attribute used to store the ZK session in the native session. */
	private static final String ATTR_SESS = "javax.zkoss.zk.ui.Session";

	public void init(WebApp wapp) {
	}
	public void destroy(WebApp wapp) {
	}

	public void put(Session sess) {
		final Object navsess = sess.getNativeSession();
		if (navsess instanceof HttpSession)
			((HttpSession)navsess).setAttribute(ATTR_SESS, sess);
		else
			((PortletSession)navsess).setAttribute(ATTR_SESS, sess, PortletSession.APPLICATION_SCOPE);
	}
	public Session get(Object navsess) {
		return (Session)(navsess instanceof HttpSession ?
			((HttpSession)navsess).getAttribute(ATTR_SESS):
			((PortletSession)navsess).getAttribute(ATTR_SESS, PortletSession.APPLICATION_SCOPE));
	}
	public void remove(Session sess) {
		final Object navsess = sess.getNativeSession();
		if (navsess instanceof HttpSession)
			((HttpSession)navsess).removeAttribute(ATTR_SESS);
		else
			((PortletSession)navsess).removeAttribute(ATTR_SESS, PortletSession.APPLICATION_SCOPE);
	}
}
