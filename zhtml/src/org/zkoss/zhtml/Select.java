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
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isAutofocus() {
		final Boolean b = (Boolean) getDynamicProperty("autofocus");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the autofocus of this select tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setAutofocus(boolean autofocus) throws WrongValueException {
		setDynamicProperty("autofocus", autofocus ? Boolean.valueOf(autofocus) : null);
	}
	/**
	 * Returns the disabled of this select tag.
	 * @since 8.0.3
	 */
	public boolean isDisabled() {
		final Boolean b = (Boolean) getDynamicProperty("disabled");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the disabled of this select tag.
	 * @since 8.0.3
	 */
	public void setDisabled(boolean disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled ? Boolean.valueOf(disabled) : null);
	}
	/**
	 * Returns the multiple of this select tag.
	 * @since 8.0.3
	 */
	public boolean isMultiple() {
		final Boolean b = (Boolean) getDynamicProperty("multiple");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the multiple of this select tag
	 * @since 8.0.3
	 */
	public void setMultiple(boolean multiple) throws WrongValueException {
		setDynamicProperty("multiple", multiple ? Boolean.valueOf(multiple) : null);
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
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isRequired() {
		final Boolean b = (Boolean) getDynamicProperty("required");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the required of this select tag
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setRequired(boolean required) throws WrongValueException {
		setDynamicProperty("required", required ? Boolean.valueOf(required) : null);
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
