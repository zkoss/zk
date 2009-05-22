/* Treeitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:15     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Iterator;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.client.Openable;

import org.zkoss.zul.impl.XulElement;

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
 * @author tomyeh
 */
public class Treeitem extends XulElement implements org.zkoss.zul.api.Treeitem {
	private transient Treerow _treerow;
	private transient Treechildren _treechildren;
	private Object _value;
	private boolean _open = true;
	private boolean _selected;
	private boolean _disabled;
	private boolean _checkable = true;;
	
	/** whether the content of this item is loaded; used if
	 * the tree owning this item is using a tree model.
	 */
	private boolean _loaded;
	
	public Treeitem() {
	}
	public Treeitem(String label) {
		setLabel(label);
	}
	public Treeitem(String label, Object value) {
		setLabel(label);
		setValue(value);
	}

	/** Returns whether it is checkable.
	 * <p>Default: true.
	 * @since 3.0.4
	 */
	public boolean isCheckable() {
		return _checkable;
	}
	/** Sets whether it is checkable.
	 * <p>Default: true.
	 * @since 3.0.4
	 */
	public void setCheckable(boolean checkable) {
		if (_checkable != checkable) {
			_checkable = checkable;
			invalidate();
		}
	}
	
	/**
	 * Unload the tree item
	 * <p>To load the tree item, with 
	 * {@link Tree#renderItem(Treeitem)}, {@link Tree#renderItem(Treeitem, Object)}, or {@link Tree#renderItems(java.util.Set)}
	 * 
	 * @since 3.0.4
	 */
	public void unload(){
		if(isLoaded()){
			//Clean its children
			if(getTreechildren() != null)
				getTreechildren().getChildren().clear();
			//Set the load status to unloaded
			setLoaded(false);
			//Change the "+/-" sign icon
			setOpen(false);
		}
	}
	
	/**
	 * Sets whether it is disabled.
	 * @since 3.0.1
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			invalidate();
		}
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 3.0.1
	 */
	public boolean isDisabled() {
		return _disabled;
	}
	
	/**
	 * Return true whether all children of this tree item, if any, is loaded
	 * @return true whether all children of this tree item is loaded
	 * @since 3.0.0
	 */
	public boolean isLoaded(){
		return _loaded;
	}
	
	/**
	 * Sets whether all children of this tree item, if any, is loaded.
	 * @since 3.0.0
	 */
	/*package*/ void setLoaded(boolean loaded){
		if (_loaded != loaded) {
			_loaded = loaded;
			if (_treerow != null)
				_treerow.smartUpdate("z.lod", _loaded ? "": "t");
		}
	}
	
	/**
	 * return the index of this container 
	 * @return the index of this container 
	 * @since 3.0.0
	 */
	public int indexOf() {
		List list = this.getParent().getChildren();
		return list.indexOf(this);
	}
	
	
	/** Returns the treerow that this tree item owns (might null).
	 * Each tree items has exactly one tree row.
	 */
	public Treerow getTreerow() {
		return _treerow;
	}
	/** Returns the treerow that this tree item owns (might null).
	 * Each tree items has exactly one tree row.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treerow getTreerowApi() {
		return getTreerow();
	}
	/** Returns the treechildren that this tree item owns, or null if
	 * doesn't have any child.
	 */
	public Treechildren getTreechildren() {
		return _treechildren;
	}
	/** Returns the treechildren that this tree item owns, or null if
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treechildren getTreechildrenApi() {
		return getTreechildren();
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
			if (tree != null && tree.getName() != null)
				smartUpdate("z.value", Objects.toString(_value));
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
			if (_treerow != null)
				_treerow.smartUpdate("z.open", _open);
			//If the item is open, its tree has model and not rendered, render the item
			boolean shouldNotify = _treechildren != null && isVisible();
			if(_open) {
				if (shouldNotify) addVisibleItemCount(_treechildren.getVisibleItemCount(), false);
				Tree tree = getTree();
				if(tree != null && tree.getModel() !=null){
					tree.renderItem(this);
				}
			} else 
				if (shouldNotify) addVisibleItemCount(-_treechildren.getVisibleItemCount(), true);
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
		final Treecell cell = getFirstCell();
		return cell != null ? cell.getLabel(): null;
	}
	/** Sets the label of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setLabel(String label) {
		autoFirstCell().setLabel(label);
	}
	private Treecell getFirstCell() {
		return _treerow != null ? (Treecell)_treerow.getFirstChild(): null;
	}
	private Treecell autoFirstCell() {
		if (_treerow == null) {
			final Treerow row = new Treerow();
			row.applyProperties();
			row.setParent(this);
		}

		Treecell cell = (Treecell)_treerow.getFirstChild();
		if (cell == null) {
			cell = new Treecell();
			cell.applyProperties();
			cell.setParent(_treerow);
		}
		return cell;
	}

	/** @deprecated As of release 3.5.0, it is redundant since it
	 * the same as {@link #getImage}.
	 */
	public String getSrc() {
		return getImage();
	}
	/** @deprecated As of release 3.5.0, it is redundant since it
	 * the same as {@link #setImage}.
	 */
	public void setSrc(String src) {
		setImage(src);
	}
	/** Returns the image of the {@link Treecell} it contains.
	 */
	public String getImage() {
		final Treecell cell = getFirstCell();
		return cell != null ? cell.getImage(): null;
	}
	/** Sets the image of the {@link Treecell} it contains.
	 *
	 * <p>If it is not created, we automatically create it.
	 */
	public void setImage(String image) {
		autoFirstCell().setImage(image);
	}

	/** Returns the parent tree item,
	 * or null if this item is already the top level of the tree.
	 * The parent tree item is actually the grandparent if any.
	 *
	 * @since 3.0.0
	 */
	public Treeitem getParentItem() {
		final Component p = getParent();
		final Component gp = p != null ? p.getParent(): null;
		return gp instanceof Treeitem ? (Treeitem)gp: null;
	}
	/** Returns the parent tree item,
	 * or null if this item is already the top level of the tree.
	 * The parent tree item is actually the grandparent if any.
	 *
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treeitem getParentItemApi() {
		return getParentItem();
	}
	/** Returns the tree owning this item.
	 */
	public Tree getTree() {
		for (Component p = this; (p = p.getParent()) != null;)
			if (p instanceof Tree)
				return (Tree)p;
		return null;
	}
	/** Returns the tree owning this item.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}

	//-- super --//

	//this is declared to make it accessible to Treerow
	protected boolean isAsapRequired(String evtnm) {
		return super.isAsapRequired(evtnm);
	}
	public void smartUpdate(String attr, String value) {
		if (_treerow != null) _treerow.smartUpdate(attr, value);
	}

	/** No callable. Use {@link Treerow#setDraggable} isntead.
	 */
	public void setDraggable(String draggable) {
		if (draggable != null)
			throw new UnsupportedOperationException("Use Treerow.setDraggable() instead");
	}
	/** No callable. Use {@link Treerow#setDroppable} isntead.
	 */
	public void setDroppable(String dropable) {
		if (dropable != null)
			throw new UnsupportedOperationException("Use Treerow.setDroppable() instead");
	}
	/** Not callable. Use {@link Treerow#setAction} instead.
	 */
	public void setAction(String action) {
		if (action != null)
			throw new UnsupportedOperationException("Use Treerow.setAction() instead");
	}

	public boolean setVisible(boolean visible) {
		if(isVisible() != visible){
			if (isVisible() != visible && _treerow != null) 
				_treerow.smartUpdate("z.visible", visible);
			int count = isOpen() && _treechildren != null ?
					_treechildren.getVisibleItemCount() + 1: 1;
					boolean result = super.setVisible(visible);
					if (isVisible()) {
						addVisibleItemCount(count, false);
					} else {
						addVisibleItemCount(-count, true);
					}
					return result;
		}
		return visible;
	}
	
	/**
	 * Returns the number of visible descendant {@link Treechildren}.
	 * Descendants include direct children, grand children and so on.
	 *
	 * @since 3.6.1
	 */
	public int getVisibleItemCount() {
		return isVisible() ? 1 + (_treechildren != null ? _treechildren.getVisibleItemCount() : 0 ): 0;
	}
	/**
	 * adds the number of the visible item to the count of its parent.
	 * @param count
	 * @param force if true, ignores {@link #isVisible()}
	 * @since 3.0.7
	 */
	void addVisibleItemCount(int count, boolean force) {
		Treechildren tc = (Treechildren) getParent();
		if (tc != null && (force || isVisible()))
			tc.addVisibleItemCount(count);
	}
	
	//impl note: no getOuterAttrs, ON_OPEN..., since treeitem is virtual
	//and the related codes are in treerow

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treechildren))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	public void setParent(Component parent) {
		final Component oldp = getParent();
		if (oldp == parent)
			return; //nothing changed

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

		if (affected != null && affected._treerow != null)
			affected._treerow.invalidate(); //only the first row

		if (parent != null) {
			final List sibs = parent.getChildren();
			final int sz = sibs.size();
			if (sz > 1 && sibs.get(sz - 1) == this) {
				affected = (Treeitem)sibs.get(sz - 2);
				if (affected._treerow != null)
					affected._treerow.invalidate(); //only the first row
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
	public void beforeChildAdded(Component child, Component refChild) {
		if (child instanceof Treerow) {
			if (_treerow != null && _treerow != child)
				throw new UiException("Only one treerow is allowed: "+this);
		} else if (child instanceof Treechildren) {
			if (_treechildren != null && _treechildren != child)
				throw new UiException("Only one treechildren is allowed: "+this);
		} else {
			throw new UiException("Unsupported child for tree item: "+child);
		}
		super.beforeChildAdded(child, refChild);
	}
	public boolean insertBefore(Component child, Component refChild) {
		if (child instanceof Treerow) {
			if (super.insertBefore(child, refChild)) {
				_treerow = (Treerow)child;
				return true;
			}
		} else if (child instanceof Treechildren) {
			if (super.insertBefore(child, refChild)) {
				_treechildren = (Treechildren)child;
				if (_treerow != null)
					_treerow.invalidate();
				return true;
			}
		} else {
			return super.insertBefore(child, refChild);
				//impossible but more extensible
		}
		return false;
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		if (_treechildren == child)
			addVisibleItemCount(_treechildren.getVisibleItemCount(), false);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Treerow) {
			_treerow = null;
		} else if (child instanceof Treechildren) {
			addVisibleItemCount(-_treechildren.getVisibleItemCount(), false);
			_treechildren = null;
			invalidate();
		}
		super.onChildRemoved(child);
	}
	
	public void invalidate() {
		//There is no counter-part at client if no tree row
		//We didn't set ATTR_NO_CHILD at insertBefore, since we cannot
		//solve the issue that a treeitem without treerow:(
		if (_treerow != null)
			super.invalidate();
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

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements Openable {
		//-- Openable --//
		public void setOpenByClient(boolean open) {
			_open = open;

			if (_treechildren != null && isVisible()) {
				if (_open)
					addVisibleItemCount(_treechildren.getVisibleItemCount(), false);
				else {
					addVisibleItemCount(-_treechildren.getVisibleItemCount(), true);
				}
			}
			if (_open) {
				final Tree tree = getTree();
				if (tree != null && tree.getModel() != null)
					tree.renderItem(Treeitem.this);
			}
		}
	}
}
