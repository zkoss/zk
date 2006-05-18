/* BigDecimals.java

{{IS_NOTE
	$Id: BigDecimals.java,v 1.2 2006/02/27 03:42:00 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Apr 17 10:25:07     2003, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import com.potix.lang.Objects;
import com.potix.math.BigIntegers;

/**
 * Utilities and constants of big decimals.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:00 $
 */
public class BigDecimals {
	/** The default rounding mode. */
	private static int _roundingMode = BigDecimal.ROUND_HALF_UP;

	/** Gets the default rounding mode for BigDecimal.
	 * It is used when a rounding mode is required and no one
	 * is specified explicitly.
	 */
	public static final int getRoundingMode() {
		return _roundingMode;
	}
	/** Sets the default rounding mode, one of BigDecimal.ROUND_xxx.
	 */
	public static final void setRoundingMode(int mode) {
		_roundingMode = mode;
	}

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
	 * The rounding mode uses the default one, getRoundingMode.
	 */
	public static final BigDecimal toBigDecimal(double v, int scale) {
		return toBigDecimal(v, scale, _roundingMode);
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
