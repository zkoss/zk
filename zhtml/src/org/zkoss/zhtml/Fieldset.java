/* Fieldset.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:01:13     2005, Created by tomyeh

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
 * The FIELDSET tag.
 * 
 * @author tomyeh
 */
public class Fieldset extends AbstractTag {
	public Fieldset() {
		super("fieldset");
	}
	/**
	 * Returns the disabled of this fieldset tag.
	 * @since 8.0.3
	 */
	public Boolean isDisabled() {
		return getDynamicProperty("disabled") != null;
	}

	/**
	 * Sets the disabled of this fieldset tag.
	 * @since 8.0.3
	 */
	public void setAutofocus(Boolean autofocus) throws WrongValueException {
		setDynamicProperty("autofocus", autofocus ? true : null);
	}
	/**
	 * Returns the form of this fieldset tag.
	 * @since 8.0.3
	 */
	public String getForm() {
		return (String) getDynamicProperty("form");
	}

	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this fieldset tag.
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	}
}
