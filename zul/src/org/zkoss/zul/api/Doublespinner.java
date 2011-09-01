/* Doublespinner.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 17, 2010 9:47:39 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.api.NumberInputElement;

/**
 * An edit box for holding a constrained double.
 * 
 * <p>
 * Default {@link #getZclass}: z-doublespinner.
 * 
 * <p>
 * Double spinner supports below key events. <lu>
 * <li>0-9 : set the value on the inner text box.
 * <li>delete : clear the value to empty (null) </lu>
 * 
 * @author jumperchen
 * @since 5.0.6
 */
public interface Doublespinner extends NumberInputElement {

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
	 * Sets the value (in Double).
	 * 
	 * @exception WrongValueException
	 *                if value is wrong
	 */
	public void setValue(Double value) throws WrongValueException;

	/**
	 * Return the step of double spinner
	 */
	public double getStep();

	/**
	 * Set the step of double spinner
	 */
	public void setStep(double step);

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
