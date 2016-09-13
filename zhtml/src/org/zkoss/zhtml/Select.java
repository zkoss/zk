/* Select.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:04:17     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;

/**
 * The SELECT tag.
 * 
 * @author tomyeh
 */
public class Select extends AbstractTag {
	static {
		addClientEvent(Select.class, Events.ON_CHANGE, 0);
	}

	public Select() {
		super("select");
	}

	/**
	 * Returns the autofocus of this select tag.
	 * @since 8.0.3
	 */
	public Boolean isAutofocus() {
		return getDynamicProperty("autofocus") != null;
	}

	/**
	 * Sets the autofocus of this select tag.
	 * @since 8.0.3
	 */
	public void setAutofocus(Boolean autofocus) throws WrongValueException {
		setDynamicProperty("autofocus", autofocus ? true : null);
	}
	/**
	 * Returns the disabled of this select tag.
	 * @since 8.0.3
	 */
	public Boolean isDisabled() {
		return getDynamicProperty("disabled") != null;
	}

	/**
	 * Sets the disabled of this select tag.
	 * @since 8.0.3
	 */
	public void setDisabled(Boolean disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled ? true : null);
	}
	/**
	 * Returns the multiple of this select tag.
	 * @since 8.0.3
	 */
	public Boolean isMultiple() {
		return getDynamicProperty("multiple") != null;
	}

	/**
	 * Sets the multiple of this select tag.
	 * @since 8.0.3
	 */
	public void setMultiple(Boolean multiple) throws WrongValueException {
		setDynamicProperty("multiple", multiple ? true : null);
	}
	/**
	 * Returns the name of this select tag.
	 * @since 8.0.3
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this select tag.
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	}
	/**
	 * Returns the required of this select tag.
	 * @since 8.0.3
	 */
	public Boolean isRequired() {
		return getDynamicProperty("required") != null;
	}

	/**
	 * Sets the required of this select tag.
	 * @since 8.0.3
	 */
	public void setRequired(Boolean required) throws WrongValueException {
		setDynamicProperty("required", required ? true : null);
	}
	/**
	 * Returns the size of this select tag.
	 * @since 8.0.3
	 */
	public Integer getSize() {
		return (Integer) getDynamicProperty("size");
	}

	/**
	 * Sets the size of this select tag.
	 * @since 8.0.3
	 */
	public void setSize(Integer size) throws WrongValueException {
		setDynamicProperty("size", size);
	}

	// -- Component --//
	/**
	 * Returns the widget class, "zhtml.Input".
	 * 
	 * @since 8.0.0
	 */
	public String getWidgetClass() {
		return "zhtml.Input";
	}

}
