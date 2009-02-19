/* Detail.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;


/**
 * The detail component is used to display a detailed section where a master row
 * and multiple detail rows are on the same row.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>onOpen is sent when this component is opened or closed by user.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Default {@link #getZclass}: z-detail.
 * 
 * <p>
 * Default {@link #getWidth}: 18px. It depends on the width of the icon of the
 * detail.
 * 
 * @author jumperchen
 * @since 3.5.2
 */
public interface Detail extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the CSS style for the content block of the window.
	 */
	public String getContentStyle();

	/**
	 * Sets the CSS style for the content block of the window.
	 * 
	 * <p>
	 * Default: null.
	 */
	public void setContentStyle(String style);

	/**
	 * Returns the style class used for the content block.
	 */
	public String getContentSclass();

	/**
	 * Sets the style class used for the content block.
	 */
	public void setContentSclass(String scls);

	/**
	 * Returns the URI of the button image.
	 */
	public String getImage();

	/**
	 * Sets the URI of the button image.
	 * 
	 * @param img
	 *            the URI of the button image. If null or empty, the default URI
	 *            is used.
	 */
	public void setImage(String img);

	/**
	 * Sets whether the detail is open.
	 */
	public void setOpen(boolean open);

	/**
	 * Returns whether the detail is open.
	 */
	public boolean isOpen();

}
