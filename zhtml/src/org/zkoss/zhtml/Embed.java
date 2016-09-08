/* Embed.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:00:58     2005, Created by tomyeh

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
 * The EMBED tag.
 * 
 * @author tomyeh
 */
public class Embed extends AbstractTag {
	public Embed() {
		super("embed");
	}
	/**
	 * Returns the height of this embed tag.
	 * @since 8.0.3
	 */
	public String getHeight() {
		return (String) getDynamicProperty("height");
	}

	/**
	 * Sets the height of this embed tag.
	 * @since 8.0.3
	 */
	public void setHeight(String height) throws WrongValueException {
		setDynamicProperty("height", height);
	};
	/**
	 * Returns the src of this embed tag.
	 * @since 8.0.3
	 */
	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	/**
	 * Sets the src of this embed tag.
	 * @since 8.0.3
	 */
	public void setSrc(String src) throws WrongValueException {
		setDynamicProperty("src", src);
	};
	/**
	 * Returns the type of this embed tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this embed tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	};
	/**
	 * Returns the width of this embed tag.
	 * @since 8.0.3
	 */
	public String getWidth() {
		return (String) getDynamicProperty("width");
	}

	/**
	 * Sets the width of this embed tag.
	 * @since 8.0.3
	 */
	public void setWidth(String width) throws WrongValueException {
		setDynamicProperty("width", width);
	};
}
