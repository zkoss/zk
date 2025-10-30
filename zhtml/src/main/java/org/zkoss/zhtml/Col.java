/* Col.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:59:46     2005, Created by tomyeh

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
 * The COL tag.
 * 
 * @author tomyeh
 */
public class Col extends AbstractTag {
	public Col() {
		super("col");
	}
	/**
	 * Returns the span of this col tag.
	 * @since 8.0.3
	 */
	public Integer getSpan() {
		return (Integer) getDynamicProperty("span");
	}

	/**
	 * Sets the span of this col tag.
	 * @since 8.0.3
	 */
	public void setSpan(Integer span) throws WrongValueException {
		setDynamicProperty("span", span);
	}
}
