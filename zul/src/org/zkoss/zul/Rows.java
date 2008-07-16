/* Rows.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:39     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
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
	private transient List _groupsInfo, _groups;
	public Rows() {
		_groupsInfo = new LinkedList();
		_groups = new AbstractList() {
			public int size() {
				return getGroupCount();
			}
			public Iterator iterator() {
				return new IterGroups();
			}
			public Object get(int index) {
				return getChildren().get(((int[])_groupsInfo.get(index))[0]);
			}
		};
	}
	/** Returns the grid that contains this rows.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Grid getGrid() {
		return (Grid)getParent();
	}
	/**
	 * Returns the number of Group
	 * @since 3.5.0
	 */
	public int getGroupCount() {
		return _groupsInfo.size();
	}
	
	/**
	 * Returns a list of all {@link Group}.
	 *	@since 3.5.0
	 */
	public List getGroups() {
		return _groups;
	}
	/**
	 * Returns whether Group exists.
	 * @since 3.5.0
	 */
	public boolean hasGroup() {
		return !_groupsInfo.isEmpty();
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
	
	/*package*/ void fixGroupIndex(int j, int to) {
		for (Iterator it = getChildren().listIterator(j);
		it.hasNext() && (to < 0 || j <= to); ++j) {
			if (it.next() instanceof Group) {
				int[] g = getGroupsInfoAtIndex(j+1, true);
				if (g != null) {
					g[0] = j;
					if (g[2] != -1) g[2]--;
				}
			}
		}
	}
	/*package*/ Group getGroupAtIndex(int index) {
		if (_groupsInfo.isEmpty()) return null;
		final int[] g = getGroupsInfoAtIndex(index);
		if (g != null) return (Group)getChildren().get(g[0]);
		return null;
	}
	/*package*/ int[] getGroupsInfoAtIndex(int index) {
		return getGroupsInfoAtIndex(index, false);
	}
	/**
	 * Returns an int array that it has two length, one is an index of Group,
	 * and the other is the number of items of Group(inclusive).
	 */
	/*package*/ int[] getGroupsInfoAtIndex(int index, boolean isGroup) {
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (isGroup) {
				if (index == g[0]) return g;
			} else if ((index > g[0] && index <= g[0] + g[1]))
				return g;
		}
		return null;
	}
	//-- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for rows: "+parent);
		super.setParent(parent);
	}
	public boolean insertBefore(Component child, Component refChild) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: "+child);
		Row newItem = (Row) child;
		final int jfrom = hasGroup() && newItem.getParent() == this ? newItem.getIndex(): -1;	
		
		if (newItem instanceof Groupfoot){
			if (!hasGroup())
				throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
			if (refChild == null){
				if (getLastChild() instanceof Groupfoot)
					throw new UiException("Only one Goupfooter is allowed per Group");
				final int[] g = (int[]) _groupsInfo.get(getGroupCount()-1);
				g[2] = getChildren().size();		
			}else{
				final int idx = ((Row)refChild).getIndex();				
				final int[] g = getGroupsInfoAtIndex(idx);
				if (g == null)
					throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");				
				if (g[2] != -1)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (idx != (g[0] + g[1]))
					throw new UiException("Groupfoot must be placed after the last Row of the Group");
				final int[] t = (int[]) _groupsInfo.get(g[0]);
				t[2] = idx-1;
			}							
		}		
		if (super.insertBefore(child, refChild)) {
			if(hasGroup()) {
				final int
					jto = refChild instanceof Row ? ((Row)refChild).getIndex(): -1,
					fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto: jfrom;
				if (fixFrom >= 0) fixGroupIndex(fixFrom,
					jfrom >=0 && jto >= 0 ? jfrom > jto ? jfrom: jto: -1);
			}
			if (newItem instanceof Group) {
				Group group = (Group) newItem;
				int index = group.getIndex();
				if (_groupsInfo.isEmpty())
					_groupsInfo.add(new int[]{group.getIndex(), getChildren().size() - index, -1});
				else {
					int idx = 0;
					int[] prev = null, next = null;
					for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
						int[] g = (int[])it.next();
						if(g[0] <= index) {
							prev = g;
							idx++;
						} else {
							next = g;
							break;
						}
					}
					if (prev != null) {
						int leng = index - prev[0], 
							size = prev[1] - leng + 1;
						prev[1] = leng;
						_groupsInfo.add(idx, new int[]{index, size, -1});	
					} else if (next != null) {
						_groupsInfo.add(idx, new int[]{index, next[0] - index, -1});
					}
				}
			} else if (hasGroup()) {
				final int[] g = getGroupsInfoAtIndex(newItem.getIndex());
				if (g != null) g[1]++;
			}
			
			afterInsert(child);
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		if (child.getParent() == this)
			beforeRemove(child);
		int index = hasGroup() ? ((Row)child).getIndex() : -1;
		if(super.removeChild(child)) {
			if (child instanceof Group) {				
				int[] prev = null, remove = null;
				for(Iterator it = _groupsInfo.iterator(); it.hasNext();) {
					int[] g = (int[])it.next();
					if (g[0] == index) {
						remove = g;
						break;
					}
					prev = g;
				}
				if (prev != null && remove !=null) {
					prev[1] += remove[1] - 1;
				}
				fixGroupIndex(index, -1);
				_groupsInfo.remove(remove);
				final int idx = remove[2];
				if (idx != -1){				
					final Component gft = (Component) getChildren().get(idx -1);
					super.removeChild(gft);
				}				
			} else if (hasGroup()) {
				final int[] g = getGroupsInfoAtIndex(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1) g[2]--;
				}
				else fixGroupIndex(index, -1);
			}
			if (child instanceof Groupfoot){
				final int[] g = getGroupsInfoAtIndex(index);
				g[2] = -1;
			}
			return true;
		}
		return false;
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
	/**
	 * An iterator used by _groups.
	 */
	private class IterGroups implements Iterator {
		private final Iterator _it = _groupsInfo.iterator();
		private int _j;

		public boolean hasNext() {
			return _j < getGroupCount();
		}
		public Object next() {
			final Object o = getChildren().get(((int[])_it.next())[0]);
			++_j;
			return o;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
