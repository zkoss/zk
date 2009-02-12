/* Timer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 17 17:03:17     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.client;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl},
 * if a component supports the one-shot timer.
 * 
 * @author tomyeh
 * @since 3.0.2
 */
public interface Timer {
	/** Called when the timer request is received.
	 */
	public void onTimer();
}
