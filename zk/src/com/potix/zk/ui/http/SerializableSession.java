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
		((WebAppCtrl)_wapp).sessionWillPassivate(this);
	}
	public void sessionDidActivate(HttpSessionEvent se) {
		//Note: in Tomcat, servlet is activated later, so we have to
		//add listener to WebManager instead of process now
		_hsess = se.getSession();

		WebManager.addListener(
			_hsess.getServletContext(),
			new ActivationListener() {
				public void onActivated(WebManager webman) {
					_wapp = webman.getWebApp();
					((WebAppCtrl)_wapp)
						.sessionDidActivate(SerializableSession.this);
				}
			});
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		initAttrs();
	}
}
