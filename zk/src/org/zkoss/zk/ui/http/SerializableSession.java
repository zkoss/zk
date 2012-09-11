/* SerializableSession.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 11:19:36     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import org.zkoss.zk.ui.WebApp;

/**
 * Serializable {@link org.zkoss.zk.ui.Session}.
 *
 * @author tomyeh
 */
public class SerializableSession extends SimpleSession
implements HttpSessionActivationListener, java.io.Serializable {
	private static final long serialVersionUID = 20080421L;

	/** Constructor.
	 *
	 * @param request the original request causing this session to be created.
	 * If HTTP and servlet, it is javax.servlet.http.HttpServletRequest.
	 * If portlet, it is javax.portlet.RenderRequest.
	 * @since 3.0.1
	 */
	public SerializableSession(WebApp wapp, HttpSession hsess, Object request) {
		super(wapp, hsess, request);
	}

	/** Constructs a ZK session with either a HTTP session or a Portlet session.
	 *
	 * @param hsess the original session, either an instance of
	 * HttpSession or PortletSession.
	 * Notice: we don't declare PortletSession in API
	 * to avoid this class failed to be loaded in some system (without
	 * portlet-api.jar)
	 * @param request the original request causing this session to be created.
	 * If portlet, it is javax.portlet.RenderRequest.
	 * @since 6.0.3
	 */
	public SerializableSession(WebApp wapp, Object hsess, Object request) {
		super(wapp, hsess, request);
	}
	//-- HttpSessionActivationListener --//
	public void sessionWillPassivate(HttpSessionEvent se) {
		sessionWillPassivate();
	}
	public void sessionDidActivate(HttpSessionEvent se) {
		sessionDidActivate(se.getSession());
	}

	//Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();
		writeThis(s);
	}
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		readThis(s);
	}
}
