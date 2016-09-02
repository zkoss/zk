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
	public String getAutofocus() {
		return (String) getDynamicProperty("autofocus");
	}

	/**
	 * Sets the autofocus of this select tag.
	 * @since 8.0.3
	 */
	public void setAutofocus(String autofocus) throws WrongValueException {
		setDynamicProperty("autofocus", autofocus);
	};
	/**
	 * Returns the disabled of this select tag.
	 * @since 8.0.3
	 */
	public String getDisabled() {
		return (String) getDynamicProperty("disabled");
	}

	/**
	 * Sets the disabled of this select tag.
	 * @since 8.0.3
	 */
	public void setDisabled(String disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled);
	};
	/**
	 * Returns the multiple of this select tag.
	 * @since 8.0.3
	 */
	public String getMultiple() {
		return (String) getDynamicProperty("multiple");
	}

	/**
	 * Sets the multiple of this select tag.
	 * @since 8.0.3
	 */
	public void setMultiple(String multiple) throws WrongValueException {
		setDynamicProperty("multiple", multiple);
	};
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
	};
	/**
	 * Returns the required of this select tag.
	 * @since 8.0.3
	 */
	public String getRequired() {
		return (String) getDynamicProperty("required");
	}

	/**
	 * Sets the required of this select tag.
	 * @since 8.0.3
	 */
	public void setRequired(String required) throws WrongValueException {
		setDynamicProperty("required", required);
	};
	/**
	 * Returns the size of this select tag.
	 * @since 8.0.3
	 */
	public String getSize() {
		return (String) getDynamicProperty("size");
	}

	/**
	 * Sets the size of this select tag.
	 * @since 8.0.3
	 */
	public void setSize(String size) throws WrongValueException {
		setDynamicProperty("size", size);
	};

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
