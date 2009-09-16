/* Doublebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Oct 14 12:59:39     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.mesg.MZul;
import org.zkoss.zul.impl.NumberInputElement;

/**
 * An edit box for holding an float point value (double).
 * <p>Default {@link #getZclass}: z-doublebox.(since 3.5.0)
 *
 * @author henrichen
 */
public class Doublebox extends NumberInputElement implements org.zkoss.zul.api.Doublebox {
	public Doublebox() {
		setCols(11);
	}
	public Doublebox(double value) throws WrongValueException {
		this();
		setValue(value);
	}
	public Doublebox(Double value) throws WrongValueException {
		this();
		setValue(value);
	}

	/** Returns the value (in Double), might be null unless
	 * a constraint stops it.
	 * @exception WrongValueException if user entered a wrong value
	 */
	public Double getValue() throws WrongValueException {
		return (Double)getTargetValue();
	}
	/** Returns the value in double. If null, zero is returned.
	 */
	public double doubleValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double)val).doubleValue(): 0.0;
	}
	/** Returns the value in integer. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double)val).intValue(): 0;
	}
	/** Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double)val).longValue(): 0;
	}
	/** Returns the value in short. If null, zero is returned.
	 */
	public short shortValue() throws WrongValueException {
		final Object val = getTargetValue();
		return val != null ? ((Double)val).shortValue(): 0;
	}
	/** Sets the value (in Double).
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(Double value) throws WrongValueException {
		validate(value);
		setRawValue(value);
	}
	/** Sets the value (in double)
	 * @exception WrongValueException if value is wrong
	 */
	public void setValue(double value) throws WrongValueException {
		setValue(new Double(value));
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-doublebox" : _zclass;
	}
	protected Object coerceFromString(String value) throws WrongValueException {
		final Object[] vals = toNumberOnly(value);
		final String val = (String)vals[0];
		if (val == null || val.length() == 0)
			return null;

		try {
			double v = Double.parseDouble(val);
			int divscale = vals[1] != null ? ((Integer)vals[1]).intValue(): 0;
			if (divscale > 0)
				v /= Math.pow(10, divscale);
			return new Double(v);
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
