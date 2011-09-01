/* Listcell.java

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

/**
 * A list cell.
 * 
 * <p>
 * Default {@link #getZclass}: z-listcell (since 5.0.0)
 * 
 * @author tomyeh
 */
public interface Listcell extends org.zkoss.zul.impl.api.LabelImageElement {

	/**
	 * Returns the list box that it belongs to.
	 */
	public org.zkoss.zul.api.Listbox getListboxApi();

	/**
	 * Returns the list header that is in the same column as this cell, or null
	 * if not available.
	 */
	public org.zkoss.zul.api.Listheader getListheaderApi();

	/**
	 * Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex();

	/**
	 * Returns the maximal length for this cell. If listbox's mold is "select",
	 * it is the same as {@link Listbox#getMaxlength} If not, it is the same as
	 * the correponding {@link #getListheaderApi}'s {@link Listheader#getMaxlength}
	 * .
	 * 
	 * <p>
	 * Note: {@link Listitem#getMaxlength} is the same as
	 * {@link Listbox#getMaxlength}.
	 */
	public int getMaxlength();

	/**
	 * Returns the value.
	 * <p>
	 * Default: null.
	 * <p>
	 * Note: the value is application dependent, you can place whatever value
	 * you want.
	 */
	public Object getValue();

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value.
	 *            <p>
	 *            Note: the value is application dependent, you can place
	 *            whatever value you want.
	 */
	public void setValue(Object value);

	/**
	 * Returns number of columns to span this cell. Default: 1.
	 */
	public int getSpan();

	/**
	 * Sets the number of columns to span this cell.
	 * <p>
	 * It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span);

}
