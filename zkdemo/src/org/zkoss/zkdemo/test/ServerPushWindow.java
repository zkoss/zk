/* ServerPushWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug  6 12:37:13     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zul.Window;

/**
 * Used to test the server-push feature.
 *
 * @author tomyeh
 */
public class ServerPushWindow extends Window {
	public void onCreate() {
		getDesktop().enableServerPush(true);
	}
}
