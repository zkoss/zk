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
	/** Returns whether the information about the important events shall be generated
	 * for the given widget class in the given desktop.
	 * The information needs to be generated only once for each widget class
	 * for a given desktop.
	 */
	public static
	boolean shallGenerateImportantEvents(Desktop desktop, String wgtcls) {
		return !(desktop instanceof DesktopImpl) //always gen if unknown
		|| ((DesktopImpl)desktop).shallGenerateImportantEvents(wgtcls);
	}
}
