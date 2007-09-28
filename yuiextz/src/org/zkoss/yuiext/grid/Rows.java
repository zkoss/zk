/* Rows.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 22, 2007 3:07:51 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkforge.yuiext.grid;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import  org.zkforge.yuiext.grid.Grid;
import  org.zkforge.yuiext.grid.Row;
import org.zkoss.zul.impl.XulElement;

/** 
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 *
 * @author jumperchen
 *
 */
public class Rows extends XulElement {
	/** Returns the grid that contains this rows. */
	public Grid getGrid() {
		return (Grid)getParent();
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for rows: "+parent);
		final boolean changed = getParent() != parent;
		super.setParent(parent);
		if (changed && parent != null) ((Grid)parent).initAtClient();
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: "+child);
		return super.insertBefore(child, insertBefore);
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);

		final Grid grid = getGrid();
		if (grid != null) {
			grid.initAtClient();
		}
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);

		final Grid grid = getGrid();
		if (grid != null) {
			grid.initAtClient();
		}
    }
}
