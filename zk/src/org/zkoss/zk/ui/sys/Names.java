/* Names.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug  2 15:03:50     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Utilities about variable's names.
 *
 * @author tomyeh
 */
public class Names {
	private Names() {}
	private static final Set _reves;
	static {
		Set reves = new HashSet();
		final String[] rs = new String[] {
			"arg", "desktop", "event", "page", "self", "session",
			"spaceOwner"
		};
		for (int j = rs.length; --j >=0;)
			reves.add(rs[j]);
		_reves = Collections.unmodifiableSet(reves);
	}

	/** Returns a set of readonly reserved words.
	 */
	public static final Set getReservedNames() {
		return _reves;
	}
	/** Returns whether the specified is valid.
	 * It also invokes {@link #isReserved}.
	 */
	public static final boolean isValid(String name) {
		if (ComponentsCtrl.isAutoUuid(name) || isReserved(name))
			return false;

		for (int j = name.length(); --j >= 0;) {
			final char cc = name.charAt(j);
			if ((cc < '0' || cc > '9') && (cc < 'a' || cc > 'z')
			&& (cc < 'A' || cc > 'Z') && cc != '_')
				return false;
		}
		return true;
	}
	/** Returns whether the specified name is reserved.
	 * If true, you cannot use it in zscript (and EL).
	 */
	public static final boolean isReserved(String name) {
		return _reves.contains(name);
	}
}
