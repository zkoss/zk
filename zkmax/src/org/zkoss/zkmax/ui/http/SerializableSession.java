/* SerializableSession.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 11:19:36     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.http;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.http.SimpleSession;

/**
 * Serializable {@link org.zkoss.zk.ui.Session}.
 *
 * @author tomyeh
 * @since 5.0.0
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
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		readThis(s);
	}
}
