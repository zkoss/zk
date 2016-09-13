/* Button.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:58:40     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The BUTTON tag.
 * 
 * @author tomyeh
 */
public class Button extends AbstractTag {
	public Button() {
		super("button");
	}
	/**
	 * Returns the autofocus of this button tag.
	 * @since 8.0.3
	 */
	public Boolean isAutofocus() {
		return getDynamicProperty("autofocus") != null;
	}

	/**
	 * Sets the autofocus of this button tag.
	 * @since 8.0.3
	 */
	public void setAutofocus(Boolean autofocus) throws WrongValueException {
		setDynamicProperty("autofocus", autofocus ? true : null);
	}
	/**
	 * Returns the disabled of this button tag.
	 * @since 8.0.3
	 */
	public Boolean isDisabled() {
		return getDynamicProperty("disabled") != null;
	}

	/**
	 * Sets the disabled of this button tag.
	 * @since 8.0.3
	 */
	public void setDisabled(Boolean disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled ? true : null);
	}
	/**
	 * Returns the name of this button tag.
	 * @since 8.0.3
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this button tag.
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	}
	/**
	 * Returns the type of this button tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this button tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
	/**
	 * Returns the value of this button tag.
	 * @since 8.0.3
	 */
	public String getValue() {
		return (String) getDynamicProperty("value");
	}

	/**
	 * Sets the value of this button tag.
	 * @since 8.0.3
	 */
	public void setValue(String value) throws WrongValueException {
		setDynamicProperty("value", value);
	}
}
