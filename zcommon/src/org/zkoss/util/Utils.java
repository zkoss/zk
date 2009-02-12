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

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

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

		return convertSubversion(
			version.substring(j, nextVerSeparator(version, j)));
	}
	/** Parses the specified version into an array of integer.
	 * For example, parseVersion("3.5.2") will return [3, 5, 2].
	 * @since 3.5.2
	 */
	public static final int[] parseVersion(String version) {
		final List vers = new LinkedList();
		for (int j = 0, len = version.length(); j < len;) {
			int k = nextVerSeparator(version, j);
			vers.add(new Integer(convertSubversion(version.substring(j, k))));
			j = k + 1;
		}

		final int[] ivs = new int[vers.size()];
		int j = 0;
		for (Iterator it = vers.iterator(); it.hasNext();)
			ivs[j++] = ((Integer)it.next()).intValue();
		return ivs;
	}
	/** Compares two version.
	 * @return 0 if equals, 1 if the first one is larger, -1 if smaller.
	 * @since 3.5.2
	 */
	public static final int compareVersion(int[] v1, int[] v2) {
		for (int j = 0;; ++j) {
			if (j == v1.length) {
				for (; j < v2.length; ++j) {
					if (v2[j] > 0) return -1;
					if (v2[j] < 0) return 1;
				}
				return 0;
			}
			if (j == v2.length) {
				for (; j < v1.length; ++j) {
					if (v1[j] > 0) return 1;
					if (v1[j] < 0) return -1;
				}
				return 0;
			}
			if (v1[j] > v2[j]) return 1;
			if (v1[j] < v2[j]) return -1;
		}
	}
	private static final int convertSubversion(String subver) {
		try {
			return Integer.parseInt(subver);
		} catch (Throwable ex) { //eat
		}

		subver = subver.toLowerCase();
		final int base, j;
		if (subver.startsWith("rc")) {
			base = -100;
			j = 2;
		} else if (subver.startsWith("beta")) {
			base = -300;
			j = 4;
		} else if (subver.startsWith("alpha")) {
			base = -500;
			j = 5;
		} else {
			return 0; //unknown
		}
		if (j < subver.length()) {
			try {
				return base + Integer.parseInt(subver.substring(j));
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
