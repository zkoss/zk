/* Decimalbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 5, 2007 5:38:01 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;

import org.zkoss.lang.JVMs;
import org.zkoss.math.RoundingModes;
import org.zkoss.mil.impl.InputElement;
import org.zkoss.mil.mesg.MMil;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.WrongValueException;

/**
 * Decimal input box.
 * @author henrichen
 *
 */
public class Decimalbox extends InputElement {
	private static final long serialVersionUID = 200707051754L;
	/** The rounding mode. */
	private int _rounding = BigDecimal.ROUND_HALF_EVEN;
	public static final int AUTO = -1000000000;
	private int _scale = AUTO;

	public Decimalbox() {
		setMaxlength(11);
	}
	public Decimalbox(BigDecimal value) throws WrongValueException {
		this();
		setValue(value);
	}

	/** Sets the rounding mode.
	 * Note: You cannot change the rounding mode unless you are
	 * using Java 6 or later.
	 *
	 * @param mode the rounding mode. Allowed value:
	 * {@link BigDecimal#ROUND_CEILING}, {@link BigDecimal#ROUND_DOWN},
	 * {@link BigDecimal#ROUND_FLOOR}, {@link BigDecimal#ROUND_HALF_DOWN},
	 * {@link BigDecimal#ROUND_HALF_UP}, {@link BigDecimal#ROUND_HALF_EVEN},
	 * {@link BigDecimal#ROUND_UNNECESSARY} and {@link BigDecimal#ROUND_UP}
	 *
	 * @exception UnsupportedOperationException if Java 5 or below
	 */
	public void setRoundingMode(int mode) {
		if (_rounding != mode) {
			if (!JVMs.isJava6())
				throw new UnsupportedOperationException("Java 6 or above is required");
			_rounding = mode;
		}
	}
	/** Sets the rounding mode by the name.
	 * Note: You cannot change the rounding mode unless you are
	 * using Java 6 or later.
	 *
	 * @param name the rounding mode's name. Allowed value:
<dl>
<dt>CEILING</dt>
	<dd>Rounding mode to round towards positive infinity.</dd>
<dt>DOWN</dt>
	<dd>Rounding mode to round towards zero.</dd>
<dt>FLOOR</dt>
	<dd>Rounding mode to round towards negative infinity.</dd>
<dt>HALF_DOWN</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round down.</dd>
<dt>HALF_EVEN</dt>
	<dd>Rounding mode to round towards the "nearest neighbor" unless both neighbors are equidistant, in which case, round towards the even neighbor.</dd>
<dt>HALF_UP</dt>
	<dd>Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant, in which case round up.</dd>
<dt>UNNECESSARY</dt>
	<dd>Rounding mode to assert that the requested operation has an exact result, hence no rounding is necessary.</dd>
<dt>UP</dt>
	<dd>Rounding mode to round away from zero.</dd>
</dl>
	 * @exception UnsupportedOperationException if Java 5 or below
	 * @see RoundingModes
	 */
	public void setRoundingMode(String name) {
		setRoundingMode(RoundingModes.valueOf(name));
	}
	/** Returns the rounding mode.
	 * <p>Default: {@link BigDecimal#ROUND_HALF_EVEN}.
	 */
	public int getRoundingMode() {
		return _rounding;
	}
	
	/** Returns the value (in BigDecimal), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public BigDecimal getValue() throws WrongValueException {
		return (BigDecimal)getTargetValue();
	}
	/** Returns the value in double. If null, zero is returned.
	 */
	public double doubleValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.doubleValue(): 0.0;
	}
	/** Returns the value in integer. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.intValue(): 0;
	}
	/** Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.longValue(): 0;
	}
	/** Returns the value in short. If null, zero is returned.
	 */
	public short shortValue() throws WrongValueException {
		final BigDecimal val = getValue();
		return val != null ? val.shortValue(): 0;
	}
	/** Sets the value (in BigDecimal).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(BigDecimal value) throws WrongValueException {
		setRawValue(value);
	}

	/** Returns the scale for the decimal number storing in this component,
	 * or {@link #AUTO} if the scale is decided automatically (based on
	 * what user has entered).
	 *
	 * <p>Default: {@link #AUTO}.
	 */
	public int getScale() {
		return _scale;
	}
	/** Returns the scale for the decimal number storing in this component,
	 * or {@link #AUTO} if the scale is decided automatically (based on
	 * what user has entered).
	 *
	 * <p>Default: {@link #AUTO}.
	 */
	public void setScale(int scale) {
		_scale = scale;
	}

	//-- super --//
	protected Object coerceFromString(String value) throws WrongValueException {
		final Object[] vals = toNumberOnly(value);
		final String val = (String)vals[0];
		if (val == null || val.length() == 0)
			return null;

		try {
			BigDecimal v = new BigDecimal(val);
			if (_scale != AUTO)
				v = v.setScale(_scale, getRoundingMode());

			int divscale = vals[1] != null ? ((Integer)vals[1]).intValue(): 0;
			if (divscale > 0) {
				final BigDecimal ten = new BigDecimal(10);
				do {
					v = v.divide(ten, _scale == AUTO ? v.scale()+1: _scale,
						getRoundingMode());
				} while (--divscale > 0);
			}
			return v;
		} catch (NumberFormatException ex) {
			throw new WrongValueException(this, MMil.NUMBER_REQUIRED, value);
		}
	}
	
	protected String coerceToString(Object value) {
		return value == null ? "" : value.toString();
	}

	/* (non-Javadoc)
	 * @see org.zkoss.mil.impl.InputElement#getInternalType()
	 */
	protected int getInternalType() {
		return DECIMAL;
	}

	/** Filters out non digit characters, such comma and whitespace,
	 * from the specified value.
	 * It is designed to let user enter data in more free style.
	 * They may or may not enter data in the specified format.
	 *
	 * @return a two element array. The first element is the string to
	 * parse with, say, Double.parseDouble. The second element is
	 * an integer to indicate how many digits the result shall be scaled.
	 * For example, if the second element is 2. Then, the result shall be
	 * divided with 10 ^ 2.
	 */
	protected Object[] toNumberOnly(String val) {
		if (val == null) return new Object[] {null, null};

		final DecimalFormatSymbols symbols =
			new DecimalFormatSymbols(Locales.getCurrent());
		final char GROUPING = symbols.getGroupingSeparator(),
			DECIMAL = symbols.getDecimalSeparator(),
			PERCENT = symbols.getPercent(),
			PER_MILL = symbols.getPerMill(), //1/1000
			//not support yet: INFINITY = symbols.getInfinity(), NAN = symbols.getNaN(),
			MINUS = symbols.getMinusSign();
		StringBuffer sb = null;
		int divscale = 0; //the second element
		boolean minus = false;
		for (int j = 0, len = val.length(); j < len; ++j) {
			final char cc = val.charAt(j);

			boolean ignore = false;
			//We handle percent and (nnn) specially
			if (cc == PERCENT) {
				divscale += 2;
				ignore = true;
			} else if (cc == PER_MILL) {
				divscale += 3;
				ignore = true;
			} else if (cc == '(') {
				minus = true;
				ignore = true;
			}

			//We don't add if cc shall be ignored (not alphanum but in fmt)
			if (!ignore)
				ignore = (cc < '0' || cc > '9')
				&& cc != DECIMAL && cc != MINUS && cc != '+'
				&& (Character.isWhitespace(cc) || cc == GROUPING || cc == ')');
			if (ignore) {
				if (sb == null)
					sb = new StringBuffer(len).append(val.substring(0, j));
			} else {
				final char c2 = cc == MINUS ? '-':
					cc == DECIMAL ? '.':  cc;
				if (cc != c2) {
					if (sb == null)
						sb = new StringBuffer(len).append(val.substring(0, j));
					sb.append(c2);
				} else if (sb != null) {
					sb.append(c2);
				}
			}
		}
		if (minus) {
			if (sb == null)
				sb = new StringBuffer(val.length() + 1).append(val);
			if (sb.length() > 0) {
				if (sb.charAt(0) == '-') {
					sb.deleteCharAt(0);
				} else {
					sb.insert(0, '-');
				}
			}
		}
		return new Object[] {
			(sb != null ? sb.toString(): val), new Integer(divscale)};
	}
}
