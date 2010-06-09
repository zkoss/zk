/* Area.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * An area of a {@link Imagemap}.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Area {
	/**
	 * Returns the text as the tooltip.
	 * <p>
	 * Default: null.
	 */
	public String getTooltiptext();

	/**
	 * Sets the text as the tooltip.
	 */
	public void setTooltiptext(String tooltiptext);
}
