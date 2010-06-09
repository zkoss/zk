/* Row.java

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

import org.zkoss.zk.ui.WrongValueException;

/**
 * A single row in a {@link Rows} element. Each child of the {@link Row} element
 * is placed in each successive cell of the grid. The row with the most child
 * elements determines the number of columns in each row.
 * 
 * <p>
 * Default {@link #getZclass}: z-row. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Row extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the child detail component.
	 * 
	 */
	public org.zkoss.zul.api.Detail getDetailChildApi();

	/** Returns the grid that contains this row. */
	public org.zkoss.zul.api.Grid getGridApi();

	/**
	 * Returns the horizontal alignment of the whole row.
	 * <p>
	 * Default: null (system default: left unless CSS specified).
	 */
	public String getAlign();

	/**
	 * Sets the horizontal alignment of the whole row.
	 */
	public void setAlign(String align);

	/**
	 * Returns the nowrap.
	 * <p>
	 * Default: null (system default: wrap).
	 */
	public boolean isNowrap();

	/**
	 * Sets the nowrap.
	 */
	public void setNowrap(boolean nowrap);

	/**
	 * Returns the vertical alignment of the whole row.
	 * <p>
	 * Default: null (system default: top).
	 */
	public String getValign();

	/**
	 * Sets the vertical alignment of the whole row.
	 */
	public void setValign(String valign);

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
	 * Returns the spans, which is a list of numbers separated by comma.
	 * 
	 * <p>
	 * Default: empty.
	 * 
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public String getSpans();

	/**
	 * Sets the spans, which is a list of numbers separated by comma.
	 * 
	 * <p>
	 * For example, "1,2,3" means the second column will span two columns and
	 * the following column span three columns, while others occupies one
	 * column.
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public void setSpans(String spans) throws WrongValueException;

	/**
	 * Returns the group that this row belongs to, or null.
	 * 
	 */
	public org.zkoss.zul.api.Group getGroupApi();

}
