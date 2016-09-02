/* Menu.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:22:56     2005, Created by tomyeh

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
 * The MENU tag.
 * 
 * @author tomyeh
 */
public class Menu extends AbstractTag {
	public Menu() {
		super("menu");
	}
	/**
	 * Returns the label of this menu tag.
	 * @since 8.0.3
	 */
	public String getLabel() {
		return (String) getDynamicProperty("label");
	}

	/**
	 * Sets the label of this menu tag.
	 * @since 8.0.3
	 */
	public void setLabel(String label) throws WrongValueException {
		setDynamicProperty("label", label);
	};
	/**
	 * Returns the type of this menu tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this menu tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	};
}
