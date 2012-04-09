/* SessionAgent.java

	History:
		Fri, Apr 06, 2012  9:06:37 PM, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import javax.servlet.http.HttpSession;
import javax.portlet.PortletSession;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.Attributes;

/**
 * Used to handle the session's attribute that works
 * with both portlet and servlet.
 *
 * @author tomyeh
 * @since 5.0.12
 */
/*package*/ class SessionAgent {
	/** Puts a ZK session to the cache.
	 * You can retrieve the native session by {@link Session#getNativeSession}.
	 * @param sess the ZK session.
	 */
	public void put(Session sess) {
		((HttpSession)sess.getNativeSession()).setAttribute(Attributes.ZK_SESSION, sess);
	}
	/** Retrieves a ZK session from the cache, or null if the ZK session
	 * is not stored.
	 */
	public Session get(Object navsess) {
		return (Session)((HttpSession)navsess).getAttribute(Attributes.ZK_SESSION);
	}
	/** Removes the ZK session from the cache.
	 */
	public void remove(Session sess) {
			((HttpSession)sess.getNativeSession()).removeAttribute(Attributes.ZK_SESSION);
	}

	/*package*/ static final SessionAgent agent;
	static {
		SessionAgent sa;
		try {
			Classes.forNameByThread("javax.portlet.PortletSession");
			sa = new SessionAgent() {
				public void put(Session sess) {
					//ZK-1029: WebSphere portal's PortletSession also implements HttpSession,
					//so we have to check PortletSession first
					final Object navsess = sess.getNativeSession();
					if (navsess instanceof PortletSession)
						((PortletSession)navsess).setAttribute(Attributes.ZK_SESSION, sess, PortletSession.APPLICATION_SCOPE);
					else
						((HttpSession)navsess).setAttribute(Attributes.ZK_SESSION, sess);
				}
				public Session get(Object navsess) {
					return (Session)(navsess instanceof PortletSession ?
							((PortletSession)navsess).getAttribute(Attributes.ZK_SESSION, PortletSession.APPLICATION_SCOPE):
						((HttpSession)navsess).getAttribute(Attributes.ZK_SESSION));
				}
				public void remove(Session sess) {
					final Object navsess = sess.getNativeSession();
					if (navsess instanceof PortletSession)
						((PortletSession)navsess).removeAttribute(Attributes.ZK_SESSION, PortletSession.APPLICATION_SCOPE);
					else
						((HttpSession)navsess).removeAttribute(Attributes.ZK_SESSION);
				}
			};
		} catch (Throwable t) {
			sa = new SessionAgent();
		}
		agent = sa;
	}
}
