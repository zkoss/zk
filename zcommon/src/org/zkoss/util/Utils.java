/* Utils.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul 11 14:47:08     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

/**
 * Generic utilities.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Utils {
	/** Returns a portion of the specified version in an integer,
	 * or 0 if no such portion exists.
	 *
	 * <p>For example, getSubversion(0) returns the so-called major version
	 * (2 in "2.4.0"), and getSubversion(1) returns the so-called
	 * minor version (4 in "2.4.0").
	 *
	 * <p>Note: alpha is consider as -500, beta -300, and rc -100.
	 * Moreover, beta3 is -497 (= -500 + 3) and rc5 -95 (-100 + 5)
	 *
	 * @param version the version. The version is assumed to
	 * a series of integer separated by a non-alphanemric separator.
	 * @param portion which portion of the version; starting from 0.
	 * If you want to retrieve the major verion, specify 0.
	 * @since 3.0.0
	 */
	public static final int getSubversion(String version, int portion) {
		if (portion < 0)
			throw new IllegalArgumentException("Negative not allowed: "+portion);

		final int len = version.length();
		int j = 0;
		while (--portion >= 0) {
			j = nextVerSeparator(version, j) + 1;
			if (j >= len) return 0; //no such portion
		}

		String s = version.substring(j, nextVerSeparator(version, j));
		try {
			return Integer.parseInt(s);
		} catch (Throwable ex) { //eat
		}

		s = s.toLowerCase();
		final int base;
		if (s.startsWith("rc")) {
			base = -100;
			j = 2;
		} else if (s.startsWith("beta")) {
			base = -300;
			j = 4;
		} else if (s.startsWith("alpha")) {
			base = -500;
			j = 5;
		} else {
			return 0; //unknown
		}
		if (j < s.length()) {
			try {
				return base + Integer.parseInt(s.substring(j));
			} catch (Throwable ex) { //eat
			}
		}
		return base;
	}
	private static final int nextVerSeparator(String version, int from) {
		for (final int len = version.length(); from < len; ++from) {
			final char cc = version.charAt(from);
			if ((cc < '0' || cc > '9') && (cc < 'a' || cc > 'z')
			&& (cc < 'A' || cc > 'Z'))
				break;
		}
		return from;
	}
}
