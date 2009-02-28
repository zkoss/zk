/* Fisheye.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 09:22:13     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkex.zul.api;

/**
 * A fisheye item.
 * 
 * <p>
 * Default {@link org.zkoss.zkex.zul.Fisheye#getZclass}: z-fisheye.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public interface Fisheye extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the label (never null).
	 * <p>
	 * Default: "".
	 */
	public String getLabel();

	/**
	 * Sets the label.
	 */
	public void setLabel(String label);

	/**
	 * Returns the image URI.
	 * <p>
	 * Default: null.
	 */
	public String getImage();

	/**
	 * Sets the image URI.
	 */
	public void setImage(String image);

}
