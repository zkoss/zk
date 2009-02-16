/* Decimalbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:40:20     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.math.BigDecimal;

import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.mesg.MZul;
import org.zkoss.zul.impl.NumberInputElement;

/**
 * An edit box for holding BigDecimal.
 * <p>Default {@link #getZclass}: z-decimalbox.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Decimalbox extends NumberInputElement implements org.zkoss.zul.api.Decimalbox {
	/** Used with {@link #setScale} to denote that the scale is decided by
	 * what user has entered.
	 */
	public static final int AUTO = -1000000000;
	private int _scale = AUTO;

	public Decimalbox() {
		setCols(11);
	}
	public Decimalbox(BigDecimal value) throws WrongValueException {
		this();
		setValue(value);
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
		validate(value);
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
	public String getZclass() {
		return _zclass == null ? "z-decimalbox" : _zclass;
	}
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
			throw showCustomError(
				new WrongValueException(this, MZul.NUMBER_REQUIRED, value));
		}
	}
	protected String coerceToString(Object value) {
		return formatNumber(value, "0.##########");
	}
}
