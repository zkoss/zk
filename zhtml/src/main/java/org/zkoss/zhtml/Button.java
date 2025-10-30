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
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isAutofocus() {
		final Boolean b = (Boolean) getDynamicProperty("autofocus");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the autofocus of this button tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setAutofocus(boolean autofocus) throws WrongValueException {
		setDynamicProperty("autofocus", autofocus ? Boolean.valueOf(autofocus) : null);
	}
	/**
	 * Returns the disabled of this button tag.
	 * @since 8.0.3
	 */
	public boolean isDisabled() {
		final Boolean b = (Boolean) getDynamicProperty("disabled");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the disabled of this button tag.
	 * @since 8.0.3
	 */
	public void setDisabled(boolean disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled ? Boolean.valueOf(disabled) : null);
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
	/**
	 * Returns the form of this button tag.
	 * @since 10.0.0
	 */
	public String getForm() {
		return (String) getDynamicProperty("form");
	}

	/**
	 * Sets the form of this button tag.
	 * <p> The {@literal <}form{@literal >} element to associate the button with (its form owner).
	 * The value of this attribute must be the id of a {@literal <}form{@literal >} in the same document.
	 * (If this attribute is not set, the {@literal <}button{@literal >} is associated with its ancestor {@literal <}form{@literal >}
	 * element, if any.)
	 * <p>
	 * This attribute lets you associate {@literal <}button{@literal >} elements to {@literal <}form{@literal >}s anywhere
	 * in the document, not just inside a {@literal <}form{@literal >}. It can also override
	 * an ancestor {@literal <}form{@literal >} element.
	 * @since 10.0.0
	 */
	public void setForm(String form) throws WrongValueException {
		setDynamicProperty("form", form);
	}

	/**
	 * Returns the formaction of this button tag.
	 * @since 10.0.0
	 */
	public String getFormaction() {
		return (String) getDynamicProperty("formaction");
	}

	/**
	 * Sets the formaction of this button tag.
	 * <p>The URL that processes the information submitted by the button. Overrides
	 * the {@code action} attribute of the button's form owner. Does nothing if there is no form owner.
	 * @since 10.0.0
	 */
	public void setFormaction(String formaction) throws WrongValueException {
		setDynamicProperty("formaction", formaction);
	}

	/**
	 * Returns the formenctype of this button tag.
	 * @since 10.0.0
	 */
	public String getFormenctype() {
		return (String) getDynamicProperty("formenctype");
	}

	/**
	 * Set the formenctype of this button tag.
	 * <p>If the button is a submit button (it's inside/associated with a {@literal <}form{@literal >}
	 * and doesn't have {@code type="button"}), specifies how to encode the form data that is submitted.
	 * @since 10.0.0
	 */
	public void setFormenctype(String formenctype) throws WrongValueException {
		setDynamicProperty("formenctype", formenctype);
	}

	/**
	 * Returns the formmethod of this tag.
	 *
	 * @since 10.0.0
	 */
	public String getFormmethod() {
		return (String) getDynamicProperty("formmethod");
	}

	/**
	 * Sets the formmethod of this tag.
	 * <p>If the button is a submit button (it's inside/associated with a {@literal <}form{@literal >}
	 * and doesn't have {@code type="button"}), this attribute specifies the HTTP method used to submit the form.
	 * @since 10.0.0
	 */
	public void setFormmethod(String formmethod) throws WrongValueException {
		setDynamicProperty("formmethod", formmethod);
	}

	/**
	 * Returns the formnovalidate of this tag.
	 *
	 * @since 10.0.0
	 */
	public String getFormnovalidate() {
		return (String) getDynamicProperty("formnovalidate");
	}

	/**
	 * Sets the formnovalidate of this tag.
	 * <p>If the button is a submit button, this Boolean attribute specifies that
	 * the form is not to be validated when it is submitted. If this attribute is
	 * specified, it overrides the novalidate attribute of the button's form owner.
	 *
	 * @since 10.0.0
	 */
	public void setFormnovalidate(String formnovalidate) throws WrongValueException {
		setDynamicProperty("formnovalidate", formnovalidate);
	}

	/**
	 * Returns the formtarget of this tag.
	 *
	 * @since 10.0.0
	 */
	public String getFormtarget() {
		return (String) getDynamicProperty("formtarget");
	}

	/**
	 * Sets the formtarget of this tag.
	 * <p>If the button is a submit button, this attribute is an author-defined name
	 * or standardized, underscore-prefixed keyword indicating where to display
	 * the response from submitting the form. This is the name of, or keyword for,
	 * a browsing context (a tab, window, or {@literal <}iframe{@literal >}).
	 * If this attribute is specified, it overrides the target attribute of the button's form owner.
	 *
	 * @since 10.0.0
	 */
	public void setFormtarget(String formtarget) throws WrongValueException {
		setDynamicProperty("formtarget", formtarget);
	}
}
