/* Ol.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:53:30     2005, Created by tomyeh

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
 * The OL tag.
 * 
 * @author tomyeh
 */
public class Ol extends AbstractTag {
	public Ol() {
		super("ol");
	}
	/**
	 * Returns the reversed of this ol tag.
	 * @since 8.0.3
	 */
	public Boolean isReversed() {
		return getDynamicProperty("reversed") != null;
	}

	/**
	 * Sets the reversed of this ol tag.
	 * @since 8.0.3
	 */
	public void setReversed(Boolean reversed) throws WrongValueException {
		setDynamicProperty("reversed", reversed ? true : null);
	}
	/**
	 * Returns the start of this ol tag.
	 * @since 8.0.3
	 */
	public Integer getStart() {
		return (Integer) getDynamicProperty("start");
	}

	/**
	 * Sets the start of this ol tag.
	 * @since 8.0.3
	 */
	public void setStart(Integer start) throws WrongValueException {
		setDynamicProperty("start", start);
	}
	/**
	 * Returns the type of this ol tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this ol tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
}
