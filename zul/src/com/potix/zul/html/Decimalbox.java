/* Decimalbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:40:20     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.math.BigDecimal;

import com.potix.math.BigDecimals;
import com.potix.zk.ui.WrongValueException;

import com.potix.zul.mesg.MZul;
import com.potix.zul.html.impl.FormatInputElement;

/**
 * An edit box for holding BigDecimal.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Decimalbox extends FormatInputElement {
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
		return (BigDecimal)getRawValue();
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
	protected Object coerceFromString(String value) throws WrongValueException {
		final String val = toNumberOnly(value);
		if (val == null || val.length() == 0)
			return null;

		try {
			int j = val.indexOf('%'); //toNumberOnly translates Locale-dependent
			BigDecimal bd = j == 0 ? BigDecimals.ZERO:
				new BigDecimal(j < 0 ? val: val.substring(0, j));
			if (_scale != AUTO) bd = bd.setScale(_scale);
			if (j <= 0) return bd;

			final BigDecimal hundred = new BigDecimal(100);
			for (final int len = val.length(); j < len && bd.signum() != 0; ++j)
				if (val.charAt(j) == '%')
					bd = bd.divide(hundred,
						_scale == AUTO ? bd.scale()+2: _scale,
						BigDecimal.ROUND_HALF_EVEN);
				else
					throw new WrongValueException(this, MZul.NUMBER_REQUIRED, value);
			return bd;
		} catch (NumberFormatException ex) {
			throw new WrongValueException(this, MZul.NUMBER_REQUIRED, value);
		}
	}
	protected String coerceToString(Object value) {
		return formatNumber(value);
	}
}
