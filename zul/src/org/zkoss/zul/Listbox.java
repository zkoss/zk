/* Listbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 15 17:25:00     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.AbstractSequentialList;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.RenderOnDemand;
import org.zkoss.zk.ui.ext.client.Selectable;
import org.zkoss.zk.ui.ext.render.ChildChangedAware;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.event.PagingEvent;

/**
 * A listbox.
 *
 * <p>Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.SelectEvent is sent when user changes
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
 * (a {@link ListitemRenderer} instance) to a listbox, such that
 * the listbox will use this
 * renderer to render the data returned by {@link ListModel#getElementAt}.
 * If not assigned, the default renderer, which assumes a label per
 * list item, is used.
 * In other words, the default renderer adds a label to
 * a row by calling toString against the object returned
 * by {@link ListModel#getElementAt}
 * 
 * <p>There are two ways to handle long content: scrolling and paging.
 * If {@link #getMold} is "default", scrolling is used if {@link #setHeight}
 * is called and too much content to display.
 * If {@link #getMold} is "paging", paging is used if two or more pages are
 * required. To control the number of items to display in a page, use
 * {@link #setPageSize}.
 *
 * <p>If paging is used, the page controller is either created automatically
 * or assigned explicity by {@link #setPaginal}.
 * The paging controller specified explicitly by {@link #setPaginal} is called
 * the external page controller. It is useful if you want to put the paging
 * controller at different location (other than as a child component), or
 * you want to use the same controller to control multiple listboxes.
 *
 * <p>Default {@link #getSclass}: listbox.
 *
 * @author tomyeh
 * @see ListModel
 * @see ListitemRenderer
 * @see ListitemRendererExt
 */
public class Listbox extends XulElement
implements RenderOnDemand {
	private static final Log log = Log.lookup(Listbox.class);

	private transient List _items;
	/** A list of selected items. */
	private transient Set _selItems;
	private int _maxlength;
	private int _rows, _jsel = -1;
	private transient Listhead _listhead;
	private transient Listfoot _listfoot;
	private ListModel _model;
	private ListitemRenderer _renderer;
	private transient ListDataListener _dataListener;
	/** The name. */
	private String _name;
	/** The paging controller, used only if mold = "paging". */
	private transient Paginal _pgi;
	/** The paging controller, used only if mold = "paging" and user
	 * doesn't assign a controller via {@link #setPaginal}.
	 * If exists, it is the last child
	 */
	private transient Paging _paging;
	private transient EventListener _pgListener;
	private int _tabindex = -1;
	private boolean _multiple;
	private boolean _disabled, _checkmark;
	private boolean _vflex;
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
			public Object get(int j) {
				final Object o =
					Listbox.this.getChildren().get(_listhead != null ? j + 1: j);
				if (!(o instanceof Listitem))
					throw new IndexOutOfBoundsException("Wrong index: "+j);
				return o;
			}
			public int size() {
				int sz = getChildren().size();
				if (_listhead != null) --sz;
				if (_listfoot != null) --sz;
				if (_paging != null) --sz;
				return sz;
			}
		};
		_selItems = new LinkedHashSet(5);
	}
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					onListDataChange(event);
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
	/*package*/ final boolean inSelectMold() {
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
			if (!inSelectMold()) invalidate();
		}
	}

	/** Returns whether to grow and shrink vertical to fit their given space,
	 * so called vertial flexibility.
	 *
	 * <p>Note: this attribute is ignored if {@link #setRows} is specified
	 *
	 * <p>Default: false.
	 */
	public final boolean isVflex() {
		return _vflex;
	}
	/** Sets whether to grow and shrink vertical to fit their given space,
	 * so called vertial flexibility.
	 *
	 * <p>Note: this attribute is ignored if {@link #setRows} is specified
	 */
	public void setVflex(boolean vflex) {
		if (_vflex != vflex) {
			_vflex = vflex;
			if (!inSelectMold()) smartUpdate("z.flex", _vflex);
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
			if (inSelectMold()) {
				smartUpdate("disabled", _disabled);
			} else {
				smartUpdate("z.disabled", _disabled);
			}
		}
	}

	/** Returns the tab order of this component.
	 * <p>Currently, only the "select" mold supports this property.
	 * <p>Default: -1 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _tabindex;
	}
	/** Sets the tab order of this component.
	 * <p>Currently, only the "select" mold supports this property.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if (_tabindex != tabindex) {
			_tabindex = tabindex;
			if (tabindex < 0) smartUpdate("tabindex", null);
			else smartUpdate("tabindex", Integer.toString(_tabindex));
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

			if (inSelectMold()) {
				smartUpdate("size", _rows > 0 ? Integer.toString(_rows): null);
			} else {
				smartUpdate("z.size", Integer.toString(_rows));
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
				//No need to update z.selId because z.multiple will do the job
			}

			if (inSelectMold()) smartUpdate("multiple", _multiple);
			else if (isCheckmark()) invalidate(); //change check mark
			else smartUpdate("z.multiple", _multiple);
				//No need to use response because such info is carried on tags
		}
	}
	/** Returns the ID of the selected item (it is stored as the z.selId
	 * attribute of the listbox).
	 */
	private String getSelectedId() {
		final Listitem sel = getSelectedItem();
		return sel != null ? sel.getUuid(): "zk_n_a";
	}

	/** Returns the maximal length of each item's label.
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
			if (inSelectMold()) //affects only the HTML-select listbox
				invalidate();
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
			if (inSelectMold()) smartUpdate("name", _name);
			else if (_name != null) smartUpdate("z.name", _name);
			else invalidate(); //1) generate _value; 2) add submit listener

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
					if (!inSelectMold()) smartUpdate("z.selId", getSelectedId());
				}
				item.setSelectedDirectly(true);
				_selItems.add(item);
				if (inSelectMold()) {
					item.smartUpdate("selected", true);
				} else {
					smartUpdateSelection();
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
				if (inSelectMold()) {
					item.smartUpdate("selected", false);
				} else {
					smartUpdateSelection();
					if (oldSel != _jsel)
						smartUpdate("z.selId", getSelectedId());
				}
			}
		}
	}
	/** Note: we have to update all selection at once, since addItemToSelection
	 * and removeItemFromSelection might be called interchangeably.
	 */
	private void smartUpdateSelection() {
		final StringBuffer sb = new StringBuffer(80);
		for (Iterator it = _selItems.iterator(); it.hasNext();) {
			if (sb.length() > 0) sb.append(',');
			sb.append(((Listitem)it.next()).getUuid());
		}
		smartUpdate("chgSel", sb.toString());
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
		return _jsel >= 0 ?
			_jsel > 0 && _selItems.size() == 1 ? //optimize for performance
				(Listitem)_selItems.iterator().next():
				getItemAtIndex(_jsel): null;
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

	/** Re-initialize the listbox at the client (actually, re-calculate
	 * the column width at the client).
	 */
	/*package*/ void initAtClient() {
		if (!inSelectMold() && !inPagingMold())
			smartUpdate("z.init", true);
	}

	//--Paging--//
	/** Returns the paging controller, or null if not available.
	 * Note: the paging controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>If mold is "paging", this method never returns null, because
	 * a child paging controller is created automcatically (if not specified
	 * by developers with {@link #setPaginal}).
	 *
	 * <p>If a paging controller is specified (either by {@link #setPaginal},
	 * or by {@link #setMold} with "paging"),
	 * the listbox will rely on the paging controller to handle long-content
	 * instead of scrolling.
	 */
	public Paginal getPaginal() {
		return _pgi;
	}
	/* Specifies the paging controller.
	 * Note: the paging controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>It is OK, though without any effect, to specify a paging controller
	 * even if mold is not "paging".
	 *
	 * @param pgi the paging controller. If null and {@link #getMold} is "paging",
	 * a paging controller is created automatically as a child component
	 * (see {@link #getPaging}).
	 */
	public void setPaginal(Paginal pgi) {
		if (!Objects.equals(pgi, _pgi)) {
			final Paginal old = _pgi;
			_pgi = pgi;

			if (inPagingMold()) {
				if (old != null) removePagingListener(old);
				if (_pgi == null) {
					if (_paging != null) _pgi = _paging;
					else newInternalPaging();
				} else { //_pgi != null
					if (_pgi != _paging) {
						if (_paging != null) _paging.detach();
						_pgi.setTotalSize(getItemCount());
						addPagingListener(_pgi);
					}
				}
			}
		}
	}
	/** Creates the internal paging component.
	 */
	private void newInternalPaging() {
		assert D.OFF || inPagingMold(): "paging mold only";
		assert D.OFF || (_paging == null && _pgi == null);

		final Paging paging = new Paging();
		paging.setAutohide(true);
		paging.setDetailed(true);
		paging.setTotalSize(getItemCount());
		paging.setParent(this);
		addPagingListener(_pgi);
	}
	/** Adds the event listener for the onPaging event. */
	private void addPagingListener(Paginal pgi) {
		if (_pgListener == null)
			_pgListener = new EventListener() {
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event event) {
					final PagingEvent evt = (PagingEvent)event;
					Events.postEvent(
						new PagingEvent(evt.getName(),
							Listbox.this, evt.getPaginal(), evt.getActivePage()));
				}
			};
		pgi.addEventListener(ZulEvents.ON_PAGING, _pgListener);
	}
	/** Removes the event listener for the onPaging event. */
	private void removePagingListener(Paginal pgi) {
		pgi.removeEventListener(ZulEvents.ON_PAGING, _pgListener);
	}

	/** Called when the onPaging event is received (from {@link #getPaginal}).
	 *
	 * <p>Default: re-render, if live data, and invalidate().
	 */
	public void onPaging() {
		if (_model != null && inPagingMold()) {
			final Renderer renderer = new Renderer();
			try {
				final Paginal pgi = getPaginal();
				int pgsz = pgi.getPageSize();
				final int ofs = pgi.getActivePage() * pgsz;
				for (final Iterator it = getItems().listIterator(ofs);
				--pgsz >= 0 && it.hasNext();)
					renderer.render((Listitem)it.next());
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}

		invalidate();
	}

	/** Returns the child paging controller that is created automatically,
	 * or null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 */
	public Paging getPaging() {
		return _paging;
	}
	/** Returns the page size, aka., the number items per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 */
	public int getPageSize() {
		if (_pgi == null)
			throw new IllegalStateException("Available only the paging mold");
		return _pgi.getPageSize();
	}
	/** Sets the page size, aka., the number items per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 */
	public void setPageSize(int pgsz) {
		if (_pgi == null)
			throw new IllegalStateException("Available only the paging mold");
		_pgi.setPageSize(pgsz);
	}

	/** Returns whether this listbox is in the paging mold.
	 */
	/*package*/ boolean inPagingMold() {
		return "paging".equals(getMold());
	}

	/** Returns the index of the first visible child.
	 * <p>Used only for component development, not for application developers.
	 */
	public int getVisibleBegin() {
		if (!inPagingMold())
			return 0;
		final Paginal pgi = getPaginal();
		return pgi.getActivePage() * pgi.getPageSize();
	}
	/** Returns the index of the last visible child.
	 * <p>Used only for component development, not for application developers.
	 */
	public int getVisibleEnd() {
		if (!inPagingMold())
			return Integer.MAX_VALUE;
		final Paginal pgi = getPaginal();
		return (pgi.getActivePage() + 1) * pgi.getPageSize() - 1; //inclusive
	}

	//-- Component --//
	public void smartUpdate(String attr, String value) {
		if (!_noSmartUpdate) super.smartUpdate(attr, value);
	}
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		if (inSelectMold()) invalidate();
			//Both IE and Mozilla are buggy if we insert options by innerHTML
		else if(inPagingMold() && (child instanceof Listitem))
			_pgi.setTotalSize(getItemCount());
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (inSelectMold()) invalidate();
			//Both IE and Mozilla are buggy if we remove options by outerHTML
			//CONSIDER: use special command to remove items
			//Cons: if user remove a lot of items it is slower
		else if(inPagingMold() && (child instanceof Listitem))
			_pgi.setTotalSize(getItemCount());
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Listitem) {
			//first: listhead
			//last two: listfoot and paging
			if (refChild != null && refChild.getParent() != this)
				refChild = null; //Bug 1649625: it becomes the last child
			if (refChild != null && refChild == _listhead)
				refChild = _items.isEmpty() ? null: (Component)_items.get(0);

			if (refChild == null) {
				if (_listfoot != null) refChild = _listfoot;
				else refChild = _paging;
			} else if (refChild == _paging && _listfoot != null) {
				refChild = _listfoot;
			}

			final Listitem newItem = (Listitem)newChild;
			final int jfrom = newItem.getParent() == this ? newItem.getIndex(): -1;

			if (super.insertBefore(newChild, refChild)) {
				final List children = getChildren();
				if (_listhead != null && children.get(1) == newChild)
					invalidate();
				//we place listhead and treeitem at different div, so
				//this case requires invalidate (because we use insert-after)

				//Maintain _items
				final int
					jto = refChild instanceof Listitem ?
							((Listitem)refChild).getIndex(): -1,
					fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto: jfrom;
						//jfrom < 0: use jto
						//jto < 0: use jfrom
						//otherwise: use min(jfrom, jto)
				if (fixFrom < 0) newItem.setIndexDirectly(_items.size() - 1);
				else fixItemIndices(fixFrom);

				//Maintain selected
				final int newIndex = newItem.getIndex();
				if (newItem.isSelected()) {
					if (_jsel < 0) {
						_jsel = newIndex;
						if (!inSelectMold()) smartUpdate("z.selId", getSelectedId());
						_selItems.add(newItem);
					} else if (_multiple) {
						if (_jsel > newIndex) {
							_jsel = newIndex;
							if (!inSelectMold()) smartUpdate("z.selId", getSelectedId());
						}
						_selItems.add(newItem);
					} else { //deselect
						newItem.setSelectedDirectly(false);
					}
				} else {
					final int oldjsel = _jsel;
					if (jfrom < 0) { //no existent child
						if (_jsel >= newIndex) ++_jsel;
					} else if (_jsel >= 0) { //any selected
						if (jfrom > _jsel) { //from below
							if (jto >= 0 && jto <= _jsel) ++_jsel;
						} else { //from above
							if (jto < 0 || jto > _jsel) --_jsel;
						}
					}

					if (oldjsel != _jsel && !inSelectMold())
						smartUpdate("z.selId", getSelectedId());
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

			if (inSelectMold())
				log.warning("Mold select ignores listhead");
			invalidate();
				//we place listhead and treeitem at different div, so...
			_listhead = (Listhead)newChild;
			return super.insertBefore(newChild, refChild);
		} else if (newChild instanceof Listfoot) {
			if (_listfoot != null && _listfoot != newChild)
				throw new UiException("Only one listfoot is allowed: "+this);

			if (inSelectMold())
				log.warning("Mold select ignores listfoot");
			invalidate();
				//we place listfoot and treeitem at different div, so...
			_listfoot = (Listfoot)newChild;
			refChild = _paging; //the last two: listfoot and paging
			return super.insertBefore(newChild, refChild);
		} else if (newChild instanceof Paging) {
			if (_paging != null && _paging != newChild)
				throw new UiException("Only one paging is allowed: "+this);
			if (_pgi != null)
				throw new UiException("External paging cannot coexist with child paging");
			if (!inPagingMold())
				throw new UiException("The child paging is allowed only in the paging mold");

			invalidate();
			_pgi = _paging = (Paging)newChild;
			refChild = null; //the last: paging
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
			final Listitem item = (Listitem)child;
			final int index = item.getIndex();
			item.setIndexDirectly(-1); //mark
			fixItemIndices(index);

			//Maintain selected
			if (item.isSelected()) {
				_selItems.remove(item);
				if (_jsel == index) {
					fixSelectedIndex(index);
					if (!inSelectMold()) smartUpdate("z.selId", getSelectedId());
				}
			} else {
				if (_jsel >= index) {
					--_jsel;
					if (!inSelectMold()) smartUpdate("z.selId", getSelectedId());
				}
			}
			initAtClient();
			return true;
		} else if (_paging == child) {
			_paging = null;
			if (_pgi == child) _pgi = null;
		}
		invalidate();
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
			((Listitem)it.next()).setIndexDirectly(j);
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
					if (!inSelectMold())
						smartUpdate("z.model", "true");
				}

				initDataListener();
				_model = model;
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
			if (!inSelectMold())
				smartUpdate("z.model", null);
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
		ListitemRenderer renderer = null;
		final int newsz = _model.getSize();
		final int oldsz = getItemCount();
		if (oldsz > 0) {
			if (newsz > 0 && min < oldsz) {
				if (max < 0 || max >= oldsz) max = oldsz - 1;
				if (max >= newsz) max = newsz - 1;
				if (min < 0) min = 0;

				for (Iterator it = _items.listIterator(min);
				min <= max && it.hasNext(); ++min) {
					final Listitem item = (Listitem)it.next();
					if (item.isLoaded()) {
						if (renderer == null)
							renderer = getRealRenderer();
						unloadItem(renderer, item);
					}
				}
			}

			for (int j = newsz; j < oldsz; ++j)
				getItemAtIndex(newsz).detach(); //detach and remove
		}

		for (int j = oldsz; j < newsz; ++j) {
			if (renderer == null)
				renderer = getRealRenderer();
			newUnloadedItem(renderer).setParent(this);
		}
	}
	/** Creates an new and unloaded listitem. */
	private final Listitem newUnloadedItem(ListitemRenderer renderer) {
		Listitem item = null;
		if (renderer instanceof ListitemRendererExt)
			item = ((ListitemRendererExt)renderer).newListitem(this);

		if (item == null) {
			item = new Listitem();
			item.applyProperties();
		}
		item.setLoaded(false);

		newUnloadedCell(renderer, item);
		return item;
	}
	private Listcell newUnloadedCell(ListitemRenderer renderer, Listitem item) {
		Listcell cell = null;
		if (renderer instanceof ListitemRendererExt)
			cell = ((ListitemRendererExt)renderer).newListcell(item);

		if (cell == null) {
			cell = new Listcell();
			cell.applyProperties();
		}
		cell.setParent(item);
		return cell;
	}
	/** Clears a listitem as if it is not loaded. */
	private final void unloadItem(ListitemRenderer renderer, Listitem item) {
		if (!(renderer instanceof ListitemRendererExt)
		|| (((ListitemRendererExt)renderer).getControls() & 
				ListitemRendererExt.DETACH_ON_UNLOAD) == 0) { //re-use (default)
			final List cells = item.getChildren();
			if (cells.isEmpty()) {
				newUnloadedCell(renderer, item);
			} else {
				//detach and remove all but the first cell
				for (Iterator it = cells.listIterator(1); it.hasNext();) {
					it.next();
					it.remove();
				}

				final Listcell listcell = (Listcell)cells.get(0);
				listcell.setLabel(null);
				listcell.setImage(null);
			}
			item.setLoaded(false);
		} else { //detach
			item.getParent().insertBefore(newUnloadedItem(renderer), item);
			item.detach();
		}
	}
	/** Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 */
	public void onInitRender() {
		final Renderer renderer = new Renderer();
		try {
			final int pgsz = inSelectMold() ? getItemCount():
				inPagingMold() ? _pgi.getPageSize(): _rows;
			int j = 0;
			for (Iterator it = getItems().iterator(); j < pgsz && it.hasNext(); ++j)
				renderer.render((Listitem)it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/** Handles when the list model's content changed.
	 */
	private void onListDataChange(ListDataEvent event) {
		if (inSelectMold()) {
			invalidate();
			syncModel(-1, -1);
			Events.postEvent("onInitRender", this, null);
			return;
		}

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

			ListitemRenderer renderer = null;
			final Listitem before = min < oldsz ? getItemAtIndex(min): null;
			for (int j = min; j <= max; ++j) {
				if (renderer == null)
					renderer = getRealRenderer();
				insertBefore(newUnloadedItem(renderer), before);
			}
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
	/** Returns the renderer used to render items.
	 */
	private ListitemRenderer getRealRenderer() {
		return _renderer != null ? _renderer: getDefaultItemRenderer();
	}

	/** Used to render listitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final ListitemRenderer _renderer;
		private boolean _rendered, _ctrled;

		private Renderer() {
			_renderer = getRealRenderer();
		}
		private void render(Listitem item) throws Throwable {
			if (item.isLoaded())
				return; //nothing to do

			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}

			final Listcell cell = (Listcell)item.getChildren().get(0);
			if (!(_renderer instanceof ListitemRendererExt)
			|| (((ListitemRendererExt)_renderer).getControls() & 
				ListitemRendererExt.DETACH_ON_RENDER) != 0) { //detach (default)
				cell.detach();
			}

			try {
				_renderer.render(item, _model.getElementAt(item.getIndex()));
			} catch (Throwable ex) {
				try {
					item.setLabel(Exceptions.getMessage(ex));
				} catch (Throwable t) {
					log.error(t);
				}
				item.setLoaded(true);
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
			} else {
				throw UiException.Aide.wrap(ex);
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

	/** Renders the specified {@link Listitem} if not loaded yet,
	 * with {@link #getItemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * In other words, it is meaningful only if live data model is used.
	 */
	public void renderItem(Listitem li) {
		if (_model == null) return;

		final Renderer renderer = new Renderer();
		try {
			renderer.render(li);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}
	/** Renders all {@link Listitem} if not loaded yet,
	 * with {@link #getItemRenderer}.
	 */
	public void renderAll() {
		if (_model == null) return;

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = getItems().iterator(); it.hasNext();)
				renderer.render((Listitem)it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	//-- RenderOnDemand --//
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
		final String old = getMold();
		if (!Objects.equals(old, mold)) {
			super.setMold(mold);

			if ("paging".equals(old)) { //change from paging
				if (_paging != null) {
					removePagingListener(_paging);
					_paging.detach();
				} else if (_pgi != null) {
					removePagingListener(_pgi);
				}
			} else if (inPagingMold()) { //change to paging
				if (_pgi != null) addPagingListener(_pgi);
				else newInternalPaging();
			}
		}
	}
	public void setHeight(String height) {
		if (!Objects.equals(height, getHeight())) {
			super.setHeight(height);
			if (!inSelectMold()) initAtClient();
		}
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());

		if (inSelectMold()) {
			HTMLs.appendAttribute(sb, "name", _name);
			HTMLs.appendAttribute(sb, "size",  getRows());

			if (isMultiple())
				HTMLs.appendAttribute(sb, "multiple",  "multiple");
			if (_disabled)
				HTMLs.appendAttribute(sb, "disabled",  "disabled");
			if (_tabindex >= 0)
				HTMLs.appendAttribute(sb, "tabindex", _tabindex);
		} else {
			HTMLs.appendAttribute(sb, "z.name", _name);
			HTMLs.appendAttribute(sb, "z.size",  _rows);
			if (_disabled)
				HTMLs.appendAttribute(sb, "z.disabled",  true);
			if (_multiple)
				HTMLs.appendAttribute(sb, "z.multiple", true);
			HTMLs.appendAttribute(sb, "z.selId", getSelectedId());
			//if (_checkmark)
			//	HTMLs.appendAttribute(sb, "z.checkmark",  true);
			if (_vflex)
				HTMLs.appendAttribute(sb, "z.vflex", true);
			if (_model != null)
				HTMLs.appendAttribute(sb, "z.model", true);
		}

		appendAsapAttr(sb, Events.ON_SELECT);
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
			if (_it == null) {
				int v = _j;
				if (_listhead != null) ++v;
				_it = getChildren().listIterator(v);
			}
		}
	}

	//Cloneable//
	public Object clone() {
		final Listbox clone = (Listbox)super.clone();
		clone.init();
		clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		int index = 0;
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Listitem) {
				final Listitem li = (Listitem)child;
				li.setIndexDirectly(index ++); //since Listitem.clone() resets index
				if (li.isSelected()) {
					_selItems.add(li);
				}
			} else if (child instanceof Listhead) {
				_listhead = (Listhead)child;
			} else if (child instanceof Listfoot) {
				_listfoot = (Listfoot)child;
			} else if (child instanceof Paging) {
				_pgi = _paging = (Paging)child;
			}
		}
	}

	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		afterUnmarshal();
		//TODO: how to marshal _pgi if _pgi != _paging
		//TODO: re-register event listener for onPaging

		if (_model != null) initDataListener();
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements Selectable, ChildChangedAware, Cropper {
		//ChildChangedAware//
		public boolean isChildChangedAware() {
			return !inSelectMold();
		}

		//--Cropper--//
		public boolean isCropper() {
			return inPagingMold();
		}
		public Set getAvailableAtClient() {
			if (!inPagingMold())
				return null;

			final Set avail = new HashSet(37);
			if (_listhead != null) avail.add(_listhead);
			if (_listfoot != null) avail.add(_listfoot);
			if (_paging != null) avail.add(_paging);
	
			final Paginal pgi = getPaginal();
			int pgsz = pgi.getPageSize();
			final int ofs = pgi.getActivePage() * pgsz;
			for (final Iterator it = getItems().listIterator(ofs);
			--pgsz >= 0 && it.hasNext();)
				avail.add(it.next());
			return avail;
		}

		//-- Selectable --//
		public void selectItemsByClient(Set selItems) {
			_noSmartUpdate = true;
			try {
				final boolean paging = inPagingMold();
				if (!_multiple
				|| (!paging && (selItems == null || selItems.size() <= 1))) {
					final Listitem item =
						selItems != null && selItems.size() > 0 ?
							(Listitem)selItems.iterator().next(): null;
					selectItem(item);
				} else {
					int from, to;
					if (paging) {
						final Paginal pgi = getPaginal();
						int pgsz = pgi.getPageSize();
						from = pgi.getActivePage() * pgsz;
						to = from + pgsz; //excluded
					} else {
						from = to = 0;
					}

					int j = 0;
					for (Iterator it = _items.iterator(); it.hasNext(); ++j) {
						final Listitem item = (Listitem)it.next();
						if (selItems.contains(item)) {
							addItemToSelection(item);
						} else if (!paging) {
							removeItemFromSelection(item);
						} else {
							final int index = item.getIndex();
							if (index >= from && index < to)
								removeItemFromSelection(item);
						}
					}
				}
			} finally {
				_noSmartUpdate = false;
			}
		}
	}
}
