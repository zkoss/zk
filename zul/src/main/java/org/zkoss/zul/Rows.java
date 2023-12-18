/* Rows.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:39     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.impl.DataLoader;
import org.zkoss.zul.impl.GroupsListModel;
import org.zkoss.zul.impl.XulElement;

/**
 * Defines the rows of a grid.
 * Each child of a rows element should be a {@link Row} element.
 * <p>Default {@link #getZclass}: z-rows.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Rows extends XulElement {
	private int _visibleItemCount;

	private transient List<int[]> _groupsInfo;
	private transient List<Group> _groups;
	private transient boolean _isReplacingRow;

	public Rows() {
		init();
	}

	private void init() {
		_groupsInfo = new LinkedList<int[]>();
		_groups = new AbstractList<Group>() {
			public int size() {
				return getGroupCount();
			}

			public Iterator<Group> iterator() {
				return new IterGroups();
			}

			public Group get(int index) {
				final int realIndex = getRealIndex(_groupsInfo.get(index)[0]);
				return (realIndex >= 0 && realIndex < getChildren().size()) ? (Group) getChildren().get(realIndex)
						: null;
			}
		};
	}

	private int getRealIndex(int index) {
		final Grid grid = getGrid();
		final int offset = grid != null && grid.getModel() != null ? grid.getDataLoader().getOffset() : 0;
		return index - (offset < 0 ? 0 : offset);
	}

	/** Returns the grid that contains this rows.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Grid getGrid() {
		return (Grid) getParent();
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
	public List<Group> getGroups() {
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
			if (grid != null) {
				if (grid.inPagingMold()) {
					final Paginal pgi = grid.getPaginal();
					int newTotalSize = grid.getDataLoader().getTotalSize();
					//ZK-3173: if visible item count reduces, active page might exceed max page count
					ListModel<?> model = grid.getModel();
					if (count < 0 && model instanceof Pageable) {
						Pageable p = (Pageable) model;
						int actpg = p.getActivePage();
						int maxPageIndex = p.getPageCount() - 1;
						if (actpg > maxPageIndex) {
							p.setActivePage(maxPageIndex);
						}
					}
					pgi.setTotalSize(newTotalSize);
					if (grid.getModel() != null)
						grid.invalidate();
					else {
						invalidate();
						grid.getDataLoader().updateModelInfo();
					}
				} else if (((Cropper) grid.getDataLoader()).isCropper()) {
					invalidate();
					grid.getDataLoader().updateModelInfo();
				} else {
					smartUpdate("visibleItemCount", _visibleItemCount);
				}
			}
		}
	}

	/**
	 * Set true to avoid unnecessary row re-indexing when render template.
	 * @param b true to skip
	 * @return original true/false status
	 * @see Grid.Renderer#render
	 */
	/*package*/ boolean setReplacingRow(boolean b) {
		final boolean old = _isReplacingRow;
		Grid grid = getGrid();
		if (grid != null && grid.getModel() != null) // B60-ZK-898: only apply when model is used.
			_isReplacingRow = b;
		return old;
	}

	/*package*/ void fixGroupIndex(int j, int to, boolean infront) {
		int realj = getRealIndex(j);
		if (realj < 0) {
			realj = 0;
		}
		if (realj < getChildren().size()) {
			final int beginning = j;
			for (Iterator<Component> it = getChildren().listIterator(realj); it.hasNext() && (to < 0 || j <= to); ++j) {
				Component o = it.next();
				((Row) o).setIndexDirectly(j);

				if (_isReplacingRow) //@see Grid.Renderer#render
					break; //set only the first Row, skip handling GroupInfo

				// if beginning is a group, we don't need to change its groupInfo,
				// because
				// it is not reliable when infront is true.
				if ((!infront || beginning != j) && o instanceof Group) {
					int[] g = getLastGroupsInfoAt(j + (infront ? -1 : 1));
					if (g != null) {
						g[0] = j;
						if (g[2] != -1)
							g[2] += (infront ? 1 : -1);
					}
				}
			}
		}
	}

	/*package*/ Group getGroup(int index) {
		if (_groupsInfo.isEmpty())
			return null;
		final int[] g = getGroupsInfoAt(index);
		if (g != null) {
			final int realIndex = getRealIndex(g[0]);
			//if realIndex < 0 means g is half loaded, Group head is not in server
			if (realIndex >= 0 && realIndex < getChildren().size()) {
				Row row = (Row) getChildren().get(realIndex);
				return (Group) row;
			}
		}
		return null;
	}

	/*package*/ int[] getGroupsInfoAt(int index) {
		return getGroupsInfoAt(index, false);
	}
	
	/**
	 * Returns an int array that it has two length, one is an index of Group,
	 * and the other is the number of items of Group(inclusive).
	 */
	/*package*/ int[] getGroupsInfoAt(int index, boolean isGroup) {
		for (Iterator<int[]> it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = it.next();
			if (isGroup) {
				if (index == g[0])
					return g;
			} else if (index > g[0] && index <= (g[0] + g[1]))
				return g;
		}
		return null;
	}

	/**
	 * Returns the last groups info which matches with the same index.
	 * Because dynamically maintain the index of the groups will occur the same index
	 * at the same time in the loop. 
	 */
	/*package*/ int[] getLastGroupsInfoAt(int index) {
		int[] rg = null;
		for (int[] g : _groupsInfo) {
			if (index == g[0])
				rg = g;
			else if (index < g[0])
				break;
		}
		return rg;
	}

	/**
	 * Returns the last groups index which matches with the same index.
	 * @param index the row index in Rows.
	 * @return the associated group index of the row index.
	 */
	/*package*/ int getGroupIndex(int index) {
		int j = 0, gindex = -1;
		int[] g = null;
		for (Iterator<int[]> it = _groupsInfo.iterator(); it.hasNext(); ++j) {
			g = it.next();
			if (index == g[0])
				gindex = j;
			else if (index < g[0])
				break;
		}
		return gindex != -1 ? gindex
				: g != null && index < (g[0] + g[1]) ? (j - 1)
						: g != null && index == (g[0] + g[1]) && g[2] == -1 ? (j - 1) : gindex;
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for rows: " + parent);
		super.beforeParentChanged(parent);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Row))
			throw new UiException("Unsupported child for rows: " + child);
		if (child instanceof Groupfoot) {
			if (!hasGroup())
				throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
			if (refChild == null) {
				if (getLastChild() instanceof Groupfoot)
					throw new UiException("Only one Groupfoot is allowed per Group");
			}
		}
		super.beforeChildAdded(child, refChild);
	}

	private boolean hasGroupsModel() {
		final Grid grid = getGrid();
		return grid != null && grid.getModel() instanceof GroupsListModel;
	}

	public boolean insertBefore(Component child, Component refChild) {
		final Grid grid = getGrid();
		final boolean isReorder = child.getParent() == this;
		//bug #3051305: Active Page not update when drag & drop item to the end
		if (isReorder) {
			checkInvalidateForMoved(child, true);
		}

		Row newItem = (Row) child;
		final int jfrom = hasGroup() && newItem.getParent() == this ? newItem.getIndex() : -1;

		fixGroupsInfoBeforeInsert(newItem, (Row) refChild, isReorder);

		if (super.insertBefore(child, refChild)) {
			final int jto = refChild instanceof Row ? ((Row) refChild).getIndex() : -1,
					fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto : jfrom;

			if (fixFrom < 0) {
				newItem.setIndexDirectly(
						getChildren().size() - 1 + (grid != null ? grid.getDataLoader().getOffset() : 0));
			} else {
				fixGroupIndex(fixFrom, jfrom >= 0 && jto >= 0 ? jfrom > jto ? jfrom : jto : -1, !isReorder);
			}
			fixGroupsInfoAfterInsert(newItem);
			//bug #3049167: Totalsize increase when drag & drop in paging Listbox/Grid
			if (!isReorder) {
				afterInsert(child);
			}
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

		final boolean hasGroup = hasGroup();
		int index = ((Row) child).getIndex();
		if (super.removeChild(child)) {
			((Row) child).setIndexDirectly(-1);
			fixGroupsInfoAfterRemove((Row) child, index);
			return true;
		}
		return false;
	}

	/**
	 * Fix Childitem._index since j-th item.
	 *
	 * @param j
	 *            the start index (inclusion)
	 * @param to
	 *            the end index (inclusion). If -1, up to the end.
	 */
	private void fixRowIndices(int j, int to) {
		int realj = getRealIndex(j);
		if (realj < 0)
			realj = 0;
		List items = getChildren();
		if (realj < items.size()) {
			for (Iterator it = items.listIterator(realj); it.hasNext() && (to < 0 || j <= to); ++j)
				((Row) it.next()).setIndexDirectly(j);
		}
	}

	/** Callback if a child has been inserted.
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	protected void afterInsert(Component comp) {
		if (_isReplacingRow) //@see Grid.Renderer#render
			return; //called by #insertBefore(), skip handling item count, etc.

		updateVisibleCount((Row) comp, false);
		checkInvalidateForMoved(comp, false);
	}

	/** Callback if a child will be removed (not removed yet).
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 */
	protected void beforeRemove(Component comp) {
		if (_isReplacingRow) //@see Grid.Renderer#render
			return; //called by #removeChild(), skip handling item count, etc.

		updateVisibleCount((Row) comp, true);
		checkInvalidateForMoved(comp, true);
	}

	private void fixGroupsInfoBeforeInsert(Row newItem, Row refChild, boolean isReorder) {
		if (_isReplacingRow) //@see Grid.Renderer#render
			return; //called by #insertBefore(), skip handling GroupInfo

		if (newItem instanceof Groupfoot) {
			if (refChild == null) {
				if (isReorder) {
					final int idx = newItem.getIndex();
					final int[] ginfo = getGroupsInfoAt(idx);
					if (ginfo != null) {
						ginfo[1]--;
						ginfo[2] = -1;
					}
				}
				final int[] g = _groupsInfo.get(getGroupCount() - 1);
				g[2] = getChildren().size() - (isReorder ? 2 : 1);
			} else {
				final int idx = ((Row) refChild).getIndex();
				final int[] g = getGroupsInfoAt(idx);
				if (g == null)
					throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
				if (g[2] != -1)
					throw new UiException("Only one Goupfooter is allowed per Group");
				if (idx != (g[0] + g[1]))
					throw new UiException("Groupfoot must be placed after the last Row of the Group");
				g[2] = idx - 1;
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
	}

	private void fixGroupsInfoAfterInsert(Row newItem) {
		if (_isReplacingRow) //@see Grid.Renderer#render
			return; //called by #insertBefore(), skip handling GroupInfo

		if (newItem instanceof Group) {
			Group group = (Group) newItem;
			int index = group.getIndex();
			if (_groupsInfo.isEmpty())
				_groupsInfo.add(new int[] { group.getIndex(), getChildren().size() - index, -1 });
			else {
				int idx = 0;
				int[] prev = null, next = null;
				for (Iterator<int[]> it = _groupsInfo.iterator(); it.hasNext();) {
					int[] g = it.next();
					if (g[0] <= index) {
						prev = g;
						idx++;
					} else {
						next = g;
						break;
					}
				}
				if (prev != null) {
					int leng = index - prev[0], size = prev[1] - leng + 1;
					prev[1] = leng;
					_groupsInfo.add(idx, new int[] { index, size, size > 1 && prev[2] >= index ? prev[2] + 1 : -1 });
					if (size > 1 && prev[2] >= index)
						prev[2] = -1; // reset groupfoot
				} else if (next != null) {
					_groupsInfo.add(idx, new int[] { index, next[0] - index, -1 });
				}
			}
		} else if (hasGroup()) {
			int index = newItem.getIndex();
			final int[] g = getGroupsInfoAt(index);
			if (g != null) {
				g[1]++;
				if (g[2] != -1 && (g[2] >= index || newItem instanceof Groupfoot))
					g[2] = g[0] + g[1] - 1;
			}

		}
	}

	private void fixGroupsInfoAfterRemove(Row child, int index) {
		if (!_isReplacingRow) { //@see Grid.Renderer#render 
			//called by #removeChild, handling GroupInfo if !isReplcingRow
			if (child instanceof Group) {
				int[] prev = null, remove = null;
				for (Iterator<int[]> it = _groupsInfo.iterator(); it.hasNext();) {
					int[] g = it.next();
					if (g[0] == index) {
						remove = g;
						break;
					}
					prev = g;
				}
				if (prev != null && remove != null) {
					prev[1] += remove[1] - 1;
				}
				fixGroupIndex(index, -1, false);
				if (remove != null) {
					_groupsInfo.remove(remove);
					final int idx = remove[2];
					if (idx != -1) {
						final int realIndex = getRealIndex(idx) - 1; //bug #2936064
						if (realIndex >= 0 && realIndex < getChildren().size())
							removeChild(getChildren().get(realIndex));
					}
				}
			} else if (hasGroup()) {
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1)
						g[2]--;
					fixGroupIndex(index, -1, false);
				} else
					fixGroupIndex(index, -1, false);
				if (child instanceof Groupfoot) {
					final int[] g1 = getGroupsInfoAt(index);
					if (g1 != null) { // group info maybe remove cause of grouphead removed in previous op
						g1[2] = -1;
					}
				}
			} else {
				fixRowIndices(index, -1);
			}
		}

		if (hasGroupsModel() && getChildren().size() <= 0) { //remove to empty, reset _groupsInfo
			_groupsInfo = new LinkedList<int[]>();
		}
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
			if (row.isVisible() && (row instanceof Groupfoot || row instanceof Group || g == null || g.isOpen())) // B50-3303770
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
		if (grid != null && grid.inPagingMold()) {
			int newTotalSize = grid.getDataLoader().getTotalSize();
			//ZK-3173: if visible item count reduces, active page might exceed max page count
			ListModel<?> model = grid.getModel();
			if (isRemove && model instanceof Pageable) {
				Pageable p = (Pageable) model;
				int actpg = p.getActivePage();
				int maxPageIndex = p.getPageCount() - 1;
				if (actpg > maxPageIndex) {
					p.setActivePage(maxPageIndex);
				}
			}
			grid.getPaginal().setTotalSize(newTotalSize);
		}
	}

	/** Checks whether to invalidate, when a child has been added or 
	 * or will be removed.
	 * @param bRemove if child will be removed
	 */
	private void checkInvalidateForMoved(Component child, boolean bRemove) {
		//No need to invalidate if
		//1) act == last and child in act
		//2) act != last and child after act
		//Except removing last element which in act and act has only one element
		final Grid grid = getGrid();
		if (grid != null && grid.inPagingMold() && !isInvalidated()) {
			final List<Component> children = getChildren();
			final int sz = children.size(), pgsz = grid.getPageSize();
			int n = sz - (grid.getActivePage() + 1) * pgsz;
			if (n <= 0) { //must be last page
				n += pgsz; //check in-act (otherwise, check after-act)
				if (bRemove && n <= 1) { //last element, in act and remove
					invalidate();
					return;
				}
			} else if (n > 50)
				n = 50; //check at most 50 items (for better performance)

			for (ListIterator<Component> it = children.listIterator(sz); --n >= 0 && it.hasPrevious();)
				if (it.previous() == child)
					return; //no need to invalidate

			invalidate();
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-rows" : _zclass;
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Grid related 
	 * components, please refer to {@link Grid} and {@link Column} instead.
	 */
	public void setWidth(String width) {
		// Don't remove this method, it's to override super.setWidth().
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Grid related 
	 * components, please refer to {@link Grid} and {@link Column} instead.
	 */
	public void setHflex(String flex) {
		// Don't remove this method, it's to override super.setHflex().
	}

	//Cloneable//
	public Object clone() {
		final Rows clone = (Rows) super.clone();
		clone.init();
		clone._groupsInfo.addAll(_groupsInfo);
		Grid grid = getGrid();
		final int offset = grid != null ? grid.getDataLoader().getOffset() : 0;
		clone.afterUnmarshal(offset);
		return clone;
	}

	private void afterUnmarshal(int index) {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			((Row) it.next()).setIndexDirectly(index++);
		}
	}

	//-- Serializable --//
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		int size = _groupsInfo.size();
		s.writeInt(size);
		if (size > 0)
			s.writeObject(_groupsInfo);
		Grid grid = getGrid();
		DataLoader loader = grid != null ? grid.getDataLoader() : null;
		if (loader != null) {
			s.writeInt(loader.getOffset());
		} else
			s.writeInt(0);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
		int size = s.readInt();
		if (size > 0) {
			List groupsInfo = (List) s.readObject();
			for (int i = 0; i < size; i++)
				_groupsInfo.add((int[]) groupsInfo.get(i));
		}
		int offset = s.readInt();
		afterUnmarshal(offset);
	}

	public <T extends Component> List<T> getChildren() {
		return (List<T>) new Children();
	}

	protected class Children extends XulElement.Children {
		protected void removeRange(int fromIndex, int toIndex) {
			ListIterator<Component> it = listIterator(toIndex);
			for (int n = toIndex - fromIndex; --n >= 0 && it.hasPrevious();) {
				it.previous();
				it.remove();
			}
		}

		public void clear() {
			final boolean oldFlag = setReplacingRow(true);
			try {
				super.clear();
			} finally {
				setReplacingRow(oldFlag);
			}
		}
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		final Grid grid = getGrid();
		renderer.render("_offset", grid == null ? 0 : grid.getDataLoader().getOffset()); //go with each cropping
		renderer.render("visibleItemCount", _visibleItemCount); //go with each cropping
	}

	//-- ComponentCtrl --//
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Cropper {
		//--Cropper--//
		public boolean isCropper() {
			final Grid grid = getGrid();
			return grid != null && ((Cropper) grid.getDataLoader()).isCropper();
		}

		public Component getCropOwner() {
			return getGrid();
		}

		public Set<? extends Component> getAvailableAtClient() {
			final Grid grid = getGrid();
			return grid != null ? ((Cropper) grid.getDataLoader()).getAvailableAtClient() : null;
		}
	}

	/**
	 * An iterator used by _groups.
	 */
	private class IterGroups implements Iterator<Group> {
		private final Iterator<int[]> _it = _groupsInfo.iterator();
		private int _j;

		public boolean hasNext() {
			return _j < getGroupCount();
		}

		public Group next() {
			++_j;
			final int realIndex = getRealIndex((_it.next())[0]);
			return (realIndex >= 0 && realIndex < getChildren().size()) ? (Group) getChildren().get(realIndex) : null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
