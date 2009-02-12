/* Decimalbox.java

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

import java.math.BigDecimal;
import org.zkoss.zk.ui.WrongValueException;

/**
 * An edit box for holding BigDecimal.
 * <p>
 * Default {@link #getZclass}: z-decimalbox.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Decimalbox extends org.zkoss.zul.impl.api.NumberInputElement {
	/**
	 * Returns the value (in BigDecimal), might be null unless a constraint
	 * stops it.
	 * 
	 * @exception WrongValueException
	 *                if user entered a wrong value
	 */
	public BigDecimal getValue() throws WrongValueException;

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
	 * Sets the value (in BigDecimal).
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(BigDecimal value) throws WrongValueException;

	/**
	 * Returns the scale for the decimal number storing in this component, or
	 * {@link org.zkoss.zul.Decimalbox#AUTO} if the scale is decided
	 * automatically (based on what user has entered).
	 * 
	 * <p>
	 * Default: {@link org.zkoss.zul.Decimalbox#AUTO}.
	 */
	public int getScale();

	/**
	 * Returns the scale for the decimal number storing in this component, or
	 * {@link org.zkoss.zul.Decimalbox#AUTO} if the scale is decided
	 * automatically (based on what user has entered).
	 * 
	 * <p>
	 * Default: {@link org.zkoss.zul.Decimalbox#AUTO}.
	 */
	public void setScale(int scale);

}
