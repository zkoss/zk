/* Base.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:19:08     2005, Created by tomyeh

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
 * The BASE tag.
 * 
 * @author tomyeh
 */
public class Base extends AbstractTag {
	public Base() {
		super("base");
	}
	/**
	 * Returns the href of this base tag.
	 * @since 8.0.3
	 */
	public String getHref() {
		return (String) getDynamicProperty("href");
	}

	/**
	 * Sets the href of this base tag.
	 * @since 8.0.3
	 */
	public void setHref(String href) throws WrongValueException {
		setDynamicProperty("href", href);
	};
	/**
	 * Returns the target of this base tag.
	 * @since 8.0.3
	 */
	public String getTarget() {
		return (String) getDynamicProperty("target");
	}

	/**
	 * Sets the target of this base tag.
	 * @since 8.0.3
	 */
	public void setTarget(String target) throws WrongValueException {
		setDynamicProperty("target", target);
	};
}
