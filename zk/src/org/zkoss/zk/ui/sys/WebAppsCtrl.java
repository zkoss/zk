/* WebAppsCtrl.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  7 17:41:50 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;

/**
 * An additional utilities for implementation related to {@link WebApp}.
 * 
 * @author tomyeh
 * @since 5.0.9
 */
public class WebAppsCtrl extends WebApps {
	/** Sets the Web application for this installation.
	 * @see WebApps#getCurrent
	 * @since 5.0.9
	 */
	public static final void setCurrent(WebApp wapp) {
		_wapp = wapp;
	}
}
