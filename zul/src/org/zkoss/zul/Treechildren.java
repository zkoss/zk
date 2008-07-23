/* Treechildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.sys.ComponentCtrl;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginal;

/**
 * A treechildren.
 *
 * @author tomyeh
 */
public class Treechildren extends XulElement implements Pageable {
	private static final String ATTR_NO_CHILD = "org.zkoss.zul.Treechildren.noChild";
	private static final String VISIBLE_ITEM = "org.zkoss.zul.Treechildren.visibleItem";

	/** the active page. */
	private int _actpg;
	/** # of items per page. Zero means the same as Tree's. */
	private int _pgsz;
	private int _visibleItemCount;
	/** Returns the {@link Tree} instance containing this element.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
	}
	/** Returns the {@link Treerow} that is associated with
	 * this treechildren, or null if no such treerow.
	 * In other words, it is {@link Treeitem#getTreerow} of
	 * {@link #getParent}.
	 *
	 * @since 2.4.1
	 * @see Treerow#getLinkedTreechildren
	 */
	public Treerow getLinkedTreerow() {
		final Component parent = getParent();
		return parent instanceof Treeitem ?
			((Treeitem)parent).getTreerow(): null;
	}

	/** Returns whether this is visible.
	 * <p>Besides depends on {@link #setVisible}, it also depends on
	 * whether all its ancestors is open.
	 */
	public boolean isVisible() {
		if (!super.isVisible())
			return false;

		Component comp = getParent();
		if (!(comp instanceof Treeitem))
			return true;
		if (!((Treeitem)comp).isOpen())
			return false;
		comp = comp.getParent();
		return !(comp instanceof Treechildren)
			|| ((Treechildren)comp).isVisible(); //recursive
	}

	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 */
	public Collection getItems() {
		return new AbstractCollection() {
			public int size() {
				return getItemCount();
			}
			public boolean isEmpty() {
				return getChildren().isEmpty();
			}
			public Iterator iterator() {
				return new Iterator() {
					private final Iterator _it = getChildren().iterator();
					private Iterator _sub;
					public boolean hasNext() {
						return (_sub != null && _sub.hasNext()) || _it.hasNext();
					}
					public Object next() {
						if (_sub != null && _sub.hasNext())
							return _sub.next();

						final Treeitem item = (Treeitem)_it.next();
						final Treechildren tc = item.getTreechildren();
						_sub = tc != null ? tc.getItems().iterator(): null;
						return item;
					}
					public void remove() {
						throw new UnsupportedOperationException("readonly");
					}
				};
			}
		};
	}
	/** Returns the number of child {@link Treeitem}
	 * including all descendants. The same as {@link #getItems}.size().
	 * <p>Note: the performance is no good.
	 */
	public int getItemCount() {
		int sz = 0;
		for (Iterator it = getChildren().iterator(); it.hasNext(); ++sz) {
			final Treeitem item = (Treeitem)it.next();
			final Treechildren tchs = item.getTreechildren();
			if (tchs != null)
				sz += tchs.getItemCount();
		}
		return sz;
	}

	/**
	 * Returns the number of visible child{@link Treeitem}
	 * @since 3.0.7
	 */
	public int getVisibleItemCount() {
		return _visibleItemCount;
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		if (((Treeitem)child).isVisible())
			addVisibleItemCount(1);
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (((Treeitem)child).isVisible())
			addVisibleItemCount(-1);
	}
	void addVisibleItemCount(int count) {
		if (count == 0) return;
		Component parent = (Component) getParent();
		if (parent instanceof Treeitem) {
			if (((Treeitem)parent).isOpen())
				((Treeitem)parent).addVisibleItemCount(count, false);
		} else if (parent instanceof Tree)
			((Tree)parent).addVisibleItemCount(count);
		_visibleItemCount += count;
	}
	
	/** Returns the page size which controls the number of
	 * visible child {@link Treeitem}, or -1 if no limitation.
	 *
	 * <p>If {@link #setPageSize} is called with a non-zero value,
	 * this method return the non-zero value.
	 * If {@link #setPageSize} is called with zero, this method
	 * returns {@link Tree#getPageSize} of {@link #getTree}.
	 *
	 * <p>Default: the same as {@link #getTree}'s {@link Tree#getPageSize}.
	 *
	 * @since 2.4.1
	 * @see Tree#getPageSize
	 * @see #setPageSize
	 * @deprecated 3.0.7
	 */
	public int getPageSize() {
		return Integer.MAX_VALUE;
		/**if (_pgsz != 0) return _pgsz;
		final Tree tree = getTree();
		return tree != null ? tree.getPageSize(): -1;*/
	}
	/*package*/ int getPageSizeDirectly() {
		return _pgsz;
	}
	/** Sets the page size which controls the number of
	 * visible child {@link Treeitem}.
	 *
	 * @param size the page size. If zero, the page size will be
	 * the same as {@link Tree#getPageSize} of {@link #getTree}.
	 * In other words, zero means the default page size is used.
	 * If negative, all {@link Treeitem} are shown.
	 * @since 2.4.1
	 * @deprecated 3.0.7
	 */
	public void setPageSize(int size) throws WrongValueException {
		return;/**
		if (size < 0) size = -1;
			//Note: -1=no limitation, 0=tree's default
		if (_pgsz != size) {
			boolean realChanged = true;
			if (_pgsz == 0 || size == 0) {
				final Tree tree = getTree();
				if (tree != null) {
					final int treepgsz = tree.getPageSize();
					if ((_pgsz == 0 && treepgsz == size)
					|| (size == 0 && treepgsz == _pgsz))
						realChanged = false;
				}
			}

			_pgsz = size;

			if (realChanged) {
				final int pgcnt = getPageCount();
				if (_actpg >= pgcnt)
					_actpg = pgcnt - 1;

				invalidate(); //due to client's limit, we have to redraw
				smartUpdatePaging();
					//it affect treerow (so invalidate won't 'eat' it)
			}
		}*/
	}

	/** Returns the number of pages (at least one).
	 * Note: there is at least one page even no item at all.
	 *
	 * @since 2.4.1
	 * @deprecated 3.0.7
	 */
	public int getPageCount() {
		return Integer.MAX_VALUE;/**
		final int cnt = getChildren().size();
		if (cnt <= 0) return 1;
		final int pgsz = getPageSize();
		return pgsz <= 0 ? 1: 1 + (cnt - 1)/pgsz;*/
	}
	/** Returns the active page (starting from 0).
	 *
	 * @since 2.4.1
	 * @deprecated 3.0.7
	 */
	public int getActivePage() {
		return _actpg;
	}
	/** Sets the active page (starting from 0).
	 *
	 * @exception WrongValueException if no such page
	 * @since 2.4.1
	 * @see Tree#setActivePage
	 * @deprecated 3.0.7
	 */
	public void setActivePage(int pg) throws WrongValueException {
		return;/**
		final int pgcnt = getPageCount();
		if (pg >= pgcnt || pg < 0)
			throw new WrongValueException("Unable to set active page to "+pg+" since only "+pgcnt+" pages");
		if (_actpg != pg) {
			_actpg = pg;

			invalidate();
			smartUpdatePaging();
				//it affect treerow (so invalidate won't 'eat' it)
		}*/
	}
	/** Called by {@link Tree} to set the active page directly. */
	/*package*/ void setActivePageDirectly(int pg) {
		_actpg = pg;
	}
	/** Returns the index of the first visible child.
	 * <p>Used only for component development, not for application developers.
	 * @since 2.4.1
	 * @deprecated 3.0.7
	 */
	public int getVisibleBegin() {
		return 0;
		/**final int pgsz = getPageSize();
		return pgsz <= 0 ? 0: getActivePage() * pgsz;*/
	}
	/** Returns the index of the last visible child.
	 * <p>Used only for component development, not for application developers.
	 * @since 2.4.1
	 * @deprecated 3.0.7
	 */
	public int getVisibleEnd() {
		return Integer.MAX_VALUE;
		/**
		final int pgsz = getPageSize();
		return pgsz <= 0 ? Integer.MAX_VALUE:
			(getActivePage() + 1) * getPageSize() - 1; //inclusive*/
	}

	//-- Component --//
	public void setParent(Component parent) {
		final Component oldp = getParent();
		if (oldp == parent)
			return; //nothing changed

		if (parent != null && !(parent instanceof Tree)
		&& !(parent instanceof Treeitem))
			throw new UiException("Wrong parent: "+parent);

		final Tree oldtree = oldp != null ? getTree(): null;

		super.setParent(parent);

		//maintain the selected status
		if (oldtree != null)
			oldtree.onTreechildrenRemoved(this);
		if (parent != null) {
			final Tree tree = getTree();
			if (tree != null) tree.onTreechildrenAdded(this);
		}
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Treeitem))
			throw new UiException("Unsupported child for treechildren: "+child);
		return super.insertBefore(child, insertBefore);
		/**if (super.insertBefore(child, insertBefore)) {
			afterInsert(child);

			final int sz = getChildren().size();
			if (sz == 1) { //the first child been added
				Executions.getCurrent().setAttribute(ATTR_NO_CHILD, Boolean.TRUE);
				//Denote this execution has no children at beginning
			} else { //second child
				final int pgsz = getPageSize();
				if (pgsz > 0 && ((sz % pgsz) == 1 || pgsz == 1)) //one more page
					smartUpdatePaging();
			}
			return true;
		}
		return false;*/
	}
	public boolean removeChild(Component child) {
		return super.removeChild(child);
		/**
		if (child.getParent() == this)
			beforeRemove(child);
		return super.removeChild(child);*/
	}
	/** Callback if a child has been inserted.
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 * @deprecated 3.0.7
	 */
	protected void afterInsert(Component comp) {
		checkInvalidateForMoved(comp, false);
	}
	/** Callback if a child will be removed (not removed yet).
	 * <p>Default: invalidate if it is the paging mold and it affects
	 * the view of the active page.
	 * @since 3.0.5
	 * @deprecated 3.0.7
	 */
	protected void beforeRemove(Component comp) {
		checkInvalidateForMoved(comp, true);
	}
	/** Checks whether to invalidate, when a child has been added or 
	 * or will be removed.
	 * @param bRemove if child will be removed
	 * @deprecated 3.0.7
	 */
	private void checkInvalidateForMoved(Component child, boolean bRemove) {
		//No need to invalidate if
		//1) act == last and child in act
		//2) act != last and child after act
		//Except removing last elem which in act and act has only one elem
		if (!isInvalidated()) {
			List children = getChildren();
			int sz = children.size();
			if (!bRemove && sz <= 1) { //Bug 1753216: no visual part
				invalidate();
				return;
			}

			int pgsz = getPageSize();
			if (pgsz > 0) {
				int n = sz - (getActivePage() + 1) * pgsz;
				if (n <= 0) { //must be last page
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
	}
	public void invalidate() {
		final Component parent = getParent();
		if (parent instanceof Tree) {
			//Browser Limitation (IE/FF): we cannot update TBODY only
			parent.invalidate();
		} else if (!getChildren().isEmpty()) {
		//Don't invalidate if no child at all, since there is no
		//counter-part at the client
			super.invalidate();
		}
	}
	public void smartUpdate(String name, String value) {
		Component comp = getParent();
		if (comp instanceof Treeitem)
			comp = ((Treeitem)comp).getTreerow();
		if (comp != null)
			comp.smartUpdate(name, value);
	}
	/**
	 * Component developer only.
	 * @since 3.0.7
	 */
	public Iterator getVisibleItemIterator() {
		return new VisibleItemIterator();
	}
	/**
	 * An iterator used by visible item.
	 */
	private class VisibleItemIterator implements Iterator {
		private final Iterator _it = getChildren().iterator();
		private Tree _tree = getTree();

		public boolean hasNext() {
			if (!_tree.inPagingMold()) return _it.hasNext();
			Integer renderedCount = (Integer)_tree.getAttribute(Attributes.RENDERED_ITEM_COUNT);
			final Paginal pgi = _tree.getPaginal();
			if (renderedCount == null || renderedCount.intValue() < pgi.getPageSize())
				return _it.hasNext();
			return false; 
		}
		public Object next() {
			return _it.next();
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements Cropper {
		//--Cropper--//
		public boolean isCropper() {
			return getTree().inPagingMold();
		}
		public Set getAvailableAtClient() {
			final Tree tree = getTree();
			final Component parent = getParent();
			if (!isCropper() || tree == null) return null;
			if ((parent instanceof Treeitem)
				&& !((Treeitem)parent).isOpen()) return Collections.EMPTY_SET;
			final Execution exe = Executions.getCurrent();
			final String uuid = tree.getUuid();
			Map map = (Map)exe.getAttribute(VISIBLE_ITEM + uuid);
			if (map == null) {
				map = tree.getCurrentVisibleItem();
				Executions.getCurrent().setAttribute(VISIBLE_ITEM + uuid, map);
			}
			final Set avail = new HashSet(37);
			for (Iterator it = getChildren().iterator();it.hasNext();) {
				Treeitem item = (Treeitem)it.next();
				if (!map.containsKey(item)) continue;
				avail.add(item);
			}
			return avail;
		}
	}
}
