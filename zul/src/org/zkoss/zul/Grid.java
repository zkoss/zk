/* Grid.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 15:40:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.io.Serializables;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.CloneableEventListener;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zul.event.DataLoadingEvent;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.PageSizeEvent;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.RenderEvent;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.impl.DataLoader;
import org.zkoss.zul.impl.GridDataLoader;
import org.zkoss.zul.impl.GroupsListModel;
import org.zkoss.zul.impl.MeshElement;
import org.zkoss.zul.impl.Padding;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A grid is an element that contains both rows and columns elements.
 * It is used to create a grid of elements.
 * Both the rows and columns are displayed at once although only one will
 * typically contain content, while the other may provide size information.
 *
 * <p>Events: onAfterRender<br/>
 * onAfterRender is sent when the model's data has been rendered.(since 5.0.4)
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
 * or assigned explicitly by {@link #setPaginal}.
 * The paging controller specified explicitly by {@link #setPaginal} is called
 * the external page controller. It is useful if you want to put the paging
 * controller at different location (other than as a child component), or
 * you want to use the same controller to control multiple grids.
 *
 * <p>Default {@link #getZclass}: z-grid.(since 3.5.0)
 *
 * <p>To have a grid without stripping, you can specify a non-existent
 * style class to {@link #setOddRowSclass}.
 *
 * <h3>Clustering and Serialization</h3>
 *
 * <p>When used in a clustering environment, you have to make {@link RowRenderer}
 * ({@link #setRowRenderer}) and {@link ListModel} ({@link #setModel}) either
 * serializable or re-assign them when {@link #sessionDidActivate} is called.
 *
 * <h3>Render on Demand (rod)</h3>
 * [ZK EE]
 * [Since 5.0.0]
 * 
 * <p>For huge data, you can turn on Grid's ROD to request ZK engine to load from 
 * {@link ListModel} only the required data chunk and create only the required
 * {@link Row}s in memory and render only the required DOM elements in browser. 
 * So it saves both the memory and the processing time in both server and browser 
 * for huge data. If you don't use the {@link ListModel} with the Grid, turn on 
 * the ROD will still have ZK engine to render only a chunk of DOM elements in 
 * browser so it at least saves the memory and processing time in browser. Note 
 * that ROD works only if the Grid is configured to has a limited "view port" 
 * height. That is, either the Grid is in the "paging" mold or you have to 
 * {@link #setHeight(String)} or {@link #setVflex(String)} of the Grid to 
 * make ROD works.</p>
 * 
 * <p>You can turn on/off ROD for all Grids in the application or only 
 * for a specific Grid. To turn on ROD for all Grids in the application, you 
 * have to specify the Library Property "org.zkoss.zul.grid.rod" to "true" in 
 * WEB-INF/zk.xml. If you did not specify the Library Property, 
 * default is false.</p>
 * 
 * <pre><code>
 *	<library-property>
 *		<name>org.zkoss.zul.grid.rod</name>
 *		<value>true</value>
 *	</library-property>
 * </code></pre>
 * 
 * <p>To turn on ROD for a specific Grid, you have to specify the Grid's attribute
 * map with key "org.zkoss.zul.grid.rod" to true. That is, for example, if in 
 * a zul file, you shall specify &lt;custom-attributes> of the Grid like this:</p>
 * <pre><code>
 *	<grid ...>
 *    <custom-attributes org.zkoss.zul.grid.rod="true"/>
 *  </grid>
 * </code></pre>
 * 
 * <p>You can mix the Library Property and &lt;custom-attributes> ways together.
 * The &lt;custom-attributes> way always takes higher priority. So you
 * can turn OFF ROD in general and turn ON only some specific Grid component. Or 
 * you can turn ON ROD in general and turn OFF only some specific Grid component.</P>
 * 
 * <p>Since only partial {@link Row}s are created and rendered in the Grid if 
 * you turn the ROD on, there will be some limitations on accessing {@link Row}s.
 * For example, if you call
 * <pre><code>
 * Row rowAt100 = (Row) getRows().getChildren().get(100);
 * </code></pre>
 * <p>The {@link Row} in index 100 is not necessary created yet if it is not in the
 * current "view port" and you will get "null" instead.</p>
 * 
 * <p>And it is generally a bad idea to "cache" the created {@link Row} in your
 * application if you turn the ROD on because rows might be removed later. 
 * Basically, you shall operate on the item of the ListModel rather than on the 
 * {@link Row} if you use the ListModel and ROD.</p>
 * 
 * <h3>Custom Attributes</h3>
 * <dl>
 * <dt>org.zkoss.zul.grid.rod</dt>
 * <dd>Specifies whether to enable ROD (render-on-demand).</br>
 * Notice that you could specify this attribute in any of its ancestor's attributes.
 * It will be inherited.</dd>
 * <dt>org.zkoss.zul.grid.autoSort</dt>.(since 5.0.7) 
 * <dd>Specifies whether to sort the model when the following cases:</br>
 * <ol>
 * <li>{@link #setModel} is called and {@link Column#setSortDirection} is set.</li>
 * <li>{@link Column#setSortDirection} is called.</li>
 * <li>Model receives {@link ListDataEvent} and {@link Column#setSortDirection} is set.</li>
 * </ol>
 * If you want to ignore sort when receiving {@link ListDataEvent}, 
 * you can specifies the value as "ignore.change".</br>
 * Notice that you could specify this attribute in any of its ancestor's attributes.
 * It will be inherited.</dd>
 * </dl>
 * 
 * <dt>org.zkoss.zul.grid.preloadSize</dt>.(since 5.0.8) 
 * <dd>Specifies the number of rows to preload when receiving
 * the rendering request from the client.
 * <p>It is used only if live data ({@link #setModel(ListModel)} and
 * not paging ({@link #getPagingChild}).</dd>
 * 
 * <dt>org.zkoss.zul.grid.initRodSize</dt>.(since 5.0.8) 
 * <dd>Specifies the number of rows rendered when the Grid first render.
 * <p>
 * It is used only if live data ({@link #setModel(ListModel)} and not paging
 * ({@link #getPagingChild}).</dd>
 * 
 * @author tomyeh
 * @see ListModel
 * @see RowRenderer
 * @see RowRendererExt
 */
public class Grid extends MeshElement {
	private static final Log log = Log.lookup(Grid.class);
	private static final long serialVersionUID = 20091111L;

	private static final String ATTR_ON_INIT_RENDER_POSTED =
		"org.zkoss.zul.Grid.onInitLaterPosted";
	private static final String ATTR_ON_PAGING_INIT_RENDER_POSTED = 
		"org.zkoss.zul.Grid.onPagingInitLaterPosted";

	private static final int INIT_LIMIT = 100;

	private transient DataLoader _dataLoader;
	private transient Rows _rows;
	private transient Columns _cols;
	private transient Foot _foot;
	private transient Frozen _frozen;
	private transient Collection<Component> _heads;
	private transient ListModel<?> _model;
	private transient RowRenderer<?> _renderer;
	private transient ListDataListener _dataListener;
	/** The paging controller, used only if mold = "paging". */
	private transient Paginal _pgi;
	/** The paging controller, used only if mold = "paging" and user
	 * doesn't assign a controller via {@link #setPaginal}.
	 * If exists, it is the last child.
	 */
	private transient Paging _paging;
	private EventListener<PagingEvent> _pgListener;
	private EventListener<Event> _pgImpListener, _modelInitListener;
	/** The style class of the odd row. */
	private String _scOddRow = null;
	/** the # of rows to preload. */
	private int _preloadsz = 7;
	private String _innerWidth = "100%";
	private int _currentTop = 0; //since 5.0.0 scroll position
	private int _currentLeft = 0;
	private int _topPad; //since 5.0.0 top padding
	private boolean _renderAll; //since 5.0.0
	
	private transient boolean _rod;
	private String _emptyMessage;
	
	static {
		addClientEvent(Grid.class, Events.ON_RENDER, CE_DUPLICATE_IGNORE|CE_IMPORTANT|CE_NON_DEFERRABLE);
		addClientEvent(Grid.class, "onInnerWidth", CE_DUPLICATE_IGNORE|CE_IMPORTANT);
		addClientEvent(Grid.class, "onScrollPos", CE_DUPLICATE_IGNORE | CE_IMPORTANT); //since 5.0.0
		addClientEvent(Grid.class, "onTopPad", CE_DUPLICATE_IGNORE); //since 5.0.0
		addClientEvent(Grid.class, "onDataLoading", CE_DUPLICATE_IGNORE|CE_IMPORTANT|CE_NON_DEFERRABLE); //since 5.0.0
		addClientEvent(Grid.class, ZulEvents.ON_PAGE_SIZE, CE_DUPLICATE_IGNORE|CE_IMPORTANT|CE_NON_DEFERRABLE); //since 5.0.2
	}
	
	public Grid() {
		init();
	}
	private void init() {
		_heads = new AbstractCollection<Component>() {
			public int size() {
				int sz = getChildren().size();
				if (_rows != null) --sz;
				if (_foot != null) --sz;
				if (_paging != null) --sz;
				if (_frozen != null) --sz;
				return sz;
			}
			public Iterator<Component> iterator() {
				return new Iter();
			}
		};
	}
	
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (oldpage == null) {
			Executions.getCurrent().setAttribute("zkoss.Grid.deferInitModel_"+getUuid(), Boolean.TRUE);
			//prepare a right moment to init Grid(must be as early as possible)
			this.addEventListener("onInitModel", _modelInitListener = new ModelInitListener());
			Events.postEvent(20000, new Event("onInitModel", this));
		}
	}
	
	private void resetDataLoader() {
		resetDataLoader(true);
	}
	private void resetDataLoader(boolean shallReset) {
		if (_dataLoader != null) {
			if (shallReset) {
				_dataLoader.reset();
				smartUpdate("_lastoffset", 0); //reset for bug 3357641
			}
			_dataLoader = null;
		}
		
		if (shallReset) {
			// Bug ZK-373
			smartUpdate("resetDataLoader", true);
			_currentTop = 0;
			_currentLeft = 0;
			_topPad = 0;
		}
	}
	
	private class ModelInitListener implements SerializableEventListener<Event>,
			CloneableEventListener<Event> {
		public void onEvent(Event event) throws Exception {
			if (_modelInitListener != null) {
				Grid.this.removeEventListener("onInitModel", _modelInitListener);
				_modelInitListener = null; 
			}
			//initialize data loader
			//Tricky! might has been initialized when apply properties
			if (_dataLoader != null) { 
				final boolean rod = evalRod();
				if (_rod != rod || getRows() == null || getRows().getChildren().isEmpty()) {
					if (_model != null) { //so has to recreate rows and items
						if (getRows() != null)
							getRows().getChildren().clear();
						resetDataLoader(); //enforce recreate dataloader, must after getRows().getChildren().clear()
						initModel();
					} else {
						resetDataLoader();  //enforce recreate dataloader
					}
				}
			} else if (_model != null){ //rows not created yet
				initModel();
			} else {
				//bug# 3039282: NullPointerException when assign a model to Grid at onCreate
				//The attribute shall be removed, otherwise DataLoader will not syncModel when setModel
				Executions.getCurrent().removeAttribute("zkoss.Grid.deferInitModel_"+getUuid());
			}
			final DataLoader loader = getDataLoader();
			
			//initialize paginal if any
			Paginal pgi = getPaginal();
			if (pgi != null) pgi.setTotalSize(loader.getTotalSize());
		}
		//reinit the model
		private void initModel() {
			Executions.getCurrent().removeAttribute("zkoss.Grid.deferInitModel_"+getUuid());
			setModel(_model);
		}

		@Override
		public Object willClone(Component comp) {
			return null; // skip to clone
		}
	}

	/** Returns whether to grow and shrink vertical to fit their given space,
	 * so called vertical flexibility.
	 *
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public boolean isVflex() {
		final String vflex = getVflex();
		if ("true".equals(vflex) || "min".equals(vflex)) {
			return true;
		}
		if (Strings.isBlank(vflex) || "false".equals(vflex)) {
			return false;
		}
		return Integer.parseInt(vflex) > 0;
	}
	/** Sets whether to grow and shrink vertical to fit their given space,
	 * so called vertical flexibility.
	 *
	 * @since 3.5.0
	 */
	public void setVflex(boolean vflex) {
		if (isVflex() != vflex) {
			setVflex(String.valueOf(vflex));
		}
	}
	/**
	 * @deprecated since 5.0.0, use {@link #setSizedByContent}(!fixedLayout) instead
	 * @param fixedLayout true to outline this grid by browser
	 */
	public void setFixedLayout(boolean fixedLayout) {
		 setSizedByContent(!fixedLayout);
	}
	/**
	 * @deprecated since 5.0.0, use !{@link #isSizedByContent} instead
	 */
	public boolean isFixedLayout() {
		return !isSizedByContent();
	}
	
	/** Returns the rows.
	 */
	public Rows getRows() {
		return _rows;
	}
	/** Returns the columns.
	 */
	public Columns getColumns() {
		return _cols;
	}
	/** Returns the foot.
	 */
	public Foot getFoot() {
		return _foot;
	}
	
	/**
	 * Returns the frozen child.
	 * @since 5.0.0
	 */
	public Frozen getFrozen() {
		return _frozen;
	}
	/** Returns a collection of heads, including {@link #getColumns}
	 * and auxiliary heads ({@link Auxhead}) (never null).
	 *
	 * @since 3.0.0
	 */
	public Collection<Component> getHeads() {
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

	/** @deprecated As of release 5.0, use CSS instead.
	 */
	public String getAlign() {
		return null;
	}
	/** @deprecated As of release 5.0, use CSS instead.
	 */
	public void setAlign(String align) {
	}

	//--Paging--//
	/** Returns the paging controller, or null if not available.
	 * Note: the paging controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>If mold is "paging", this method never returns null, because
	 * a child paging controller is created automatically (if not specified
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
	/** Specifies the paging controller.
	 * Note: the paging controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>It is OK, though without any effect, to specify a paging controller
	 * even if mold is not "paging".
	 *
	 * @param pgi the paging controller. If null and {@link #getMold} is "paging",
	 * a paging controller is created automatically as a child component
	 * (see {@link #getPagingChild}).
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
						_pgi.setTotalSize(_rows != null ? getDataLoader().getTotalSize(): 0);
						addPagingListener(_pgi);
						if (_pgi instanceof Component)
							smartUpdate("paginal", _pgi);
					}
				}
			}
		}
	}
	/** Creates the internal paging component.
	 */
	private void newInternalPaging() {
//		assert inPagingMold(): "paging mold only";
//		assert (_paging == null && _pgi == null);

		final Paging paging = new Paging();
		paging.setAutohide(true);
		paging.setDetailed(true);
		paging.applyProperties();
		paging.setTotalSize(_rows != null ? getDataLoader().getTotalSize(): 0);
		paging.setParent(this);
		if (_pgi != null)
			addPagingListener(_pgi);
	}
	private class PGListener implements SerializableEventListener<PagingEvent>,
			CloneableEventListener<PagingEvent> {
		public void onEvent(PagingEvent event) {
			Events.postEvent(
				new PagingEvent(event.getName(), Grid.this,
					event.getPageable(), event.getActivePage()));
		}
		@Override
		public Object willClone(Component comp) {
			return null; // skip to clone
		}
	}
	private class PGImpListener implements SerializableEventListener<Event>,
			CloneableEventListener<Event> {
		public void onEvent(Event event) {
			if (_rows != null && _model != null && inPagingMold()) {
			//theoretically, _rows shall not be null if _model is not null when
			//this method is called. But, just in case -- if sent manually
				final Paginal pgi = getPaginal();
				int pgsz = pgi.getPageSize();
				final int ofs = pgi.getActivePage() * pgsz;
				if (_rod) {
					getDataLoader().syncModel(ofs, pgsz);
				}
				postOnPagingInitRender();
			}
			if (getModel() != null || getPagingPosition().equals("both")) invalidate(); // just in case.
			else if (_rows != null) {
				_rows.invalidate();
				
				// Bug 3218078
				if (_frozen != null)
					_frozen.invalidate();
			}
		}

		@Override
		public Object willClone(Component comp) {
			return null; // skip to clone
		}
	}
	/** Adds the event listener for the onPaging event. */
	private void addPagingListener(Paginal pgi) {
		if (_pgListener == null)
			_pgListener = new PGListener();
		pgi.addEventListener(ZulEvents.ON_PAGING, _pgListener);

		if (_pgImpListener == null)
			_pgImpListener = new PGImpListener();
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
	protected Paginal pgi() {
		if (_pgi == null)
			throw new IllegalStateException("Available only the paging mold");
		return _pgi;
	}

	/** Returns whether this grid is in the paging mold.
	 */
	/*package*/ boolean inPagingMold() {
		return "paging".equals(getMold());
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
	@SuppressWarnings("unchecked")
	public <T> ListModel<T> getModel() {
		return (ListModel)_model;
	}
	/** Returns the list model associated with this grid, or null
	 * if this grid is associated with a {@link GroupsModel}
	 * or not associated with any list data model.
	 * @see #setModel(ListModel)
	 * @since 3.5.0
	 */
	@SuppressWarnings("unchecked")
	public <T> ListModel<T> getListModel() {
		return _model instanceof GroupsListModel ? null: (ListModel)_model;
	}
	/** Returns the groups model associated with this grid, or null
	 * if this grid is associated with a {@link ListModel}
	 * or not associated with any list data model.
	 * @since 3.5.0
	 * @see #setModel(GroupsModel)
	 */
	@SuppressWarnings("unchecked")
	public <D, G, F> GroupsModel<D, G, F> getGroupsModel() {
		return _model instanceof GroupsListModel ?
			(GroupsModel)((GroupsListModel)_model).getGroupsModel(): null;
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
	public void setModel(ListModel<?> model) {
		if (model != null) {
			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
					if (_model instanceof GroupsListModel)
						_rows.getChildren().clear();
					
					resetDataLoader(); // Bug 3357641
				} else {
					if (_rows != null) _rows.getChildren().clear(); //Bug 1807414
					smartUpdate("model", true);
				}

				_model = model;
				initDataListener();
			}

			final Execution exec = Executions.getCurrent();
			final boolean defer = exec == null ? false : exec.getAttribute("zkoss.Grid.deferInitModel_"+getUuid()) != null;
			final boolean rod = evalRod();
			//Always syncModel because it is easier for user to enforce reload
			if (!defer || !rod) { //if attached and rod, defer the model sync
				getDataLoader().syncModel(-1, -1); //create rows if necessary
			} else if (inPagingMold()) { 
				//B30-2129667, B36-2782751, (ROD) exception when zul applyProperties
				//must update paginal totalSize or exception in setActivePage
				final Paginal pgi = getPaginal();
				pgi.setTotalSize(getDataLoader().getTotalSize());
			}
			if (!doSort(this))
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
			smartUpdate("model", false);
		}
		getDataLoader().updateModelInfo();
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
	public void setModel(GroupsModel<?, ?, ?> model) {
		if (evalRod()) {
			throw new UnsupportedOperationException("Not support GroupsModel in ROD yet!");
		}
		setModel((ListModel)(model != null ? GroupsListModel.toListModel(model): null));
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
	
	/**
	 * Sort the rows based on {@link Column#getSortDirection}.
	 * @return
	 */
	private static boolean doSort(Grid grid) {
		Columns cols = grid.getColumns();
		if (!grid.isAutosort() || cols == null) return false;
		for (Iterator it = cols.getChildren().iterator();
		it.hasNext();) {
			final Column hd = (Column)it.next();
			String dir = hd.getSortDirection();
			if (!"natural".equals(dir)) {
				hd.doSort("ascending".equals(dir));
				return true;
			}
		}
		return false;
	}

	/** Returns the renderer to render each row, or null if the default
	 * renderer is used.
	 */
	@SuppressWarnings("unchecked")
	public <T> RowRenderer<T> getRowRenderer() {
		return (RowRenderer)_renderer;
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
	public void setRowRenderer(RowRenderer<?> renderer) {
		if (_renderer != renderer) {
			_renderer = renderer;

			if (_model != null) {
				if ((renderer instanceof RowRendererExt)
				|| (_renderer instanceof RowRendererExt)) {
					//bug# 2388345, a new renderer that might new own Row, shall clean all Row first
					getRows().getChildren().clear();
					getDataLoader().syncModel(-1, -1); //we have to recreate all
				} else if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
					getDataLoader().syncModel(-1, -1); //we have to recreate all
				} else {
					//bug# 3039282, we need to resyncModel if not in a defer mode
					final Execution exec = Executions.getCurrent();
					final boolean defer = exec == null ? false : exec.getAttribute("zkoss.Grid.deferInitModel_"+getUuid()) != null;
					final boolean rod = evalRod();
					if (!defer || !rod)
						getDataLoader().syncModel(-1, -1);
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

	/** @deprecated As of release 5.0.8, use custom attributes (org.zkoss.zul.listbox.preloadSize) instead.
	 * Returns the number of rows to preload when receiving
	 * the rendering request from the client.
	 *
	 * <p>Default: 7.
	 *
	 * <p>It is used only if live data ({@link #setModel(ListModel)} and
	 * not paging ({@link #getPagingChild}.
	 *
	 * <p>Note: if the "pre-load-size" attribute of component is specified, it's prior to the original value.(@since 3.0.4)
	 * @since 2.4.1
	 */
	public int getPreloadSize() {
		final String size = (String) getAttribute("pre-load-size");
		return size != null ? Integer.parseInt(size) : _preloadsz;
	}
	/** @deprecated As of release 5.0.8, use custom attributes (org.zkoss.zul.listbox.preloadSize) instead.
	 * Sets the number of rows to preload when receiving
	 * the rendering request from the client.
	 * <p>It is used only if live data ({@link #setModel(ListModel)} and
	 * not paging ({@link #getPagingChild}.
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
			//no need to update client since paging done at server
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
			smartUpdate("innerWidth", innerWidth);
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
	
	/** Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 */
	public void onInitRender() {
		removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		doInitRenderer();
	}

	/**
	 * Handles a private event, onPagingInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 */
	public void onPagingInitRender() {
		removeAttribute(ATTR_ON_PAGING_INIT_RENDER_POSTED);
		doInitRenderer();
	}

	private void doInitRenderer() {
		final Renderer renderer = new Renderer();
		try {
			int pgsz, ofs;
			if (inPagingMold()) {
				pgsz = _pgi.getPageSize();
				ofs = _pgi.getActivePage() * pgsz;
			} else {
				pgsz = getDataLoader().getLimit();
				ofs = getDataLoader().getOffset();
				//we don't know # of visible rows, so a 'smart' guess
				//It is OK since client will send back request if not enough
			}
			final int cnt = _rows.getChildren().size() + getDataLoader().getOffset();
			if (ofs >= cnt) { //not possible; just in case
				ofs = cnt - pgsz;
				if (ofs < 0) ofs = 0;
			}

			int j = 0;
			int realOfs = ofs - getDataLoader().getOffset();
			if (realOfs < 0) realOfs = 0;
			boolean open = true;
			for (Row row = _rows.getChildren().size() <= realOfs ? null: (Row)_rows.getChildren().get(realOfs), nxt;
			j < pgsz && row != null; row = nxt) {
				nxt = (Row)row.getNextSibling();

				if (row.isVisible()
				&& (open || row instanceof Groupfoot || row instanceof Group)) {
					renderer.render(row); 
					++j;
				}
				if (row instanceof Group)
					open = ((Group) row).isOpen();
			}

		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null);// notify the grid when all of the row have been rendered. 		
	}
	
	private void postOnInitRender() {
		//20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
		}
	}

	private void postOnPagingInitRender() {
		if (getAttribute(ATTR_ON_PAGING_INIT_RENDER_POSTED) == null && 
				getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) { // B50-ZK-345
			setAttribute(ATTR_ON_PAGING_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onPagingInitRender", this, null);
		}
	}

	/** Handles when the list model's content changed.
	 */
	private void onListDataChange(ListDataEvent event) {
		//sort when add
		int type = event.getType();
		if ((type == ListDataEvent.INTERVAL_ADDED || 
				type == ListDataEvent.CONTENTS_CHANGED) && 
				!isIgnoreSortWhenChanged()) {
			doSort(this);
		} else {
			getDataLoader().doListDataChange(event);
			postOnInitRender(); // to improve performance
		}
	}

	/** Returns the label for the cell generated by the default renderer.
	 */
	private static Label newRenderLabel(String value) {
		final Label label =
			new Label(value != null && value.length() > 0 ? value: " ");
		label.setPre(true); //to make sure &nbsp; is generated, and then occupies some space
		return label;
	}

	/** Used to render row if _model is specified. */
	/*package*/ class Renderer {
		private final RowRenderer _renderer;
		private boolean _rendered, _ctrled;

		/*package*/ Renderer() {
			_renderer = (RowRenderer) getDataLoader().getRealRenderer();
		}
		/*package*/ @SuppressWarnings("unchecked")
		void render(Row row) throws Throwable {
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
				Object v = row.getAttribute("org.zkoss.zul.model.renderAs");
				if (v != null) //a new row is created to replace the existent one
					row = (Row)v;
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
	/** Renders all {@link Row} if not loaded yet,
	 * with {@link #getRowRenderer}.
	 */
	public void renderAll() {
		if (_model == null) return;

		_renderAll = true;
		getDataLoader().setLoadAll(_renderAll);

		final Renderer renderer = new Renderer();
		try {
			for (Row row = _rows.getChildren().size() <= 0 ? null: (Row)_rows.getChildren().get(0), nxt; row != null; row = nxt) {
				nxt = (Row)row.getNextSibling(); //retrieve first since it might be changed
				renderer.render(row);
			}
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
			smartUpdate("oddRowSclass", scls);
		}
	}

	/** Sets the mold to render this component.
	 *
	 * @param mold the mold. If null or empty, "default" is assumed.
	 * Allowed values: default, paging
	 * @see org.zkoss.zk.ui.metainfo.ComponentDefinition
	 */
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
				if (getModel() != null) {
					getDataLoader().syncModel(0, initRodSize()); //change offset back to 0
					postOnInitRender();
				}
				invalidate(); //paging mold -> non-paging mold
			} else if (inPagingMold()) { //change to paging
				if (_pgi != null) addPagingListener(_pgi);
				else newInternalPaging();
				_topPad = 0;
				_currentTop = 0;
				_currentLeft = 0;
				//enforce a page loading
				// B50-ZK-345: speed up onPagingImpl to surpass onInitRender
				Events.postEvent(10001, new PagingEvent("onPagingImpl", 
						(Component)_pgi, _pgi.getActivePage()));
				invalidate(); //non-paging mold -> paging mold
			}
		}
	}
	public String getZclass() {
		return _zclass == null ? "z-grid" : _zclass;
	}

	//-- Component --//
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (newChild instanceof Rows) {
			if (_rows != null && _rows != newChild)
				throw new UiException("Only one rows child is allowed: "+this+"\nNote: rows is created automatically if live data");
		} else if (newChild instanceof Columns) {
			if (_cols != null && _cols != newChild)
				throw new UiException("Only one columns child is allowed: "+this);
		} else if (newChild instanceof Frozen) {
			if (_frozen != null && _frozen != newChild)
				throw new UiException("Only one frozen child is allowed: "+this);
		} else if (newChild instanceof Paging) {
			if (_pgi != null)
				throw new UiException("External paging cannot coexist with child paging");
			if (_paging != null && _paging != newChild)
				throw new UiException("Only one paging is allowed: "+this);
			if (!inPagingMold())
				throw new UiException("The child paging is allowed only in the paging mold");
		} else if (newChild instanceof Foot) {
			if (_foot != null && _foot != newChild)
				throw new UiException("Only one foot child is allowed: "+this);
		} else if (!(newChild instanceof Auxhead)) {
			throw new UiException("Unsupported child for grid: "+newChild);
		}
 
		super.beforeChildAdded(newChild, refChild);
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Rows) {
			if (super.insertBefore(newChild, refChild)) {
				_rows = (Rows)newChild;
				return true;
			}
		} else if (newChild instanceof Columns) {
			if (super.insertBefore(newChild, refChild)) {
				_cols = (Columns)newChild;
				return true;
			}
		} else if (newChild instanceof Frozen) {
			if (super.insertBefore(newChild, refChild)) {
				_frozen = (Frozen)newChild;
				return true;
			}
		} else if (newChild instanceof Paging) {
			if (super.insertBefore(newChild, refChild)) {
				_pgi = _paging = (Paging)newChild;
				return true;
			}
		} else if (newChild instanceof Foot) {
			if (super.insertBefore(newChild, refChild)) {
				_foot = (Foot)newChild;
				return true;
			}
		} else {
			return super.insertBefore(newChild, refChild);
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
		else if (_frozen == child) _frozen = null;
		else if (_foot == child) _foot = null;
		else if (_paging == child) {
			_paging = null;
			if (_pgi == child) _pgi = null;
		}
		return true;
	}
	
	/*package*/ boolean evalRod() {
		return Utils.testAttribute(this, "org.zkoss.zul.grid.rod", false, true);
	}
	
	/** Returns whether to sort all of item when model or sort direction be changed.
	 * @since 5.0.7
	 */
	/*package*/ boolean isAutosort() {
		String attr = "org.zkoss.zul.grid.autoSort";
		Object val = getAttribute(attr, true);
		if (val == null)
			val = Library.getProperty(attr);
		return val instanceof Boolean ? ((Boolean)val).booleanValue():
			val != null ? "true".equals(val) || "ignore.change".equals(val): false;
	}
	
	/** 
	 * Returns the number of rows to preload when receiving the rendering
	 * request from the client.
	 * <p>
	 * Default: 7.
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 */
	private int preloadSize() {
		final String size = (String) getAttribute("pre-load-size");
		int sz = size != null ? Integer.parseInt(size) : _preloadsz;
		
		if ((sz = Utils.getIntAttribute(this, 
				"org.zkoss.zul.grid.preloadSize", sz, true)) < 0)
			throw new UiException("nonnegative is required: " + sz);
		return sz;
	}
	
	/** 
	 * Returns the number of rows rendered when the Grid first render.
	 *  <p>
	 * Default: 100.
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 */
	private int initRodSize() {
		int sz = Utils.getIntAttribute(this, "org.zkoss.zul.grid.initRodSize",
				INIT_LIMIT, true);
		if ((sz) < 0)
			throw new UiException("nonnegative is required: " + sz);
		return sz;
	}
	
	/** Returns whether to sort all of item when model or sort direction be changed.
	 * @since 5.0.7
	 */
	private boolean isIgnoreSortWhenChanged() {
		String attr = "org.zkoss.zul.grid.autoSort";
		Object val = getAttribute(attr, true);
		if (val == null)
			val = Library.getProperty(attr);
		return val == null ? true: "ignore.change".equals(val);
	}
	
	/*package*/ DataLoader getDataLoader() {
		if (_dataLoader == null) {
			_rod = evalRod();
			final String loadercls = (String) Library.getProperty("org.zkoss.zul.grid.DataLoader.class");
			try {
				_dataLoader = _rod && loadercls != null ? 
						(DataLoader) Classes.forNameByThread(loadercls).newInstance() :
						new GridDataLoader();
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
			_dataLoader.init(this, 0, initRodSize());
		}
		return _dataLoader;
	}

	//Cloneable//
	public Object clone() {
		final Grid clone = (Grid)super.clone();
		clone.init();
		
		// remove cached listeners
		clone._pgListener = null;
		clone._pgImpListener = null;
		
		//recreate the DataLoader 
		final int offset = clone.getDataLoader().getOffset(); 
		
		int cnt = 0;
		if (clone._rows != null) ++cnt;
		if (clone._cols != null) ++cnt;
		if (clone._foot != null) ++cnt;
		if (clone._frozen != null) ++cnt;
		if (clone._paging != null) ++cnt;
		if (cnt > 0) clone.afterUnmarshal(cnt);

		// after _pgi ready, and then getLimit() will work
		final int limit = clone.getDataLoader().getLimit();
		clone.resetDataLoader(false);
		clone.getDataLoader().init(clone, offset, limit);
		
		if (clone._model != null) {
			if (clone._model instanceof ComponentCloneListener) {
				final ListModel model = (ListModel) ((ComponentCloneListener) clone._model).willClone(clone);
				if (model != null)
					clone._model = model;
			}
			clone._dataListener = null;
			clone.initDataListener();
			clone.getDataLoader().setLoadAll(_renderAll);
		}
		
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
			} else if (child instanceof Paging) {
				_pgi = _paging = (Paging)child;
				addPagingListener(_pgi);
				if (--cnt == 0) break;
			} else if (child instanceof Frozen) {
				_frozen = (Frozen)child;
				if (--cnt == 0) break;
			} else if (child instanceof Foot) {
				_foot = (Foot)child;
				if (--cnt == 0) break;
			}
		}
	}

	
	
	public String getEmptyMessage() {
		return _emptyMessage;
	}
	
	public void setEmptyMessage(String emptyMessage) {
		if(!Objects.equals(emptyMessage, _emptyMessage)){
			_emptyMessage = emptyMessage;
			smartUpdate("emptyMessage",_emptyMessage);
		}
	}
	
	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		willSerialize(_model);
		Serializables.smartWrite(s, _model);
		willSerialize(_renderer);
		Serializables.smartWrite(s, _renderer);
		
		// keep the scrolling status after serialized
		if (_dataLoader != null) {
			s.writeInt(_dataLoader.getOffset());
			s.writeInt(_dataLoader.getLimit());
		} else {
			s.writeInt(0);
			s.writeInt(100);
		}		
	}
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_model = (ListModel)s.readObject();
		didDeserialize(_model);
		_renderer = (RowRenderer)s.readObject();
		didDeserialize(_renderer);

		init();
		afterUnmarshal(-1);
		
		int offset = s.readInt();
		int limit = s.readInt();
		resetDataLoader(false); // no need to reset, it will reset the old reference.
		getDataLoader().init(this, offset, limit);
		
		//TODO: how to marshal _pgi if _pgi != _paging
		//TODO: re-register event listener for onPaging

		if (_model != null) {
			initDataListener();
			getDataLoader().setLoadAll(_renderAll);
			
			// Map#Entry cannot be serialized, we have to restore them
			if (_model instanceof ListModelMap && _rows != null) {
				for (Component o : _rows.getChildren()) {
					Row item = (Row) o;
					item.setValue(_model.getElementAt(item.getIndex()));
				}
			}
		}
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "oddRowSclass", _scOddRow);
		
		if (_model != null)
			render(renderer, "model", true);

		if (!"100%".equals(_innerWidth))
			render(renderer, "innerWidth", _innerWidth);
		if (_currentTop != 0)
			renderer.render("_currentTop", _currentTop);
		if (_currentLeft != 0)
			renderer.render("_currentLeft", _currentLeft);

		renderer.render("_topPad", _topPad);
		
		renderer.render("emptyMessage", _emptyMessage);
		
		renderer.render("_totalSize", getDataLoader().getTotalSize());
		renderer.render("_offset", getDataLoader().getOffset());
		
		if (_rod) {
			if (((Cropper)getDataLoader()).isCropper())//bug #2936064 
					renderer.render("_grid$rod", true);
			int sz = initRodSize();
			if (sz != INIT_LIMIT)
				renderer.render("initRodSize", initRodSize());
		}
		
		if (_pgi != null && _pgi instanceof Component)
			renderer.render("paginal", _pgi);

	}
	/*package*/ boolean isRod() {
		return _rod;
	}
	public void sessionWillPassivate(Page page) {
		super.sessionWillPassivate(page);
		willPassivate(_model);
		willPassivate(_renderer);
	}
	public void sessionDidActivate(Page page) {
		super.sessionDidActivate(page);
		didActivate(_model);
		didActivate(_renderer);
	}

	//-- ComponentCtrl --//
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements Padding {
		//-- Padding --//
		public int getHeight() {
			return _topPad;
		}

		public void setHeight(int height) {
			_topPad = height;
		}
	}

	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals("onDataLoading")) {
			Events.postEvent(DataLoadingEvent.getDataLoadingEvent(request, preloadSize()));
		} else if (inPagingMold() && cmd.equals(ZulEvents.ON_PAGE_SIZE)) {
			final Map<String, Object> data = request.getData();
			final int oldsize = getPageSize();
			int size = AuRequests.getInt(data, "size", oldsize);
			if (size != oldsize) {
				int begin = getActivePage() * oldsize;
				int end = begin + oldsize;
				end = Math.min(getPaginal().getTotalSize(), end); 
				int sel = size > oldsize ? (end-1) : begin;
				int newpg = sel / size;
				setPageSize(size);
				setActivePage(newpg);
				// Bug: B50-3204965: onPageSize is not fired in autopaging scenario
				Events.postEvent(new PageSizeEvent(cmd, this, pgi(), size));
			}
		} else if (cmd.equals("onScrollPos")) {
			final Map<String, Object> data = request.getData();
			_currentTop = AuRequests.getInt(data, "top", 0);
			_currentLeft = AuRequests.getInt(data, "left", 0);
		} else if (cmd.equals("onTopPad")) {
			_topPad = AuRequests.getInt(request.getData(), "topPad", 0);
		} else if (cmd.equals("onInnerWidth")) {
			final String width = AuRequests.getInnerWidth(request);
			_innerWidth = width == null ? "100%": width;
		} else if (cmd.equals(Events.ON_RENDER)) {
			final RenderEvent<Row> event = RenderEvent.getRenderEvent(request);
			final Set<Row> items = event.getItems();

			int cnt = items.size();
			if (cnt == 0) return; //nothing to do

			cnt = 20 - cnt;
			if (cnt > 0 && _preloadsz > 0) { //Feature 1740072: pre-load
				if (cnt > _preloadsz) cnt = _preloadsz;

				//1. locate the first item found in items
				final List<Row> toload = new LinkedList<Row>();
				Iterator<Component> it = getRows().getChildren().iterator();
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
					for (Iterator<Row> e = toload.iterator();
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
			renderItems(items);
		} else
			super.service(request, everError);
	}
	/** An iterator used by _heads.
	 */
	private class Iter implements Iterator<Component> {
		private final ListIterator<Component> _it = getChildren().listIterator();

		public boolean hasNext() {
			while (_it.hasNext()) {
				Component o = _it.next();
				if (o instanceof Columns || o instanceof Auxhead) {
					_it.previous();
					return true;
				}
			}
			return false;
		}
		public Component next() {
			for (;;) {
				Component o = _it.next();
				if (o instanceof Columns || o instanceof Auxhead)
					return o;
			}
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
