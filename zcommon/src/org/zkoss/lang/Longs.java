/* Longs.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 25 10:42:53     2004, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

/**
 * Long relevant utilities.
 *
 * @author tomyeh
 */
public class Longs {
	/** The zero. */
	public static final Long ZERO = Objects.ZERO_LONG;

	/** urns a String object representing the specified integer, with
	 * the (at-least) specified digits.
	 *
	 * <p>Example: toStringByScale(123, 5) returns "00123",
	 * toStringByScale(123, 2) returns "123".
	 */
	public static final String toStringByScale(long val, int digits) {
		String sval = Long.toString(val);
		int df = digits - sval.length();
		if (df <= 0)
			return sval;

		final StringBuffer sb = new StringBuffer(digits);
		while (--df >= 0) sb.append('0');
		return sb.append(sval).toString();
	}
}
