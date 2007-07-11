/* Utils.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul 11 14:47:08     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

/**
 * Generic utilities.
 *
 * @author tomyeh
 * @since 2.5.0
 */
public class Utils {
	/** Returns a portion of the specified version in an integer,
	 * or -1 if no such portion exists.
	 *
	 * <p>For example, getSubversion(0) returns the so-called major version
	 * (2 in "2.4.0"), and getSubversion(1) returns the so-called
	 * minor version (4 in "2.4.0").
	 *
	 * @param version the version. The version is assumed to
	 * a serial
	 * @param portion which portion of the version; starting from 0.
	 * If you want to retrieve the major verion, specify 0.
	 * @since 2.5.0
	 */
	public static final int getSubversion(String version, int portion) {
		if (portion < 0)
			throw new IllegalArgumentException("Negative not allowed: "+portion);

		final int len = version.length();
		int j = 0;
		while (--portion >= 0) {
			j = nextVerSeparator(version, j) + 1;
			if (j >= len) return -1; //no such portion
		}
		try {
			return Integer.parseInt(
				version.substring(j, nextVerSeparator(version, j)));
		} catch (Throwable ex) {
			return -1; //unknow
		}
	}
	private static final int nextVerSeparator(String version, int from) {
		for (final int len = version.length(); from < len; ++from) {
			final char cc = version.charAt(from);
			if (cc < '0' || cc > '9')
				break;
		}
		return from;
	}
}
