/* Treechildren.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:55:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import static org.zkoss.lang.Generics.cast;

import java.io.IOException;
import java.io.Writer;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.impl.XulElement;

/**
 * A treechildren.
 *
 * @author tomyeh
 */
public class Treechildren extends XulElement {
	private static final String VISIBLE_ITEM = "org.zkoss.zul.Treechildren.visibleItem";

	private int _visibleItemCount;

	/** Returns the {@link Tree} instance containing this element.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree) p;
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
		return parent instanceof Treeitem ? ((Treeitem) parent).getTreerow() : null;
	}

	/*package*/ boolean isRealVisible() {
		if (!isVisible())
			return false;
		Component comp = getParent();
		if (comp == null)
			return true;
		if (!(comp instanceof Treeitem))
			return comp.isVisible();
		Treeitem item = (Treeitem) comp;
		return item.isOpen() && item.isRealVisible();
	}

	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 */
	public Collection<Treeitem> getItems() {
		return new AbstractCollection<Treeitem>() {
			public int size() {
				return getItemCount();
			}

			public boolean isEmpty() {
				return getChildren().isEmpty();
			}

			public Iterator<Treeitem> iterator() {
				return new Iterator<Treeitem>() {
					private final Iterator<Component> _it = getChildren().iterator();
					private Iterator<Treeitem> _sub;

					public boolean hasNext() {
						return (_sub != null && _sub.hasNext()) || _it.hasNext();
					}

					public Treeitem next() {
						if (_sub != null && _sub.hasNext())
							return _sub.next();

						final Treeitem item = (Treeitem) _it.next();
						final Treechildren tc = item.getTreechildren();
						_sub = tc != null ? tc.getItems().iterator() : null;
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
			final Treeitem item = (Treeitem) it.next();
			final Treechildren tchs = item.getTreechildren();
			if (tchs != null)
				sz += tchs.getItemCount();
		}
		return sz;
	}

	/**
	 * Returns the number of visible descendant {@link Treeitem}.
	 * Descendants include direct children, grand children and so on.
	 *
	 * @since 3.0.7
	 */
	public int getVisibleItemCount() {
		return _visibleItemCount;
	}

	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		addVisibleItemCount(((Treeitem) child).getVisibleItemCount());
	}

	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		addVisibleItemCount(-((Treeitem) child).getVisibleItemCount());
	}

	/*package*/ void addVisibleItemCount(int count) {
		if (count == 0)
			return;
		Component parent = getParent();
		if (parent instanceof Treeitem) {
			if (((Treeitem) parent).isOpen())
				((Treeitem) parent).addVisibleItemCount(count);
		} else if (parent instanceof Tree)
			((Tree) parent).addVisibleItemCount(count);
		_visibleItemCount += count;
	}

	//bug #3051305: Active Page not update when drag & drop item to the end
	public boolean insertBefore(Component newChild, Component refChild) {
		final Tree tree = getTree();
		if (newChild.getParent() == this && tree != null && tree.inPagingMold() && !tree.isInvalidated()) { //might change page, have to invalidate 
			tree.invalidate();
		}
		return super.insertBefore(newChild, refChild);
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Tree related 
	 * components, please refer to {@link Tree} and {@link Treecol} instead.
	 */
	public void setWidth(String width) {
		// Don't remove this method, it's to override super.setWidth().
	}

	/**
	 * @deprecated as of release 6.0.0. To control the size of Tree related 
	 * components, please refer to {@link Tree} and {@link Treecol} instead.
	 */
	public void setHflex(String flex) {
		// Don't remove this method, it's to override super.setHflex().
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Tree) && !(parent instanceof Treeitem))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	public void setParent(Component parent) {
		final Component oldp = getParent();
		if (oldp == parent)
			return; //nothing changed

		final Tree oldtree = oldp != null ? getTree() : null;

		super.setParent(parent);

		//maintain the selected status
		if (oldtree != null)
			oldtree.onTreechildrenRemoved(this);
		if (parent != null) {
			final Tree tree = getTree();
			if (tree != null)
				tree.onTreechildrenAdded(this);
		}
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Treeitem))
			throw new UiException("Unsupported child for treechildren: " + child);
		super.beforeChildAdded(child, refChild);
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-treechildren" : _zclass;
	}

	@SuppressWarnings("unchecked")
	protected void smartUpdate(String name, Object value) {
		Component comp = getParent();
		if (comp instanceof Treeitem) {
			Treerow tr = ((Treeitem) comp).getTreerow();
			if (tr != null)
				tr.smartUpdate(name, value);
		} else if (comp instanceof Tree) {
			((Tree) comp).smartUpdate(name, value);
		} else {
			// do it later for bug ZK-2206
			Map<String, Object> attributes = (Map<String, Object>) getAttribute(
					"org.zkoss.zul.Treechildren_smartUpdate");
			if (attributes == null) {
				attributes = new LinkedHashMap<String, Object>(3);
				setAttribute("org.zkoss.zul.Treechildren_smartUpdate", attributes);
			}
			attributes.put(name, value);
		}
	}

	@SuppressWarnings("unchecked")
	public void onPageAttached(Page newpage, Page oldpage) {
		Map<String, Object> attributes = (Map<String, Object>) removeAttribute(
				"org.zkoss.zul.Treechildren_smartUpdate");
		if (attributes != null) {
			for (Map.Entry<String, Object> me : attributes.entrySet())
				smartUpdate(me.getKey(), me.getValue());
		}
	}

	/**
	 * An iterator used by visible children.
	 */
	private class VisibleChildrenIterator implements Iterator {
		private final Iterator _it = getChildren().iterator();
		private Tree _tree = getTree();

		private VisibleChildrenIterator() {
		}

		public boolean hasNext() {
			if (_tree == null || !_tree.inPagingMold())
				return _it.hasNext();

			Integer renderedCount = (Integer) _tree.getAttribute(Attributes.RENDERED_ITEM_COUNT);
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

	protected void redrawChildren(Writer out) throws IOException {
		if (getAttribute(Attributes.SHALL_RENDER_ITEM) == null) {
			for (Iterator it = new VisibleChildrenIterator(); it.hasNext();)
				((ComponentCtrl) it.next()).redraw(out);
		}
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
			final Tree tree = getTree();
			return tree != null && tree.inPagingMold();
		}

		public Component getCropOwner() {
			return getTree();
			//the whole tree is a single cropping scope
		}

		public Set<? extends Component> getAvailableAtClient() {
			if (!isCropper())
				return null;

			final Tree tree = getTree();
			final Component parent = getParent();
			final Execution exe = Executions.getCurrent();
			final String attrnm = VISIBLE_ITEM + tree.getUuid();
			Map<Treeitem, Boolean> map = cast((Map) exe.getAttribute(attrnm));
			if (map == null) {
				//Test very simple case first since getVisibleItems costly
				if (parent instanceof Treeitem) {
					for (Treeitem ti = (Treeitem) parent;;) {
						if (!ti.isOpen())
							return Collections.emptySet();
						Component gp = ti.getParent().getParent();
						if (!(gp instanceof Treeitem))
							break;
						ti = (Treeitem) gp;
					}
				}

				map = tree.getVisibleItems();
				Executions.getCurrent().setAttribute(attrnm, map);
			}
			return map.keySet();
			//yes, we return all visible items, not just direct children
			//in other words, we consider the whole tree as a single scope
			//See also bug 2814504
		}
	}
}
