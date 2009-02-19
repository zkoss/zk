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

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Cropper;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Paginal;

/**
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 * <p>Default {@link #getZclass}: z-rows.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Rows extends XulElement implements org.zkoss.zul.api.Rows {
	private int _visibleItemCount;
	
	private transient List _groupsInfo, _groups;
	public Rows() {
		init();
	}
	private void init () {
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
	/** Returns the grid that contains this rows.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Grid getGridApi() {
		return getGrid();
	}
	/**
	 * Returns the number of groups.
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
	/**
	 * Returns the number of visible descendant {@link Row}.
	 * @since 3.5.1
	 */
	public int getVisibleItemCount() {
		return _visibleItemCount;
	}
	/*package*/ void addVisibleItemCount(int count) {
		if (count != 0) {
			_visibleItemCount += count;
			final Grid grid = getGrid();
			if (grid != null && grid.inPagingMold()) {
				final Paginal pgi = grid.getPaginal();
				pgi.setTotalSize(_visibleItemCount);
				if (grid.getModel() != null)
					grid.invalidate();
				else invalidate(); // the set of visible items might change
			}
		}
	}
	/** 
	 * @deprecated As of release As of release 3.5.1 
	 */
	public int getVisibleBegin() {
		return 0;
	}
	/** 
	 * @deprecated As of release As of release 3.5.1 
	 */
	public int getVisibleEnd() {
		return Integer.MAX_VALUE;
	}
	
	/*package*/ void fixGroupIndex(int j, int to, boolean infront) {
		for (Iterator it = getChildren().listIterator(j);
		it.hasNext() && (to < 0 || j <= to); ++j) {
			Object o = it.next();
			if (o instanceof Group) {
				int[] g = getLastGroupsInfoAt(j + (infront ? -1 : 1));
				if (g != null) {
					g[0] = j;
					if (g[2] != -1) g[2] += (infront ? 1 : -1);
				}
			}
		}
	}
	/*package*/ Group getGroup(int index) {
		if (_groupsInfo.isEmpty()) return null;
		final int[] g = getGroupsInfoAt(index);
		if (g != null) return (Group)getChildren().get(g[0]);
		return null;
	}
	/*package*/ int[] getGroupsInfoAt(int index) {
		return getGroupsInfoAt(index, false);
	}
	/**
	 * Returns the last groups info which matches with the same index.
	 * Because dynamically maintain the index of the groups will occur the same index
	 * at the same time in the loop. 
	 */
	/*package*/ int[] getLastGroupsInfoAt(int index) {
		int [] rg = null;
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[])it.next();
			if (index == g[0]) rg = g;
			else if (index < g[0]) break;
		}
		return rg;
	}
	/**
	 * Returns an int array that it has two length, one is an index of Group,
	 * and the other is the number of items of Group(inclusive).
	 */
	/*package*/ int[] getGroupsInfoAt(int index, boolean isGroup) {
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

		final boolean isReorder = child.getParent() == this;
		if (newItem instanceof Groupfoot){
			if (!hasGroup())
				throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
			if (refChild == null) {
				if (getLastChild() instanceof Groupfoot)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (isReorder) {
					final int idx = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(idx);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
				final int[] g = (int[]) _groupsInfo.get(getGroupCount()-1);
				g[2] = getChildren().size() - (isReorder ? 2 : 1);
			} else {
				final int idx = ((Row)refChild).getIndex();				
				final int[] g = getGroupsInfoAt(idx);
				if (g == null)
					throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");				
				if (g[2] != -1)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (idx != (g[0] + g[1]))
					throw new UiException("Groupfoot must be placed after the last Row of the Group");
				g[2] = idx-1;
				if (isReorder) {
					final int nindex = newItem.getIndex();				
					final int[] ginfo = getGroupsInfoAt(nindex);
					if (ginfo != null) {
						ginfo[1]--; 
						ginfo[2] = -1;
					}
				}
			}							
		}
		if (super.insertBefore(child, refChild)) {
			if(hasGroup()) {
				final int
					jto = refChild instanceof Row ? ((Row)refChild).getIndex(): -1,
					fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto: jfrom;
				if (fixFrom >= 0) fixGroupIndex(fixFrom,
					jfrom >=0 && jto >= 0 ? jfrom > jto ? jfrom: jto: -1, !isReorder);
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
						_groupsInfo.add(idx, new int[]{index, size, size > 1 ? prev[2] : -1});
						if (size > 1) prev[2] = -1; // reset groupfoot
					} else if (next != null) {
						_groupsInfo.add(idx, new int[]{index, next[0] - index, -1});
					}
				}
			} else if (hasGroup()) {
				int index = newItem.getIndex();
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]++;
					if (g[2] != -1) g[2]++;
				}
				
			}
			
			afterInsert(child);
			return true;
		}
		return false;
	}
	/**
	 * If the child is a group, its groupfoot will be removed at the same time.
	 */
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
				fixGroupIndex(index, -1, false);
				if (remove != null) {
					_groupsInfo.remove(remove);
					final int idx = remove[2];
					if (idx != -1) {
						removeChild((Component) getChildren().get(idx -1));
							// Because the fixGroupIndex will skip the first groupinfo,
							// we need to subtract 1 from the idx variable
					}
				}
			} else if (hasGroup()) {
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1) g[2]--;
					fixGroupIndex(index, -1, false);
				}
				else fixGroupIndex(index, -1, false);
				if (child instanceof Groupfoot){
					final int[] g1 = getGroupsInfoAt(index);	
					if(g1 != null){ // group info maybe remove cause of grouphead removed in previous op
						g1[2] = -1;
					}
				}
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
		updateVisibleCount((Row) comp, false);
		checkInvalidateForMoved(comp, false);
	}
	/** Callback if a child will be removed (not removed yet).
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	protected void beforeRemove(Component comp) {
		updateVisibleCount((Row) comp, true);
		checkInvalidateForMoved(comp, true);
	}
	/**
	 * Update the number of the visible item before it is removed or after it is added.
	 */
	private void updateVisibleCount(Row row, boolean isRemove) {
		if (row instanceof Group || row.isVisible()) {
			final Group g = getGroup(row.getIndex());
			
			// We shall update the number of the visible item in the following cases.
			// 1) If the row is a type of Groupfoot, it is always shown.
			// 2) If the row is a type of Group, it is always shown.
			// 3) If the row doesn't belong to any group.
			// 4) If the group of the row is open.
			if (row instanceof Groupfoot || row instanceof Group || g == null || g.isOpen())
				addVisibleItemCount(isRemove ? -1 : 1);
			
			if (row instanceof Group) {
				final Group group = (Group) row;
				
				// If the previous group exists, we shall update the number of
				// the visible item from the number of the visible item of the current group.
				final Row preRow = (Row) row.getPreviousSibling();
				if (preRow == null) {
					if (!group.isOpen()) {
						addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				} else {
					final Group preGroup = preRow instanceof Group ? (Group) preRow : getGroup(preRow.getIndex());
					if (preGroup != null) {
						if (!preGroup.isOpen() && group.isOpen())
							addVisibleItemCount(isRemove ? -group.getVisibleItemCount() : group.getVisibleItemCount());
						else if (preGroup.isOpen() && !group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					} else {
						if (!group.isOpen())
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
					}
				}
			}
		}
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold())
			grid.getPaginal().setTotalSize(getVisibleItemCount());
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

	/** Returns an iterator to iterate thru all visible children.
	 * Unlike {@link #getVisibleItemCount}, it handles only the direct children.
	 * Component developer only.
	 * @since 3.5.1
	 */
	public Iterator getVisibleChildrenIterator() {
		final Grid grid = getGrid();
		if (grid != null && grid.inSpecialMold())
			return grid.getDrawerEngine().getVisibleChildrenIterator();
		return new VisibleChildrenIterator();
	}
	/**
	 * An iterator used by visible children.
	 */
	private class VisibleChildrenIterator implements Iterator {
		private final ListIterator _it = getChildren().listIterator();
		private Grid _grid = getGrid();
		private int _count = 0;
		public boolean hasNext() {
			if (_grid == null || !_grid.inPagingMold()) return _it.hasNext();
			
			if (_count >= _grid.getPaginal().getPageSize()) {
				return false;
			}

			if (_count == 0) {
				final Paginal pgi = _grid.getPaginal();
				int begin = pgi.getActivePage() * pgi.getPageSize();
				for (int i = 0; i < begin && _it.hasNext();) {
					getVisibleRow((Row)_it.next());
					i++;
				}
			}
			return _it.hasNext();
		}
		private Row getVisibleRow(Row row) {
			if (row instanceof Group) {
				final Group g = (Group) row;
				if (!g.isOpen()) {
					for (int j = 0, len = g.getItemCount(); j < len
							&& _it.hasNext(); j++)
						_it.next();
				}
			}
			while (!row.isVisible())
				row = (Row)_it.next();
			return row;
		}
		public Object next() {
			if (_grid == null || !_grid.inPagingMold()) return _it.next();
			_count++;
			final Row row = (Row)_it.next();
			return _it.hasNext() ? getVisibleRow(row) : row;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	public String getZclass() {
		return _zclass == null ? "z-rows" : _zclass;
	}
	//Cloneable//
	public Object clone() {
		final Rows clone = (Rows)super.clone();
		clone.init();
		return clone;
	}
	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
	}
	protected List newChildren() {
		return new Children();
	}
	protected class Children extends AbstractComponent.Children {
	    protected void removeRange(int fromIndex, int toIndex) {
	        ListIterator it = listIterator(toIndex);
	        for (int n = toIndex - fromIndex; --n >= 0;) {
	            it.previous();
	            it.remove();
	        }
	    }
	};
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
			int ofs = pgi.getActivePage() * pgsz;
			
			Row row = (Row) getFirstChild();
			while(row != null) {
				if (pgsz == 0) break;
				if (row.isVisible()) {
					if (--ofs < 0) {
						--pgsz;
						avail.add(row);
					}
				}
				if (row instanceof Group) {
					final Group g = (Group) row;
					if (!g.isOpen()) {
						for (int j = 0, len = g.getItemCount(); j < len; j++)
							row = (Row) row.getNextSibling();
					}
				}
				if (row != null)
					row = (Row) row.getNextSibling();
			}
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
