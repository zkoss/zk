/* ComponentsCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:41:22     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

/**
 * Utilities for implementing components.
 *
 * @author tomyeh
 */
public class ComponentsCtrl {
	/** The prefix for auto generated ID. */
	public static final String AUTO_ID_PREFIX = "z__",
		AUTO_PAGE_ID_PREFIX = "z__p_";

	/** Returns whether an ID is generated automatically.
	 */
	public static final boolean isAutoId(String id) {
		return id.startsWith(AUTO_ID_PREFIX);
	}
	/** Returns whether an ID is a valid UUID. */
	public static final boolean isUuid(String id) {
		return isAutoId(id);
	}
}
