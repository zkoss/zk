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

import org.zkoss.lang.Strings;

/**
 * Utilities for implementing components.
 *
 * @author tomyeh
 */
public class ComponentsCtrl {
	/** The prefix for auto generated ID. */
	private static final String
		AUTO_ID_PREFIX = "z_", AUTO_PAGE_ID_PREFIX = "z__p",
		ANONYMOUS_ID = "z__i";

	/** Returns the automatically generate component's UUID/ID.
	 */
	public static final String toAutoId(String prefix, int id) {
		final StringBuffer sb = new StringBuffer(16)
			.append(AUTO_ID_PREFIX).append(prefix).append('_');
		Strings.encode(sb, id);
		return sb.toString();
	}
	/** Returns the automatically generated page ID.
	 */
	public static final String toAutoPageId(int id) {
		return Strings.encode(
			new StringBuffer(12).append(AUTO_PAGE_ID_PREFIX),
			id).toString();
	}
	/** Returns the anonymous UUID.
	 */
	public static final String getAnonymousId() {
		return ANONYMOUS_ID;
	}

	/** Returns whether an ID is generated automatically.
	 */
	public static final boolean isAutoId(String id) {
		return id.startsWith(AUTO_ID_PREFIX)
			&& id.indexOf('_', AUTO_ID_PREFIX.length()) > 0;
	}
	/** Returns whether an ID is a valid UUID. */
	public static final boolean isUuid(String id) {
		return isAutoId(id);
	}
}
