/* Tree.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:51:33     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.Selectable;

import com.potix.zul.html.impl.XulElement;
import com.potix.zk.au.AuInit;

/**
 *  A container which can be used to hold a tabular
 * or hierarchical set of rows of elements.
 *
 * <p>Event:
 * <ol>
 * <li>com.potix.zk.ui.event.SelectEvent is sent when user changes
 * the selection.</li>
 * </ol>
 *
 * <p>Default {@link #getSclass}: tree.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Tree extends XulElement implements Selectable {
	private Treecols _treecols;
	private Treechildren _treechildren;
	/** A list of selected items. */
	private final Set _selItems = new LinkedHashSet(5);
	/** The first selected item. */
	private Treeitem _sel;
	private int _rows = 0;
	/** The name. */
	private String _name;
	private boolean _multiple, _checkmark;
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;

	public Tree() {
		setSclass("tree");
	}

	/** Returns the treecols that this tree owns (might null).
	 */
	public Treecols getTreecols() {
		return _treecols;
	}
	/** Returns the treechildren that this tree owns; never null.
	 */
	public Treechildren getTreechildren() {
		if (_treechildren == null) {
			new Treechildren().setParent(this); //to make caller easy to handle
		}
		return _treechildren;
	}

	/** Returns the rows. Zero means no limitation.
	 * <p>Default: 0.
	 */
	public int getRows() {
		return _rows;
	}
	/** Sets the rows.
	 * <p>Note: if both {@link #setHeight} is specified with non-empty,
	 * {@link #setRows} is ignored
	 */
	public void setRows(int rows) throws WrongValueException {
		if (rows < 0)
			throw new WrongValueException("Illegal rows: "+rows);

		if (_rows != rows) {
			_rows = rows;
			smartUpdate("zk_size", Integer.toString(_rows));
			initAtClient();
				//Don't use smartUpdate because client has to extra job
				//besides maintaining HTML DOM
		}
	}

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			if (_name != null) smartUpdate("zk_name", _name);
			else invalidate(OUTER); //1) generate _value; 2) add submit listener
			_name = name;
		}
	}

	/** Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: false.
	 */
	public final boolean isCheckmark() {
		return _checkmark;
	}
	/** Sets whether the check mark shall be displayed in front
	 * of each item.
	 * <p>The check mark is a checkbox if {@link #isMultiple} returns
	 * true. It is a radio button if {@link #isMultiple} returns false.
	 */
	public void setCheckmark(boolean checkmark) {
		if (_checkmark != checkmark) {
			_checkmark = checkmark;
			invalidate(INNER);
		}
	}

	/** Returns the seltype.
	 * <p>Default: "single".
	 */
	public String getSeltype() {
		return _multiple ? "multiple": "single";
	}
	/** Sets the seltype.
	 * Currently, only "single" is supported.
	 */
	public void setSeltype(String seltype) throws WrongValueException {
		if ("single".equals(seltype)) setMultiple(false);
		else if ("multiple".equals(seltype)) setMultiple(true);
		else throw new WrongValueException("Unknown seltype: "+seltype);
	}
	/** Returns whether multiple selections are allowed.
	 * <p>Default: false.
	 */
	public boolean isMultiple() {
		return _multiple;
	}
	/** Sets whether multiple selections are allowed.
	 */
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			if (!_multiple && _selItems.size() > 1) {
				final Treeitem item = getSelectedItem();
				_selItems.clear();
				if (item != null)
					_selItems.add(item);
				//No need to update zk_selId because zk_multiple will do the job
			}
			if (isCheckmark()) invalidate(INNER); //change check mark
			else smartUpdate("zk_multiple", _multiple);
		}
	}
	/** Returns the ID of the selected item (it is stored as the zk_selId
	 * attribute of the tree).
	 */
	private String getSelectedId() {
		//NOTE: Treerow's uuid; not Treeitem's
		return _sel != null ? _sel.getTreerow().getUuid(): "zk_n_a";
	}

	/** Returns a readonly list of all {@link Treeitem} including
	 * all descendants.
	 */
	public Collection getItems() {
		return _treechildren != null ? _treechildren.getChildren(): Collections.EMPTY_LIST;
	}
	/** Returns the number of child {@link Treeitem}.
	 * The same as {@link #getItems}.size().
	 * <p>Note: the performance of this method is no good.
	 */
	public int getItemCount() {
		return _treechildren != null ? _treechildren.getItemCount(): 0;
	}

	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #setSelectedItem}.
	 * @param item the item to select. If null, all items are deselected.
	 */
	public void selectItem(Treeitem item) {
		if (item == null) {
			clearSelection();
		} else {
			if (item.getTree() != this)
				throw new UiException("Not a child: "+item);

			if (_sel != item
			|| (_multiple && _selItems.size() > 1)) {
				for (Iterator it = _selItems.iterator(); it.hasNext();) {
					final Treeitem ti = (Treeitem)it.next();
					ti.setSelectedDirectly(false);
				}
				_selItems.clear();

				_sel = item;
				item.setSelectedDirectly(true);
				_selItems.add(item);

				smartUpdate("select", item.getTreerow().getUuid());
				//No need to use response because such info is carried on tags
			}
		}
	}
	/** Selects the given item, without deselecting any other items
	 * that are already selected..
	 */
	public void addItemToSelection(Treeitem item) {
		if (item.getTree() != this)
			throw new UiException("Not a child: "+item);

		if (!item.isSelected()) {
			if (!_multiple) {
				selectItem(item);
			} else {
				item.setSelectedDirectly(true);
				_selItems.add(item);
				smartUpdate("addSel", item.getTreerow().getUuid());
				if (fixSelected())
					smartUpdate("zk_selId", getSelectedId());
				//No need to use response because such info is carried on tags
			}
		}
	}
	/**  Deselects the given item without deselecting other items.
	 */
	public void removeItemFromSelection(Treeitem item) {
		if (item.getTree() != this)
			throw new UiException("Not a child: "+item);

		if (item.isSelected()) {
			if (!_multiple) {
				clearSelection();
			} else {
				item.setSelectedDirectly(false);
				_selItems.remove(item);
				smartUpdate("rmSel", item.getTreerow().getUuid());
				if (fixSelected())
					smartUpdate("zk_selId", getSelectedId());
				//No need to use response because such info is carried on tags
			}
		}
	}
	/** If the specified item is selected, it is deselected.
	 * If it is not selected, it is selected. Other items in the tree
	 * that are selected are not affected, and retain their selected state.
	 */
	public void toggleItemSelection(Treeitem item) {
		if (item.isSelected()) removeItemFromSelection(item);
		else addItemToSelection(item);
	}
	/** Clears the selection.
	 */
	public void clearSelection() {
		if (!_selItems.isEmpty()) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Treeitem item = (Treeitem)it.next();
				item.setSelectedDirectly(false);
			}
			_selItems.clear();
			_sel = null;
			smartUpdate("select", "");
		}
	}
	/** Selects all items.
	 */
	public void selectAll() {
		if (!_multiple)
			throw new UiException("Appliable only to the multiple seltype: "+this);

		//we don't invoke getItemCount first because it is slow!
		boolean changed = false, first = true;
		for (Iterator it = getItems().iterator(); it.hasNext();) {
			final Treeitem item = (Treeitem)it.next();
			if (!item.isSelected()) {
				_selItems.add(item);
				item.setSelectedDirectly(true);
				changed = true;
			}
			if (first) {
				_sel = item;
				first = false;
			}
		}
		smartUpdate("selectAll", "true");
	}


	/** Returns the selected item.
	 */
	public Treeitem getSelectedItem() {
		return _sel;
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #selectItem}.
	 */
	public void setSelectedItem(Treeitem item) {
		selectItem(item);
	}

	/** Returns all selected items.
	 */
	public Set getSelectedItems() {
		return Collections.unmodifiableSet(_selItems);
	}
	/** Returns the number of items being selected.
	 */
	public int getSelectedCount() {
		return _selItems.size();
	}

	/** Clears all child tree items ({@link Treeitem}.
	 * <p>Note: after clear, {@link #getTreechildren} won't be null, but
	 * it has no child
	 */
	public void clear() {
		if (_treechildren == null)
			return;

		final List l = _treechildren.getChildren();
		if (l.isEmpty())
			return; //nothing to do

		for (Iterator it = new ArrayList(l).iterator(); it.hasNext();)
			((Component)it.next()).detach();
	}

	/** Re-init the tree at the client.
	 */
	/*package*/ void initAtClient() {
		response("init", new AuInit(this));
	}

	//-- Selectable --//
	public void selectItemsByClient(Set selItems) {
		_noSmartUpdate = true;
		try {
			if (!_multiple || selItems == null || selItems.size() <= 1) {
				final Treeitem item =
					selItems != null && selItems.size() > 0 ?
						(Treeitem)selItems.iterator().next(): null;
				selectItem(item);
			} else {
				for (Iterator it = new ArrayList(_selItems).iterator(); it.hasNext();) {
					final Treeitem item = (Treeitem)it.next();
					if (!selItems.remove(item))
						removeItemFromSelection(item);
				}
				for (Iterator it = selItems.iterator(); it.hasNext();)
					addItemToSelection((Treeitem)it.next());
			}
		} finally {
			_noSmartUpdate = false;
		}
	}

	//-- Component --//
	public void setHeight(String height) {
		if (!Objects.equals(height, getHeight())) {
			super.setHeight(height);
			initAtClient();
		}
	}
	public void smartUpdate(String attr, String value) {
		if (!_noSmartUpdate) super.smartUpdate(attr, value);
	}
	public void invalidate(Range range) {
		super.invalidate(OUTER);
			//OUTER is required because zk_selId might be overwritten by INNER
			//If OUTER, init is invoked automatically by client
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Treecols) {
			if (_treecols != null && _treecols != child)
				throw new UiException("Only one treecols is allowed: "+this);
			if (!getChildren().isEmpty())
				insertBefore = (Component)getChildren().get(0);
				//always makes treecols as the first child
			_treecols = (Treecols)child;
			invalidate(INNER);
		} else if (child instanceof Treechildren) {
			if (_treechildren != null && _treechildren != child)
				throw new UiException("Only one treechildren is allowed: "+this);
			if (insertBefore instanceof Treecols)
				throw new UiException("treecols must be the first child");
			_treechildren = (Treechildren)child;
			invalidate(INNER);

			fixSelectedSet();
		} else {
			throw new UiException("Unsupported child for tree: "+child);
		}
		return super.insertBefore(child, insertBefore);
	}
	/** Called by {@link Treeitem} when is added to a tree. */
	/*package*/ void onTreeitemAdded(Treeitem item) {
		fixNewChild(item);
		onTreechildrenAdded(item.getTreechildren());
	}
	/** Called by {@link Treeitem} when is removed from a tree. */
	/*package*/ void onTreeitemRemoved(Treeitem item) {
		boolean fixSel = false;
		if (item.isSelected()) {
			_selItems.remove(item);
			fixSel = _sel == item;
			if (fixSel && !_multiple) {
				_sel = null;
				smartUpdate("zk_selId", getSelectedId());
				assert _selItems.isEmpty();
			}
		}
		onTreechildrenRemoved(item.getTreechildren());
		if (fixSel) fixSelected();
	}
	/** Called by {@link Treechildren} when is added to a tree. */
	/*package*/ void onTreechildrenAdded(Treechildren tchs) {
		if (tchs == null || tchs.getParent() == this)
			return; //already being processed by insertBefore

		//main the selected status
		for (Iterator it = tchs.getItems().iterator(); it.hasNext();)
			fixNewChild((Treeitem)it.next());
	}
	/** Fixes the status of new added child. */
	private void fixNewChild(Treeitem item) {
		if (item.isSelected()) {
			if (_sel != null && !_multiple) {
				item.setSelectedDirectly(false);
				item.invalidate(OUTER);
			} else {
				if (_sel == null)
					_sel = item;
				_selItems.add(item);
				smartUpdate("zk_selId", getSelectedId());
			}
		}
	}
	/** Called by {@link Treechildren} when is removed from a tree. */
	/*package*/ void onTreechildrenRemoved(Treechildren tchs) {
		if (tchs == null || tchs.getParent() == this)
			return; //already being processed by onChildRemoved

		//main the selected status
		boolean fixSel = false;
		for (Iterator it = tchs.getItems().iterator(); it.hasNext();) {
			final Treeitem item = (Treeitem)it.next();
			if (item.isSelected()) {
				_selItems.remove(item);
				if (_sel == item) {
					if (!_multiple) {
						_sel = null;
						smartUpdate("zk_selId", getSelectedId());
						assert _selItems.isEmpty();
						return; //done
					}
					fixSel = true;
				}
			}
		}
		if (fixSel) fixSelected();
	}

	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		invalidate(OUTER);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Treecols) {
			_treecols = null;
		} else if (child instanceof Treechildren) {
			_treechildren = null;
			_selItems.clear();
			_sel = null;
		}
		super.onChildRemoved(child);
		invalidate(OUTER);
	}

	/** Fixes all info about the selected status. */
	private void fixSelectedSet() {
		_sel = null; _selItems.clear();
		for (Iterator it = getItems().iterator(); it.hasNext();) {
			final Treeitem item = (Treeitem)it.next();
			if (item.isSelected()) {
				if (_sel == null) {
					_sel = item;
				} else if (!_multiple) {
					item.setSelectedDirectly(false);
					continue;
				}
				_selItems.add(item);
			}
		}
	}
	/** Make _sel to be the first selected item. */
	private boolean fixSelected() {
		Treeitem sel = null;
		switch (_selItems.size()) {
		case 1:
			sel = (Treeitem)_selItems.iterator().next();
		case 0:
			break;
		default:
			for (Iterator it = getItems().iterator(); it.hasNext();) {
				final Treeitem item = (Treeitem)it.next();
				if (item.isSelected()) {
					sel = item;
					break;
				}
			}
		}

		if (sel != _sel) {
			_sel = sel;
			return true;
		}
		return false;
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64)
			.append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "zk_name", _name);
		HTMLs.appendAttribute(sb, "zk_size",  getRows());
		HTMLs.appendAttribute(sb, "zk_selId", getSelectedId());
		if (_multiple)
			HTMLs.appendAttribute(sb, "zk_multiple", _multiple);
		//if (_checkmark)
		//	HTMLs.appendAttribute(sb, "zk_checkmark",  _checkmark);
		if (isAsapRequired("onSelect"))
			HTMLs.appendAttribute(sb, "zk_onSelect", true);
		return sb.toString();
	}
}
