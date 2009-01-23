/* Columns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * Defines the columns of a grid. Each child of a columns element should be a
 * {@link Column} element.
 * <p>
 * Default {@link #getZclass}: z-columns.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Columns extends org.zkoss.zul.impl.api.HeadersElement {

	/**
	 * Returns the grid that it belongs to.
	 * <p>
	 * It is the same as {@link #getParent}.
	 */
	public org.zkoss.zul.api.Grid getGridApi();

	/**
	 * Sets whether to enable hiding of columns with the header context menu.
	 * <p>
	 * Note that it is only applied when {@link #getMenupopup()} is auto.
	 * 
	 */
	public void setColumnshide(boolean columnshide);

	/**
	 * Returns whether to enable hiding of columns with the header context menu.
	 * <p>
	 * Default: true.
	 * 
	 */
	public boolean isColumnshide();

	/**
	 * Sets whether to enable grouping of columns with the header context menu.
	 * <p>
	 * Note that it is only applied when {@link #getMenupopup()} is auto.
	 * 
	 */
	public void setColumnsgroup(boolean columnsgroup);

	/**
	 * Returns whether to enable grouping of columns with the header context
	 * menu.
	 * <p>
	 * Default: true.
	 * 
	 */
	public boolean isColumnsgroup();

}
