/* Rows.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:39     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ext.Cropper;

import com.potix.zul.html.impl.XulElement;
import com.potix.zul.html.ext.Paginal;

/**
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Rows extends XulElement implements Cropper {
	/** Returns the grid that contains this rows. */
	public Grid getGrid() {
		return (Grid)getParent();
	}

	//Paging//
	/** Returns the index of the first visible child.
	 * <p>Used only for component development, not for application developers.
	 */
	public int getVisibleBegin() {
		final Grid grid = getGrid();
		if (grid == null || !grid.inPagingMold())
			return 0;
		final Paginal pgi = grid.getPaginal();
		return pgi.getActivePage() * pgi.getPageSize();
	}
	/** Returns the index of the last visible child.
	 * <p>Used only for component development, not for application developers.
	 */
	public int getVisibleEnd() {
		final Grid grid = getGrid();
		if (grid == null || !grid.inPagingMold())
			return Integer.MAX_VALUE;
		final Paginal pgi = grid.getPaginal();
		return (pgi.getActivePage() + 1) * pgi.getPageSize() - 1; //inclusive
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
			if (grid.inPagingMold())
				grid.getPaginal().setTotalSize(getChildren().size());
		}
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);

		final Grid grid = getGrid();
		if (grid != null) {
			grid.initAtClient();
			if (grid.inPagingMold())
				grid.getPaginal().setTotalSize(getChildren().size());
		}
    }

	//--Cropper--//
	public Set getAvailableAtClient() {
		final Grid grid = getGrid();
		if (grid == null || !grid.inPagingMold())
			return null;

		final Set avail = new HashSet(37);
		final Paginal pgi = grid.getPaginal();
		int pgsz = pgi.getPageSize();
		final int ofs = pgi.getActivePage() * pgsz;
		for (final Iterator it = getChildren().listIterator(ofs);
		--pgsz >= 0 && it.hasNext();)
			avail.add(it.next());
		return avail;
	}
}
