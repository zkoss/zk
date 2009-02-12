/* Integers.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Feb 24 21:25:22     2004, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Integer utilities.
 *
 * @author tomyeh
 */
public class Integers {
	/** The zero. */
	public static final Integer ZERO = Objects.ZERO_INTEGER;

	/** urns a String object representing the specified integer, with
	 * the (at-least) specified digits.
	 *
	 * <p>Example: toStringByScale(123, 5) returns "00123",
	 * toStringByScale(123, 2) returns "123".
	 */
	public static final String toStringByScale(int val, int digits) {
		String sval = Integer.toString(val);
		int df = digits - sval.length();
		if (df <= 0)
			return sval;

		final StringBuffer sb = new StringBuffer(digits);
		while (--df >= 0) sb.append('0');
		return sb.append(sval).toString();
	}
}