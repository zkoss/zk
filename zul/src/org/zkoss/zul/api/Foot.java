/* Foot.java

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
 * Defines a set of footers ({@link Footer}) for a grid ({@link Grid}).
 * <p>
 * Default {@link #getZclass}: z-foot.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Foot extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the grid that it belongs to. It is the same as {@link #getParent}
	 * .
	 */
	public org.zkoss.zul.api.Grid getGridApi();
}
