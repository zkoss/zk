/* Portallayout.java

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
package org.zkoss.zkmax.zul.api;

import org.zkoss.zul.Panel;

/**
 * A portal layout lays out a container which can have multiple columns, and
 * each column may contain one or more panel. Portal layout provides a way to
 * drag-and-drop panel into other portalchildren from the same portal layout.<br>
 * 
 * Use Portallayout need assign width (either present or pixel) on every
 * Portalchildren, or we cannot make sure about layout look.
 * 
 * <p>
 * Events:<br/>
 * onPortalMove.<br/>
 * 
 * <p>
 * Default {@link org.zkoss.zkmax.zul.Portallayout#getZclass}: z-portal-layout.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public interface Portallayout extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the specified pnael, or null if not available.
	 * 
	 * @param col
	 *            which Portalchildren to fetch (starting at 0).
	 * @param row
	 *            which Panel to fetch (starting at 0).
	 */
	public Panel getPanel(int col, int row);

	/**
	 * Sets the specified panel via the position(col and row).
	 * 
	 * @param panel
	 *            a new panel component
	 * @param col
	 *            which Portalchildren to fetch (starting at 0).
	 * @param row
	 *            which Panel to fetch (starting at 0).
	 * @return If false, the added panel fails.
	 */
	public boolean setPanel(Panel panel, int col, int row);

	/**
	 * Returns an int array[col, row] that indicates the specified panel located
	 * within this portal layout. If not found, [-1, -1] is assumed.
	 */
	public int[] getPosition(Panel panel);

}
