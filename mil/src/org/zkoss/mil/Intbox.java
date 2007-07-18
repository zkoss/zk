/* Intbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 5, 2007 5:02:21 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.mil.impl.InputElement;
import org.zkoss.mil.mesg.MMil;
import org.zkoss.zk.ui.WrongValueException;


/**
 * A integer input box.
 * @author henrichen
 *
 */
public class Intbox extends InputElement {
	private static final long serialVersionUID = 200707051728L;

	public Intbox() {
		setMaxlength(11);
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
		setRawValue(value);
	}

	//super
	protected Object coerceFromString(String value) throws WrongValueException {
		try {
			int v = Integer.parseInt(value);
			return new Integer(v);
		} catch (NumberFormatException ex) {
			throw new WrongValueException(this, MMil.INTEGER_REQUIRED, value);
		}
	}

	protected String coerceToString(Object value) {
		return value != null ? value.toString(): "";
	}

	protected int getInternalType() {
		return NUMERIC;
	}

}
