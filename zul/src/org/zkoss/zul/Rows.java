/* Rows.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:39     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Cropper;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Paginal;

/**
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 *
 * @author tomyeh
 */
public class Rows extends XulElement {
	/** Returns the grid that contains this rows.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Grid getGrid() {
		return (Grid)getParent();
	}

	//Paging//
	/** Returns the index of the first visible child.
	 * <p>Used only for component development, not for application developers.
	 */
	public int getVisibleBegin() {
		final Grid grid = getGrid();
		if (grid == null)
			return 0;
		if (grid.inSpecialMold())
			return grid.getDrawerEngine().getRenderBegin();
		if (!grid.inPagingMold()) return 0;
		final Paginal pgi = grid.getPaginal();
		return pgi.getActivePage() * pgi.getPageSize();
	}
	/** Returns the index of the last visible child.
	 * <p>Used only for component development, not for application developers.
	 */
	public int getVisibleEnd() {
		final Grid grid = getGrid();
		if (grid == null)
			return Integer.MAX_VALUE;
		if (grid.inSpecialMold())
			return grid.getDrawerEngine().getRenderEnd();
		if (!grid.inPagingMold()) return Integer.MAX_VALUE;
		final Paginal pgi = grid.getPaginal();
		return (pgi.getActivePage() + 1) * pgi.getPageSize() - 1; //inclusive
	}

	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for rows: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: "+child);

		if (super.insertBefore(child, insertBefore)) {
			afterInsert(child);
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		if (child.getParent() == this)
			beforeRemove(child);
		return super.removeChild(child);
	}
	/** Callback if a child has been inserted.
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	protected void afterInsert(Component comp) {
		checkInvalidateForMoved(comp, false);
	}
	/** Callback if a child will be removed (not removed yet).
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	protected void beforeRemove(Component comp) {
		checkInvalidateForMoved(comp, true);
	}
	/** Checks whether to invalidate, when a child has been added or 
	 * or will be removed.
	 * @param bRemove if child will be removed
	 */
	private void checkInvalidateForMoved(Component child, boolean bRemove) {
		//No need to invalidate if
		//1) act == last and child in act
		//2) act != last and child after act
		//Except removing last elem which in act and act has only one elem
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold() && !isInvalidated()) {
			final List children = getChildren();
			final int sz = children.size(),
				pgsz = grid.getPageSize();
			int n = sz - (grid.getActivePage() + 1) * pgsz;
			if (n <= 0) {//must be last page
				n += pgsz; //check in-act (otherwise, check after-act)
				if (bRemove && n <= 1) { //last elem, in act and remove
					invalidate();
					return;
				}
			} else if (n > 50)
				n = 50; //check at most 50 items (for better perf)

			for (ListIterator it = children.listIterator(sz);
			--n >= 0 && it.hasPrevious();)
				if (it.previous() == child)
					return; //no need to invalidate

			invalidate();
		}
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);

		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold())
			grid.getPaginal().setTotalSize(getChildren().size());
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);

		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold())
			grid.getPaginal().setTotalSize(getChildren().size());
    }

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Cropper {
		//--Cropper--//
		public boolean isCropper() {
			final Grid grid = getGrid();
			return grid != null &&
				((grid.inPagingMold()
					&& grid.getPageSize() <= getChildren().size())
				|| grid.inSpecialMold());
				//Single page is considered as not a cropper.
				//isCropper is called after a component is removed, so
				//we have to test >= rather than >
	}
		public Set getAvailableAtClient() {
			if (!isCropper())
				return null;

			final Grid grid = getGrid();
			if (grid.inSpecialMold())
				return grid.getDrawerEngine().getAvailableAtClient();

			final Set avail = new LinkedHashSet(32);
			final Paginal pgi = grid.getPaginal();
			int pgsz = pgi.getPageSize();
			final int ofs = pgi.getActivePage() * pgsz;
			for (final Iterator it = getChildren().listIterator(ofs);
			--pgsz >= 0 && it.hasNext();)
				avail.add(it.next());
			return avail;
		}
	}
}
