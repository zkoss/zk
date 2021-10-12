/* Img.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:02:25     2005, Created by tomyeh

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
 * The IMG tag.
 * 
 * @author tomyeh
 */
public class Img extends AbstractTag {
	public Img() {
		super("img");
	}
	/**
	 * Returns the alt of this img tag.
	 * @since 8.0.3
	 */
	public String getAlt() {
		return (String) getDynamicProperty("alt");
	}

	/**
	 * Sets the alt of this img tag.
	 * @since 8.0.3
	 */
	public void setAlt(String alt) throws WrongValueException {
		setDynamicProperty("alt", alt);
	}
	/**
	 * Returns the crossorigin of this img tag.
	 * @since 8.0.3
	 */
	public String getCrossorigin() {
		return (String) getDynamicProperty("crossorigin");
	}

	/**
	 * Sets the crossorigin of this img tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setCrossorigin(String crossorigin) throws WrongValueException {
		setDynamicProperty("crossorigin", crossorigin);
	}
	/**
	 * Returns the height of this img tag.
	 * @since 8.0.3
	 */
	public String getHeight() {
		return (String) getDynamicProperty("height");
	}

	/**
	 * Sets the height of this img tag.
	 * @since 8.0.3
	 */
	public void setHeight(String height) throws WrongValueException {
		setDynamicProperty("height", height);
	}
	/**
	 * Returns the ismap of this img tag.
	 * @since 8.0.3
	 */
	public boolean isIsmap() {
		final Boolean b = (Boolean) getDynamicProperty("ismap");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the ismap of this img tag.
	 * @since 8.0.3
	 */
	public void setIsmap(boolean ismap) throws WrongValueException {
		setDynamicProperty("ismap", ismap ? Boolean.valueOf(ismap) : null);
	}
	/**
	 * Returns the longdesc of this img tag.
	 * @since 8.0.3
	 */
	public String getLongdesc() {
		return (String) getDynamicProperty("longdesc");
	}

	/**
	 * Sets the longdesc of this img tag.
	 * @since 8.0.3
	 */
	public void setLongdesc(String longdesc) throws WrongValueException {
		setDynamicProperty("longdesc", longdesc);
	}
	/**
	 * Returns the src of this img tag.
	 * @since 8.0.3
	 */
	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	/**
	 * Sets the src of this img tag.
	 * @since 8.0.3
	 */
	public void setSrc(String src) throws WrongValueException {
		setDynamicProperty("src", src);
	}
	/**
	 * Returns the usemap of this img tag.
	 * @since 8.0.3
	 */
	public String getUsemap() {
		return (String) getDynamicProperty("usemap");
	}

	/**
	 * Sets the usemap of this img tag.
	 * @since 8.0.3
	 */
	public void setUsemap(String usemap) throws WrongValueException {
		setDynamicProperty("usemap", usemap);
	}
	/**
	 * Returns the width of this img tag.
	 * @since 8.0.3
	 */
	public String getWidth() {
		return (String) getDynamicProperty("width");
	}

	/**
	 * Sets the width of this img tag.
	 * @since 8.0.3
	 */
	public void setWidth(String width) throws WrongValueException {
		setDynamicProperty("width", width);
	}
}
