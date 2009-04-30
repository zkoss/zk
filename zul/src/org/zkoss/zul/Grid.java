/* Grid.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 15:40:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collection;
import java.util.AbstractCollection;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Set;

import org.zkoss.lang.D;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.au.Command;
import org.zkoss.zk.au.in.GenericCommand;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.client.RenderOnDemand;
import org.zkoss.zk.ui.ext.client.InnerWidth;
import org.zkoss.zk.ui.ext.render.ChildChangedAware;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Paginated;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.event.PagingEvent;

/**
 * A grid is an element that contains both rows and columns elements.
 * It is used to create a grid of elements.
 * Both the rows and columns are displayed at once although only one will
 * typically contain content, while the other may provide size information.
 *
 * <p>Besides creating {@link Row} programmingly, you can assign
 * a data model (a {@link ListModel} or {@link GroupsModel} instance) to a grid via
 * {@link #setModel(ListModel)} or {@link #setModel(GroupsModel)}
 * and then the grid will retrieve data
 * by calling {@link ListModel#getElementAt} when necessary.
 *
 * <p>Besides assign a list model, you could assign a renderer
 * (a {@link RowRenderer} instance) to a grid, such that
 * the grid will use this
 * renderer to render the data returned by {@link ListModel#getElementAt}.
 * If not assigned, the default renderer, which assumes a label per row,
 * is used.
 * In other words, the default renderer adds a label to
 * a row by calling toString against the object returned
 * by {@link ListModel#getElementAt}
 * 
 * <p>There are two ways to handle long content: scrolling and paging.
 * If {@link #getMold} is "default", scrolling is used if {@link #setHeight}
 * is called and too much content to display.
 * If {@link #getMold} is "paging", paging is used if two or more pages are
 * required. To control the number of rows to display in a page, use
 * {@link #setPageSize}.
 *
 * <p>If paging is used, the page controller is either created automatically
 * or assigned explicity by {@link #setPaginal}.
 * The paging controller specified explicitly by {@link #setPaginal} is called
 * the external page controller. It is useful if you want to put the paging
 * controller at different location (other than as a child component), or
 * you want to use the same controller to control multiple grids.
 *
 * <p>Default {@link #getZclass}: z-grid.(since 3.5.0)
 *
 * <p>To have a grid without stripping, you can specify a non-existent
 * style class to {@link #setOddRowSclass}.
 * @author tomyeh
 * @see ListModel
 * @see RowRenderer
 * @see RowRendererExt
 */
public class Grid extends XulElement implements Paginated, org.zkoss.zul.api.Grid {
	private static final Log log = Log.lookup(Grid.class);

	private static final String ATTR_ON_INIT_RENDER_POSTED =
		"org.zkoss.zul.Grid.onInitLaterPosted";

	private transient Rows _rows;
	private transient Columns _cols;
	private transient Foot _foot;
	private transient Collection _heads;
	private String _align;
	private String _pagingPosition = "bottom";
	private ListModel _model;
	private RowRenderer _renderer;
	private transient ListDataListener _dataListener;
	/** The paging controller, used only if mold = "paging". */
	private transient Paginal _pgi;
	/** The paging controller, used only if mold = "paging" and user
	 * doesn't assign a controller via {@link #setPaginal}.
	 * If exists, it is the last child.
	 */
	private transient Paging _paging;
	private transient EventListener _pgListener, _pgImpListener;
	/** The style class of the odd row. */
	private String _scOddRow = null;
	/** the # of rows to preload. */
	private int _preloadsz = 7;
	private String _innerWidth = "100%";
	/** ROD mold use only*/
	private String _innerHeight = null,
	_innerTop = "height:0px;display:none", _innerBottom = "height:0px;display:none";
	private transient GridDrawerEngine _engine;
	private boolean _fixedLayout, _vflex;

	public Grid() {
		init();
	}
	private void init() {
		_heads = new AbstractCollection() {
			public int size() {
				int sz = getChildren().size();
				if (_rows != null) --sz;
				if (_foot != null) --sz;
				if (_paging != null) --sz;
				return sz;
			}
			public Iterator iterator() {
				return new Iter();
			}
		};
	}

	/** Returns whether to grow and shrink vertical to fit their given space,
	 * so called vertial flexibility.
	 *
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public final boolean isVflex() {
		return _vflex;
	}
	/** Sets whether to grow and shrink vertical to fit their given space,
	 * so called vertial flexibility.
	 *
	 * @since 3.5.0
	 */
	public void setVflex(boolean vflex) {
		if (_vflex != vflex) {
			_vflex = vflex;
			smartUpdate("z.flex", _vflex);
		}
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
	
	/** Returns the rows.
	 */
	public Rows getRows() {
		return _rows;
	}
	/** Returns the rows.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Rows getRowsApi() {
		return getRows();
	}
	/** Returns the columns.
	 */
	public Columns getColumns() {
		return _cols;
	}
	/** Returns the columns.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Columns getColumnsApi() {
		return getColumns();
	}
	/** Returns the foot.
	 */
	public Foot getFoot() {
		return _foot;
	}
	/** Returns the foot.
	 * @since 3.5.2
	 */	
	public org.zkoss.zul.api.Foot getFootApi() {
		return getFoot();
	}
	/** Returns a collection of heads, including {@link #getColumns}
	 * and auxiliary heads ({@link Auxhead}) (never null).
	 *
	 * @since 3.0.0
	 */
	public Collection getHeads() {
		return _heads;
	}

	/** Returns the specified cell, or null if not available.
	 * @param row which row to fetch (starting at 0).
	 * @param col which column to fetch (starting at 0).
	 */
	public Component getCell(int row, int col) {
		final Rows rows = getRows();
		if (rows == null) return null;

		List children = rows.getChildren();
		if (children.size() <= row) return null;

		children = ((Row)children.get(row)).getChildren();
		return children.size() <= col ? null: (Component)children.get(col);
	}

	/** Returns the horizontal alignment of the whole grid.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment of the whole grid.
	 * <p>Allowed: "left", "center", "right"
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	//--Paging--//
	/**
	 * Sets how to position the paging of grid at the client screen.
	 * It is meaningless if the mold is not in "paging".
	 * @param pagingPosition how to position. It can only be "bottom" (the default), or
	 * "top", or "both".
	 * @since 3.0.4
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
	 * Returns how to position the paging of grid at the client screen.
	 * It is meaningless if the mold is not in "paging".
	 * @since 3.0.4
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
	 * the grid will rely on the paging controller to handle long-content
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
			_pgi = pgi; //assign before detach paging, since removeChild assumes it

			if (inPagingMold()) {
				if (old != null) removePagingListener(old);
				if (_pgi == null) {
					if (_paging != null) _pgi = _paging;
					else newInternalPaging();
				} else { //_pgi != null
					if (_pgi != _paging) {
						if (_paging != null) _paging.detach();
						_pgi.setTotalSize(_rows != null ? _rows.getVisibleItemCount(): 0);
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
		paging.setTotalSize(_rows != null ? _rows.getVisibleItemCount(): 0);
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
						new PagingEvent(evt.getName(), Grid.this,
							evt.getPageable(), evt.getActivePage()));
				}
			};
		pgi.addEventListener(ZulEvents.ON_PAGING, _pgListener);

		if (_pgImpListener == null)
			_pgImpListener = new EventListener() {
	public void onEvent(Event event) {
		if (_rows != null && _model != null && inPagingMold()) {
		//theorectically, _rows shall not be null if _model is not null when
		//this method is called. But, just in case -- if sent manually
			final Renderer renderer = new Renderer();
			try {
				final Paginal pgi = getPaginal();
				int pgsz = pgi.getPageSize();
				final int ofs = pgi.getActivePage() * pgsz;
				for (final Iterator it = _rows.getChildren().listIterator(ofs);
				--pgsz >= 0 && it.hasNext();)
					renderer.render((Row)it.next());
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
		if (getModel() != null || getPagingPosition().equals("both")) invalidate(); // just in case.
		else if (_rows != null) _rows.invalidate();
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
	/** @deprecated As of release 3.0.7, replaced with {@link #getPagingChild}
	 * to avoid the confusion with {@link #getPaginal}.
	 */
	public Paging getPaging() {
		return getPagingChild();
	}
	/**
	 * @since 3.5.2
	 * */
	public org.zkoss.zul.api.Paging getPagingApi() {
		return getPagingChild();
	}
	/** Returns the page size, aka., the number rows per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 */
	public int getPageSize() {
		return pgi().getPageSize();
	}
	/** Sets the page size, aka., the number rows per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 */
	public void setPageSize(int pgsz) throws WrongValueException {
		pgi().setPageSize(pgsz);
	}
	/** Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 * @since 3.0.4
	 */
	public int getPageCount() {
		return pgi().getPageCount();
	}
	/** Returns the active page (starting from 0).
	 * @since 3.0.4
	 */
	public int getActivePage() {
		return pgi().getActivePage();
	}
	/** Sets the active page (starting from 0).
	 * @since 3.0.4
	 */
	public void setActivePage(int pg) throws WrongValueException {
		pgi().setActivePage(pg);
	}
	private Paginal pgi() {
		if (_pgi == null)
			throw new IllegalStateException("Available only the paging mold");
		return _pgi;
	}

	/** Returns whether this grid is in the paging mold.
	 */
	/*package*/ boolean inPagingMold() {
		return "paging".equals(getMold());
	}

	/** ROD mold use only.
	 */
	/*package*/ final boolean inSpecialMold() {
		final String mold = (String) getAttribute("special-mold-name");
		return mold != null && getMold().equals(mold);
	}
	/** ROD mold use only.
	 */
	/*package*/ final GridDrawerEngine getDrawerEngine() {
		return _engine;
	}
	
	/**
	 *Internal use only.
	 *@since 3.0.4
	 */
	public void setInnerHeight(String innerHeight) {
		if (innerHeight == null) innerHeight = "100%";
		if (!Objects.equals(_innerHeight, innerHeight)) {
			_innerHeight = innerHeight;
			smartUpdate("z.innerHeight", _innerHeight);
		}
	}
	
	/**
	 *Internal use only.
	 *@since 3.0.4
	 */
	public String getInnerHeight() {
		return _innerHeight;
	}	
	/**
	 *Internal use only.
	 *@since 3.0.4
	 */
	public void setInnerTop(String innerTop) {
		if (innerTop == null) innerTop = "height:0px;display:none";
		if (!Objects.equals(_innerTop, innerTop)) {
			_innerTop = innerTop;
			smartUpdate("z.innerTop", _innerTop);
		}
	}
	/**
	 *Internal use only.
	 *@since 3.0.4
	 */
	public String getInnerTop() {
		return _innerTop;
	}
	/**
	 *Internal use only.
	 *@since 3.0.4
	 */
	public void setInnerBottom(String innerBottom) {
		if (innerBottom == null) innerBottom = "height:0px;display:none";
		if (!Objects.equals(_innerBottom, innerBottom)) {
			_innerBottom = innerBottom;
			smartUpdate("z.innerBottom", _innerBottom);
		}
	}
	/**
	 *Internal use only.
	 *@since 3.0.4
	 */
	public String getInnerBottom() {
		return _innerBottom;
	}
	//-- ListModel dependent codes --//
	/** Returns the model associated with this grid, or null
	 * if this grid is not associated with any list data model.
	 *
	 * <p>Note: if {@link #setModel(GroupsModel)} was called with a
	 * groups model, this method returns an instance of {@link ListModel}
	 * encapsulating it.
	 *
	 * @see #setModel(ListModel)
	 * @see #setModel(GroupsModel)
	 */
	public ListModel getModel() {
		return _model;
	}
	/** Returns the list model associated with this grid, or null
	 * if this grid is associated with a {@link GroupsModel}
	 * or not associated with any list data model.
	 * @see #setModel(ListModel)
	 * @since 3.5.0
	 */
	public ListModel getListModel() {
		return _model instanceof GroupsListModel ? null: _model;
	}
	/** Returns the groups model associated with this grid, or null
	 * if this grid is associated with a {@link ListModel}
	 * or not associated with any list data model.
	 * @since 3.5.0
	 * @see #setModel(GroupsModel)
	 */
	public GroupsModel getGroupsModel() {
		return _model instanceof GroupsListModel ?
			((GroupsListModel)_model).getGroupsModel(): null;
	}
	/** Sets the list model associated with this grid.
	 * If a non-null model is assigned, no matter whether it is the same as
	 * the previous, it will always cause re-render.
	 *
	 * @param model the list model to associate, or null to dis-associate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 * @see #getListModel
	 * @see #setModel(GroupsModel)
	 */
	public void setModel(ListModel model) {
		if (model != null) {
			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
					if (_model instanceof GroupsListModel)
						_rows.getChildren().clear();
				} else {
					if (_rows != null) _rows.getChildren().clear(); //Bug 1807414
					smartUpdate("z.model", "true");
				}

				_model = model;
				initDataListener();
			}

			//Always syncModel because it is easier for user to enfore reload
			syncModel(-1, -1); //create rows if necessary
			postOnInitRender();
			//Since user might setModel and setRender separately or repeatedly,
			//we don't handle it right now until the event processing phase
			//such that we won't render the same set of data twice
			//--
			//For better performance, we shall load the first few row now
			//(to save a roundtrip)
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
			if (_rows != null) _rows.getChildren().clear();
			smartUpdate("z.model", null);
		}
	}
	/** Sets the groups model associated with this grid.
	 * If a non-null model is assigned, no matter whether it is the same as
	 * the previous, it will always cause re-render.
	 *
	 * <p>The groups model is used to represent a list of data with
	 * grouping.
	 *
	 * @param model the groups model to associate, or null to dis-associate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.5.0
	 * @see #setModel(ListModel)
	 * @see #getGroupsModel()
	 */
	public void setModel(GroupsModel model) {
		setModel((ListModel)(model != null ? new GroupsListModel(model): null));
	}
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					onListDataChange(event);
				}
			};
		_model.addListDataListener(_dataListener);
	}

	/** Returns the renderer to render each row, or null if the default
	 * renderer is used.
	 */
	public RowRenderer getRowRenderer() {
		return _renderer;
	}
	/** Sets the renderer which is used to render each row
	 * if {@link #getModel} is not null.
	 *
	 * <p>Note: changing a render will not cause the grid to re-render.
	 * If you want it to re-render, you could assign the same model again 
	 * (i.e., setModel(getModel())), or fire an {@link ListDataEvent} event.
	 *
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize with the model
	 */
	public void setRowRenderer(RowRenderer renderer) {
		if (_renderer != renderer) {
			_renderer = renderer;

			if (_model != null) {
				if ((renderer instanceof RowRendererExt)
				|| (_renderer instanceof RowRendererExt)) {
					//bug# 2388345, a new renderer that might new own Row, shall clean all Row first
					getRows().getChildren().clear();
					syncModel(-1, -1); //we have to recreate all
				} else if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
					unloadAll();
				}
			}
		}
	}
	/** Sets the renderer by use of a class name.
	 * It creates an instance automatically.
	 */
	public void setRowRenderer(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setRowRenderer((RowRenderer)Classes.newInstanceByThread(clsnm));
	}

	/** Returns the number of rows to preload when receiving
	 * the rendering request from the client.
	 *
	 * <p>Default: 7.
	 *
	 * <p>It is used only if live data ({@link #setModel(ListModel)} and
	 * not paging ({@link #getPaging}.
	 *
	 * <p>Note: if the "pre-load-size" attribute of component is specified, it's prior to the original value.(@since 3.0.4)
	 * @since 2.4.1
	 */
	public int getPreloadSize() {
		final String size = (String) getAttribute("pre-load-size");
		return size != null ? Integer.parseInt(size) : _preloadsz;
	}
	/** Sets the number of rows to preload when receiving
	 * the rendering request from the client.
	 * <p>It is used only if live data ({@link #setModel(ListModel)} and
	 * not paging ({@link #getPaging}.
	 *
	 * @param sz the number of rows to preload. If zero, no preload
	 * at all.
	 * @exception UiException if sz is negative
	 * @since 2.4.1
	 */
	public void setPreloadSize(int sz) {
		if (sz < 0)
			throw new UiException("nonnegative is required: "+sz);
		_preloadsz = sz;
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
	
	/** Synchronizes the grid to be consistent with the specified model.
	 *
	 * @param min the lower index that a range of invalidated rows
	 * @param max the higher index that a range of invalidated rows
	 */
	private void syncModel(int min, int max) {
		final int newsz = _model.getSize();
		final int oldsz = _rows != null ? _rows.getChildren().size(): 0;

		int newcnt = newsz - oldsz;
		int atg = _pgi != null ? getActivePage(): 0;
		RowRenderer renderer = null;
		Component next = null;		
		if (oldsz > 0) {
			if (min < 0) min = 0;
			else if (min > oldsz - 1) min = oldsz - 1;
			if (max < 0) max = oldsz - 1;
			else if (max > oldsz - 1) max = oldsz - 1;
			if (min > max) {
				int t = min; min = max; max = t;
			}

			int cnt = max - min + 1; //# of affected
			if (_model instanceof GroupsListModel) {
			//detach all from end to front since groupfoot
			//must be detached before group
				newcnt += cnt; //add affected later
				if (newcnt > 50 && !inPagingMold())
					invalidate(); //performance is better

				Component comp = (Component)_rows.getChildren().get(max);
				next = comp.getNextSibling();
				while (--cnt >= 0) {
					Component p = comp.getPreviousSibling();
					comp.detach();
					comp = p;
				}
			} else { //ListModel
				int addcnt = 0;
				Component row = (Component)_rows.getChildren().get(min);
				while (--cnt >= 0) {
					next = row.getNextSibling();

					if (cnt < -newcnt) { //if shrink, -newcnt > 0
						row.detach(); //remove extra
					} else if (((Row)row).isLoaded()) {
						if (renderer == null)
							renderer = getRealRenderer();
						row.detach(); //always detach
						_rows.insertBefore(newUnloadedRow(renderer, min++), next);
						++addcnt;
					}

					row = next;
				}

				if ((addcnt > 50 || addcnt + newcnt > 50) && !inPagingMold())
					invalidate(); //performance is better
			}
		} else {
			min = 0;

			//auto create but it means <grid model="xx"><rows/>... will fail
			if (_rows == null)
				new Rows().setParent(this);
		}

		for (; --newcnt >= 0; ++min) {
			if (renderer == null)
				renderer = getRealRenderer();
			_rows.insertBefore(newUnloadedRow(renderer, min), next);
		}

		if (_pgi != null) {
			if (atg > _pgi.getPageCount())
				atg = _pgi.getPageCount();
			_pgi.setActivePage(atg);
		}
	}
	/** Unloads rows.
	 * It unloads all loaded rows by recreating them.
	 * Note: it assumes the model and renderer remains the same,
	 * and the render doesn't implement
	 * RowRenderExt (to create row in app-specific way)
	 */
	private void unloadAll() {
		final List rows = _rows.getChildren();
		int cnt = rows.size();
		if (cnt <= 0) return; //nothing to do

		RowRenderer renderer = null;
		Component comp = (Component)rows.get(cnt - 1);
		while (--cnt >= 0) {
			Component prev = comp.getPreviousSibling();
			if (((Row)comp).isLoaded()) {
				if (renderer == null)
					renderer = getRealRenderer();
				Component next = comp.getNextSibling();
				comp.detach(); //always detach
				_rows.insertBefore(newUnloadedRow(renderer, cnt), next);
			}
			comp = prev;
		}
	}

	/** Creates an new and unloaded row. */
	private final Row newUnloadedRow(RowRenderer renderer, int index) {
		Row row = null;
		if (_model instanceof GroupsListModel) {
			final GroupsListModel model = (GroupsListModel) _model;
			final GroupDataInfo info = model.getDataInfo(index);
			switch(info.type){
			case GroupDataInfo.GROUP:
				row = newGroup(renderer);
				break;
			case GroupDataInfo.GROUPFOOT:
				row = newGroupfoot(renderer);
				break;
			default:
				row = newRow(renderer);
			}		
		}else{
			row = newRow(renderer);
		}
		row.setLoaded(false);

		newUnloadedCell(renderer, row);
		return row;
	}
	private Row newRow(RowRenderer renderer) {
		Row row = null;
		if (renderer instanceof RowRendererExt)
			row = ((RowRendererExt)renderer).newRow(this);
		if (row == null) {
			row = new Row();
			row.applyProperties();
		}
		return row;
	}
	private Group newGroup(RowRenderer renderer) {
		Group group = null;
		if (renderer instanceof GroupRendererExt)
			group = ((GroupRendererExt)renderer).newGroup(this);
		if (group == null) {
			group = new Group();
			group.applyProperties();
		}
		return group;
	}
	private Groupfoot newGroupfoot(RowRenderer renderer) {
		Groupfoot groupfoot = null;
		if (renderer instanceof GroupRendererExt)
			groupfoot = ((GroupRendererExt)renderer).newGroupfoot(this);
		if (groupfoot == null) {
			groupfoot = new Groupfoot();
			groupfoot.applyProperties();
		}
		return groupfoot;
	}
	private Component newUnloadedCell(RowRenderer renderer, Row row) {
		Component cell = null;
		if (renderer instanceof RowRendererExt)
			cell = ((RowRendererExt)renderer).newCell(row);

		if (cell == null) {
			cell = newRenderLabel(null);
			cell.applyProperties();
		}
		cell.setParent(row);
		return cell;
	}
	/** Returns the label for the cell generated by the default renderer.
	 */
	private static Label newRenderLabel(String value) {
		final Label label =
			new Label(value != null && value.length() > 0 ? value: " ");
		label.setPre(true); //to make sure &nbsp; is generated, and then occupies some space
		return label;
	}

	/** Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 */
	public void onInitRender() {
		removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		if (inSpecialMold()) {
			_engine.onInitRender();
		} else {
			final Renderer renderer = new Renderer();
			try {
				int pgsz, ofs;
				if (inPagingMold()) {
					pgsz = _pgi.getPageSize();
					ofs = _pgi.getActivePage() * pgsz;
					final int cnt = _rows.getChildren().size();
					if (ofs >= cnt) { //not possible; just in case
						ofs = cnt - pgsz;
						if (ofs < 0) ofs = 0;
					}
				} else {
					pgsz = 20;
					ofs = 0;
					//we don't know # of visible rows, so a 'smart' guess
					//It is OK since client will send back request if not enough
				}
	
				int j = 0;
				for (Iterator it = _rows.getChildren().listIterator(ofs);
				j < pgsz && it.hasNext(); ++j)
					renderer.render((Row)it.next());
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
	}
	private void postOnInitRender() {
		//20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
			smartUpdate("z.render", true);
		}
	}

	/** Handles when the list model's content changed.
	 */
	private void onListDataChange(ListDataEvent event) {
		//when this is called _model is never null
		final int newsz = _model.getSize(), oldsz = _rows.getChildren().size();
		int min = event.getIndex0(), max = event.getIndex1(), cnt;

		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			cnt = newsz - oldsz;
			if (cnt <= 0)
				throw new UiException("Adding causes a smaller list?");
			if (cnt > 50 && !inPagingMold())
				invalidate(); //performance is better
			if (min < 0)
				if (max < 0) min = 0;
				else min = max - cnt + 1;
			if (min > oldsz) min = oldsz;

			RowRenderer renderer = null;
			final Component next =
				min < oldsz ? (Component)_rows.getChildren().get(min): null;
			while (--cnt >= 0) {
				if (renderer == null)
					renderer = getRealRenderer();
				_rows.insertBefore(newUnloadedRow(renderer, min++), next);
			}
			break;

		case ListDataEvent.INTERVAL_REMOVED:
			cnt = oldsz - newsz;
			if (cnt <= 0)
				throw new UiException("Removal causes a larger list?");
			if (min >= 0) max = min + cnt - 1;
			else if (max < 0) max = cnt - 1; //0 ~ cnt - 1			
			if (max > oldsz - 1) max = oldsz - 1;

			//detach from end (due to groopfoot issue)
			Component comp = (Component)_rows.getChildren().get(max);
			while (--cnt >= 0) {
				Component p = comp.getPreviousSibling();
				comp.detach();
				comp = p;
			}
			break;

		default: //CONTENTS_CHANGED
			syncModel(min, max);
		}

		postOnInitRender(); //to improve performance
	}

	private static final RowRenderer getDefaultRowRenderer() {
		return _defRend;
	}
	private static final RowRenderer _defRend = new RowRenderer() {
		public void render(Row row, Object data) {
			final Label label = newRenderLabel(Objects.toString(data));
			label.applyProperties();
			label.setParent(row);
			row.setValue(data);
		}
	};
	/** Returns the renderer used to render rows.
	 */
	private RowRenderer getRealRenderer() {
		return _renderer != null ? _renderer: getDefaultRowRenderer();
	}

	/** Used to render row if _model is specified. */
	/*package*/ class Renderer {
		private final RowRenderer _renderer;
		private boolean _rendered, _ctrled;

		/*package*/ Renderer() {
			_renderer = getRealRenderer();
		}
		/*package*/ void render(Row row) throws Throwable {
			if (row.isLoaded())
				return; //nothing to do

			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}

			final Component cell = row.getFirstChild();
			if (!(_renderer instanceof RowRendererExt)
			|| (((RowRendererExt)_renderer).getControls() & 
				RowRendererExt.DETACH_ON_RENDER) != 0) { //detach (default)
				cell.detach();
			}

			try {
				_renderer.render(row, _model.getElementAt(row.getIndex()));
			} catch (Throwable ex) {
				try {
					final Label label = newRenderLabel(Exceptions.getMessage(ex));
					label.applyProperties();
					label.setParent(row);
				} catch (Throwable t) {
					log.error(t);
				}
				row.setLoaded(true);
				throw ex;
			} finally {
				if (row.getChildren().isEmpty())
					cell.setParent(row);
			}

			row.setLoaded(true);
			_rendered = true;
		}
		/*package*/ void doCatch(Throwable ex) {
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
		/*package*/ void doFinally() {
			if (_ctrled)
				((RendererCtrl)_renderer).doFinally();
		}
	}

	/** Renders the specified {@link Row} if not loaded yet,
	 * with {@link #getRowRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * In other words, it is meaningful only if live data model is used.
	 */
	public void renderRow(Row row) {
		if (_model == null) return;

		final Renderer renderer = new Renderer();
		try {
			renderer.render(row);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}
	/** Renders the specified {@link Row} if not loaded yet,
	 * with {@link #getRowRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * In other words, it is meaningful only if live data model is used.
	 * @param rowApi assume as a {@link org.zkoss.zul.Row}   
	 * @since 3.5.2
	 */
	public void renderRowApi(org.zkoss.zul.api.Row rowApi) {
		Row row = (Row) rowApi;
		renderRow(row);		
	}
	/** Renders all {@link Row} if not loaded yet,
	 * with {@link #getRowRenderer}.
	 */
	public void renderAll() {
		if (_model == null) return;

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = _rows.getChildren().iterator(); it.hasNext();)
				renderer.render((Row)it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}
	/** Renders a set of specified rows.
	 * It is the same as {@link #renderItems}.
	 */
	public void renderRows(Set rows) {
		renderItems(rows);
	}

	public void renderItems(Set rows) {
		if (_model == null) { //just in case that app dev might change it
			if (log.debugable()) log.debug("No model no render");
			return;
		}

		if (rows.isEmpty())
			return; //nothing to do

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = rows.iterator(); it.hasNext();)
				renderer.render((Row)it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/** Returns the style class for the odd rows.
	 *
	 * <p>Default: {@link #getZclass()}-odd. (since 3.5.0)
	 *
	 * @since 3.0.0
	 */
	public String getOddRowSclass() {
		return _scOddRow == null ? getZclass() + "-odd" : _scOddRow;
	}
	/** Sets the style class for the odd rows.
	 * If the style class doesn't exist, the striping effect disappears.
	 * You can provide different effects by providing the proper style
	 * classes.
	 * @since 3.0.0
	 */
	public void setOddRowSclass(String scls) {
		if (scls != null && scls.length() == 0) scls = null;
		if (!Objects.equals(_scOddRow, scls)) {
			_scOddRow = scls;
			smartUpdate("z.scOddRow", scls);
		}
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
			} else if (inSpecialMold() && _engine == null) {
				_engine = new GridDrawerEngine(this);
			}
		}
	}
	public String getZclass() {
		return _zclass == null ? "z-grid" : _zclass;
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		if (_align != null)
			HTMLs.appendAttribute(sb, "align", _align);
		if (_model != null) {
			if (inPagingMold()) {
				final Rows rows = getRows();
				if (rows != null && rows.hasGroup()) HTMLs.appendAttribute(sb, "z.hasgroup", true);
			}
			HTMLs.appendAttribute(sb, "z.model", true);
			final List rows = getRows().getChildren();
			int index = rows.size();
			for(final ListIterator it = rows.listIterator(index);
			it.hasPrevious(); --index)
				if(((Row)it.previous()).isLoaded()) break;
			HTMLs.appendAttribute(sb, "z.lastLoadIdx", !inSpecialMold() || 
					rows.size() <= _engine.getVisibleAmount() ? index : _engine.getVisibleAmount());
		}
		if (getOddRowSclass() != null)
			HTMLs.appendAttribute(sb, "z.scOddRow", getOddRowSclass());
		HTMLs.appendAttribute(sb, "z.fixed", isFixedLayout());
		if (inSpecialMold()) {
			HTMLs.appendAttribute(sb, "z.cnt",  getRows().getChildren().size());
			HTMLs.appendAttribute(sb, "z.cur",  _engine.getCurpos());
			HTMLs.appendAttribute(sb, "z.amt",  _engine.getVisibleAmount());
			HTMLs.appendAttribute(sb, "z.isrod",  true);
			HTMLs.appendAttribute(sb, "z.preload",  getPreloadSize());
		}
		if (_vflex)
			HTMLs.appendAttribute(sb, "z.vflex", true);
		return sb.toString();
	}

	//-- Component --//
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Rows) {
			if (_rows != null && _rows != newChild)
				throw new UiException("Only one rows child is allowed: "+this+"\nNote: rows is created automatically if live data");
			_rows = (Rows)newChild;
		} else if (newChild instanceof Columns) {
			if (_cols != null && _cols != newChild)
				throw new UiException("Only one columns child is allowed: "+this);
			_cols = (Columns)newChild;
		} else if (newChild instanceof Foot) {
			if (_foot != null && _foot != newChild)
				throw new UiException("Only one foot child is allowed: "+this);
			_foot = (Foot)newChild;
		} else if (newChild instanceof Paging) {
			if (_pgi != null)
				throw new UiException("External paging cannot coexist with child paging");
			if (_paging != null && _paging != newChild)
				throw new UiException("Only one paging is allowed: "+this);
			if (!inPagingMold())
				throw new UiException("The child paging is allowed only in the paging mold");
			_pgi = _paging = (Paging)newChild;
		} else if (!(newChild instanceof Auxhead)) {
			throw new UiException("Unsupported child for grid: "+newChild);
		}
 
		if (super.insertBefore(newChild, refChild)) {
			//not need to invalidate since auxhead visible only with _cols
			if (!(newChild instanceof Auxhead))
				invalidate();
			return true;
		}
		return false;
	}
	public boolean removeChild(Component child) {
		if (_paging == child && _pgi == child && inPagingMold())
			throw new IllegalStateException("The paging component cannot be removed manually. It is removed automatically when changing the mold");
				//Feature 1906110: prevent developers from removing it accidently

		if (!super.removeChild(child))
			return false;

		if (_rows == child) _rows = null;
		else if (_cols == child) _cols = null;
		else if (_foot == child) _foot = null;
		else if (_paging == child) {
			_paging = null;
			if (_pgi == child) _pgi = null;
		}
		invalidate();
		return true;
	}

	//Cloneable//
	public Object clone() {
		final Grid clone = (Grid)super.clone();
		clone.init();

		int cnt = 0;
		if (clone._rows != null) ++cnt;
		if (clone._cols != null) ++cnt;
		if (clone._foot != null) ++cnt;
		if (clone._paging != null) ++cnt;
		if (cnt > 0) clone.afterUnmarshal(cnt);

		return clone;
	}
	/** @param cnt # of children that need special handling (used for optimization).
	 * -1 means process all of them
	 */
	private void afterUnmarshal(int cnt) {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Rows) {
				_rows = (Rows)child;
				if (--cnt == 0) break;
			} else if (child instanceof Columns) {
				_cols = (Columns)child;
				if (--cnt == 0) break;
			} else if (child instanceof Foot) {
				_foot = (Foot)child;
				if (--cnt == 0) break;
			} else if (child instanceof Paging) {
				_pgi = _paging = (Paging)child;
				if (--cnt == 0) break;
			}
		}
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		init();
		afterUnmarshal(-1);
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
	implements InnerWidth, RenderOnDemand, ChildChangedAware {
		//ChildChangedAware//
		public boolean isChildChangedAware() {
			return !isFixedLayout();
		}
		//InnerWidth//
		public void setInnerWidthByClient(String width) {
			_innerWidth = width == null ? "100%": width;
		}

		//RenderOnDemand//
		public void renderItems(Set items) {
			int cnt = items.size();
			if (cnt == 0)
				return; //nothing to do
			cnt = 20 - cnt;
			if (cnt > 0 && _preloadsz > 0) { //Feature 1740072: pre-load
				if (cnt > _preloadsz) cnt = _preloadsz;

				//1. locate the first item found in items
				final List toload = new LinkedList();
				Iterator it = getRows().getChildren().iterator();
				while (it.hasNext()) {
					final Row row = (Row)it.next();
					if (items.contains(row)) //found
						break;
					if (!row.isLoaded())
						toload.add(0, row); //reverse order
				}

				//2. add unload items before the found one
				if (!toload.isEmpty()) {
					int bfcnt = cnt/3;
					for (Iterator e = toload.iterator();
					bfcnt > 0 && e.hasNext(); --bfcnt, --cnt) {
						items.add(e.next());
					}
				}

				//3. add unloaded after the found one
				while (cnt > 0 && it.hasNext()) {
					final Row row = (Row)it.next();
					if (!row.isLoaded() && items.add(row))
						--cnt;
				}
			}

			Grid.this.renderItems(items);
		}
	}
	/** An iterator used by _heads.
	 */
	private class Iter implements Iterator {
		private final ListIterator _it = getChildren().listIterator();

		public boolean hasNext() {
			while (_it.hasNext()) {
				Object o = _it.next();
				if (o instanceof Columns || o instanceof Auxhead) {
					_it.previous();
					return true;
				}
			}
			return false;
		}
		public Object next() {
			for (;;) {
				Object o = _it.next();
				if (o instanceof Columns || o instanceof Auxhead)
					return o;
			}
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
