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
import java.util.LinkedHashSet;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.render.Cropper;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Paginal;

/**
 * A treechildren.
 *
 * @author tomyeh
 */
public class Treechildren extends XulElement implements org.zkoss.zul.api.Treechildren {
	private static final String VISIBLE_ITEM = "org.zkoss.zul.Treechildren.visibleItem";

	private int _visibleItemCount;
	/** Returns the {@link Tree} instance containing this element.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
	}
	/** Returns the {@link Tree} instance containing this element.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
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
	/** Returns the {@link Treerow} that is associated with
	 * this treechildren, or null if no such treerow.
	 * In other words, it is {@link Treeitem#getTreerow} of
	 * {@link #getParent}.
	 *
	 * @since 3.5.2
	 * @see Treerow#getLinkedTreechildren
	 */
	public org.zkoss.zul.api.Treerow getLinkedTreerowApi() {
		return getLinkedTreerow();
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
	 * Returns the number of visible descendant {@link Treeitem}.
	 * Descendants include direct children, gran children and so on.
	 *
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
	
	/** @deprecated As of release 3.0.7, the page size is controlled
	 * by {@link Tree#getPageSize} rather than this method.
	 * It always return -1 since 3.0.7.
	 */
	public int getPageSize() {
		return -1;
	}
	/** @deprecated As of release 3.0.7, the page size is controlled
	 * by {@link Tree#setPageSize} rather than this method.
	 * It always does nothing since 3.0.7
	 */
	public void setPageSize(int size) throws WrongValueException {
	}

	/** @deprecated As of release 3.0.7, the page size is controlled
	 * by {@link Tree#getPageSize} rather than this method.
	 * It always return 1 since 3.0.7.
	 */
	public int getPageCount() {
		return 1;
	}
	/** @deprecated As of release 3.0.7, the page size is controlled
	 * by {@link Tree#getPageSize} rather than this method.
	 * It always return 0 since 3.0.7.
	 */
	public int getActivePage() {
		return 0;
	}
	/** @deprecated As of release 3.0.7, the page size is controlled
	 * by {@link Tree#setPageSize} rather than this method.
	 * It always does nothing since 3.0.7
	 */
	public void setActivePage(int pg) throws WrongValueException {
	}
	/** @deprecated As of release 3.0.7, the page size is controlled
	 * by {@link Tree#setPageSize} rather than this method.
	 * It always returns 0 since 3.0.7
	 */
	public int getVisibleBegin() {
		return 0;
	}
	/** @deprecated As of release 3.0.7, the page size is controlled
	 * by {@link Tree#setPageSize} rather than this method.
	 * It always returns Integer.MAX_VALUE since 3.0.7
	 */
	public int getVisibleEnd() {
		return Integer.MAX_VALUE;
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

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-tree-children" : _zclass;
	}
	protected void smartUpdate(String name, Object value) {
		Component comp = getParent();
		if (comp instanceof Treeitem)
			((Treeitem)comp).getTreerow().smartUpdate(name, value);
		else
			((Tree)comp).smartUpdate(name, value);
	}
	/** Returns an iterator to iterate thru all visible children.
	 * Unlike {@link #getVisibleItemCount}, it handles only the direct children.
	 * Component developer only.
	 * @since 3.0.7
	 */
	public Iterator getVisibleChildrenIterator() {
		return new VisibleChildrenIterator();
	}
	/**
	 * An iterator used by visible children.
	 */
	private class VisibleChildrenIterator implements Iterator {
		private final Iterator _it = getChildren().iterator();
		private Tree _tree = getTree();

		public boolean hasNext() {
			if (_tree == null || !_tree.inPagingMold()) return _it.hasNext();

			Integer renderedCount = (Integer)_tree.getAttribute(Attributes.RENDERED_ITEM_COUNT);
			if (renderedCount == null || renderedCount.intValue() < _tree.getPaginal().getPageSize())
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
			final Tree tree = getTree();
			return tree != null && tree.inPagingMold();
		}
		public Set getAvailableAtClient() {
			if (!isCropper()) return null;

			final Tree tree = getTree();
			final Component parent = getParent();
			final Execution exe = Executions.getCurrent();
			final String attrnm = VISIBLE_ITEM + tree.getUuid();
			Map map = (Map)exe.getAttribute(attrnm);
			if (map == null) {
				//Test very simple case first since getVisibleItems costly
				if (parent instanceof Treeitem) {
					for (Treeitem ti = (Treeitem)parent;;) {
						if (!ti.isOpen())
							return Collections.EMPTY_SET;
						Component gp = ti.getParent().getParent();
						if (!(gp instanceof Treeitem))
							break;
						ti = (Treeitem)gp;
					}
				}

				map = tree.getVisibleItems();
				Executions.getCurrent().setAttribute(attrnm, map);
			}

			//If parent is not in map, all its children not visible
			if (parent != tree && !map.containsKey(parent))
				return Collections.EMPTY_SET;

			final Set avail = new LinkedHashSet(32);
			for (Iterator it = getChildren().iterator();it.hasNext();) {
				Treeitem item = (Treeitem)it.next();
				if (!map.containsKey(item))
					if (avail.isEmpty() || !item.isVisible()) continue;
					else break;
				avail.add(item);
			}
			return avail;
		}
	}
}
