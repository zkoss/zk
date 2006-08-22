/* Treeitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:15     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.Iterator;

import com.potix.lang.Objects;
import com.potix.lang.Strings;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ext.Openable;
import com.potix.zk.ui.ext.Transparent;
import com.potix.zk.ui.event.EventListener;

import com.potix.zul.html.impl.XulElement;

/**
 * A treeitem.
 *
 * <p>Event:
 * <ol>
 * <li>onOpen is sent when a tree item is opened or closed by user.</li>
 * <li>onDoubleClick is sent when user double-clicks the treeitem.</li>
 * <li>onRightClick is sent when user right-clicks the treeitem.</li>
 * </ol>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Treeitem extends XulElement implements Openable, Transparent {
	private transient Treerow _treerow;
	private transient Treechildren _treechildren;
	private Object _value;
	private boolean _open = true;
	private boolean _selected = false;

	public Treeitem() {
	}
	public Treeitem(String label) {
		setLabel(label);
	}
	public Treeitem(String label, Object value) {
		setLabel(label);
		setValue(value);
	}

	/** Returns the treerow that this tree item owns (might null).
	 * Each tree items has exactly one tree row.
	 */
	public Treerow getTreerow() {
		return _treerow;
	}
	/** Returns the treechildren that this tree item owns, or null if
	 * doesn't have any child.
	 */
	public Treechildren getTreechildren() {
		return _treechildren;
	}

	/** Returns whether the element is to act as a container
	 * which can have child elements.
	 */
	public boolean isContainer() {
		return _treechildren != null;
	}
	/** Returns whether this element contains no child elements.
	 */
	public boolean isEmpty() {
		return _treechildren == null || _treechildren.getChildren().isEmpty();
	}

	/** Returns the value. It could be anything you want.
	 * <p>Default: null.
	 * <p>Note: the value is not sent to the browser, so it is OK to be
	 * anything.
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * Note: the value is not sent to the browser, so it is OK to be
	 * anything.
	 */
	public void setValue(Object value) {
		if (!Objects.equals(_value, value)) {
			_value = value;

			final Tree tree = getTree();
			if (_treerow != null && tree != null && tree.getName() != null)
				_treerow.smartUpdate("zk_value", Objects.toString(_value));
		}
	}

	/** Returns whether this container is open.
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Sets whether this container is open.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;

			//Note: _treerow might not be ready yet because it might be
			//initialized before creating child components (for ZK pages)
			final Tree tree = getTree();
			if (tree != null && _treerow != null)
				tree.smartUpdate("open", _treerow.getUuid()+":"+_open);
		}
	}
	/** Returns whether this item is selected.
	 */
	public boolean isSelected() {
		return _selected;
	}
	/** Returns whether this item is selected.
	 */
	public void setSelected(boolean selected) {
		if (_selected != selected) {
			final Tree tree = getTree();
			if (tree != null) {
				//Note: we don't update it here but let its parent does the job
				tree.toggleItemSelection(this);
			} else {
				_selected = selected;
			}
		}
	}
	/** Updates _selected directly and invalidate getTreerow if necessary.
	 */
	/*package*/ void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}

	/** Returns the level this cell is. The root is level 0.
	 */
	public int getLevel() {
		int level = 0;
		for (Component item = this;; ++level) {
			final Component tch = item.getParent();
			if (tch == null)
				break;

			item = tch.getParent();
			if (item == null || item instanceof Tree)
				break;
		}
		return level;
	}

	/** Returns the label of the {@link Treecell} it contains, or null
	 * if no such cell.
	 */
	public String getLabel() {
		if (_treerow == null)
			return null;
		final List cells = _treerow.getChildren();
		return cells.isEmpty() ? null: ((Treecell)cells.get(0)).getLabel();
	}
	/** Sets the label of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		if (_treerow == null)
			new Treerow().setParent(this);
		final List cells = _treerow.getChildren();
		if (cells.isEmpty())
			new Treecell().setParent(_treerow);
		((Treecell)cells.get(0)).setLabel(label);
	}

	/** Returns the src of the {@link Treecell} it contains, or null
	 * if no such cell.
	 */
	public String getSrc() {
		if (_treerow == null)
			return null;
		final List cells = _treerow.getChildren();
		return cells.isEmpty() ? null: ((Treecell)cells.get(0)).getSrc();
	}
	/** Sets the src of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 *
	 * <p>The same as {@link #setImage}.
	 */
	public void setSrc(String src) {
		if (_treerow == null)
			new Treerow().setParent(this);
		final List cells = _treerow.getChildren();
		if (cells.isEmpty())
			new Treecell().setParent(_treerow);
		((Treecell)cells.get(0)).setSrc(src);
	}
	/** Returns the image of the {@link Treecell} it contains.
	 *
	 * <p>The same as {@link #getImage}.
	 */
	public String getImage() {
		return getSrc();
	}
	/** Sets the image of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 *
	 * <p>The same as {@link #setSrc}.
	 */
	public void setImage(String image) {
		setSrc(image);
	}

	/** Returns the nestest parent {@link Treeitem}.
	 */
	public Treeitem getTreeitem() {
		final Component p = getParent();
		final Component gp = p != null ? p.getParent(): null;
		return gp instanceof Treeitem ? (Treeitem)gp: null;
	}
	/** Returns the tree owning this item.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
	}

	/** Returns whether this item shall have the default focus. */
	/*package*/ boolean isFocusRequired() {
		final Tree tree = getTree();
		if (tree == null)
			return false;

		final Treeitem sel = tree.getSelectedItem();
		if (sel != null) {
			return sel == this;
		} else {
			final Iterator it = tree.getItems().iterator();
			return it.hasNext() && it.next() == this;
		}
	}

	//-- super --//

	/** Returns the attributes for onClick, onRightClick and onDoubleClick
	 * by checking whether the corresponding listeners are added,
	 * or null if none is added.
	 *
	 * <p>Implementation Note: it is declared here to make it accessible by
	 * {@link Treerow}.
	 *
	 * @param ignoreOnClick whether to ignore onClick
	 */
	protected String getAllOnClickAttrs(boolean ignoreOnClick) {
		return super.getAllOnClickAttrs(ignoreOnClick);
	}

	//this is declared to make it accessible to Treeitem 
	protected boolean isAsapRequired(String evtnm) {
		return super.isAsapRequired(evtnm);
	}
	public boolean addEventListener(String evtnm, EventListener listener) {
		//don't check isTransparent
		final boolean asap = _treerow == null || isAsapRequired(evtnm);
			//_treerow might not be ready yet (a trick to avoid smartUpdate)
		final boolean ret = super.addEventListener(evtnm, listener);
		if (ret && !asap && isAsapRequired(evtnm))
			_treerow.smartUpdate("zk_" + evtnm, "true");
		return ret;
	}
	public boolean removeEventListener(String evtnm, EventListener listener) {
		//don't check isTransparent
		final boolean asap = _treerow == null || isAsapRequired(evtnm);
			//_treerow might not be ready yet (a trick to avoid smartUpdate)
		final boolean ret = super.removeEventListener(evtnm, listener);
		if (ret && asap && !isAsapRequired(evtnm))
			_treerow.smartUpdate("zk_" + evtnm, null);
		return ret;
	}

	public void smartUpdate(String attr, String value) {
		if ("zk_ctx".equals(attr) || "zk_pop".equals(attr) || "zk_tip".equals(attr)) {
			if (_treerow != null) _treerow.smartUpdate(attr, value);
		} else {
			super.smartUpdate(attr, value); //cause IllegalStateException
		}
	}

	//-- Openable --//
	public void setOpenByClient(boolean open) {
		_open = open;
	}

	//-- Transparent --//
	/** Always returns true. */
	public boolean isTransparent() {
		return true;
	}

	//-- Component --//
	public void setParent(Component parent) {
		final Component oldp = getParent();
		if (oldp == parent)
			return; //nothing changed

		if (parent != null && !(parent instanceof Treechildren))
			throw new UiException("Wrong parent: "+parent);

		Treeitem affected = null; //what to invalidate
		if (oldp != null) {
			final List sibs = oldp.getChildren();
			final int sz = sibs.size();
			if (sz > 1 && sibs.get(sibs.size() - 1) == this)
				affected = (Treeitem)sibs.get(sibs.size() - 2);
				//we have to invalidate it because it will become last-child
		}
		final Tree oldtree = oldp != null ? getTree(): null;

		super.setParent(parent);

		if (affected != null) {
			if (affected._treerow != null)
				affected._treerow.invalidate(INNER); //only the first Treecell
			if (affected._treechildren != null)
				affected._treechildren.invalidate(INNER);
		}

		if (parent != null) {
			final List sibs = parent.getChildren();
			final int sz = sibs.size();
			if (sz > 1 && sibs.get(sz - 1) == this) {
				affected = (Treeitem)sibs.get(sz - 2);
				if (affected._treerow != null)
					affected._treerow.invalidate(INNER); //only the first Treecell
				if (affected._treechildren != null)
					affected._treechildren.invalidate(INNER);
			}
		}

		//maintain the selected status
		if (oldtree != null)
			oldtree.onTreeitemRemoved(this);
		if (parent != null) {
			final Tree tree = getTree();
			if (tree != null) tree.onTreeitemAdded(this);
		}
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Treerow) {
			if (_treerow != null && _treerow != child)
				throw new UiException("Only one treerow is allowed: "+this);
			_treerow = (Treerow)child;
		} else if (child instanceof Treechildren) {
			if (_treechildren != null && _treechildren != child)
				throw new UiException("Only one treechildren is allowed: "+this);
			_treechildren = (Treechildren)child;
			if (_treerow != null)
				_treerow.invalidate(OUTER);
		} else {
			throw new UiException("Unsupported child for tree item: "+child);
		}
		return super.insertBefore(child, insertBefore);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Treerow) {
			_treerow = null;
		} else if (child instanceof Treechildren) {
			_treechildren = null;
		}
		super.onChildRemoved(child);
	}

	//Cloneable//
	public Object clone() {
		final Treeitem clone = (Treeitem)super.clone();

		int cnt = 0;
		if (clone._treerow != null) ++cnt;
		if (clone._treechildren != null) ++cnt;
		if (cnt > 0) clone.afterUnmarshal(cnt);

		return clone;
	}
	private void afterUnmarshal(int cnt) {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Treerow) {
				_treerow = (Treerow)child;
				if (--cnt == 0) break;
			} else if (child instanceof Treechildren) {
				_treechildren = (Treechildren)child;
				if (--cnt == 0) break;
			}
		}
	}

	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		afterUnmarshal(-1);
	}
}
