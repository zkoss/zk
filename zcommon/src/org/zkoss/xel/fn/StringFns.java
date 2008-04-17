/* StringFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar 31 12:25:57     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.fn;

import org.zkoss.lang.Objects;

/**
 * Functions to manipulate strings in EL.
 *
 * @author tomyeh
 */
public class StringFns {
	/** Catenates two strings.
	 * Note: null is considered as empty.
	 */
	public static String cat(String s1, String s2) {
		if (s1 == null)
			return s2 != null ? s2: "";
		return s2 != null ? s1 + s2: s1;
	}
	/** Catenates three strings.
	 * Note: null is considered as empty.
	 */
	public static String cat3(String s1, String s2, String s3) {
		return cat(cat(s1, s2), s3);
	}
	/** Catenates four strings.
	 * Note: null is considered as empty.
	 */
	public static String cat4(String s1, String s2, String s3, String s4) {
		return cat(cat(cat(s1, s2), s3), s4);
	}
	/** Catenates four strings.
	 * Note: null is considered as empty.
	 */
	public static String cat5(String s1, String s2, String s3, String s4, String s5) {
		return cat(cat(cat(cat(s1, s2), s3), s4), s5);
	}

	/** Replaces all occurrances of 'from' in 'src' with 'to'
	 */
	public static String replace(String src, String from, String to) {
		if (Objects.equals(from, to))
			return src;

		final StringBuffer sb = new StringBuffer(src);
		if ("\n".equals(from) || "\r\n".equals(from)) {
			replace0(sb, "\r\n", to);
			replace0(sb, "\n", to);
		} else {
			replace0(sb, from, to);
		}
		return sb.toString();
	}
	private static void replace0(StringBuffer sb, String from, String to) {
		final int len = from.length();
		for (int j = 0; (j = sb.indexOf(from, j)) >= 0;) {
			sb.replace(j, j += len, to);
		}
	}
}
