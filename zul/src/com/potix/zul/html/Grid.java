/* Grid.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 15:40:35     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;

import com.potix.lang.Objects;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.XulElement;

/**
 * A grid is an element that contains both rows and columns elements.
 * It is used to create a grid of elements.
 * Both the rows and columns are displayed at once although only one will
 * typically contain content, while the other may provide size information.
 *
 * <p>Default {@link #getSclass}: grid.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Grid extends XulElement {
	private Rows _rows;
	private Columns _cols;
	private String _align;

	public Grid() {
		setSclass("grid");
	}

	/** Returns the rows.
	 */
	public Rows getRows() {
		return _rows;
	}
	/** Returns the columns.
	 */
	public Columns getColumns() {
		return _cols;
	}

	/** Returns the specified cell, or null if not available.
	 * @param row which row to fetch (starting at 0).
	 * @param col which column to fetch (starting at 0).
	 */
	public Component getCell(int row, int col) {
		final Rows rows = getRows();
		if (rows == null) return null;

		List children = rows.getChildren();
		if (children.size() <= row) return null;

		children = ((Row)children.get(row)).getChildren();
		return children.size() <= col ? null: (Component)children.get(col);
	}

	/** Returns the horizontal alignment of the whole grid.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment of the whole grid.
	 * <p>Allowed: "left", "center", "right"
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	/** Re-init the grid at the client.
	 */
	/*package*/ void initAtClient() {
		smartUpdate("zk_init", true);
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		return _align != null ? attrs + " align=\"" + _align +'"': attrs;
	}

	//-- Component --//
	public void invalidate(Range range) {
		super.invalidate(range);
		if (range != OUTER) initAtClient();
			//We have to re-init because inner tags are changed
			//If OUTER, init is invoked automatically by client
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Rows) {
			if (_rows != null && _rows != child)
				throw new UiException("Only a rows child is allowed: "+this);
			_rows = (Rows)child;
		} else if (child instanceof Columns) {
			if (_cols != null && _cols != child)
				throw new UiException("Only a columns child is allowed: "+this);
			_cols = (Columns)child;
		} else {
			throw new UiException("Unsupported child for grid: "+child);
		}
 
		if (super.insertBefore(child, insertBefore)) {
			invalidate(INNER);
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		if (!super.removeChild(child))
			return false;

		if (_rows == child) _rows = null;
		else if (_cols == child) _cols = null;
		invalidate(INNER);
		return true;
	}
}
