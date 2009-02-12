/* BigIntegers.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 19 13:06:48     2003, Created by tomyeh
}}IS_NOTE

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.math;

import java.math.BigInteger;
import org.zkoss.lang.Objects;

/**
 * BigInteger utilities.
 *
 * @author tomyeh
 */
public class BigIntegers {
	/** Represents 0 in big integer.
	 */
	public static final BigInteger ZERO = BigInteger.ZERO;

	/** Converts an integer to a big integer.
	 */
	public static final BigInteger toBigInteger(int v) {
		return v == 0 ? ZERO: new BigInteger(Objects.toByteArray(v));
	}
	/** Converts a long to a big integer.
	 */
	public static final BigInteger toBigInteger(long v) {
		return v == 0 ? ZERO: new BigInteger(Objects.toByteArray(v));
	}
	/** Converts a short to a big integer.
	 */
	public static final BigInteger toBigInteger(short v) {
		return v == 0 ? ZERO: new BigInteger(Objects.toByteArray(v));
	}
	/** Converts a byte to a big integer.
	 */
	public static final BigInteger toBigInteger(byte v) {
		return v == 0 ? ZERO: new BigInteger(Objects.toByteArray(v));
	}

	/** Converts an integer to a big integer.
	 */
	public static final BigInteger toBigInteger(Integer v) {
		return toBigInteger(v.intValue());
	}
	/** Converts a long to a big integer.
	 */
	public static final BigInteger toBigInteger(Long v) {
		return toBigInteger(v.longValue());
	}
	/** Converts a short to a big integer.
	 */
	public static final BigInteger toBigInteger(Short v) {
		return toBigInteger(v.shortValue());
	}
	/** Converts a byte to a big integer.
	 */
	public static final BigInteger toBigInteger(Byte v) {
		return toBigInteger(v.byteValue());
	}
}
