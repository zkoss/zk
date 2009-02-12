/* BigDecimals.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr 17 10:25:07     2003, Created by tomyeh
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.zkoss.lang.Objects;
import org.zkoss.math.BigIntegers;

/**
 * Utilities and constants of big decimals.
 *
 * @author tomyeh
 */
public class BigDecimals {
	/** Represents 0 in big decimal.
	 * @see #ONE
	 * @see #MINUS_ONE
	 */
	public static final BigDecimal ZERO = Objects.ZERO_BIG_DECIMAL;
	/** Represents 1 in big decimal.
	 * @see #ZERO
	 * @see #MINUS_ONE
	 */
	public static final BigDecimal ONE = new BigDecimal(BigInteger.ONE);
	/** Represents -1 in big decimal.
	 * @see #ZERO
	 * @see #ONE
	 */
	public static final BigDecimal MINUS_ONE = new BigDecimal(BigInteger.ONE.negate());
	
	/** Represents our number precision.
	 */
	public static final int NUMBER_PRECISION = 38;
	
	/** Represents our number scale.
	 */
	public static final int NUMBER_SCALE = 6;
	
	/** Represents our fine number precision.
	 */
	public static final int FINE_NUMBER_PRECISION = 20;
	/** Represents our fine number scale.
	 */
	public static final int FINE_NUMBER_SCALE = 8;

	/** Converts a double to a big decimal with a scale.
	 *
	 * <p>It is strongly deprecated to use new Dicimal(double) since
	 * the scale is unpredictable and usually surprising.
	 * Example, BigDecimal(.1) will becomes
	 * .1000000000000000055511151231257827021181583404541015625.
	 * On the other hand, BigDecimal("0.1") will be 0.1 correctly.
	 *
	 * @param scale the BigDecimal's scale
	 * @param roundingMode the rounding mode
	 */
	public static final BigDecimal
	toBigDecimal(double v, int scale, int roundingMode) {
		return new BigDecimal(v).setScale(scale, roundingMode);
	}
	/** Converts a double to a big decimal with a scale.
	 * It uses {@link BigDecimal#ROUND_HALF_UP}.
	 */
	public static final BigDecimal toBigDecimal(double v, int scale) {
		return toBigDecimal(v, scale, BigDecimal.ROUND_HALF_UP);
	}
	/** Converts an integer to a big decimal with a scale.
	 */
	public static final BigDecimal toBigDecimal(int v, int scale) {
		return new BigDecimal(BigIntegers.toBigInteger(v), scale);
	}
	/** Converts an integer to a big decimal with a scale.
	 */
	public static final BigDecimal toBigDecimal(long v, int scale) {
		return new BigDecimal(BigIntegers.toBigInteger(v), scale);
	}
	/** Converts an integer to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(int v) {
		return v == 0 ? ZERO: new BigDecimal(BigIntegers.toBigInteger(v));
	}
	/** Converts a long to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(long v) {
		return v == 0 ? ZERO: new BigDecimal(BigIntegers.toBigInteger(v));
	}
	/** Converts a short to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(short v) {
		return v == 0 ? ZERO: new BigDecimal(BigIntegers.toBigInteger(v));
	}
	/** Converts a byte to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(byte v) {
		return v == 0 ? ZERO: new BigDecimal(BigIntegers.toBigInteger(v));
	}

	/** Converts an integer to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(Integer v) {
		return toBigDecimal(v.intValue());
	}
	/** Converts a long to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(Long v) {
		return toBigDecimal(v.longValue());
	}
	/** Converts a short to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(Short v) {
		return toBigDecimal(v.shortValue());
	}
	/** Converts a byte to a big decimal with a scale without.
	 * losing precision -- zero scale in this case.
	 */
	public static final BigDecimal toBigDecimal(Byte v) {
		return toBigDecimal(v.byteValue());
	}
}
