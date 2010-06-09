/* Separator.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * A separator.
 * <p>
 * Default {@link #getZclass} as follows: (since 3.5.0)
 * <ol>
 * <li>Case 1: If {@link #getOrient()} is vertical and {@link #isBar()} is
 * false, "z-separator-ver" is assumed</li>
 * <li>Case 2: If {@link #getOrient()} is vertical and {@link #isBar()} is true,
 * "z-separator-ver-bar" is assumed</li>
 * <li>Case 3: If {@link #getOrient()} is horizontal and {@link #isBar()} is
 * false, "z-separator-hor" is assumed</li>
 * <li>Case 4: If {@link #getOrient()} is horizontal and {@link #isBar()} is
 * true, "z-separator-hor-bar" is assumed</li>
 * </ol>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Separator extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the orient.
	 * <p>
	 * Default: "horizontal".
	 */
	public String getOrient();

	/**
	 * Sets the orient.
	 * 
	 * @param orient
	 *            either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException;

	/**
	 * Returns whether it is a horizontal separator.
	 * 
	 */
	public boolean isHorizontal();

	/**
	 * Returns whether it is a vertical separator.
	 * 
	 */
	public boolean isVertical();

	/**
	 * Returns whether to display a visual bar as the separator.
	 * <p>
	 * Default: false
	 */
	public boolean isBar();

	/**
	 * Sets whether to display a visual bar as the separator.
	 */
	public void setBar(boolean bar);

	/**
	 * Returns the spacing.
	 * <p>
	 * Default: null (depending on CSS).
	 */
	public String getSpacing();

	/**
	 * Sets the spacing.
	 * 
	 * @param spacing
	 *            the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	public void setSpacing(String spacing);
}
