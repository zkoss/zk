/* Object.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:03:49     2005, Created by tomyeh

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
 * The OBJECT tag.
 * 
 * @author tomyeh
 */
public class Object extends AbstractTag {
	public Object() {
		super("object");
	}
	/**
	 * Returns the data of this object tag.
	 * @since 8.0.3
	 */
	public String getData() {
		return (String) getDynamicProperty("data");
	}

	/**
	 * Sets the data of this object tag.
	 * @since 8.0.3
	 */
	public void setData(String data) throws WrongValueException {
		setDynamicProperty("data", data);
	};
	/**
	 * Returns the height of this object tag.
	 * @since 8.0.3
	 */
	public String getHeight() {
		return (String) getDynamicProperty("height");
	}

	/**
	 * Sets the height of this object tag.
	 * @since 8.0.3
	 */
	public void setHeight(String height) throws WrongValueException {
		setDynamicProperty("height", height);
	};
	/**
	 * Returns the name of this object tag.
	 * @since 8.0.3
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this object tag.
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	};
	/**
	 * Returns the type of this object tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this object tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	};
	/**
	 * Returns the usemap of this object tag.
	 * @since 8.0.3
	 */
	public String getUsemap() {
		return (String) getDynamicProperty("usemap");
	}

	/**
	 * Sets the usemap of this object tag.
	 * @since 8.0.3
	 */
	public void setUsemap(String usemap) throws WrongValueException {
		setDynamicProperty("usemap", usemap);
	};
	/**
	 * Returns the width of this object tag.
	 * @since 8.0.3
	 */
	public String getWidth() {
		return (String) getDynamicProperty("width");
	}

	/**
	 * Sets the width of this object tag.
	 * @since 8.0.3
	 */
	public void setWidth(String width) throws WrongValueException {
		setDynamicProperty("width", width);
	};
}
