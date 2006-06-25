/* Listbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 15 17:25:00     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.AbstractSequentialList;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collections;
import java.util.NoSuchElementException;

import com.potix.lang.Classes;
import com.potix.lang.Objects;
import com.potix.lang.Strings;
import com.potix.util.logging.Log;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.Selectable;
import com.potix.zk.ui.ext.Render;
import com.potix.zk.ui.event.Events;

import com.potix.zul.html.impl.XulElement;
import com.potix.zul.html.event.ListDataEvent;
import com.potix.zul.html.event.ListDataListener;

/**
 * A listbox.
 *
 * <p>Event:
 * <ol>
 * <li>com.potix.zk.ui.event.SelectEvent is sent when user changes
 * the selection.</li>
 * </ol>
 *
 * <p>See <a href="package-summary.html">Specification</a>.</p>
 *
 * <p>Besides creating {@link Listitem} programmingly, you could assign
 * a data model (a {@link ListModel} instance) to a listbox
 * via {@link #setModel} and then
 * the listbox will retrieve data via {@link ListModel#getElementAt} when
 * necessary.
 *
 * <p>Besides assign a list model, you could assign a renderer
 * (a {@link ListitemRenderer} instance) to a listbox, and then it will use this
 * renderer to render the data returned by {@link ListModel#getElementAt}.
 * 
 * <p>Default {@link #getSclass}: listbox.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see ListModel
 * @see ListitemRenderer
 */
public class Listbox extends XulElement
implements Selectable, Render, java.io.Serializable {
	private static final Log log = Log.lookup(Listbox.class);

	private transient List _items;
	/** A list of selected items. */
	private transient Set _selItems = new LinkedHashSet(5);
	private int _maxlength;
	private int _rows, _jsel = -1;
	private transient Listhead _listhead;
	private transient Listfoot _listfoot;
	private ListModel _model;
	private ListitemRenderer _renderer;
	private ListDataListener _dataListener;
	/** The name. */
	private String _name;
	private boolean _multiple;
	private boolean _disabled, _readonly, _checkmark;
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;

	public Listbox() {
		setSclass("listbox");
		init();
	}
	private void init() {
		_items = new AbstractSequentialList () {
			public ListIterator listIterator(int index) {
				return new ItemIter(index);
			}
			public int size() {
				int sz = getChildren().size();
				if (_listhead != null) --sz;
				if (_listfoot != null) --sz;
				return sz;
			}
		};
	}

	/** Returns {@link Listhead} belonging to this listbox, or null
	 * if no list headers at all.
	 */
	public Listhead getListhead() {
		return _listhead;
	}
	/** Returns {@link Listfoot} belonging to this listbox, or null
	 * if no list footers at all.
	 */
	public Listfoot getListfoot() {
		return _listfoot;
	}

	/** Returns whether the HTML's select tag is used.
	 */
	/*package*/ final boolean isHtmlSelect() {
		return "select".equals(getMold());
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
			if (!isHtmlSelect()) invalidate(INNER);
		}
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public final boolean isDisabled() {
		return _disabled;
	}
	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			if (isHtmlSelect()) {
				smartUpdate("disabled", _disabled);
			} else {
				smartUpdate("zk_disabled", _disabled);
			}
		}
	}
	/** Returns whether it is readonly.
	 * <p>Default: false.
	 */
	public final boolean isReadonly() {
		return _readonly;
	}
	/** Sets whether it is readonly.
	 */
	public void setReadonly(boolean readonly) {
		if (_readonly != readonly) {
			_readonly = readonly;
			if (isHtmlSelect()) {
				smartUpdate("readonly", _readonly);
			} else {
				smartUpdate("zk_readonly", _readonly);
			}
		}
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

			if (isHtmlSelect()) {
				smartUpdate("size", _rows > 0 ? Integer.toString(_rows): null);
			} else {
				smartUpdate("zk_size", Integer.toString(_rows));
				initAtClient();
				//Don't use smartUpdate because client has to extra job
				//besides maintaining HTML DOM
			}
		}
	}

	/** Returns the seltype.
	 * <p>Default: "single".
	 */
	public String getSeltype() {
		return _multiple ? "multiple": "single";
	}
	/** Sets the seltype.
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
				final Listitem item = getSelectedItem();
				_selItems.clear();
				if (item != null)
					_selItems.add(item);
				//No need to update zk_selId because zk_multiple will do the job
			}

			if (isHtmlSelect()) smartUpdate("multiple", _multiple);
			else if (isCheckmark()) invalidate(INNER); //change check mark
			else smartUpdate("zk_multiple", _multiple);
				//No need to use response because such info is carried on tags
		}
	}
	/** Returns the ID of the selected item (it is stored as the zk_selId
	 * attribute of the listbox).
	 */
	private String getSelectedId() {
		final Listitem sel = getSelectedItem();
		return sel != null ? sel.getUuid(): "zk_n_a";
	}

	/** Returns the maximal length of each item's label.
	 * <p>Note: DBCS counts  two bytes (range 0x4E00~0x9FF).
	 * Default: 0 (no limit).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of each item's label.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			if (isHtmlSelect()) //affects only the HTML-select listbox
				invalidate(OUTER);
				//Both IE and Mozilla are buggy if we insert options by innerHTML
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
			if (isHtmlSelect()) smartUpdate("name", _name);
			else if (_name != null) smartUpdate("zk_name", _name);
			else invalidate(OUTER); //1) generate _value; 2) add submit listener

			_name = name;
		}
	}

	/** Returns a live list of all {@link Listitem}.
	 * By live we mean you can add or remove them directly with
	 * the List interface. In other words, you could add or remove
	 * an item by manipulating the returned list directly.
	 */
	public List getItems() {
		return _items;
	}
	/** Returns the number of items.
	 */
	public int getItemCount() {
		return _items.size();
	}
	/** Returns the item at the specified index.
	 */
	public Listitem getItemAtIndex(int index) {
		return (Listitem)_items.get(index);
	}
	/** Returns the index of the specified item, or -1 if not found.
	 */
	public int getIndexOfItem(Listitem item) {
		return item == null ? -1: item.getIndex();
	}

	/** Returns the index of the selected item (-1 if no one is selected).
	 */
	public int getSelectedIndex() {
		return _jsel;
	}
	/** Deselects all of the currently selected items and selects
	 * the item with the given index.
	 */
	public void setSelectedIndex(int jsel) {
		if (jsel >= _items.size())
			throw new UiException("Out of bound: "+jsel+" while size="+_items.size());
		if (jsel < -1) 
			jsel = -1;
		if (jsel < 0) { //unselct all
			clearSelection();
		} else if (jsel != _jsel
		|| (_multiple && _selItems.size() > 1)) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Listitem item = (Listitem)it.next();
				item.setSelectedDirectly(false);
			}
			_selItems.clear();

			_jsel = jsel;
			final Listitem item = getItemAtIndex(_jsel);
			item.setSelectedDirectly(true);
			_selItems.add(item);
			smartUpdate("selectedIndex", Integer.toString(_jsel));
				//No need to use response because such info is carried on tags
		}
	}

	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #setSelectedItem}.
	 * @param item the item to select. If null, all items are deselected.
	 */
	public void selectItem(Listitem item) {
		if (item == null) {
			setSelectedIndex(-1);
		} else {
			if (item.getParent() != this)
				throw new UiException("Not a child: "+item);
			if (_multiple || !item.isSelected())
				setSelectedIndex(item.getIndex());
		}
	}
	/** Selects the given item, without deselecting any other items
	 * that are already selected..
	 */
	public void addItemToSelection(Listitem item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: "+item);

		if (!item.isSelected()) {
			if (!_multiple) {
				selectItem(item);
			} else {
				if (item.getIndex() < _jsel || _jsel < 0) {
					_jsel = item.getIndex();
					if (!isHtmlSelect()) smartUpdate("zk_selId", getSelectedId());
				}
				item.setSelectedDirectly(true);
				_selItems.add(item);
				if (isHtmlSelect()) {
					item.smartUpdate("selected", true);
				} else {
					smartUpdate("addSel", item.getUuid());
					//No need to use response because such info is carried on tags
				}
			}
		}
	}
	/**  Deselects the given item without deselecting other items.
	 */
	public void removeItemFromSelection(Listitem item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: "+item);

		if (item.isSelected()) {
			if (!_multiple) {
				clearSelection();
			} else {
				final int oldSel = _jsel;
				item.setSelectedDirectly(false);
				_selItems.remove(item);
				fixSelectedIndex(0);
				if (isHtmlSelect()) {
					item.smartUpdate("selected", false);
				} else {
					smartUpdate("rmSel", item.getUuid());
					if (oldSel != _jsel)
						smartUpdate("zk_selId", getSelectedId());
					//No need to use response because such info is carried on tags
				}
			}
		}
	}
	/** If the specified item is selected, it is deselected.
	 * If it is not selected, it is selected. Other items in the list box
	 * that are selected are not affected, and retain their selected state.
	 */
	public void toggleItemSelection(Listitem item) {
		if (item.isSelected()) removeItemFromSelection(item);
		else addItemToSelection(item);
	}
	/** Clears the selection.
	 */
	public void clearSelection() {
		if (!_selItems.isEmpty()) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Listitem item = (Listitem)it.next();
				item.setSelectedDirectly(false);
			}
			_selItems.clear();
			_jsel = -1;
			smartUpdate("selectedIndex", "-1");
		}
	}
	/** Selects all items.
	 */
	public void selectAll() {
		if (!_multiple)
			throw new UiException("Appliable only to the multiple seltype: "+this);

		if (_items.size() != _selItems.size()) {
			for (Iterator it = _items.iterator(); it.hasNext();) {
				final Listitem item = (Listitem)it.next();
				_selItems.add(item);
				item.setSelectedDirectly(true);
			}
			_jsel = _items.isEmpty() ? -1: 0;
			smartUpdate("selectAll", "true");
		}
	}

	/** Returns the selected item.
	 */
	public Listitem getSelectedItem() {
		return _jsel >= 0 ? getItemAtIndex(_jsel): null;
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #selectItem}.
	 */
	public void setSelectedItem(Listitem item) {
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

	/** Appends an item.
	 */
	public Listitem appendItem(String label, String value) {
		final Listitem item = new Listitem(label, value);
		item.applyProperties();
		item.setParent(this);
		return item;
	}
	/**  Removes the child item in the list box at the given index.
	 * @return the removed item.
	 */
	public Listitem removeItemAt(int index) {
		final Listitem item = getItemAtIndex(index);
		removeChild(item);
		return item;
	}

	/** Re-init the listbox at the client.
	 */
	/*package*/ void initAtClient() {
		smartUpdate("zk_init", true);
	}

	//-- Selectable --//
	public void selectItemsByClient(Set selItems) {
		_noSmartUpdate = true;
		try {
			if (!_multiple || selItems == null || selItems.size() <= 1) {
				final Listitem item =
					selItems != null && selItems.size() > 0 ?
						(Listitem)selItems.iterator().next(): null;
				selectItem(item);
			} else {
				int j = 0;
				for (Iterator it = _items.iterator(); it.hasNext(); ++j) {
					final Listitem item = (Listitem)it.next();
					if (selItems.contains(item)) {
						addItemToSelection(item);
					} else {
						removeItemFromSelection(item);
					}
				}
			}
		} finally {
			_noSmartUpdate = false;
		}
	}

	//-- Component --//
	public void smartUpdate(String attr, String value) {
		if (!_noSmartUpdate) super.smartUpdate(attr, value);
	}
	/** When detached from a page, it is detached from the model
	 * (by invoking {@link #setModel} with null) automatically.
	 */
	public void setPage(Page page) {
		super.setPage(page);
		if (page == null && _model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
		}
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		if (isHtmlSelect()) invalidate(OUTER);
			//Both IE and Mozilla are buggy if we insert options by innerHTML
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (isHtmlSelect()) invalidate(OUTER);
			//Both IE and Mozilla are buggy if we remove options by outerHTML
			//CONSIDER: use special command to remove items
			//Cons: if user remove a lot of items it is slower
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Listitem) {
			if (refChild instanceof Listhead)
				throw new UiException("listhead must be the first child");
			if (refChild == null) {
				if (_listfoot != null) refChild = _listfoot; //listfoot must be the last one
			} else if (refChild == _listhead)
				throw new UiException("Unable to insert before listhead: "+newChild);

			final boolean existChild = newChild.getParent() == this;
			if (super.insertBefore(newChild, refChild)) {
				final List children = getChildren();
				if (_listhead != null && children.get(1) == newChild)
					invalidate(INNER);
				//we place listhead and treeitem at different div, so
				//this case requires invalidate (because we use insert-after)

				//Maintain _items
				final Listitem childItem = (Listitem)newChild;
				int fixFrom = childItem.getIndex();
				if (refChild != null && refChild != _listfoot
				&& refChild.getParent() == this) {
					final int k = ((Listitem)refChild).getIndex();
					if (fixFrom < 0 || k < fixFrom) fixFrom = k;
				}
				if (fixFrom < 0) childItem.setIndex(_items.size() - 1);
				else fixItemIndices(fixFrom);

				//Maintain selected
				final int childIndex = childItem.getIndex();
				if (childItem.isSelected()) {
					if (_jsel < 0) {
						_jsel = childIndex;
						if (!isHtmlSelect()) smartUpdate("zk_selId", getSelectedId());
						_selItems.add(childItem);
					} else if (_multiple) {
						if (_jsel > childIndex) {
							_jsel = childIndex;
							if (!isHtmlSelect()) smartUpdate("zk_selId", getSelectedId());
						}
						_selItems.add(childItem);
					} else { //deselect
						childItem.setSelectedDirectly(false);
						childItem.invalidate(OUTER);
					}
				} else {
					if (_jsel >= childIndex) {
						++_jsel;
						if (!isHtmlSelect()) smartUpdate("zk_selId", getSelectedId());
					}
				}
				initAtClient();
				return true;
			}
			return false;
		} else if (newChild instanceof Listhead) {
			if (_listhead != null && _listhead != newChild)
				throw new UiException("Only one listhead is allowed: "+this);
			if (!getChildren().isEmpty())
				refChild = (Component)getChildren().get(0);
				//always makes listhead as the first child

			if (isHtmlSelect())
				log.warning("Mold select ignores listhead");
			invalidate(INNER);
				//we place listhead and treeitem at different div, so...
			_listhead = (Listhead)newChild;
			return super.insertBefore(newChild, refChild);
		} else if (newChild instanceof Listfoot) {
			if (_listfoot != null && _listfoot != newChild)
				throw new UiException("Only one listfoot is allowed: "+this);
			refChild = null; //always makes listfoot as the last child

			if (isHtmlSelect())
				log.warning("Mold select ignores listfoot");
			invalidate(INNER);
				//we place listfoot and treeitem at different div, so...
			_listfoot = (Listfoot)newChild;
			return super.insertBefore(newChild, refChild);
		} else {
			throw new UiException("Unsupported child for Listbox: "+newChild);
		}
	}
	public boolean removeChild(Component child) {
		if (!super.removeChild(child))
			return false;

		if (_listhead == child) {
			_listhead = null;
		} else if (_listfoot == child) {
			_listfoot = null;
		} else if (child instanceof Listitem) {
			//maintain items
			final Listitem childItem = (Listitem)child;
			final int childIndex = childItem.getIndex();
			childItem.setIndex(-1); //mark
			fixItemIndices(childIndex);

			//Maintain selected
			if (childItem.isSelected()) {
				_selItems.remove(childItem);
				if (_jsel == childIndex) {
					fixSelectedIndex(childIndex);
					if (!isHtmlSelect()) smartUpdate("zk_selId", getSelectedId());
				}
			} else {
				if (_jsel >= childIndex) {
					--_jsel;
					if (!isHtmlSelect()) smartUpdate("zk_selId", getSelectedId());
				}
			}
		}
		initAtClient();
		return true;
	}
	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the item.
	 */
	private void fixSelectedIndex(int j) {
		if (!_selItems.isEmpty()) {
			for (Iterator it = _items.listIterator(j); it.hasNext(); ++j) {
				final Listitem item = (Listitem)it.next();
				if (item.isSelected()) {
					_jsel = j;
					return;
				}
			}
		}
		_jsel = -1;
	}
	/** Fix Childitem._index since j-th item. */
	private void fixItemIndices(int j) {
		for (Iterator it = _items.listIterator(j); it.hasNext(); ++j)
			((Listitem)it.next()).setIndex(j);
	}

	//-- ListModel dependent codes --//
	/** Returns the list model associated with this listbox, or null
	 * if this listbox is not associated with any list data model.
	 */
	public ListModel getModel() {
		return _model;
	}
	/** Sets the list model associated with this listbox.
	 * If a non-null model is assigned, no matter whether it is the same as
	 * the previous, it will always cause re-render.
	 *
	 * @param model the list model to associate, or null to dis-associate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 */
	public void setModel(ListModel model) {
		if (model != null) {
			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
				} else {
					if (isHtmlSelect())
						throw new UnsupportedOperationException("Mold select doesn't support ListModel");
					smartUpdate("zk_model", "true");
				}

				_model = model;

				if (_dataListener == null) {
					_dataListener = new ListDataListener() {
						public void onChange(ListDataEvent event) {
							onListDataChange(event);
						}
					};
				}
				_model.addListDataListener(_dataListener);
			}

			//Always syncModel because it is easier for user to enfore reload
			syncModel(-1, -1);
			Events.postEvent("onInitRender", this, null);
			//Since user might setModel and setRender separately or repeatedly,
			//we don't handle it right now until the event processing phase
			//such that we won't render the same set of data twice
			//--
			//For better performance, we shall load the first few row now
			//(to save a roundtrip)
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
			getItems().clear();
			smartUpdate("zk_model", null);
		}
	}
	/** Returns the renderer to render each item, or null if the default
	 * renderer is used.
	 */
	public ListitemRenderer getItemRenderer() {
		return _renderer;
	}
	/** Sets the renderer which is used to render each item
	 * if {@link #getModel} is not null.
	 *
	 * <p>Note: changing a render will not cause the listbox to re-render.
	 * If you want it to re-render, you could assign the same model again 
	 * (i.e., setModel(getModel())), or fire an {@link ListDataEvent} event.
	 *
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize with the model
	 */
	public void setItemRenderer(ListitemRenderer renderer) {
		if (_renderer != renderer)
			_renderer = renderer;
	}
	/** Sets the renderer by use of a class name.
	 * It creates an instance automatically.
	 */
	public void setItemRenderer(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setItemRenderer((ListitemRenderer)Classes.newInstanceByThread(clsnm));
	}

	/** Synchronizes the listbox to be consistent with the specified model.
	 * @param min the lower index that a range of invalidated items
	 * @param max the higher index that a range of invalidated items
	 */
	private void syncModel(int min, int max) {
		final int newsz = _model.getSize();
		final int oldsz = getItemCount();
		if (oldsz > 0) {
			if (newsz > 0 && min < oldsz) {
				if (max < 0 || max >= oldsz) max = oldsz - 1;
				if (max >= newsz) max = newsz - 1;
				if (min < 0) min = 0;

				for (Iterator it = _items.listIterator(min);
				min <= max && it.hasNext(); ++min)
					clearItemAsUnloaded((Listitem)it.next());
			}

			for (int j = newsz; j < oldsz; ++j)
				getItemAtIndex(newsz).detach(); //detach and remove
		}

		for (int j = oldsz; j < newsz; ++j)
			newUnloadedItem().setParent(this);
	}
	/** Creates an new and unloaded listitem. */
	private static Listitem newUnloadedItem() {
		final Listitem item = new Listitem();
		item.applyProperties();
		item.setLoaded(false);

		final Listcell cell = new Listcell();
		cell.applyProperties();
		cell.setParent(item); //an empty listheader
		return item;
	}
	/** Clears a listitem as if it is not loaded. */
	private static void clearItemAsUnloaded(Listitem item) {
		final List cells = item.getChildren();
		if (cells.isEmpty()) {
			final Listcell cell = new Listcell();
			cell.applyProperties();
			cell.setParent(item);
		} else {
			final Listcell listcell = (Listcell)cells.get(0);
			listcell.setLabel(null);
			listcell.setImage(null);
			for (int k = cells.size(); --k > 0;)
				((Component)cells.get(1)).detach(); //detach and remove
		}
		item.setLoaded(false);
	}
	/** Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 */
	public void onInitRender() {
		final Renderer renderer = new Renderer();
		try {
			for (int j = 0, sz = getItemCount(); j < _rows && j < sz; ++j)
				renderer.render(getItemAtIndex(j));
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/** Handles when the list model's content changed.
	 */
	private void onListDataChange(ListDataEvent event) {
		//when this is called _model is never null
		final int newsz = _model.getSize(), oldsz = getItemCount();
		int min = event.getIndex0(), max = event.getIndex1();
		if (min < 0) min = 0;

		boolean done = false;
		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			if (max < 0) max = newsz - 1;
			if ((max - min + 1) != (newsz - oldsz)) {
				log.warning("Conflict event: number of added items not matched: "+event);
				break; //handle it as CONTENTS_CHANGED
			}
			final Listitem before = min < oldsz ? getItemAtIndex(min): null;
			for (int j = min; j <= max; ++j)
				insertBefore(newUnloadedItem(), before);
			done = true;
			break;

		case ListDataEvent.INTERVAL_REMOVED:
			if (max < 0) max = oldsz - 1;
			if ((max - min + 1) != (oldsz - newsz)) {
				log.warning("Conflict event: number of removed items not matched: "+event);
				break; //handle it as CONTENTS_CHANGED
			}
			for (int j = min; j <= max; ++j)
				getItemAtIndex(min).detach(); //detach and remove
			done = true;
			break;
		}

		if (!done) //CONTENTS_CHANGED
			syncModel(min, max);

		initAtClient();
			//client have to send back for what have to reload
	}

	private static final ListitemRenderer getDefaultItemRenderer() {
		return _defRend;
	}
	private static final ListitemRenderer _defRend = new ListitemRenderer() {
		public void render(Listitem item, Object data) {
			item.setLabel(Objects.toString(data));
			item.setValue(data);
		}
	};

	/** Used to render listitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final ListitemRenderer _renderer;
		private boolean _rendered, _ctrled;

		private Renderer() {
			_renderer = Listbox.this._renderer != null ?
				Listbox.this._renderer: getDefaultItemRenderer();
		}
		private void render(Listitem item) throws Throwable {
			if (item.isLoaded())
				return; //nothing to do

			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}

			final Listcell cell = (Listcell)item.getChildren().get(0);
			cell.detach();
			try {
				_renderer.render(item, _model.getElementAt(item.getIndex()));
			} catch (Throwable ex) {
				clearItemAsUnloaded(item); //recover it
				throw ex;
			} finally {
				if (item.getChildren().isEmpty())
					cell.setParent(item);
			}

			item.setLoaded(true);
			_rendered = true;
		}
		private void doCatch(Throwable ex) {
			if (_ctrled) {
				try {
					((RendererCtrl)_renderer).doCatch(ex);
				} catch (Throwable t) {
					throw UiException.Aide.wrap(t);
				}
			}
		}
		private void doFinally() {
			if (_rendered)
				initAtClient();
					//reason: after rendering, the column width might change
					//Also: Mozilla remembers scrollTop when user's pressing
					//RELOAD, it makes init more desirable.
			if (_ctrled)
				((RendererCtrl)_renderer).doFinally();
		}
	}

	/** Renders the specified listitem if not not loaded yet,
	 * with {@link #getItemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * In other words, it is meaningful only if live data model is used.
	 */
	public void renderItem(Listitem li) {
		if (_model == null) { //just in case that app dev might change it
			if (log.debugable()) log.debug("No model no render");
			return;
		}

		final Renderer renderer = new Renderer();
		try {
			renderer.render(li);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	//-- Render --//
	public void renderItems(Set items) {
		if (_model == null) { //just in case that app dev might change it
			if (log.debugable()) log.debug("No model no render");
			return;
		}

		if (items.isEmpty())
			return; //nothing to do

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = items.iterator(); it.hasNext();)
				renderer.render((Listitem)it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	//-- super --//
	public void setMold(String mold) {
		if (_model != null && "select".equals(mold))
			throw new UnsupportedOperationException("Mold select doesn't support ListModel");
		super.setMold(mold);
	}
	public void invalidate(Range range) {
		if (isHtmlSelect()) {
			super.invalidate(range);
		} else {
			super.invalidate(OUTER);
				//OUTER is required because zk_selId might be overwrite by INNER
				//If OUTER, cleanup is invoked automatically
		}
	}
	public void setHeight(String height) {
		if (!Objects.equals(height, getHeight())) {
			super.setHeight(height);
			if (!isHtmlSelect()) initAtClient();
		}
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());

		if (isHtmlSelect()) {
			HTMLs.appendAttribute(sb, "name", _name);
			HTMLs.appendAttribute(sb, "size",  getRows());

			if (isMultiple())
				HTMLs.appendAttribute(sb, "multiple",  "multiple");
			if (_disabled)
				HTMLs.appendAttribute(sb, "disabled",  "disabled");
			if (_readonly)
				HTMLs.appendAttribute(sb, "readonly", "readonly");
		} else {
			HTMLs.appendAttribute(sb, "zk_name", _name);
			HTMLs.appendAttribute(sb, "zk_size",  _rows);
			if (_disabled)
				HTMLs.appendAttribute(sb, "zk_disabled",  _disabled);
			if (_readonly)
				HTMLs.appendAttribute(sb, "zk_readonly", _readonly);
			if (_multiple)
				HTMLs.appendAttribute(sb, "zk_multiple", _multiple);
			HTMLs.appendAttribute(sb, "zk_selId", getSelectedId());
			//if (_checkmark)
			//	HTMLs.appendAttribute(sb, "zk_checkmark",  _checkmark);
			if (_model != null)
				HTMLs.appendAttribute(sb, "zk_model", true);
		}

		if (isAsapRequired("onSelect"))
			HTMLs.appendAttribute(sb, "zk_onSelect", true);
		return sb.toString();
	}

	private class ItemIter implements ListIterator, java.io.Serializable {
		private ListIterator _it;
		private int _j;
		private boolean _bNxt;
		private ItemIter(int index) {
			_j = index;
		}
		public void add(Object o) {
			prepare();
			_it.add(o);
			++_j;
		}
		public boolean hasNext() {
			return _j < _items.size();
		}
		public boolean hasPrevious() {
			return _j > 0;
		}
		public Object next() {
			if (!hasNext())
				throw new NoSuchElementException();

			prepare();
			final Object o = _it.next();
			++_j;
			_bNxt = true;
			return o;
		}
		public Object previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();

			prepare();
			final Object o = _it.previous();
			--_j;
			_bNxt = false;
			return o;
		}
		public int nextIndex() {
			return _j;
		}
		public int previousIndex() {
			return _j - 1;
		}
		public void remove() {
			if (_it == null) throw new IllegalStateException();
			_it.remove();
			if (_bNxt) --_j;
		}
		public void set(Object o) {
			if (_it == null) throw new IllegalStateException();
			_it.set(o);
		}
		private void prepare() {
			if (_it == null)
				_it = getChildren()
					.listIterator(_listhead != null ? _j + 1: _j);
		}
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		//TODO
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		//TODO
	}

	//Cloneable//
	public Object clone() {
		final Listbox clone = (Listbox)super.clone();
		fixClone(clone);
		return clone;
	}
	private static void fixClone(Listbox clone) {
		clone.init();

		int cnt = clone._selItems.size();
		if (clone._listhead != null) ++cnt;
		if (clone._listfoot != null) ++cnt;
		if (cnt == 0) return; //nothing to do

		clone._selItems = new LinkedHashSet(5);
		for (Iterator it = clone.getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Listitem) {
				final Listitem li = (Listitem)child;
				if (li.isSelected()) {
					clone._selItems.add(li);
					if (--cnt == 0) break;
				}
			} else if (child instanceof Listhead) {
				clone._listhead = (Listhead)child;
				if (--cnt == 0) break;
			} else if (child instanceof Listfoot) {
				clone._listfoot = (Listfoot)child;
				if (--cnt == 0) break;
			}
		}
	}
}
