/* Treeitem.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:56:15     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.sys.ComponentCtrl;

import org.zkoss.zul.ext.Paginal;
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

	static {
		addClientEvent(Treeitem.class, Events.ON_OPEN, CE_IMPORTANT);
	}
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
			smartUpdate("checkable", checkable);
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
			smartUpdate("disabled", disabled);
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
			smartUpdate("_loaded", _loaded);
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
		_value = value;
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
			smartUpdate("open", _open);
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
	 * <p>If treerow and treecell are not created, we automatically create it.
	 *
	 * <p>Notice that this method will create a treerow and treecell automatically
	 * if they don't exist. Thus, you cannot attach a treerow to it again if
	 * set an image or a label.
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
	 * <p>If treerow and treecell are not created, we automatically create it.
	 *
	 * <p>Notice that this method will create a treerow and treecell automatically
	 * if they don't exist. Thus, you cannot attach a treerow to it again if
	 * set an image or a label.
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

	public boolean setVisible(boolean visible) {
		if(isVisible() != visible){
			smartUpdate("visible", visible);
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

		final Tree oldtree = oldp != null ? getTree(): null;
		super.setParent(parent);

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
		}
		super.onChildRemoved(child);
	}
	
	// Returns whether the treeitem should be visited.
	private static boolean shallVisitTree(Tree tree, Component child) {
		final Treeitem item = (Treeitem) child;
		int count = item.isOpen() && item.getTreechildren() != null ? 
				item.getTreechildren().getVisibleItemCount(): 0;
		Integer visited = (Integer)tree.getAttribute(Attributes.VISITED_ITEM_COUNT);
		final Paginal pgi = tree.getPaginal();
		final int ofs = pgi.getActivePage() * pgi.getPageSize();
		int visit = visited != null ? visited.intValue() + 1 : 1;
		boolean shoulbBeVisited = ofs < visit + count;
		if (visited == null) visited = new Integer(shoulbBeVisited ? 1 : count + 1);
		else visited = new Integer(visited.intValue()+ (shoulbBeVisited ? 1 : count + 1));

		Integer total = (Integer)tree.getAttribute(Attributes.VISITED_ITEM_TOTAL);
		if (total == null) total = new Integer(count + 1);
		else total = new Integer(total.intValue() + count + 1);
		tree.setAttribute(Attributes.VISITED_ITEM_COUNT, visited);
		tree.setAttribute(Attributes.VISITED_ITEM_TOTAL, total);
		return shoulbBeVisited;
	}

	// Returns whether the specified should be rendered.
	static boolean shallRenderTree(Tree tree) {
		Integer visited = (Integer)tree.getAttribute(Attributes.VISITED_ITEM_COUNT);
		final Paginal pgi = tree.getPaginal();
		final int ofs = pgi.getActivePage() * pgi.getPageSize();
		if(ofs < visited.intValue()) {
			// count the rendered item
			Integer renderedCount = (Integer) tree.getAttribute(Attributes.RENDERED_ITEM_COUNT);
			if (renderedCount == null) renderedCount = new Integer(1);
			else renderedCount = new Integer(renderedCount.intValue() + 1);
			tree.setAttribute(Attributes.RENDERED_ITEM_COUNT, renderedCount);
			return true;
		}
		return false;
	}
	
	protected void redrawChildren(Writer out) throws IOException {
		Tree tree = getTree();
		if (!tree.inPagingMold()) {
			super.redrawChildren(out);
		} else if (isVisible() && shallVisitTree(tree, this)) {
			if (shallRenderTree(tree)) {
				ComponentCtrl child = getTreerow();
				if (child != null)
					child.redraw(out);
			}
			boolean close = !isOpen();
			ComponentCtrl child = getTreechildren();
			if (child != null) {
				if (close) ((Component)child).setAttribute(Attributes.SHALL_RENDER_ITEM, Boolean.TRUE);
				child.redraw(out);
				if (close) ((Component)child).removeAttribute(Attributes.SHALL_RENDER_ITEM);
			}			
		}
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
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		render(renderer, "selected", isSelected());
		render(renderer, "disabled", isDisabled());
		if (!isOpen()) renderer.render("open", false);
		if (!isCheckable()) renderer.render("checkable", false);
		render(renderer, "_loaded", isLoaded());
		
		if (_value instanceof String)
			render(renderer, "value", _value);
	}
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);

			_open = evt.isOpen();
			if (_treechildren != null && isVisible()) {
				if (_open)
					addVisibleItemCount(_treechildren.getVisibleItemCount(), false);
				else {
					addVisibleItemCount(-_treechildren.getVisibleItemCount(), true);
				}
			}

			final Tree tree = getTree();
			if ( _open && !isLoaded() && tree != null && tree.getModel() != null) {
				tree.renderItem(Treeitem.this);

				// better client side performance with invalidate
				if (_treechildren != null && _treechildren.getChildren().size() >= 5)
					invalidate();
			}

			// Bug #2838782
			if (tree != null && tree.inPagingMold())
				tree.focus();
			
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
