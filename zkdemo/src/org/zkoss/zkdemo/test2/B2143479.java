/* B2143479.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct  3 10:16:18     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopInit;

/**
 * Used to verify Bug 2143479: whether able to sendRedirect in desktopInit.
 * 
 * @author tomyeh
 */
public class B2143479 implements DesktopInit {
	public void init(Desktop desktop,  Object request) throws Exception {
		final String path = desktop.getRequestPath();
		if (path != null && path.indexOf("B30-2143479.zul") >= 0) {
			System.out.println("sendRedirect "+path);
			Executions.getCurrent().sendRedirect("B30-2143479_1.zul");
			//Executions.getCurrent().forward("B30-2143479-1.zul");
		}
	}
}
