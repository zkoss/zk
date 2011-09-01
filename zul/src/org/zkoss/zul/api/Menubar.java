/* Menubar.java

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
 * A container that usually contains menu elements.
 * 
 * <p>
 * Default {@link #getZclass}: z-menubar-hor, if {@link #getOrient()} ==
 * vertical, z-menubar-ver will be added.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Menubar extends org.zkoss.zul.impl.api.XulElement {

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
	 *            either horizontal or vertical
	 */
	public void setOrient(String orient) throws WrongValueException;

	/** Returns whether to automatically drop down menus if user moves mouse
	 * over it.
	 * <p>Default: false.
	 */
	public boolean isAutodrop();
	/**
	 * Sets whether to automatically drop down menus if user moves mouse over
	 * it.
	 */
	public void setAutodrop(boolean autodrop);

}
