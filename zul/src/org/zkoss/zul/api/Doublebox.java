/* Doublebox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * An edit box for holding an float point value (double).
 * <p>
 * Default {@link #getZclass}: z-doublebox.(since 3.5.0)
 * 
 * @author henrichen
 * @since 3.5.2
 */
public interface Doublebox extends org.zkoss.zul.impl.api.NumberInputElement {

	/**
	 * Returns the value (in Double), might be null unless a constraint stops
	 * it.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public Double getValue() throws WrongValueException;

	/**
	 * Returns the value in double. If null, zero is returned.
	 */
	public double doubleValue() throws WrongValueException;

	/**
	 * Returns the value in integer. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException;

	/**
	 * Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException;

	/**
	 * Returns the value in short. If null, zero is returned.
	 */
	public short shortValue() throws WrongValueException;

	/**
	 * Sets the value (in Double).
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(Double value) throws WrongValueException;

	/**
	 * Sets the value (in double)
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(double value) throws WrongValueException;
}
