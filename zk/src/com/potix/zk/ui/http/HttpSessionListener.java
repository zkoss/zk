/* HttpSessionListener.java

{{IS_NOTE
	$Id: HttpSessionListener.java,v 1.3 2006/03/10 09:37:27 tomyeh Exp $
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
package com.potix.zk.ui.http;

import javax.servlet.http.HttpSessionEvent;

/**
 * Used to clean up desktops that a session owns.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/03/10 09:37:27 $
 */
public class HttpSessionListener
implements javax.servlet.http.HttpSessionListener {
	public void sessionCreated(HttpSessionEvent se) {
	}
	public void sessionDestroyed(HttpSessionEvent se) {
		DHtmlLayoutServlet.onSessionDestroyed(se.getSession());
	}
}
