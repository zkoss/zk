/* Footer.java

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
 * A column of the footer of a grid ({@link Grid}). Its parent must be
 * {@link Foot}.
 * 
 * <p>
 * Unlike {@link Column}, you could place any child in a grid footer.
 * <p>
 * Default {@link #getZclass}: z-footer.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Footer extends org.zkoss.zul.impl.api.LabelImageElement {
	/**
	 * Returns the grid that this belongs to.
	 */
	public org.zkoss.zul.api.Grid getGridApi();

	/**
	 * Returns the column index, starting from 0.
	 */
	public int getColumnIndex();

	/**
	 * Returns the column that is in the same column as this footer, or null if
	 * not available.
	 */
	public org.zkoss.zul.api.Column getColumnApi();

	/**
	 * Returns number of columns to span this footer. Default: 1.
	 */
	public int getSpan();

	/**
	 * Sets the number of columns to span this footer.
	 * <p>
	 * It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span);

}
