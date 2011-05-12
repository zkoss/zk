/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Thu May  5 11:09:46 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import org.zkoss.zk.ui.Desktop;

/**
 * Utilities for implementation.
 * @author tomyeh
 * @since 5.0.7
 */
public class Utils {
	/** Marks the per-desktop information of the given key will be generated,
	 * and returns true if the information is not generated yet
	 * (i.e., this method is NOT called with the given key).
	 * You could use this method to minimize the bytes to be sent to
	 * the client if the information is required only once per desktop.
	 */
	public static
	boolean markClientInfoPerDesktop(Desktop desktop, String key) {
		return !(desktop instanceof DesktopImpl) //always gen if unknown
		|| ((DesktopImpl)desktop).markClientInfoPerDesktop(key);
	}
}
