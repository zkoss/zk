/* Tree.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 18:51:33     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.AbstractCollection;
import java.util.ArrayList;

import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.client.Selectable;
import org.zkoss.zk.ui.ext.client.InnerWidth;
import org.zkoss.zk.ui.ext.render.ChildChangedAware;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

//import org.zkoss.zul.Listbox.Renderer;

import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Paginated;
import org.zkoss.zul.impl.XulElement;

/**
 *  A container which can be used to hold a tabular
 * or hierarchical set of rows of elements.
 *
 * <p>Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.SelectEvent is sent when user changes
 * the selection.</li>
 * </ol>
 *
 * <p>Default {@link #getZclass}: z-tree, and an other option is z-dottree. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Tree extends XulElement implements Paginated, org.zkoss.zul.api.Tree {
	private static final Log log = Log.lookup(Tree.class);

	private transient Treecols _treecols;
	private transient Treefoot _treefoot;
	private transient Treechildren _treechildren;
	/** A list of selected items. */
	private transient Set _selItems;
	/** The first selected item. */
	private transient Treeitem _sel;
	private transient Collection _heads;
	private int _rows = 0;
	/** The name. */
	private String _name;
	private boolean _multiple, _checkmark;
	private boolean _vflex;
	/** disable smartUpdate; usually caused by the client. */
	private transient boolean _noSmartUpdate;
	private String _innerWidth = "100%";

	private TreeModel _model;
	private TreeitemRenderer _renderer;	
	private transient TreeDataListener _dataListener;
	private boolean _fixedLayout;

	private transient Paginal _pgi;
	/** The paging controller, used only if mold = "paging" and user
	 * doesn't assign a controller via {@link #setPaginal}.
	 * If exists, it is the last child
	 */
	private transient Paging _paging;
	private transient EventListener _pgListener, _pgImpListener;
	private String _pagingPosition = "bottom";
	

	public Tree() {
		init();
	}
	private void init() {
		_selItems = new LinkedHashSet(5);
		_heads = new AbstractCollection() {
			public int size() {
				int sz = getChildren().size();
				if (_treechildren != null) --sz;
				if (_treefoot != null) --sz;
				if (_paging != null) --sz;
				return sz;
			}
			public Iterator iterator() {
				return new Iter();
			}
		};
	}

	void addVisibleItemCount(int count) {
		if (inPagingMold()) {
			Paginal pgi = getPaginal();
			pgi.setTotalSize(pgi.getTotalSize() + count);
			invalidate(); //the set of visible items might change
		}
	}
	/**
	 * Returens a map of current visible item.
	 * @since 3.0.7
	 */
	Map getVisibleItems() {
		Map map = new HashMap();
		final Paginal pgi = getPaginal();
		final int pgsz = pgi.getPageSize();
		final int ofs = pgi.getActivePage() * pgsz;
		
		// data[pageSize, beginPageIndex, visitedCount, visitedTotal, RenderedCount]
		int[] data = new int[]{pgsz, ofs, 0, 0, 0};
		getVisibleItemsDFS(getChildren(), map, data);
		return map;
	}
	/**
	 * Prepare the map of the visible items recursively in deep-first order.
	 */
	private boolean getVisibleItemsDFS(List list, Map map, int[] data) {
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Component cmp = (Component)it.next();
			if (cmp instanceof Treeitem) {
				if (data[4] >= data[0]) return false; // full
				final Treeitem item = (Treeitem) cmp;
				if (item.isVisible()) {
					int count = item.isOpen() && item.getTreechildren() != null ? 
							item.getTreechildren().getVisibleItemCount(): 0;
					boolean shoulbBeVisited = data[1] < data[2] + 1 + count;
					data[2] += (shoulbBeVisited ? 1 : count + 1);
					data[3] += count + 1;
					if (shoulbBeVisited) {
						if(data[1] < data[2]) {
							// count the rendered item
							data[4]++;
							map.put(item, Boolean.TRUE);
						}
						if (item.isOpen()) {
							if (!getVisibleItemsDFS(item.getChildren(), map, data)) {
								return false;
							} else {
								// the children may be visible.
								map.put(item, Boolean.TRUE);
							}
						}
					}
				}
			} else if (cmp instanceof Treechildren) {
				if(!getVisibleItemsDFS(cmp.getChildren(), map, data)) return false;
			}
		}
		return true;
	}
	//-- super --//
	public void setMold(String mold) {
		final String old = getMold();
		if (!Objects.equals(old, mold)) {
			super.setMold(mold);
				//we have to change model before detaching paging,
				//since removeChild assumes it

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
				setFixedLayout(true);
			}
		}
	}
	
	//--Paging--//
	/**
	 * Sets how to position the paging of tree at the client screen.
	 * It is meaningless if the mold is not in "paging".
	 * @param pagingPosition how to position. It can only be "bottom" (the default), or
	 * "top", or "both".
	 * @since 3.0.7
	 */
	public void setPagingPosition(String pagingPosition) {
		if (pagingPosition == null || (!pagingPosition.equals("top") &&
			!pagingPosition.equals("bottom") && !pagingPosition.equals("both")))
			throw new WrongValueException("Unsupported position : "+pagingPosition);
		if(!Objects.equals(_pagingPosition, pagingPosition)){
			_pagingPosition = pagingPosition;
			invalidate();
		}
	}
	/**
	 * Returns how to position the paging of tree at the client screen.
	 * It is meaningless if the mold is not in "paging".
	 * @since 3.0.7
	 */
	public String getPagingPosition() {
		return _pagingPosition;
	}
	/** Returns the paging controller, or null if not available.
	 * Note: the paging controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>If mold is "paging", this method never returns null, because
	 * a child paging controller is created automcatically (if not specified
	 * by developers with {@link #setPaginal}).
	 *
	 * <p>If a paging controller is specified (either by {@link #setPaginal},
	 * or by {@link #setMold} with "paging"),
	 * the tree will rely on the paging controller to handle long-content
	 * instead of scrolling.
	 * @since 3.0.7
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
	 * @since 3.0.7
	 */
	public void setPaginal(Paginal pgi) {
		if (!Objects.equals(pgi, _pgi)) {
			final Paginal old = _pgi;
			_pgi = pgi; //assign before detach paging, since removeChild assumes it

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
		paging.setTotalSize(getVisibleItemCount());
		paging.setParent(this);
		addPagingListener(_pgi);
	}
	/** Adds the event listener for the onPaging event. */
	private void addPagingListener(Paginal pgi) {
		if (_pgListener == null)
			_pgListener = new EventListener() {
				public void onEvent(Event event) {
					final PagingEvent evt = (PagingEvent)event;
					Events.postEvent(
						new PagingEvent(evt.getName(),
							Tree.this, evt.getPageable(), evt.getActivePage()));
				}
			};
		pgi.addEventListener(ZulEvents.ON_PAGING, _pgListener);

		if (_pgImpListener == null)
			_pgImpListener = new EventListener() {
	public void onEvent(Event event) {
		if (inPagingMold()) {
			invalidate();
		}
	}
			};
		pgi.addEventListener("onPagingImpl", _pgImpListener);
	}
	/** Removes the event listener for the onPaging event. */
	private void removePagingListener(Paginal pgi) {
		pgi.removeEventListener(ZulEvents.ON_PAGING, _pgListener);
		pgi.removeEventListener("onPagingImpl", _pgImpListener);
	}

	/** Returns the child paging controller that is created automatically,
	 * or null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 * @since 3.0.7
	 */
	public Paging getPagingChild() {
		return _paging;
	}

	/** Returns the child paging controller that is created automatically,
	 * or null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Paging getPagingChildApi() { 
		return getPagingChild(); 
	}
	/** Returns the page size, aka., the number items per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 * @since 2.4.1
	 */
	public int getPageSize() {
		return inPagingMold() ? pgi().getPageSize(): 0;
	}
	/** Sets the page size, aka., the number items per page.
	 * <p>Note: mold is not "paging" and no external controller is specified.
	 * @since 2.4.1
	 */
	public void setPageSize(int pgsz) throws WrongValueException {
		if (pgsz < 0 || !inPagingMold()) return;
		pgi().setPageSize(pgsz);
	}
	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @since 3.0.7
	 */
	public int getPageCount() {
		return pgi().getPageCount();
	}
	/** Returns the active page (starting from 0).
	 * @since 3.0.7
	 */
	public int getActivePage() {
		return pgi().getActivePage();
	}
	/** Sets the active page (starting from 0).
	 * @since 3.0.7
	 */
	public void setActivePage(int pg) throws WrongValueException {
		pgi().setActivePage(pg);
	}
	private Paginal pgi() {
		if (_pgi == null)
			throw new IllegalStateException("Available only the paging mold");
		return _pgi;
	}

	/** Returns whether this tree is in the paging mold.
	 * @since 3.0.7
	 */
	/*package*/ boolean inPagingMold() {
		return "paging".equals(getMold());
	}
	
	private int getVisibleItemCount() {
		return _treechildren != null ? _treechildren.getVisibleItemCount() : 0;
	}
	
	/**
	 * Sets the outline of grid whether is fixed layout.
	 * If true, the outline of grid will be depended on browser. It means, we don't 
	 * calculate the width of each cell. Otherwise, the outline will count on the content of body.
	 * In other words, the outline of grid is like ZK 2.4.1 version that the header's width is only for reference.
	 * 
	 * <p> You can also specify the "fixed-layout" attribute of component in lang-addon.xml directly, it's a top priority. 
	 * @since 3.0.4
	 */
	public void setFixedLayout(boolean fixedLayout) {
		if(_fixedLayout != fixedLayout) {
			_fixedLayout = fixedLayout;
			invalidate();
		}
	}
	/**
	 * Returns the outline of grid whether is fixed layout.
	 * <p>Default: false.
	 * <p>Note: if the "fixed-layout" attribute of component is specified, it's prior to the original value.
	 * @since 3.0.4
	 */
	public boolean isFixedLayout() {
		final String s = (String) getAttribute("fixed-layout");
		return s != null ? "true".equalsIgnoreCase(s) : _fixedLayout;
	}
	
	/** Returns the treecols that this tree owns (might null).
	 */
	public Treecols getTreecols() {
		return _treecols;
	}
	/** Returns the treecols that this tree owns (might null).
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treecols getTreecolsApi() {
		return getTreecols();
	}
	/** Returns the treefoot that this tree owns (might null).
	 */
	public Treefoot getTreefoot() {
		return _treefoot;
	}
	/** Returns the treefoot that this tree owns (might null).
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treefoot getTreefootApi() {
		return getTreefoot();
	}
	/** Returns the treechildren that this tree owns (might null).
	 */
	public Treechildren getTreechildren() {
		return _treechildren;
	}
	/** Returns the treechildren that this tree owns (might null).
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treechildren getTreechildrenApi() {
		return getTreechildren();
	}
	/** Returns a collection of heads, including {@link #getTreecols}
	 * and auxiliary heads ({@link Auxhead}) (never null).
	 *
	 * @since 3.0.0
	 */
	public Collection getHeads() {
		return _heads;
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
			smartUpdate("z.size", Integer.toString(_rows));
		}
	}

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 *
	 * @param name the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			if (_name != null) smartUpdate("z.name", _name);
			else invalidate(); //1) generate _value; 2) add submit listener
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
			invalidate();
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
			smartUpdate("z.flex", _vflex);
		}
	}

	/**
	 * Sets the inner width of this component.
	 * The inner width is the width of the inner table.
	 * By default, it is 100%. That is, it is the same as the width
	 * of this component. However, it is changed when the user
	 * is sizing the column's width.
	 *
	 * <p>Application developers rarely call this method, unless
	 * they want to preserve the widths of sizable columns
	 * changed by the user.
	 * To preserve the widths, the developer have to store the widths of
	 * all columns and the inner width ({@link #getInnerWidth}),
	 * and then restore them when re-creating this component.
	 *
	 * @param innerWidth the inner width. If null, "100%" is assumed.
	 * @since 3.0.0
	 */
	public void setInnerWidth(String innerWidth) {
		if (innerWidth == null) innerWidth = "100%";
		if (!_innerWidth.equals(innerWidth)) {
			_innerWidth = innerWidth;
			smartUpdate("z.innerWidth", innerWidth);
		}
	}
	/**
	 * Returns the inner width of this component.
	 * The inner width is the width of the inner table.
	 * <p>Default: "100%"
	 * @see #setInnerWidth
	 * @since 3.0.0
	 */
	public String getInnerWidth() {
		return _innerWidth;
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
				for (Iterator it = _selItems.iterator(); it.hasNext();) {
					final Treeitem ti = (Treeitem)it.next();
					if (ti != item) {
						ti.setSelectedDirectly(false);
						it.remove();
					}
				}
				//No need to update z.selId because z.multiple will do the job
			}
			if (isCheckmark()) invalidate(); //change check mark
			else smartUpdate("z.multiple", _multiple);
		}
	}

	/** Sets the active page in which the specified item is.
	 * The active page will become the page that contains the specified item.
	 *
	 * @param item the item to show. If the item is null, invisible, or doesn't belong
	 * to the same tree, nothing happens.
	 * @since 3.0.4
	 */
	public void setActivePage(Treeitem item) {
		if (item.isVisible() && item.getTree() == this && isVisible()) {
			int index = getVisibleIndexOfItem(item);
			if (index != -1) {
				final Paginal pgi = getPaginal();
				int pg = index / pgi.getPageSize();
				if (pg != getActivePage())
					setActivePage(pg);
			}
				
		}
	}
	/** Sets the active page in which the specified item is.
	 * The active page will become the page that contains the specified item.
	 *
	 * @param itemApi assume as a {@link org.zkoss.zul.Treeitem}   
 	 * @since 3.5.2
	 */
	public void setActivePageApi(org.zkoss.zul.api.Treeitem itemApi) {
		Treeitem item = (Treeitem) itemApi;
		setActivePage(item);
	}
	/**
	 * Returns the index of the specified item in which it should be shown on the
	 * paging mold recursively in breadth-first order.
	 * @return -1 if the item is invisible.
	 * @since 3.0.7
	 */
	private int getVisibleIndexOfItem(Treeitem item) {
		int count = getVisibleIndexOfItem0(item, false);
		if (count <= 0) return -1;
		return --count;
	}
	/**
	 * Returns the count the specified item in which it should be shown on the
	 * paging mold recursively in breadth-first order.
	 * @return If 0, the item is top. If -1, the item is invisible.
	 * @since 3.0.7
	 */
	private int getVisibleIndexOfItem0(Treeitem item, boolean inclusive) {
		if (item == null) return 0;
		int count = 0;
		if (item.isVisible()) {
			count++;
			if (inclusive && item.isOpen() && item.getTreechildren() != null)
				count += item.getTreechildren().getVisibleItemCount();
		}
		int c = getVisibleIndexOfItem0((Treeitem) item.getPreviousSibling(), true);
		if (c == -1) return -1;
		else if (c != 0) {
			count += c;
		} else {
			Component cmp = item.getParent().getParent();
			if (cmp instanceof Treeitem) {
				Treeitem parent = (Treeitem)cmp;
				if (parent.isVisible()) {
					parent.setOpen(true);
					int cnt = getVisibleIndexOfItem0((Treeitem)parent, false);
					if (cnt == -1) return -1;
					count += cnt;
				} else return -1; // invisible item
			}
		}
		return count;
	}
	

	/** Returns the ID of the selected item (it is stored as the z.selId
	 * attribute of the tree).
	 */
	private String getSelectedId() {
		//NOTE: Treerow's uuid; not Treeitem's
		final Treerow tr = _sel != null ? _sel.getTreerow(): null;
		return tr != null ? tr.getUuid(): "zk_n_a";
	}

	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 */
	public Collection getItems() {
		return _treechildren != null ? _treechildren.getItems(): Collections.EMPTY_LIST;
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

				final Treerow tr = item.getTreerow();
				if (tr != null)
					smartUpdate("select", tr.getUuid());
			}
			if (inPagingMold())
				setActivePage(item);
		}
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #setSelectedItem}.
	 * @param itemApi assume as a {@link org.zkoss.zul.Treeitem}
	 * @since 3.5.2   
	 */
	public void selectItemApi(org.zkoss.zul.api.Treeitem itemApi) {
		Treeitem item = (Treeitem) itemApi;
		selectItem(item);
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
				smartUpdateSelection();
				if (fixSelected())
					smartUpdate("z.selId", getSelectedId());
			}
		}
	}
	/** Selects the given item, without deselecting any other items
	 * that are already selected..
	 * @param itemApi 
	 * 				assume as a {@link org.zkoss.zul.Treeitem}   
	 * @since 3.5.2
	 */
	public void addItemToSelectionApi(org.zkoss.zul.api.Treeitem itemApi) {
		Treeitem item = (Treeitem) itemApi;
		addItemToSelection(item);
		
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
				smartUpdateSelection();
				if (fixSelected())
					smartUpdate("z.selId", getSelectedId());
				//No need to use response because such info is carried on tags
			}
		}
	}
	/**  Deselects the given item without deselecting other items.
	 * @param itemApi 
	 * 			assume as a {@link org.zkoss.zul.Treeitem}
	 * @since 3.5.2
	 */
	public void removeItemFromSelectionApi(org.zkoss.zul.api.Treeitem itemApi) {		
		Treeitem item = (Treeitem) itemApi;
		removeItemFromSelection(item);
	}
	/** Note: we have to update all selection at once, since addItemToSelection
	 * and removeItemFromSelection might be called interchangeably.
	 */
	private void smartUpdateSelection() {
		final StringBuffer sb = new StringBuffer(80);
		for (Iterator it = _selItems.iterator(); it.hasNext();) {
			final Treeitem item = (Treeitem)it.next();
			final Treerow tr = item.getTreerow();
			if (tr != null) {
				if (sb.length() > 0) sb.append(',');
				sb.append(tr.getUuid());
			}			
		}
		smartUpdate("chgSel", sb.toString());
	}
	/** If the specified item is selected, it is deselected.
	 * If it is not selected, it is selected. Other items in the tree
	 * that are selected are not affected, and retain their selected state.
	 */
	public void toggleItemSelection(Treeitem item) {
		if (item.isSelected()) removeItemFromSelection(item);
		else addItemToSelection(item);
	}
	/** If the specified item is selected, it is deselected.
	 * If it is not selected, it is selected. Other items in the tree
	 * that are selected are not affected, and retain their selected state.
	 * 
	 * @param itemApi assume as a {@link org.zkoss.zul.Treeitem}   
	 * @since 3.5.2
	 */
	public void toggleItemSelectionApi(org.zkoss.zul.api.Treeitem itemApi) {
		Treeitem item = (Treeitem) itemApi;
		toggleItemSelection(item);
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
	/** Returns the selected item.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treeitem getSelectedItemApi() {
		return getSelectedItem();
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #selectItem}.
	 */
	public void setSelectedItem(Treeitem item) {
		selectItem(item);
	}
	/**  Deselects all of the currently selected items and selects
	 * the given item.
	 * <p>It is the same as {@link #selectItem}.
	 * @param itemApi assume as a {@link org.zkoss.zul.Treeitem}
	 * @since 3.5.2  
	 */
	public void setSelectedItemApi(org.zkoss.zul.api.Treeitem itemApi) {
		Treeitem item = (Treeitem) itemApi;
		setSelectedItem(item);
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

	/** Returns the style class prefix used to generate the icons of this tree.
	 *
	 * <p>Default: tree.</br>
	 * Another builtin style class: dottree (the style used prior 3.0).
	 *
	 * <p>Assume that the icon style class is <code>tree</code>, then
	 * the following style classes are used for the icons of each tree item:
	 * <dl>
	 * <dt>tree-root-open</dt>
	 * <dd>The icon used to represent the open state for tree items at the root level.</dd>
	 * <dt>tree-root-close</dt>
	 * <dd>The icon used to represent the close state for tree items at the root level.</dd>
	 * <dt>tree-tee-open</dt>
	 * <dd>The icon used to represent the open state for tree items that have next siblings.</dd>
	 * <dt>tree-tee-close</dt>
	 * <dd>The icon used to represent the close state for tree items at have next siblings.</dd>
	 * <dt>tree-last-open</dt>
	 * <dd>The icon used to represent the open state for tree items that don't have next siblings.</dd>
	 * <dt>tree-last-close</dt>
	 * <dd>The icon used to represent the close state for tree items at don't have next siblings.</dd>
	 * <dt>tree-tee</dt>
	 * <dd>The icon used to represent the T-shape icon.</dd>
	 * <dt>tree-vbar</dt>
	 * <dd>The icon used to represent the |-shape (vertical bar) icon.</dd>
	 * <dt>tree-last</dt>
	 * <dd>The icon used to represent the L-shape icon -- no next sibling.</dd>
	 * <dt>tree-spacer</dt>
	 * <dd>The icon used to represent the blank icon.</dd>
	 * </dl>
	 *
	 * @since 3.0.0
	 * @deprecated As of release 3.5.0, replaced with {@link #getZclass()}
	 */
	public String getIconSclass() {
		return null;
	}
	/** Sets the style class prefix used to generate the icons of this tree.
	 *
	 * @since 3.0.0
	 * @see #getIconSclass
	 * @deprecated As of release 3.5.0, replaced with {@link #setZclass(String)}
	 */
	public void setIconSclass(String scls) {
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-tree" : _zclass;
	}
	public void smartUpdate(String attr, String value) {
		if (!_noSmartUpdate) super.smartUpdate(attr, value);
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Treecols) {
			if (_treecols != null && _treecols != newChild)
				throw new UiException("Only one treecols is allowed: "+this);
			_treecols = (Treecols)newChild;
		} else if (newChild instanceof Treefoot) {
			if (_treefoot != null && _treefoot != newChild)
				throw new UiException("Only one treefoot is allowed: "+this);
			_treefoot = (Treefoot)newChild;
			refChild = _paging; //the last two: listfoot and paging
		} else if (newChild instanceof Treechildren) {
			if (_treechildren != null && _treechildren != newChild)
				throw new UiException("Only one treechildren is allowed: "+this);
			_treechildren = (Treechildren)newChild;
			fixSelectedSet();
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
		} else if (!(newChild instanceof Auxhead)) {
			throw new UiException("Unsupported newChild: "+newChild);
		}

		if (super.insertBefore(newChild, refChild)) {
			//not need to invalidate since auxhead visible only with _treecols
			if (!(newChild instanceof Auxhead))
				invalidate();
			return true;
		}
		return false;
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
				smartUpdate("z.selId", getSelectedId());
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
				item.invalidate();
			} else {
				if (_sel == null)
					_sel = item;
				_selItems.add(item);
				smartUpdate("z.selId", getSelectedId());
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
						smartUpdate("z.selId", getSelectedId());
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
		invalidate();
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Treecols) {
			_treecols = null;
		} else if (child instanceof Treefoot) {
			_treefoot = null;
		} else if (child instanceof Treechildren) {
			_treechildren = null;
			_selItems.clear();
			_sel = null;
		} else if (_paging == child) {
			_paging = null;
			if (_pgi == child) _pgi = null;
		}
		super.onChildRemoved(child);
		invalidate();
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
		HTMLs.appendAttribute(sb, "z.name", _name);
		HTMLs.appendAttribute(sb, "z.size",  getRows());
		HTMLs.appendAttribute(sb, "z.selId", getSelectedId());
		if (_multiple)
			HTMLs.appendAttribute(sb, "z.multiple", true);
		//if (_checkmark)
		//	HTMLs.appendAttribute(sb, "z.checkmark",  _checkmark);
		if (_vflex)
			HTMLs.appendAttribute(sb, "z.vflex", true);
		appendAsapAttr(sb, Events.ON_SELECT);

		final Treechildren tc = getTreechildren();
		if (tc != null) {
			HTMLs.appendAttribute(sb, "z.tchsib", tc.getUuid());
				//we have to generate first, since # of page might grow later
		}

		HTMLs.appendAttribute(sb, "z.fixed", isFixedLayout());
		return sb.toString();
	}

	//Cloneable//
	public Object clone() {
		int cntSel = _selItems.size();

		final Tree clone = (Tree)super.clone();
		clone.init();

		int cnt = 0;
		if (_treecols != null) ++cnt;
		if (_treefoot != null) ++cnt;
		if (_treechildren != null) ++cnt;
		if (_paging != null) ++cnt;
		if (cnt > 0 || cntSel > 0) clone.afterUnmarshal(cnt, cntSel);
		if(clone._model != null){
			clone._dataListener = null;
			clone.initDataListener();
		}
		return clone;
	}
	/** @param cnt # of children that need special handling (used for optimization).
	 * -1 means process all of them
	 * @param cntSel # of selected items
	 */
	private void afterUnmarshal(int cnt, int cntSel) {
		if (cnt != 0) {
			for (Iterator it = getChildren().iterator(); it.hasNext();) {
				final Object child = it.next();
				if (child instanceof Treecols) {
					_treecols = (Treecols)child;
					if (--cnt == 0) break;
				} else if (child instanceof Treefoot) {
					_treefoot = (Treefoot)child;
					if (--cnt == 0) break;
				} else if (child instanceof Treechildren) {
					_treechildren = (Treechildren)child;
					if (--cnt == 0) break;
				}else if (child instanceof Paging) {
					_pgi = _paging = (Paging)child;
					if (--cnt == 0) break;
				}
			}
		}

		_sel = null;
		_selItems.clear();
		if (cntSel != 0) {
			for (Iterator it = getItems().iterator(); it.hasNext();) {
				final Treeitem ti = (Treeitem)it.next();
				if (ti.isSelected()) {
					if (_sel == null) _sel = ti;
					_selItems.add(ti);
					if (--cntSel == 0) break;
				}
			}
		}
	}

	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		afterUnmarshal(-1, -1);
		
		if (_model != null) initDataListener();
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	
	/*
	 * Handles when the tree model's content changed 
	 * <p>Author: jeffliu
	 */
	private void onTreeDataChange(TreeDataEvent event){	
		//if the treeparent is empty, render tree's treechildren
		Object node = event.getParent();
		Component parent = getChildByNode(node);
		/* 
		 * Loop through indexes array
		 * if INTERVAL_REMOVED, from end to beginning
		 * 
		 * 2008/02/12 --- issue: [ 1884112 ] 
		 * When getChildByNode returns null, do nothing
		 */
		if(parent != null &&
		(!(parent instanceof Treeitem) || ((Treeitem)parent).isLoaded())){
			int indexFrom = event.getIndexFrom();
			int indexTo = event.getIndexTo();
			switch (event.getType()) {
			case TreeDataEvent.INTERVAL_ADDED:
				for(int i=indexFrom;i<=indexTo;i++)
					onTreeDataInsert(parent,node,i);
				break;
			case TreeDataEvent.INTERVAL_REMOVED:
				for(int i=indexTo;i>=indexFrom;i--)
					onTreeDataRemoved(parent,node,i);
				break;
			case TreeDataEvent.CONTENTS_CHANGED:
				for(int i=indexFrom;i<=indexTo;i++)
					onTreeDataContentChange(parent,node,i);
				break;
			}
		}			
	}

	/** @param parent either a Tree or Treeitem instance. */
	private static Treechildren treechildrenOf(Component parent){
		Treechildren tc = (parent instanceof Tree) ?
			((Tree)parent).getTreechildren() : ((Treeitem)parent).getTreechildren();
		if (tc == null) {
			tc = new Treechildren();
			tc.setParent(parent);
		}
		return tc;
	}
	
	/*
	 * Handle Treedata insertion
	 */
	private void onTreeDataInsert(Component parent,Object node, int index){
		/* 	Find the sibling to insertBefore;
		 * 	if there is no sibling or new item is inserted at end.
		 */
		Treeitem newTi = newUnloadedItem();
		Treechildren tc= treechildrenOf(parent);
		List siblings = tc.getChildren();
		//if there is no sibling or new item is inserted at end.
		if(siblings.size()==0 || index == siblings.size() ){
			tc.insertBefore(newTi, null);
		}else{
			tc.insertBefore(newTi, (Treeitem)siblings.get(index));
		}

		renderChangedItem(newTi,_model.getChild(node,index));
	}
		
	/*
	 * Handle event that child is removed
	 */
	private void onTreeDataRemoved(Component parent,Object node, int index){
		final Treechildren tc = treechildrenOf(parent);
		final List items = tc.getChildren();		
		if(items.size()>1){
			((Treeitem)items.get(index)).detach();
		}else{
			tc.detach();
		}
	}
	
	/*
	 * Handle event that child's content is changed
	 */
	private void onTreeDataContentChange(Component parent,Object node, int index){
		List items = treechildrenOf(parent).getChildren();		

		/*
		 * 2008/02/01 --- issue: [ 1884112 ] When Updating TreeModel, throws a IndexOutOfBoundsException
		 * When I update a children node data of the TreeModel , and fire a 
		 * CONTENTS_CHANGED event, it will throw a IndexOutOfBoundsException , If a 
		 * node doesn't open yet or not load yet.
		 * 
		 * if parent is loaded, change content. 
		 * else do nothing
		 */
		if(!items.isEmpty())
			renderChangedItem(
				(Treeitem)items.get(index), _model.getChild(node,index));
	}
	
	/**
	 * Return the Tree or Treeitem component by a given associated node in model.<br>
	 * This implmentation calls {@link TreeModel#getPath} method to locate assoicated
	 * Treeitem (or Tree) via path. You can override this method to speed up 
	 * performance if possible. 
	 * Return null, if the Tree or Treeitem is not yet rendered.
	 * <p>Author: jeffliu
	 * @since 3.0.0
	 */
	protected Component getChildByNode(Object node){
		int[] path = _model.getPath(_model.getRoot(), node);
		
		//If path is null or empty, return root(Tree) 
		if(path == null || path.length == 0)
			return this;
		else{
			
			Treeitem ti = null;
			List children =null;
			for(int i=0; i<path.length; i++){
				if(i==0){
					children = this.getTreechildren().getChildren(); 
				}else{
					children = ti.getTreechildren().getChildren();
				}
				/*
				 * If the children are not rendered yet, return null
				 */
				if(children.size()>path[i]&&0<=path[i]){
					ti = (Treeitem) children.get(path[i]);
				}else{
					return null;
				}
			}
			return ti;
		}
	}
	
	/*
	 * Initial Tree data listener
	 * <p>Author: jeffliu
	 */
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new TreeDataListener() {
				public void onChange(TreeDataEvent event) {
					onTreeDataChange(event);
				}
			};

		_model.addTreeDataListener(_dataListener);
	}
	
	/** Sets the tree model associated with this tree. 
	 *
	 * <p>Note: changing a render will not cause the tree to re-render.
	 * If you want it to re-render, you could assign the same model again 
	 * (i.e., setModel(getModel())), or fire an {@link TreeDataEvent} event.
	 * 
	 * <p>Author: jeffliu
	 * @param model the tree model to associate, or null to dis-associate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.0.0
	 */
	public void setModel(TreeModel model) {
		if (model != null) {
			if (_model != model) {
				if (_model != null) {
					_model.removeTreeDataListener(_dataListener);
				} else {
					getItems().clear();
				}

				_model = model;
				initDataListener();
			}
			syncModel();
		} else if (_model != null) {
			_model.removeTreeDataListener(_dataListener);
			_model = null;
			getItems().clear();
		}
	}
	
	//--TreeModel dependent codes--//
	/** Returns the list model associated with this tree, or null
	 * if this tree is not associated with any tree data model.
	 * <p>Author: jeffliu
	 * @return the list model associated with this tree
	 * @since 3.0.0
	 */
	public TreeModel getModel(){
		return _model;
	}
	
	/** Synchronizes the tree to be consistent with the specified model.
	 * <p>Author: jeffliu
	 */
	private void syncModel() {
		renderTree();
	}
	
	/** Sets the renderer which is used to render each item
	 * if {@link #getModel} is not null.
	 *
	 * <p>Note: changing a render will not cause the tree to re-render.
	 * If you want it to re-render, you could assign the same model again 
	 * (i.e., setModel(getModel())), or fire an {@link TreeDataEvent} event.
	 *
	 * <p>Author: jeffliu
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.0.0
	 */
	public void setTreeitemRenderer(TreeitemRenderer renderer){
		if (_renderer != renderer) {
			_renderer = renderer;
			if (_model != null)
				syncModel();
		}
	}
	
	/** Returns the renderer to render each item, or null if the default
	 * renderer is used.
	 * @return the renderer to render each item, or null if the default
	 * @since 3.0.0
	 */
	public TreeitemRenderer getTreeitemRenderer(){
		return _renderer;
	}

	/*
	 * Render the root of Tree
	 * Notice: _model.getRoot() is mapped to Tree, not first Treeitem
	 */
	private void renderTree(){
		if(_treechildren == null) {
			Treechildren children = new Treechildren();
			children.setParent(this);
		} else {
			_treechildren.getChildren().clear();
		}
	
		Object node = _model.getRoot();
		final Renderer renderer = new Renderer();
		try {
			renderChildren(renderer, _treechildren, node);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
}
	
	/*
	 * Renders the direct children for the specifed parent
	 */
	private void renderChildren(Renderer renderer, Treechildren parent,
	Object node) throws Throwable {
		for(int i = 0; i < _model.getChildCount(node); i++) {
			Treeitem ti = newUnloadedItem();
			ti.setParent(parent);
			Object childNode = _model.getChild(node, i);
			renderer.render(ti, childNode);
			if(!_model.isLeaf(childNode) && ti.getTreechildren() == null){	
				Treechildren tc = new Treechildren();
				tc.setParent(ti);
			}
		}
	}
	private Treeitem newUnloadedItem() {
		Treeitem ti = new Treeitem();
		ti.setOpen(false);
		return ti;
	}
	
	private static final TreeitemRenderer getDefaultItemRenderer() {
		return _defRend;
	}
	private static final TreeitemRenderer _defRend = new TreeitemRenderer() {
		public void render(Treeitem ti, Object node){
			Treecell tc = new Treecell(Objects.toString(node));
			Treerow tr = null;
			if(ti.getTreerow()==null){
				tr = new Treerow();
				tr.setParent(ti);
			}else{
				tr = ti.getTreerow(); 
				tr.getChildren().clear();
			}		
			tc.setParent(tr);
		}
	};
	/** Returns the renderer used to render items.
	 */
	private TreeitemRenderer getRealRenderer() {
		return _renderer != null ? _renderer: getDefaultItemRenderer();
	}

	/** Used to render treeitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final TreeitemRenderer _renderer;
		private boolean _rendered, _ctrled;
		private Renderer() {
			_renderer = getRealRenderer();
		}
		
		private void render(Treeitem item, Object node) throws Throwable {
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}
			
			try {
				_renderer.render(item, node);
			} catch (Throwable ex) {
				try {
					item.setLabel(Exceptions.getMessage(ex));
				} catch (Throwable t) {
					log.error(t);
				}
				throw ex;
			}
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
			if (_ctrled)
				((RendererCtrl)_renderer).doFinally();
		}
	}
	
	/** Renders the specified {@link Treeitem}, if not loaded yet,
	 * with {@link #getTreeitemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * <p>To unload treeitem, use {@link Treeitem#unload()}.
	 * @see #renderItems
	 * @since 3.0.0
	 */
	public void renderItem(Treeitem item) {
		if(_model != null) {
			final Renderer renderer = new Renderer();
			try {
				renderItem0(renderer, item);
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
	}
	/** Renders the specified {@link Treeitem}, if not loaded yet,
	 * with {@link #getTreeitemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * <p>To unload treeitem, use {@link Treeitem#unload()}.
	 * @param itemApi assume as a {@link org.zkoss.zul.Treeitem}
	 * @see #renderItems
	 * @since 3.5.2
	 */
	public void renderItemApi(org.zkoss.zul.api.Treeitem itemApi) {
		Treeitem item = (Treeitem) itemApi;
		renderItem(item);
	}
	
	/** Renders the specified {@link Treeitem}, if not loaded yet,
	 * with {@link #getTreeitemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 *
	 *<p>Note: Since the corresponding node is given,
	 * This method has better performance than 
	 * renderItem(Treeitem item) due to not searching for its 
	 * corresponding node.
	 * <p>To unload treeitem, use {@link Treeitem#unload()}.
	 * @see #renderItems
	 * @since 3.0.0
	 */
	public void renderItem(Treeitem item, Object node) {
		if(_model != null) {
			final Renderer renderer = new Renderer();
			try {
				renderItem0(renderer, item, node);
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
	}
	/** Renders the specified {@link Treeitem}, if not loaded yet,
	 * with {@link #getTreeitemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 *
	 *<p>Note: Since the corresponding node is given,
	 * This method has better performance than 
	 * renderItem(Treeitem item) due to not searching for its 
	 * corresponding node.
	 * <p>To unload treeitem, use {@link Treeitem#unload()}.
	 * @param itemApi assume as a {@link org.zkoss.zul.Treeitem}
	 * @see #renderItems
	 * @since 3.5.2
	 */
	public void renderItemApi(org.zkoss.zul.api.Treeitem itemApi, Object node) {
		Treeitem item = (Treeitem) itemApi;
		renderItem(item, node);		
	}
	/** Note: it doesn't call render doCatch/doFinally */
	private void renderItem0(Renderer renderer, Treeitem item)
	throws Throwable {
		renderItem0(renderer, item, getAssociatedNode(item,this));
	}	
	/** Note: it doesn't call render doCatch/doFinally */
	private void renderItem0(Renderer renderer, Treeitem item, Object node)
	throws Throwable {
		if(item.isLoaded()) //all direct children are loaded
			return;

		/*
		 * After modified the node in tree model, if node is leaf, 
		 * its treechildren is needed to be dropped.
		 */
		Treechildren tc = item.getTreechildren();
		if(_model.isLeaf(node)){
			if(tc != null)
				tc.detach(); //just in case

			//no children to render
			//Note item already renderred, so no need:
			//renderer.render(item, node);
		}else{
			if (tc != null) tc.getChildren().clear(); //just in case
			else {
				tc = new Treechildren();
				tc.setParent(item);
			}

			renderChildren(renderer, tc, node);
		}
		item.setLoaded(true);
	}
	
	private void renderChangedItem(Treeitem item, Object node){
		/*
		 * After modified the node in tree model, if node is leaf, 
		 * its treechildren is needed to be dropped.
		 */
		if(_model != null) {
			Treechildren tc = item.getTreechildren();
			if(_model.isLeaf(node)){
				if(tc != null)
					tc.detach(); //just in case
			}else{
				if (tc == null) {
					tc = new Treechildren();
					tc.setParent(item);
				}
			}

			final Renderer renderer = new Renderer();
			try {
				renderer.render(item, node); //re-render
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
	}

	/** Renders the specified {@link Treeitem} if not loaded yet,
	 * with {@link #getTreeitemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * <p>To unload treeitem, with {@link Treeitem#unload()}.
	 * @see #renderItem
	 * @since 3.0.0
	 */
	public void renderItems(Set items) {
		if (_model == null) return;

		if (items.isEmpty())
			return; //nothing to do

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = items.iterator(); it.hasNext();){
				renderItem0(renderer, (Treeitem)it.next());
			}
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/**
	 * Return a node which is an associated Treeitem ti in a Tree tree
	 * @since 3.0.0
	 */
	protected Object getAssociatedNode(Treeitem ti, Tree t){
		return getNodeByPath(getTreeitemPath(t,ti),_model.getRoot());
	}
	
	/**
	 * return the path which is from ZK Component root to ZK Component lastNode 
	 */
	private List getTreeitemPath(Component root, Component lastNode){
		List al = new ArrayList();
		Component curNode = lastNode;
		while(!root.equals(curNode)){
			if(curNode instanceof Treeitem){
				al.add(new Integer(((Treeitem)curNode).indexOf()));
			}
			curNode = curNode.getParent();
		}
		return al;
	}
	
	/**
	 * Get the node from tree by given path
	 * @param path
	 * @param root
	 * @return the node from tree by given path
	 * @since 3.0.0
	 */
	private Object getNodeByPath(List path, Object root) {
		Object node = root;
		int pathSize = path.size()-1;
		for(int i=pathSize; i >= 0; i--){
			node = _model.getChild(node, ((Integer)(path.get(i))).intValue());
		}
		return node;
	}
	
	/**
	 * Load treeitems through path <b>path</b>
	 * <br>Note: By using this method, all treeitems in path will be rendered
	 * and opened ({@link Treeitem#setOpen}). If you want to visit the rendered
	 * item in paging mold, please invoke {@link #setActivePage(Treeitem)}.
	 * @param path - an int[] path, see {@link TreeModel#getPath} 
	 * @return the treeitem from tree by given path
	 * @since 3.0.0
	 */
	public Treeitem renderItemByPath(int[] path){
		if(path == null || path.length == 0)
			return null;
		//Start from root-Tree
		Treeitem ti = null;
		List children = this.getTreechildren().getChildren();
		/*
		 * Go through each stop in path and render corresponding treeitem
		 */
		for(int i=0; i<path.length; i++){
			if(path[i] <0 || path[i] > children.size())
				return null;
			Treeitem parentTi = ti;
			
			ti = (Treeitem) children.get(path[i]);
			
			if(i<path.length-1) 
				ti.setOpen(true);
			
			if(ti.getTreechildren()!=null){
				children = ti.getTreechildren().getChildren();
			}else{
				if(i!=path.length-1){
					return null;
				}
			}
		}
		return ti;
	}
	/**
	 * Load treeitems through path <b>path</b>
	 * <br>Note: By using this method, all treeitems in path will be rendered
	 * and opened ({@link Treeitem#setOpen}). If you want to visit the rendered
	 * item in paging mold, please invoke {@link #setActivePage(Treeitem)}.
	 * @param path - an int[] path, see {@link TreeModel#getPath} 
	 * @return the treeitem from tree by given path
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treeitem renderItemByPathApi(int[] path) {
		return renderItemByPath(path);
	}
	
	// AREA JEFF ADDED END
	
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements InnerWidth, Selectable, ChildChangedAware {
		//ChildChangedAware//
		public boolean isChildChangedAware() {
			return !isFixedLayout();
		}
		//InnerWidth//
		public void setInnerWidthByClient(String width) {
			_innerWidth = width == null ? "100%": width;
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
	}
	/** An iterator used by _heads.
	 */
	private class Iter implements Iterator {
		private final ListIterator _it = getChildren().listIterator();

		public boolean hasNext() {
			while (_it.hasNext()) {
				Object o = _it.next();
				if (o instanceof Treecols || o instanceof Auxhead) {
					_it.previous();
					return true;
				}
			}
			return false;
		}
		public Object next() {
			for (;;) {
				Object o = _it.next();
				if (o instanceof Treecols || o instanceof Auxhead)
					return o;
			}
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
