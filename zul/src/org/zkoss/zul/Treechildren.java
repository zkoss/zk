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

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Transparent;

import org.zkoss.zul.impl.XulElement;

/**
 * A treechildren.
 *
 * @author tomyeh
 */
public class Treechildren extends XulElement {
	/** Returns the {@link Tree} instance containing this element.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
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

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Transparent {
		//Transparent//
		/** It is transparent if its parent is {@link Treeitem}.
		 */
		public boolean isTransparent() {
			return getParent() instanceof Treeitem;
		}	
	}
}
