/* SerializableSession.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  6 11:19:36     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.http;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.sys.WebAppCtrl;

/**
 * Serializable {@link com.potix.zk.ui.Session}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SerializableSession extends SimpleSession
implements HttpSessionActivationListener, java.io.Serializable {
    private static final long serialVersionUID = 20060706L;

	public SerializableSession(WebApp wapp, HttpSession hsess,
	String clientAddr, String clientHost) {
		super(wapp, hsess, clientAddr, clientHost);
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
