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
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isReversed() {
		final Boolean b = (Boolean) getDynamicProperty("reversed");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the reversed of this ol tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setReversed(boolean reversed) throws WrongValueException {
		setDynamicProperty("reversed", reversed ? Boolean.valueOf(reversed) : null);
	}
	/**
	 * Returns the start of this ol tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public Integer getStart() {
		return (Integer) getDynamicProperty("start");
	}

	/**
	 * Sets the start of this ol tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
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
