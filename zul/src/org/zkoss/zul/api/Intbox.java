/* Intbox.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * An edit box for holding an integer.
 * <p>
 * Default {@link #getZclass}: z-intbox.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Intbox extends org.zkoss.zul.impl.api.NumberInputElement {

	/**
	 * Returns the value (in Integer), might be null unless a constraint stops
	 * it.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public Integer getValue() throws WrongValueException;

	/**
	 * Returns the value in int. If null, zero is returned.
	 */
	public int intValue() throws WrongValueException;

	/**
	 * Sets the value (in Integer).
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(Integer value) throws WrongValueException;
}
