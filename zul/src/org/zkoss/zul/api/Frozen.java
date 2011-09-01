/* Frozen.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 2, 2009 9:45:47 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.api;

import org.zkoss.zul.impl.api.XulElement;

/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * @author jumperchen
 * @since 5.0.0
 */
public interface Frozen extends XulElement {

	/**
	 * Sets the number of columns to freeze.(from left to right)
	 * @param columns positive only
	 */
	public void setColumns(int columns);

	/**
	 * Returns the number of columns to freeze.
	 * <p>Default: 0
	 */
	public int getColumns();

	/**
	 * Sets the number of rows to freeze.(from top to bottom)
	 * @param rows positive only
	 */
	public void setRows(int rows);

	/**
	 * Returns the number of rows to freeze.
	 * <p>Default: 0
	 */
	public int getRows();
}
