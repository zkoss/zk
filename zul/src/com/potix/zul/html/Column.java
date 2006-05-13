/* Column.java

{{IS_NOTE
	$Id: Column.java,v 1.11 2006/02/27 03:55:11 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:36     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.impl.HeaderElement;

/**
 * A single column in a {@link Columns} element.
 * Each child of the {@link Column} element is placed in each successive
 * cell of the grid.
 * The column with the most child elements determines the number of rows
 * in each column.
 *
 * <p>The use of column is mainly to define attributes for each cell
 * in the grid.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.11 $ $Date: 2006/02/27 03:55:11 $
 */
public class Column extends HeaderElement {
	public Column() {
	}
	public Column(String label) {
		setLabel(label);
	}
	public Column(String label, String src) {
		setLabel(label);
		setImage(src);
	}

	/** Returns the grid that contains this column. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid)parent.getParent(): null;
	}

	//-- super --//
	/** Invalidates the whole grid. */
	protected void invalidateWhole() {
		final Grid grid = getGrid();
		if (grid != null) grid.invalidate(INNER);
	}

	//-- Component --//
	public void invalidate(Range range) {
		super.invalidate(range);
		initAtClient();
	}
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Columns))
			throw new UiException("Unsupported parent for column: "+parent);
		super.setParent(parent);
	}
	private void initAtClient() {
		final Grid grid = getGrid();
		if (grid != null) grid.initAtClient(); //because width might change
	}
}
