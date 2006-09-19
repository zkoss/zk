/* HttpSessionListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Nov 21 19:22:15     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import javax.servlet.http.HttpSessionEvent;

/**
 * Used to clean up desktops that a session owns.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class HttpSessionListener
implements javax.servlet.http.HttpSessionListener {
	public void sessionCreated(HttpSessionEvent se) {
	}
	public void sessionDestroyed(HttpSessionEvent se) {
		WebManager.onSessionDestroyed(se.getSession());
	}
}
