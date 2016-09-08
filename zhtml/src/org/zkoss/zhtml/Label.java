/* Label.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:03:03     2005, Created by tomyeh

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
 * The LABEL tag.
 * 
 * @author tomyeh
 */
public class Label extends AbstractTag {
	public Label() {
		super("label");
	}
	/**
	 * Returns the for of this label tag.
	 * @since 8.0.3
	 */
	public String getFor() {
		return (String) getDynamicProperty("for");
	}

	/**
	 * Sets the for of this label tag.
	 * @since 8.0.3
	 */
	public void setFor(String forValue) throws WrongValueException {
		setDynamicProperty("for", forValue);
	};
}
