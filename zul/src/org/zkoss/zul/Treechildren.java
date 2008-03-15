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

import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.render.Cropper;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Pageable;

/**
 * A treechildren.
 *
 * @author tomyeh
 */
public class Treechildren extends XulElement implements Pageable {
	private static final String ATTR_NO_CHILD = "org.zkoss.zul.Treechildren.noChild";

	/** the active page. */
	private int _actpg;
	/** # of items per page. Zero means the same as Tree's. */
	private int _pgsz;

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
	 */
	public int getPageSize() {
		if (_pgsz != 0) return _pgsz;
		final Tree tree = getTree();
		return tree != null ? tree.getPageSize(): -1;
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
	 */
	public void setPageSize(int size) throws WrongValueException {
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
		}
	}

	/** Returns the number of pages (at least one).
	 * Note: there is at least one page even no item at all.
	 *
	 * @since 2.4.1
	 */
	public int getPageCount() {
		final int cnt = getChildren().size();
		if (cnt <= 0) return 1;
		final int pgsz = getPageSize();
		return pgsz <= 0 ? 1: 1 + (cnt - 1)/pgsz;
	}
	/** Returns the active page (starting from 0).
	 *
	 * @since 2.4.1
	 */
	public int getActivePage() {
		return _actpg;
	}
	/** Sets the active page (starting from 0).
	 *
	 * @exception WrongValueException if no such page
	 * @since 2.4.1
	 */
	public void setActivePage(int pg) throws WrongValueException {
		final int pgcnt = getPageCount();
		if (pg >= pgcnt || pg < 0)
			throw new WrongValueException("Unable to set active page to "+pg+" since only "+pgcnt+" pages");
		if (_actpg != pg) {
			_actpg = pg;

			invalidate();
			smartUpdatePaging();
				//it affect treerow (so invalidate won't 'eat' it)
		}
	}
	/** Called by {@link Tree} to set the active page directly. */
	/*package*/ void setActivePageDirectly(int pg) {
		_actpg = pg;
	}
	/** Returns the index of the first visible child.
	 * <p>Used only for component development, not for application developers.
	 * @since 2.4.1
	 */
	public int getVisibleBegin() {
		final int pgsz = getPageSize();
		return pgsz <= 0 ? 0: getActivePage() * pgsz;
	}
	/** Returns the index of the last visible child.
	 * <p>Used only for component development, not for application developers.
	 * @since 2.4.1
	 */
	public int getVisibleEnd() {
		final int pgsz = getPageSize();
		return pgsz <= 0 ? Integer.MAX_VALUE:
			(getActivePage() + 1) * getPageSize() - 1; //inclusive
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

		if (super.insertBefore(child, insertBefore)) {
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
		return false;
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);

		final int pgsz = getPageSize();
		if (pgsz > 0) {
			final int sz = getChildren().size();
			if (sz > 0 && ((sz % pgsz) == 0 || pgsz == 1)) { //one page less
				final int pgcnt = smartUpdatePaging();
				if (_actpg >= pgcnt) { //removing the last page
					_actpg = pgcnt - 1;
					getParent().invalidate();
				//We have to invalidate the parent, since
				//no client at the item when user removes the last one
				//Server: generate rm and outer in this case
				}
			} else if (getParent() instanceof Tree) {
				smartUpdatePaging(); //Bug 1877059
			}
		}
	}
	public void invalidate() {
		final Component parent = getParent();
		if (parent instanceof Tree) {
			//Browser Limitation (IE/FF): we cannot update TBODY only
			parent.invalidate();
		} else if (!getChildren().isEmpty()
		&& Executions.getCurrent().getAttribute(ATTR_NO_CHILD) == null) {
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
	/** Updates paging related information.
	 * @return # of pages
	 */
	private int smartUpdatePaging() {
		//We update all attributes at once, because
		//1) if pgsz <= 1, none of them are generated (to save HTML size)
		final int pgcnt = getPageCount();
		smartUpdate("z.pgInfo",
			pgcnt+","+getActivePage()+","+getPageSize());
		return pgcnt;
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
			final int pgsz = getPageSize();
			return pgsz > 0 && getChildren().size() >= pgsz;
				//Single page is considered as not a cropper.
				//It is called after a component is removed, so
				//we have to test >= rather than >
		}
		public Set getAvailableAtClient() {
			int pgsz = getPageSize();
			if (pgsz <= 0 || getChildren().size() <= pgsz)
				return null;

			final Set avail = new HashSet(37);
			final int ofs = getActivePage() * pgsz;
			for (final Iterator it = getChildren().listIterator(ofs);
			--pgsz >= 0 && it.hasNext();)
				avail.add(it.next());
			return avail;
		}
	}
}
