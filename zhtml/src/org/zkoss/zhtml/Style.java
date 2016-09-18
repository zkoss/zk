/* Style.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:31:44     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zk.ui.WrongValueException;

/**
 * The STYLE tag.
 * 
 * @author tomyeh
 */
public class Style extends org.zkoss.zhtml.impl.ContentTag {
	public Style() {
		super("style");
	}

	public Style(String content) {
		super("style", content);
	}
	/**
	 * Returns the media of this style tag.
	 * @since 8.0.3
	 */
	public String getMedia() {
		return (String) getDynamicProperty("media");
	}

	/**
	 * Sets the media of this style tag.
	 * @since 8.0.3
	 */
	public void setMedia(String media) throws WrongValueException {
		setDynamicProperty("media", media);
	}
	/**
	 * Returns the scoped of this style tag.
	 * @since 8.0.3
	 */
	public Boolean isScoped() {
		return getDynamicProperty("scoped") != null;
	}

	/**
	 * Sets the scoped of this style tag.
	 * @since 8.0.3
	 */
	public void setScoped(Boolean scoped) throws WrongValueException {
		setDynamicProperty("scoped", scoped ? true : null);
	}
	/**
	 * Returns the type of this style tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this style tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
}
