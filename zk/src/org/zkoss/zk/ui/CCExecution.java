/* CCExecution.java

	Purpose:
		
	Description:
		
	History:
		Tue May 12 15:55:52     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui;

import javax.servlet.ServletContext;

import org.zkoss.zk.ui.impl.DesktopImpl;
import org.zkoss.zk.ui.http.WebManager;

/**
 * Used for create components and other temporary tasks.
 * @author tomyeh
 */
/*package*/ class CCExecution extends org.zkoss.zk.ui.http.ExecutionImpl {
	/*package*/ static CCExecution newInstance(WebApp wapp) {
		final ServletContext ctx = (ServletContext)wapp.getNativeContext();
		final String updateURI = WebManager.getWebManager(ctx).getUpdateURI();
		return new CCExecution(ctx,
			new DesktopImpl(wapp, updateURI, "/", null, null));
	}

	private CCExecution(ServletContext ctx, Desktop desktop) {
		super(ctx, null, null, desktop, null);
		setDesktop(desktop);
	}
}
