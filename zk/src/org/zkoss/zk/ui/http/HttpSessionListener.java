/* HttpSessionListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Nov 21 19:22:15     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.zkoss.zk.ui.impl.Attributes;

/**
 * Used to clean up desktops that a session owns.
 *
 * @author tomyeh
 */
public class HttpSessionListener
implements javax.servlet.http.HttpSessionListener {
	public void sessionCreated(HttpSessionEvent se) {
	}
	public void sessionDestroyed(HttpSessionEvent se) {
		//Note: Session Fixation Protection (such as Spring Security)
		//might invalidate HTTP session and restore with a new one.
		//Thus, we use an attribute to denote this case and avoid the callback
		final HttpSession hsess = se.getSession();
		if (hsess.getAttribute(Attributes.RENEW_NATIVE_SESSION) == null)
			WebManager.sessionDestroyed(hsess);
	}
}
