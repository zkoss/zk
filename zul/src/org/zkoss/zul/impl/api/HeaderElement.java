/* HeaderElement.java

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
package org.zkoss.zul.impl.api;

/**
 * A skeletal implementation for a header.
 * 
 * @author tomyeh
 */
abstract public interface HeaderElement extends LabelImageElement {
	/**
	 * Returns the horizontal alignment of this column.
	 * <p>
	 * Default: null (system default: left unless CSS specified).
	 */
	public String getAlign();

	/**
	 * Sets the horizontal alignment of this column.
	 */
	public void setAlign(String align);

	/**
	 * Returns the vertical alignment of this grid.
	 * <p>
	 * Default: null (system default: top).
	 */
	public String getValign();

	/**
	 * Sets the vertical alignment of this grid.
	 */
	public void setValign(String valign);

	/**
	 * Returns the attributes used to generate HTML TD tag for each cell of the
	 * rows contained in the parent control, e.g.,
	 * {@link org.zkoss.zul.Listcell}.
	 * <p>
	 * Used by component developers.
	 */
	public String getColAttrs();

	// -- super --//
	public boolean setVisible(boolean visible);

	public String getOuterAttrs();
}
