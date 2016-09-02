/* Optgroup.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:04:02     2005, Created by tomyeh

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
 * The OPTGROUP tag.
 * 
 * @author tomyeh
 */
public class Optgroup extends AbstractTag {
	public Optgroup() {
		super("optgroup");
	}
	/**
	 * Returns the disabled of this optgroup tag.
	 * @since 8.0.3
	 */
	public String getDisabled() {
		return (String) getDynamicProperty("disabled");
	}

	/**
	 * Sets the disabled of this optgroup tag.
	 * @since 8.0.3
	 */
	public void setDisabled(String disabled) throws WrongValueException {
		setDynamicProperty("disabled", disabled);
	};
	/**
	 * Returns the label of this optgroup tag.
	 * @since 8.0.3
	 */
	public String getLabel() {
		return (String) getDynamicProperty("label");
	}

	/**
	 * Sets the label of this optgroup tag.
	 * @since 8.0.3
	 */
	public void setLabel(String label) throws WrongValueException {
		setDynamicProperty("label", label);
	};
}
