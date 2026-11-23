/* Option.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:04:11     2005, Created by tomyeh

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
 * The OPTION tag.
 * 
 * @author tomyeh
 */
public class Option extends AbstractTag {
	public Option() {
		super("option");
	}
	/**
	 * Returns the disabled of this option tag.
	 * @since 8.0.3
	 */
	public boolean isDisabled() {
		final Boolean b = (Boolean) getDynamicProperty("disabled");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the disabled of this option tag.
	 * @since 8.0.3
	 */
	public void setDisabled(boolean disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled ? Boolean.valueOf(disabled) : null);
	}
	/**
	 * Returns the label of this option tag.
	 * @since 8.0.3
	 */
	public String getLabel() {
		return (String) getDynamicProperty("label");
	}

	/**
	 * Sets the label of this option tag.
	 * @since 8.0.3
	 */
	public void setLabel(String label) throws WrongValueException {
		setDynamicProperty("label", label);
	}
	/**
	 * Returns the selected of this option tag.
	 * @since 8.0.3
	 */
	public boolean isSelected() {
		final Boolean b = (Boolean) getDynamicProperty("selected");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the selected of this option tag.
	 * @since 8.0.3
	 */
	public void setSelected(boolean selected) throws WrongValueException {
		setDynamicProperty("selected", selected ? Boolean.valueOf(selected) : null);
	}
	/**
	 * Returns the value of this option tag.
	 * @since 8.0.3
	 */
	public String getValue() {
		return (String) getDynamicProperty("value");
	}

	/**
	 * Sets the value of this option tag.
	 * @since 8.0.3
	 */
	public void setValue(String value) throws WrongValueException {
		setDynamicProperty("value", value);
	}
}
