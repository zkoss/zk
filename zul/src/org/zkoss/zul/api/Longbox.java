/* Longbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
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
 * An edit box for holding an integer.
 * <p>
 * Default {@link #getZclass}: z-longbox.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Longbox extends org.zkoss.zul.impl.api.NumberInputElement {

	/**
	 * Returns the value (in Long), might be null unless a constraint stops it.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public Long getValue() throws WrongValueException;

	/**
	 * Returns the value in long. If null, zero is returned.
	 */
	public long longValue() throws WrongValueException;

	/**
	 * Returns the value in int. If null, zero is returned.
	 */
	public long intValue() throws WrongValueException;

	/**
	 * Sets the value (in Long).
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(Long value) throws WrongValueException;

}
