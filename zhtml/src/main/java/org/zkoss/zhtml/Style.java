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
	public boolean isScoped() {
		final Boolean b = (Boolean) getDynamicProperty("scoped");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the scoped of this style tag.
	 * @since 8.0.3
	 */
	public void setScoped(boolean scoped) throws WrongValueException {
		setDynamicProperty("scoped", scoped ? Boolean.valueOf(scoped) : null);
	}
}
