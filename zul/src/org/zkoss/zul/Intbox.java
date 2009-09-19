/* Intbox.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:39:37     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.mesg.MZul;
import org.zkoss.zul.impl.NumberInputElement;

/**
 * An edit box for holding an integer.
 * <p>Default {@link #getZclass}: z-intbox.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Intbox extends NumberInputElement implements org.zkoss.zul.api.Intbox {
	public Intbox() {
		setCols(11);
	}
	public Intbox(int value) throws WrongValueException {
		this();
		setValue(new Integer(value));
	}

	/** Returns the value (in Integer), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Integer getValue() throws WrongValueException {
		return (Integer)getTargetValue();
	}
	/** Returns the value in int. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Integer)val).intValue(): 0;
	}
	/** Sets the value (in Integer).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Integer value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-intbox" : _zclass;
	}
	protected Object coerceFromString(String value) throws WrongValueException {
		final Object[] vals = toNumberOnly(value);
		final String val = (String)vals[0];
		if (val == null || val.length() == 0)
			return null;

		try {
			int v = Integer.parseInt(val);
			int divscale = vals[1] != null ? ((Integer)vals[1]).intValue(): 0;
			while (v != 0 && --divscale >= 0)
				v /= 10;
			return new Integer(v);
		} catch (NumberFormatException ex) {
			throw showCustomError(
				new WrongValueException(this, MZul.NUMBER_REQUIRED, value));
		}
	}
	protected String coerceToString(Object value) {
		return value != null && getFormat() == null ?
			value.toString(): formatNumber(value, null);
	}
}
