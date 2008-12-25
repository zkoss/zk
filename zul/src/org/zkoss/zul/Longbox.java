/* Longbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 28 13:39:37     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.mesg.MZul;
import org.zkoss.zul.impl.NumberInputElement;

/**
 * An edit box for holding an integer.
 * <p>Default {@link #getZclass}: z-longbox.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Longbox extends NumberInputElement implements org.zkoss.zul.api.Longbox {
	public Longbox() {
		setCols(11);
	}
	public Longbox(long value) throws WrongValueException {
		this();
		setValue(new Long(value));
	}
	public Longbox(int value) throws WrongValueException {
		this();
		setValue(new Long(value));
	}

	/** Returns the value (in Long), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Long getValue() throws WrongValueException {
		return (Long)getTargetValue();
	}
	/** Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Long)val).longValue(): 0;
	}
	/** Returns the value in int. If null, zero is returned.
	 */
	public long intValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Long)val).intValue(): 0;
	}
	/** Sets the value (in Long).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Long value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-longbox" : _zclass;
	}
	protected Object coerceFromString(String value) throws WrongValueException {
		final Object[] vals = toNumberOnly(value);
		final String val = (String)vals[0];
		if (val == null || val.length() == 0)
			return null;

		try {
			long v = Long.parseLong(val);
			int divscale = vals[1] != null ? ((Integer)vals[1]).intValue(): 0;
			while (v != 0 && --divscale >= 0)
				v /= 10;
			return new Long(v);
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
