/* Spinner.java

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
 * An edit box for holding a constrained integer.
 * 
 * <p>
 * Default {@link #getZclass}: z-spinner.
 * 
 * <p>
 * spinner supports below key events. <lu>
 * <li>0-9 : set the value on the inner text box.
 * <li>delete : clear the value to empty (null) </lu>
 * 
 * @author gracelin
 * @since 3.5.2
 */
public interface Spinner extends org.zkoss.zul.impl.api.NumberInputElement {

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

	/**
	 * Return the step of spinner
	 */
	public int getStep();

	/**
	 * Set the step of spinner
	 */
	public void setStep(int step);

	/**
	 * Returns whether the button (on the right of the textbox) is visible.
	 * <p>
	 * Default: true.
	 */
	public boolean isButtonVisible();

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible);

}
