/* Del.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:20:29     2005, Created by tomyeh

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
 * The DEL tag.
 * 
 * @author tomyeh
 */
public class Del extends AbstractTag {
	public Del() {
		super("del");
	}
	/**
	 * Returns the cite of this del tag.
	 * @since 8.0.3
	 */
	public String getCite() {
		return (String) getDynamicProperty("cite");
	}

	/**
	 * Sets the cite of this del tag.
	 * @since 8.0.3
	 */
	public void setCite(String cite) throws WrongValueException {
		setDynamicProperty("cite", cite);
	}
	/**
	 * Returns the datetime of this del tag.
	 * @since 8.0.3
	 */
	public String getDatetime() {
		return (String) getDynamicProperty("datetime");
	}

	/**
	 * Sets the datetime of this del tag.
	 * @since 8.0.3
	 */
	public void setDatetime(String datetime) throws WrongValueException {
		setDynamicProperty("datetime", datetime);
	}
}
