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
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isDisabled() {
		final Boolean b = (Boolean) getDynamicProperty("disabled");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the disabled of this fieldset tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setDisabled(boolean disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled ? Boolean.valueOf(disabled) : null);
	}

	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this fieldset tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	}
}
