/* Tree.java

	Purpose:

	Description:

	History:
		Wed Jul  6 18:51:33     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.lang.reflect.Method;
import java.io.IOException;
import java.io.Writer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

import org.zkoss.io.Serializables;
import org.zkoss.lang.Exceptions;
import static org.zkoss.lang.Generics.*;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CloneableEventListener;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.PageSizeEvent;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.RenderEvent;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Sortable;
import org.zkoss.zul.ext.TreeOpenableModel;
import org.zkoss.zul.ext.TreeSelectableModel;
import org.zkoss.zul.impl.MeshElement;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 *  A container which can be used to hold a tabular
 * or hierarchical set of rows of elements.
 *
 * <p>Event:
 * <ol>
 * <li>{@link org.zkoss.zk.ui.event.SelectEvent} is sent when user changes
 * the selection.</li>
 * <li>onAfterRender is sent when the model's data has been rendered.(since 5.0.4)</li>
 * </ol>
 *
 * <p>Default {@link #getZclass}: z-tree, and an other option is z-dottree. (since 3.5.0)
 *
 * <p>Custom Attributes:
 * <dl>
 * <dt>org.zkoss.zul.tree.rightSelect</dt>
 * <dd>Specifies whether the selection shall be toggled when user right clicks on
 * item, if the checkmark ({@link #isCheckmark}) is enabled.</br>
 * Notice that you could specify this attribute in any of its ancestor's attributes.
 * It will be inherited.</dd>
 * <dt>org.zkoss.zul.tree.autoSort</dt>.(since 5.0.7) 
 * <dd>Specifies whether to sort the model when the following cases:</br>
 * <ol>
 * <li>{@link #setModel} is called and {@link Treecol#setSortDirection} is set.</li>
 * <li>{@link Treecol#setSortDirection} is called.</li>
 * <li>Model receives {@link TreeDataEvent} and {@link Treecol#setSortDirection} is set.</li>
 * </ol>
 * If you want to ignore sort when receiving {@link TreeDataEvent}, 
 * you can specifies the value as "ignore.change".</br>
 * Notice that you could specify this attribute in any of its ancestor's attributes.
 * It will be inherited.</dd>
 * </dl>
 * 
 * <br/>
 * [Since 6.0.0]
 * <p>To retrieve what are selected in Tree with a {@link TreeSelectableModel},
 * you shall use {@link TreeSelectableModel#isPathSelected(int[])}
 * to check whether the current path is selected in {@link TreeSelectableModel}
 * rather than using {@link Tree#getSelectedItems()}. That is, you shall operate on
 * the item of the {@link TreeSelectableModel} rather than on the {@link Treeitem}
 * of the {@link Tree} if you use the {@link TreeSelectableModel} and {@link TreeModel}.</p>
 *
 * <pre><code>
 * TreeSelectableModel selModel = ((TreeSelectableModel)getModel());
 * int[][] paths = selModel.getSelectionPaths();
 * List<E> selected = new ArrayList<E>();
 * AbstractTreeModel model = (AbstractTreeModel) selModel;
 * for (int i = 0; i < paths.length; i++) {
 * 		selected.add(model.getChild(paths[i]));
 * }
 * </code></pre>
 * 
 * <br/>
 * [Since 6.0.0]
 * <p> If the TreeModel in Tree implements a {@link TreeSelectableModel}, the
 * multiple selections status is applied from the method of
 * {@link TreeSelectableModel#isMultiple()}
 * </p>
 * <pre><code>
 * DefaultTreeModel selModel = new DefaultTreeModel(treeNode);
 * selModel.setMultiple(true);
 * tree.setModel(selModel);
 * </code></pre>
 * 
 * <br/>
 * [Since 6.0.0]
 * <p>To retrieve what are opened nodes in Tree with a {@link TreeOpenableModel},
 * you shall use {@link TreeOpenableModel#isPathOpened(int[])}
 * to check whether the current path is opened in {@link TreeOpenableModel}
 * rather than using {@link Treeitem#isOpen()}. That is, you shall operate on
 * the item of the {@link TreeOpenableModel} rather than on the {@link Treeitem}
 * of the {@link Tree} if you use the {@link TreeOpenableModel} and {@link TreeModel}.</p>
 *
 * <pre><code>
 * TreeOpenableModel openModel = ((TreeOpenableModel)getModel());
 * int[][] paths = openModel.getOpenPaths();
 * List<E> opened = new ArrayList<E>();
 * AbstractTreeModel model = (AbstractTreeModel) openModel;
 * for (int i = 0; i < paths.length; i++) {
 * 		opened.add(model.getChild(paths[i]));
 * }
 * </code></pre>
 * 
 * </br/>
 * [Since 7.0.0] (EE version only)
 * 
 * <dt>org.zkoss.zul.tree.initRodSize</dt>. 
 * <dd>Specifies the number of items rendered when the Tree first render.
 * 
 * <dt>org.zkoss.zul.tree.preloadSize</dt>. 
 * <dd>Specifies the number of items to preload when receiving
 * the rendering request from the client.
 * <p>It is used only if live data ({@link #setModel(TreeModel)} and
 * not paging ({@link #getPagingChild}).</dd>
 * 
 * 
 * @author tomyeh
 */
public class Tree extends MeshElement {
	private static final Log log = Log.lookup(Tree.class);
	private static final String ATTR_ON_INIT_RENDER_POSTED =
		"org.zkoss.zul.Tree.onInitLaterPosted";

	private transient Treecols _treecols;
	private transient Treefoot _treefoot;
	private transient Treechildren _treechildren;
	/** A list of selected items. */
	private transient Set<Treeitem> _selItems;
	/** The first selected item. */
	private transient Treeitem _sel;
	private transient Collection<Component> _heads;
	private int _rows = 0;
	/** The name. */
	private String _name;
	private boolean _multiple, _checkmark;
	private boolean _vflex;
	private String _innerWidth = "100%";

	private transient TreeModel<Object> _model;
	private transient TreeitemRenderer<?> _renderer;
	private transient TreeDataListener _dataListener;

	private transient Paginal _pgi;
	private String _nonselTags; //since 5.0.5 for non-selectable tags
	
	/** The paging controller, used only if mold = "paging" and user
	 * doesn't assign a controller via {@link #setPaginal}.
	 * If exists, it is the last child
	 */
	private transient Paging _paging;
	private EventListener<PagingEvent> _pgListener, _pgImpListener;
	private EventListener<Event> _modelInitListener;

	private int _currentTop = 0; // since 5.0.8 scroll position
	private int _currentLeft = 0;
	
	private int _anchorTop = 0 ; //since 5.0.11/6.0.0 anchor position
	private int _anchorLeft = 0 ;
	
	private static final int INIT_LIMIT = -1; // since 7.0.0
	private int _preloadsz = 50; // since 7.0.0
	
	static {
		addClientEvent(Tree.class, Events.ON_RENDER, CE_DUPLICATE_IGNORE|CE_IMPORTANT|CE_NON_DEFERRABLE);
		addClientEvent(Tree.class, "onInnerWidth", CE_DUPLICATE_IGNORE|CE_IMPORTANT);
		addClientEvent(Tree.class, Events.ON_SELECT, CE_DUPLICATE_IGNORE|CE_IMPORTANT);
		addClientEvent(Tree.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Tree.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
		addClientEvent(Tree.class, ZulEvents.ON_PAGE_SIZE, CE_DUPLICATE_IGNORE|CE_IMPORTANT|CE_NON_DEFERRABLE); //since 5.0.2
		addClientEvent(Tree.class, "onScrollPos", CE_DUPLICATE_IGNORE | CE_IMPORTANT); //since 5.0.4
		addClientEvent(Tree.class, "onAnchorPos", CE_DUPLICATE_IGNORE | CE_IMPORTANT); //since 5.0.11 / 6.0.0
		
	}

	public Tree() {
		init();
	}
	private void init() {
		_selItems = new LinkedHashSet<Treeitem>(4);
		_heads = new AbstractCollection<Component>() {
			public int size() {
				int sz = getChildren().size();
				if (_treechildren != null) --sz;
				if (_treefoot != null) --sz;
				if (_paging != null) --sz;
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
			//prepare a right moment to init Tree(must be as early as possible)
			addEventListener("onInitModel", _modelInitListener = new ModelInitListener());
			Events.postEvent(20000, new Event("onInitModel", this));
		}
	}
	
	private class ModelInitListener implements SerializableEventListener<Event>,
			CloneableEventListener<Event> {
		public void onEvent(Event event) throws Exception {
			if (_modelInitListener != null) {
				Tree.this
						.removeEventListener("onInitModel", _modelInitListener);
				_modelInitListener = null;
			}
			if (_model != null) { //rows not created yet
				//ZK-1007 Left the job to onInitRenderer if exist
				if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) { 
					if (_treechildren == null) {
						renderTree();
					} else{
						setModel(_model);
					}
				}
			}
		}
		
		
		public Object willClone(Component comp) {
			return null; // skip to clone
		}
	}
	void addVisibleItemCount(int count) {
		if (inPagingMold()) {
			Paginal pgi = getPaginal();
			pgi.setTotalSize(pgi.getTotalSize() + count);
			invalidate(); //the set of visible items might change
		}
	}
	/**
	 * Returns a map of current visible item.
	 * @since 3.0.7
	 */
	Map<Treeitem, Boolean> getVisibleItems() {
		Map<Treeitem, Boolean> map = new HashMap<Treeitem, Boolean>();
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
	private boolean getVisibleItemsDFS(List<Component> list, Map<Treeitem, Boolean> map, int[] data) {
		for (Component cmp: list) {
			if (cmp instanceof Treeitem) {
				if (data[4] >= data[0]) return false; // full
				final Treeitem item = (Treeitem) cmp;
				if (item.isRealVisible()) {
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
				invalidate(); //paging mold -> non-paging mold
			} else if (inPagingMold()) { //change to paging
				if (_pgi != null) addPagingListener(_pgi);
				else newInternalPaging();
				setFixedLayout(true);
				_currentTop = 0;
				_currentLeft = 0;
				_anchorTop = 0;
				_anchorLeft = 0 ;
				invalidate(); //non-paging mold -> paging mold
			}
		}
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
	 * (see {@link #getPagingChild}).
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
		paging.setTotalSize(getVisibleItemCount());
		paging.setParent(this);
		if (_pgi != null)
			addPagingListener(_pgi);
	}
	private class PGListener implements SerializableEventListener<PagingEvent>,
			CloneableEventListener<PagingEvent> {
		public void onEvent(PagingEvent event) {
			//Bug ZK-1622: reset anchor position after changing page
			_anchorTop = 0;
			_anchorLeft = 0;
			Events.postEvent(
				new PagingEvent(event.getName(),
					Tree.this, event.getPageable(), event.getActivePage()));
		}
		
		public Object willClone(Component comp) {
			return null; // skip to clone
		}
	}
	private class PGImpListener implements SerializableEventListener<PagingEvent>,
			CloneableEventListener<PagingEvent> {
		public void onEvent(PagingEvent event) {
			if (inPagingMold()) {
				if (WebApps.getFeature("ee") && getModel() != null) {
					int ap =  event.getActivePage();
					int size = Tree.this.getPaginal().getPageSize();
					int start = ap * size;
					int end = start + size;
					int i = 0;
					final Renderer renderer = new Renderer();
					try {							
						for (Treeitem ti : new ArrayList<Treeitem>(Tree.this.getItems())) {
							if (i < start) {
								i++;
								continue;
							}
							if (i >= end) {
								break;
							}
							if (!ti.isRendered()) {
									ti.getChildren().clear();
									Treechildren parent = (Treechildren) ti.getParent();
									Object childNode = Tree.this.getAssociatedNode(ti, Tree.this);
									renderChildren0(renderer, parent, ti, childNode, i);
							}
								
							i++;
						}
					} catch (Throwable ex) {
						renderer.doCatch(ex);
					} finally {
						renderer.doFinally();
					}
				}
				invalidate();
			}
		}

		
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
	/** Returns the page size, a.k.a., the number items per page.
	 * @exception IllegalStateException if {@link #getPaginal} returns null,
	 * i.e., mold is not "paging" and no external controller is specified.
	 * @since 2.4.1
	 */
	public int getPageSize() {
		return inPagingMold() ? pgi().getPageSize(): 0;
	}
	/** Sets the page size, a.k.a., the number items per page.
	 * <p>Note: mold is "paging" and no external controller is specified.
	 * @since 2.4.1
	 */
	public void setPageSize(int pgsz) throws WrongValueException {
		if (pgsz < 0 || !inPagingMold()) return;
		pgi().setPageSize(pgsz);
	}

	protected Paginal pgi() {
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

	/** Returns the treecols that this tree owns (might null).
	 */
	public Treecols getTreecols() {
		return _treecols;
	}
	/** Returns the treefoot that this tree owns (might null).
	 */
	public Treefoot getTreefoot() {
		return _treefoot;
	}
	/** Returns the treechildren that this tree owns (might null).
	 */
	public Treechildren getTreechildren() {
		return _treechildren;
	}
	/** Returns a collection of heads, including {@link #getTreecols}
	 * and auxiliary heads ({@link Auxhead}) (never null).
	 *
	 * @since 3.0.0
	 */
	public Collection<Component> getHeads() {
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
			smartUpdate("rows", _rows);
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
			smartUpdate("name", name);
		}
	}

	/** Sets a list of HTML tag names that shall <i>not</i> cause the tree item
	 * being selected if they are clicked.
	 * <p>Default: null (it means button, input, textarea and a). If you want
	 * to select no matter which tag is clicked, please specify an empty string.
	 * @param tags a list of HTML tag names that will <i>not</i> cause the tree item
	 * being selected if clicked. Specify null to use the default and "" to
	 * indicate none.
	 * @since 5.0.5
	 */
	public void setNonselectableTags(String tags) {
		if (!Objects.equals(_nonselTags, tags)) {
			_nonselTags = tags;
			smartUpdate("nonselectableTags", tags);
		}
	}
	/** Returns a list of HTML tag names that shall <i>not</i> cause the tree item
	 * being selected if they are clicked.
	 * <p>Refer to {@link #setNonselectableTags} for details.
	 * @since 5.0.5
	 */
	public String getNonselectableTags() {
		return _nonselTags;
	}

	/** Returns whether the check mark shall be displayed in front
	 * of each item.
	 * <p>Default: false.
	 */
	public boolean isCheckmark() {
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
			smartUpdate("checkmark", checkmark);
		}
	}

	/** Returns whether to grow and shrink vertical to fit their given space,
	 * so called vertical flexibility.
	 *
	 * <p>Note: this attribute is ignored if {@link #setRows} is specified
	 *
	 * <p>Default: false.
	 */
	public boolean isVflex() {
		return _vflex;
	}
	/** Sets whether to grow and shrink vertical to fit their given space,
	 * so called vertical flexibility.
	 *
	 * <p>Note: this attribute is ignored if {@link #setRows} is specified
	 */
	public void setVflex(boolean vflex) {
		if (_vflex != vflex) {
			_vflex = vflex;
			smartUpdate("vflex", _vflex);
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

	/** Returns the seltype.
	 * <p>Default: "single".
	 */
	public String getSeltype() {
		return _multiple ? "multiple": "single";
	}
	/** Sets the seltype.
	 * "single","multiple" is supported.
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
	 * <p>Notice that, if a model is assigned, it will change the model's
	 * state (by {@link TreeSelectableModel#setMultiple}).
	 */
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			if (!_multiple && _selItems.size() > 1) {
				final Treeitem item = getSelectedItem();
				for (Iterator<Treeitem> it = _selItems.iterator(); it.hasNext();) {
					final Treeitem ti = it.next();
					if (ti != item) {
						ti.setSelectedDirectly(false);
						it.remove();
					}
				}
				//No need to update selId because z.multiple will do the job
			}
			if (_model != null)
				((TreeSelectableModel)_model).setMultiple(multiple);
			smartUpdate("multiple", _multiple);
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
		if (item.isRealVisible() && item.getTree() == this && isVisible()) {
			int index = getVisibleIndexOfItem(item);
			if (index != -1) {
				final Paginal pgi = getPaginal();
				int pg = index / pgi.getPageSize();
				if (pg != getActivePage())
					setActivePage(pg);
			}

		}
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
		if (item.isRealVisible()) {
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
				if (parent.isRealVisible()) {
					parent.setOpen(true);
					int cnt = getVisibleIndexOfItem0(parent, false);
					if (cnt == -1) return -1;
					count += cnt;
				} else return -1; // invisible item
			}
		}
		return count;
	}

	// used by Treechildren
	public void smartUpdate(String attr, Object value) {
		super.smartUpdate(attr, value);
	}
	/** Returns a readonly list of all descending {@link Treeitem}
	 * (children's children and so on).
	 *
	 * <p>Note: the performance of the size method of returned collection
	 * is no good.
	 */
	public Collection<Treeitem> getItems() {
		if (_treechildren != null)
			return _treechildren.getItems();
		return Collections.emptyList();
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
				for (Treeitem ti : _selItems)
					ti.setSelectedDirectly(false);
				_selItems.clear();

				_sel = item;
				item.setSelectedDirectly(true);
				_selItems.add(item);

				smartUpdate("selectedItem", item);
			}
			if (inPagingMold())
				setActivePage(item);
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
				if(_sel == null)
					_sel = _selItems.iterator().next();
				smartUpdateSelection();
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
				
				if(_sel == item) //bug fix:3131173 
					_sel = _selItems.size() > 0 ? _selItems.iterator().next() : null;
								
				smartUpdateSelection();
			}
		}
	}
	/** Note: we have to update all selection at once, since addItemToSelection
	 * and removeItemFromSelection might be called interchangeably.
	 */
	private void smartUpdateSelection() {
		final StringBuffer sb = new StringBuffer(80);
		for (Treeitem item : _selItems) {
			if (sb.length() > 0) sb.append(',');
			sb.append(item.getUuid());
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
	/** Clears the selection.
	 */
	public void clearSelection() {
		if (!_selItems.isEmpty()) {
			for (Treeitem item : _selItems)
				item.setSelectedDirectly(false);
			_selItems.clear();
			_sel = null;
			smartUpdate("selectedItem", null);
		}
	}
	/** Selects all items.
	 */
	public void selectAll() {
		if (!_multiple)
			throw new UiException("Appliable only to the multiple seltype: "+this);

		//we don't invoke getItemCount first because it is slow!
		boolean first = true;
		for (Treeitem item : getItems()) {
			if (!item.isSelected()) {
				_selItems.add(item);
				item.setSelectedDirectly(true);
			}
			if (first) {
				_sel = item;
				first = false;
			}
		}
		smartUpdate("selectAll", true);
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
	public Set<Treeitem> getSelectedItems() {
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
		if (_treechildren != null)
			_treechildren.getChildren().clear();
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-tree" : _zclass;
	}
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (newChild instanceof Treecols) {
			if (_treecols != null && _treecols != newChild)
				throw new UiException("Only one treecols is allowed: "+this);
		} else if (newChild instanceof Treefoot) {
			if (_treefoot != null && _treefoot != newChild)
				throw new UiException("Only one treefoot is allowed: "+this);
		} else if (newChild instanceof Treechildren) {
			if (_treechildren != null && _treechildren != newChild)
				throw new UiException("Only one treechildren is allowed: "+this);
		} else if (newChild instanceof Paging) {
			if (_paging != null && _paging != newChild)
				throw new UiException("Only one paging is allowed: "+this);
			if (_pgi != null)
				throw new UiException("External paging cannot coexist with child paging");
			if (!inPagingMold())
				throw new UiException("The child paging is allowed only in the paging mold");
		} else if (!(newChild instanceof Auxhead)) {
			throw new UiException("Unsupported newChild: "+newChild);
		}
		super.beforeChildAdded(newChild, refChild);
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Treecols) {
			if (super.insertBefore(newChild, refChild)) {
				_treecols = (Treecols)newChild;
				return true;
			}
		} else if (newChild instanceof Treefoot) {
			refChild = _paging; //the last two: listfoot and paging
			if (super.insertBefore(newChild, refChild)) {
				_treefoot = (Treefoot)newChild;
				return true;
			}
		} else if (newChild instanceof Treechildren) {
			if (super.insertBefore(newChild, refChild)) {
				_treechildren = (Treechildren)newChild;
				fixSelectedSet();
				return true;
			}
		} else if (newChild instanceof Paging) {
			refChild = null; //the last: paging
			if (super.insertBefore(newChild, refChild)) {
				_pgi = _paging = (Paging)newChild;
				return true;
			}
		} else { //Auxhead
			return super.insertBefore(newChild, refChild);
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
			if (fixSel && !_multiple)
				_sel = null;
		}
		onTreechildrenRemoved(item.getTreechildren());
		if (fixSel) fixSelected();
	}
	/** Called by {@link Treechildren} when is added to a tree. */
	/*package*/ void onTreechildrenAdded(Treechildren tchs) {
		if (tchs == null || tchs.getParent() == this)
			return; //already being processed by insertBefore

		//main the selected status
		for (Treeitem item : tchs.getItems())
			fixNewChild(item);
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
			}
		}
	}
	/** Called by {@link Treechildren} when is removed from a tree. */
	/*package*/ void onTreechildrenRemoved(Treechildren tchs) {
		if (tchs == null || tchs.getParent() == this)
			return; //already being processed by onChildRemoved

		//main the selected status
		boolean fixSel = false;
		for (Treeitem item : tchs.getItems()) {
			if (item.isSelected()) {
				_selItems.remove(item);
				if (_sel == item) {
					if (!_multiple) {
						_sel = null;
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
		if (child instanceof Treechildren)
			addVisibleItemCount(((Treechildren) child).getVisibleItemCount());
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
			addVisibleItemCount(-((Treechildren) child).getVisibleItemCount());
		} else if (_paging == child) {
			_paging = null;
			if (_pgi == child) _pgi = null;
		}
		super.onChildRemoved(child);
	}

	/** Fixes all info about the selected status. */
	private void fixSelectedSet() {
		_sel = null; _selItems.clear();
		for (Treeitem item : getItems()) {
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
			sel = _selItems.iterator().next();
		case 0:
			break;
		default:
			for (Treeitem item : getItems()) {
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

	//Cloneable//
	@SuppressWarnings("unchecked")
	public Object clone() {
		int cntSel = _selItems.size();

		final Tree clone = (Tree)super.clone();
		clone.init();
		
		// remove cached listeners
		clone._pgListener = null;
		clone._pgImpListener = null;
		
		int cnt = 0;
		if (_treecols != null) ++cnt;
		if (_treefoot != null) ++cnt;
		if (_treechildren != null) ++cnt;
		if (_paging != null) ++cnt;
		if (cnt > 0 || cntSel > 0) clone.afterUnmarshal(cnt, cntSel);
		if(clone._model != null){
			if (clone._model instanceof ComponentCloneListener) {
				final TreeModel<Object> model = (TreeModel<Object>) 
					((ComponentCloneListener) clone._model).willClone(clone);
				if (model != null)
					clone._model = model;
			}
			clone._dataListener = null;
			clone.initDataListener();

			// As the bug in B30-1892446.zul, the component clone won't
			// clone the posted event, so we need to remove the attributes here.
			clone.removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		}
		return clone;
	}
	/** @param cnt # of children that need special handling (used for optimization).
	 * -1 means process all of them
	 * @param cntSel # of selected items
	 */
	private void afterUnmarshal(int cnt, int cntSel) {
		if (cnt != 0) {
			for (Component child : getChildren()) {
				if (child instanceof Treecols) {
					_treecols = (Treecols)child;
					if (--cnt == 0) break;
				} else if (child instanceof Treefoot) {
					_treefoot = (Treefoot)child;
					if (--cnt == 0) break;
				} else if (child instanceof Treechildren) {
					_treechildren = (Treechildren)child;
					if (--cnt == 0) break;
				} else if (child instanceof Paging) {
					_pgi = _paging = (Paging)child;
					addPagingListener(_pgi);
					if (--cnt == 0) break;
				}
			}
		}

		_sel = null;
		_selItems.clear();
		if (cntSel != 0) {
			for (Treeitem ti : getItems()) {
				if (ti.isSelected()) {
					if (_sel == null) _sel = ti;
					_selItems.add(ti);
					if (--cntSel == 0) break;
				}
			}
		}
	}

	//-- Serializable --//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		willSerialize(_model);
		Serializables.smartWrite(s, _model);
		willSerialize(_renderer);
		Serializables.smartWrite(s, _renderer);
	}
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_model = (TreeModel)s.readObject();
		didDeserialize(_model);
		_renderer = (TreeitemRenderer)s.readObject();
		didDeserialize(_renderer);

		init();
		afterUnmarshal(-1, -1);

		if (_model != null) initDataListener();
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
	/**
	 * Handles when the tree model's content changed
	 */
	private void onTreeDataChange(TreeDataEvent event){
		final int type = event.getType();
		final int[] path = event.getPath();
		final Component target = path != null ? getChildByPath(path): null;
		switch (type) {
		case TreeDataEvent.STRUCTURE_CHANGED:
			renderTree();

			if (_model instanceof Sortable) {
				final Sortable<Object> smodel = cast(_model);
				final List<Treecol> cols = cast(_treecols.getChildren());
				boolean found = false;
				for (final Treecol col : cols) {
					if (found) {
						col.setSortDirection("natural");
					} else {
						Comparator<Object> cmpr = cast(col.getSortAscending());
						String dir = smodel.getSortDirection(cmpr);
						found = !"natural".equals(dir);
						if (!found) {
							cmpr = cast(col.getSortDescending());
							dir = smodel.getSortDirection(cmpr);
							found = !"natural".equals(dir);
						}
						col.setSortDirection(dir);
					}
				}
			}
			return;
		case TreeDataEvent.SELECTION_CHANGED:
			if (target instanceof Treeitem)
				((Treeitem)target).setSelected(
					((TreeSelectableModel)_model).isPathSelected(path));
			return;
		case TreeDataEvent.OPEN_CHANGED:
			if (_model instanceof TreeOpenableModel) {
				if (target instanceof Treeitem)
					((Treeitem)target).setOpen(
						((TreeOpenableModel)_model).isPathOpened(path));
			}
			return;
		case TreeDataEvent.MULTIPLE_CHANGED:
			setMultiple(((TreeSelectableModel)_model).isMultiple());
			return;
		}

		/*
		 * Loop through indexes array
		 * if INTERVAL_REMOVED, from end to beginning
		 *
		 * 2008/02/12 --- issue: [ 1884112 ]
		 * When getChildByNode returns null, do nothing
		 */
		if(target != null){
			Object node = _model.getChild(path);
			int indexFrom = event.getIndexFrom();
			int indexTo = event.getIndexTo();
			if ((type == TreeDataEvent.INTERVAL_ADDED || 
					type == TreeDataEvent.CONTENTS_CHANGED) && 
					!isIgnoreSortWhenChanged()) {
				doSort(this);
			}
			switch (type) {
			case TreeDataEvent.INTERVAL_ADDED:
				for(int i=indexFrom;i<=indexTo;i++)
					onTreeDataInsert(target,node,i);
				break;
			case TreeDataEvent.INTERVAL_REMOVED:
				for(int i=indexTo;i>=indexFrom;i--)
					onTreeDataRemoved(target,node,i);
				break;
			case TreeDataEvent.CONTENTS_CHANGED:
				for(int i=indexFrom;i<=indexTo;i++)
					onTreeDataContentChange(target,node,i);
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
	private void onTreeDataInsert(Component parent, Object node, int index) {
		/*
		 * Find the sibling to insertBefore; if there is no sibling or new item
		 * is inserted at end.
		 */
		Treeitem newTi = newUnloadedItem();
		Treechildren tc = treechildrenOf(parent);

		//B50-ZK-721
		if (!(parent instanceof Treeitem) || ((Treeitem) parent).isLoaded()) {
			List<Component> siblings = tc.getChildren();
			// if there is no sibling or new item is inserted at end.
			tc.insertBefore(newTi,
					// Note: we don't use index >= size(); reason: it detects bug
					siblings.isEmpty() || index == siblings.size() ? null
							: (Treeitem) siblings.get(index));
			renderChangedItem(newTi, _model.getChild(node, index));
		}
	}

	/*
	 * Handle event that child is removed
	 */
	private void onTreeDataRemoved(Component parent,Object node, int index){
		final Treechildren tc = treechildrenOf(parent);
		final List<Component> items = tc.getChildren();
		if (items.size() > index) {
			((Treeitem) items.get(index)).detach();
		} else if (!(parent instanceof Treeitem) || ((Treeitem) parent).isLoaded()) {
			tc.detach();
		}
	}

	/*
	 * Handle event that child's content is changed
	 */
	private void onTreeDataContentChange(Component parent,Object node, int index){
		List<Component> items = treechildrenOf(parent).getChildren();

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
	 * Return the Tree or Treeitem component by a given associated node in model,
	 * or null if the treeitem is not instantiated (i.e., rendered) yet.
	 * It returns this tree if the given node is the root node
	 * (i.e., {@link TreeModel#getRoot}).
	 * @since 3.0.0
	 * @exception IllegalStateException if no model is assigned ({@link #setModel}).
	 * @see #renderItemByNode
	 */
	protected Component getChildByNode(Object node) {
		if (_model == null)
			throw new IllegalStateException("model required");
		
		final Object root = _model.getRoot();
		if (Objects.equals(root, node))
			return this;
		
		return getChildByPath(_model.getPath(node));
	}
	/**
	 * Return the Tree or Treeitem component by a path, or null if corresponding
	 * Treeitem is not instantiated (i.e., rendered) yet. It returns this tree 
	 * if the given node is the root node. (i.e., {@link TreeModel#getRoot}).
	 * @since 6.0.0
	 */
	protected Component getChildByPath(int[] path) {
		if (path.length == 0)
			return this; // return Tree
		
		Treeitem item = getChildTreeitem(getTreechildren(), path[0]);
		for (int j = 1; j < path.length && item != null; j++)
			item = getChildTreeitem(item.getTreechildren(), path[j]);
		
		return item;
	}
	private static Treeitem getChildTreeitem(Treechildren tc, int i) {
		if (tc == null)
			return null;
		List<Component> cs = tc.getChildren();
		return i < 0 || i >= cs.size() ? null : (Treeitem) cs.get(i);
	}
	/*
	 * Initial Tree data listener
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
	 * @param model the tree model to associate, or null to dissociate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.0.0
	 */
	public void setModel(TreeModel<?> model) {
		if (model != null) {
			if (!(model instanceof TreeSelectableModel))
				throw new UiException(model.getClass() + " must implement "+TreeSelectableModel.class);

			if (_model != model) {
				if (_model != null) {
					_model.removeTreeDataListener(_dataListener);
				} else {
					if (_treechildren != null) _treechildren.detach();
						//don't call getItems().clear(), since it readonly
					//bug# 3095453: tree can't expand if model is set in button onClick
					smartUpdate("model", true);
				}

				setModelDirectly(model);
				initDataListener();
			}
			doSort(this);
			postOnInitRender();
		} else if (_model != null) {
			_model.removeTreeDataListener(_dataListener);
			_model = null;
			if (_treechildren != null) _treechildren.detach();
				//don't call getItems().clear(), since it readonly
			//bug# 3095453: tree can't expand if model is set in button onClick
			smartUpdate("model", false);
		}
	}
	@SuppressWarnings("unchecked")
	private final void setModelDirectly(TreeModel model) {
		_model = model;
	}

	/** Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 * @since 6.0.0
	 */
	public void onInitRender() {
		removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		renderTree();
	}
	private void postOnInitRender() {
		//20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
		}
	}

	//--TreeModel dependent codes--//
	/** Returns the list model associated with this tree, or null
	 * if this tree is not associated with any tree data model.
	 * @return the list model associated with this tree
	 * @since 3.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> TreeModel<T> getModel(){
		return (TreeModel)_model;
	}

	private static boolean doSort(Tree tree) {
		Treecols cols = tree.getTreecols();
		if (!tree.isAutosort() || cols == null) return false;
		for (Component c : cols.getChildren()) {
			final Treecol hd = (Treecol) c;
			String dir = hd.getSortDirection();
			if (!"natural".equals(dir)) {
				hd.doSort("ascending".equals(dir));
				return true;
			}
		}
		return false;
	}
	
	/** Sets the renderer which is used to render each item
	 * if {@link #getModel} is not null.
	 *
	 * <p>Note: changing a render will not cause the tree to re-render.
	 * If you want it to re-render, you could assign the same model again
	 * (i.e., setModel(getModel())), or fire an {@link TreeDataEvent} event.
	 *
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize with the model
	 * @since 5.0.6
	 */
	public void setItemRenderer(TreeitemRenderer<?> renderer){
		if (_renderer != renderer) {
			_renderer = renderer;
			if (_model != null)
				postOnInitRender();
		}
	}

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 * @since 6.5.2
	 */
	public void setItemRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setItemRenderer((TreeitemRenderer) Classes
					.newInstanceByThread(clsnm));
	}


	/** Returns the renderer to render each item, or null if the default
	 * renderer is used.
	 * @return the renderer to render each item, or null if the default
	 * @since 5.0.6
	 */
	@SuppressWarnings("unchecked")
	public <T> TreeitemRenderer<T> getItemRenderer(){
		return (TreeitemRenderer)_renderer;
	}
	/** @deprecated As of release 5.0.6, replaced with {@link #setItemRenderer}.
	 * Sets the renderer which is used to render each item
	 * if {@link #getModel} is not null.
	 *
	 * <p>Note: changing a render will not cause the tree to re-render.
	 * If you want it to re-render, you could assign the same model again
	 * (i.e., setModel(getModel())), or fire an {@link TreeDataEvent} event.
	 *
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.0.0
	 */
	public void setTreeitemRenderer(TreeitemRenderer<?> renderer){
		setItemRenderer(renderer);
	}

	/** @deprecated As of release 5.0.6, replaced with {@link #getItemRenderer}.
	/** Returns the renderer to render each item, or null if the default
	 * renderer is used.
	 * @return the renderer to render each item, or null if the default
	 * @since 3.0.0
	 */
	public <T> TreeitemRenderer<T> getTreeitemRenderer(){
		return getItemRenderer();
	}

	/*
	 * Render the root of Tree
	 * Notice: _model.getRoot() is mapped to Tree, not first Treeitem
	 */
	private void renderTree() {
		if(_treechildren == null) {
			Treechildren children = new Treechildren();
			children.setParent(this);
		} else {
			_treechildren.getChildren().clear();
		}
		if (_model instanceof TreeSelectableModel)
			this.setMultiple(((TreeSelectableModel) _model).isMultiple());
		
		Object node = _model.getRoot();
		final Renderer renderer = new Renderer();
		try {
			renderChildren(renderer, _treechildren, node);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null);// notify the tree when items have been rendered.
	}

	private int[] getPath0(Treechildren parent, int index) {
		List<Integer> path = new LinkedList<Integer>();
		path.add(index);
		Component p = parent;
		while(true) {
			p = p.getParent();
			if (p instanceof Treeitem) {
				Component treechildren = p.getParent();
				if (treechildren != null) {
					path.add(0, treechildren.getChildren().indexOf(p));
					p = treechildren;
				}
			} else
				break;
		}
		final int[] ipath = new int[path.size()];
		for (int j = 0; j < ipath.length; j++)
			ipath[j] = path.get(j);
		return ipath;
	}
	
	private void renderChildren0(Renderer renderer, Treechildren parent, Treeitem ti,
			Object childNode, int i) throws Throwable {
		renderer.render(ti, childNode, i);
		Object v = ti.getAttribute("org.zkoss.zul.model.renderAs");
		if (v != null) {//a new item is created to replace the existent one
			(ti = (Treeitem) v).setOpen(false);
		}
		ti.setRendered(true);

		// B60-ZK-767: handle selected/open state here, as it might be replaced
		int[] path = null;
		boolean isLeaf = childNode != null && _model.isLeaf(childNode);
		if (_model instanceof TreeSelectableModel) {
			TreeSelectableModel model = (TreeSelectableModel) _model;
			if (!model.isSelectionEmpty() && 
					getSelectedCount() != model.getSelectionCount() &&
					model.isPathSelected(path = getPath0(parent, i)))
				addItemToSelection(ti);
		}
		if (_model instanceof TreeOpenableModel) {
			TreeOpenableModel model = (TreeOpenableModel) _model;
			if (!model.isOpenEmpty()) {
				if (!isLeaf) {
					if (path == null)
						path = getPath0(parent, i);
					ti.setOpen(model.isPathOpened(path));
				}
			}
		}
		if (!isLeaf && ti.getTreechildren() == null) {
			Treechildren tc = new Treechildren();
			tc.setParent(ti);
		}
	}
	/*
	 * Renders the direct children for the specified parent
	 */
	private void renderChildren(Renderer renderer, Treechildren parent,
	Object node) throws Throwable {
		final int initSize = initRodSize();
		for (int i = 0, j = _model.getChildCount(node); i < j; i++) {
			Treeitem ti = newUnloadedItem();
			ti.setParent(parent);
			if (initSize >= 0 && i >= initSize) {
				ti.appendChild(new Treerow());
				ti.getTreerow().appendChild(new Treecell());
				continue;
			}
			Object childNode = _model.getChild(node, i);
			renderChildren0(renderer, parent, ti, childNode, i);
		}
	}

	/** 
	 * Returns the number of rows to preload when receiving the rendering
	 * request from the client.
	 * <p>
	 * Default: 50. (since 7.0.0)
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 */
	private int preloadSize() {
		final String size = (String) getAttribute("pre-load-size");
		int sz = size != null ? Integer.parseInt(size) : _preloadsz;
		
		if ((sz = Utils.getIntAttribute(this, 
				"org.zkoss.zul.tree.preloadSize", sz, true)) < 0)
			throw new UiException("nonnegative is required: " + sz);
		return sz;
	}

	
	/** 
	 * Returns the number of items rendered when the Tree first render.
	 *  <p>
	 * Default: 50. (Since 7.0.0)
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 */
	private int initRodSize() {
		if (WebApps.getFeature("ee")) {
			 final int sz = Utils.getIntAttribute(this, "org.zkoss.zul.tree.initRodSize",
						INIT_LIMIT, true);
			 if (inPagingMold()) {
				 if (getPageSize() < sz) {
					 return getPageSize();
				 }
			 }
			 return sz;
		}
		return -1;
	}
	private Treeitem newUnloadedItem() {
		Treeitem ti = new Treeitem();
		ti.setOpen(false);
		return ti;
	}

	/** Returns the renderer used to render items.
	 */
	private TreeitemRenderer getRealRenderer() {
		return _renderer != null ? _renderer: _defRend;
	}
	private static final TreeitemRenderer _defRend = new TreeitemRenderer() {
		public void render(Treeitem ti, final Object node, final int index){
			Tree tree = ti.getTree();
			final Template tm = tree.getTemplate("model");
			if (tm == null) {
				Treecell tc = new Treecell(Objects.toString(node));
				Treerow tr = null;
				ti.setValue(node);
				if(ti.getTreerow()==null){
					tr = new Treerow();
					tr.setParent(ti);
				}else{
					tr = ti.getTreerow();
					tr.getChildren().clear();
				}
				tc.setParent(tr);
			} else {
				final Component[] items = tm.create(ti.getParent(), ti,
					new VariableResolver() {
						public Object resolveVariable(String name) {
							if ("each".equals(name)) {
								return node;
							} else if ("forEachStatus".equals(name)) {
								return new ForEachStatus() {
									
									public ForEachStatus getPrevious() {
										return null;
									}
									
									public Object getEach() {
										return node;
									}
									
									public int getIndex() {
										return index;
									}
									
									public Integer getBegin() {
										return 0;
									}
									
									public Integer getEnd() {
										throw new UnsupportedOperationException("end not available");
									}
								};
							} else {
								return null;
							}
						}
					}, null);
				if (items.length != 1)
					throw new UiException("The model template must have exactly one item, not "+items.length);

				final Treeitem nti = (Treeitem)items[0];
				if (nti.getValue() == null) //template might set it
					nti.setValue(node);
				ti.setAttribute("org.zkoss.zul.model.renderAs", nti);
					//indicate a new item is created to replace the existent one
				ti.detach();
			}
		}
	};

	/** Used to render treeitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final TreeitemRenderer _renderer;
		private boolean _rendered, _ctrled;
		private Renderer() {
			_renderer = getRealRenderer();
		}

		// B65-ZK-1608: Tree node become leaf node after update
		@SuppressWarnings("unchecked")
		private void renderChangedItem(Treeitem item, Object node, int index) throws Throwable {
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}
			try {
				try {
					if (item.getTreerow() != null) // just in case
						item.getTreerow().detach();
					Treechildren tc = item.getTreechildren();
					_renderer.render(item, node, index);
					Object newTreeitem = item.getAttribute("org.zkoss.zul.model.renderAs");
					if (newTreeitem instanceof Treeitem) {
						Treeitem newItem = ((Treeitem)newTreeitem);
						// B65-ZK-1765 : Add new Tree node cause Null Pointer Exception (treechildren is null, in case of a leaf node)
						if(tc != null) {
							newItem.appendChild(tc);
						}
						if (_model instanceof TreeOpenableModel) {
							TreeOpenableModel model = (TreeOpenableModel) _model;

							// reset open status - B65-ZK-1639
							newItem.setOpen(!(model.isOpenEmpty() || !model.isPathOpened(getPath0((Treechildren)newItem.getParent(), index))));
							if (!item.isLoaded() && newItem.isOpen())
								Tree.this.renderChildren(this, tc, node);
							newItem.setLoaded(item.isLoaded());
						}
					}
				} catch (AbstractMethodError ex) {
					final Method m = _renderer.getClass()
						.getMethod("render", new Class<?>[] {Treeitem.class, Object.class});
					m.setAccessible(true);
					m.invoke(_renderer, new Object[] {item, node});
				}
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

		@SuppressWarnings("unchecked")
		private void render(Treeitem item, Object node, int index) throws Throwable {
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}
			try {
				try {
					_renderer.render(item, node, index);
				} catch (AbstractMethodError ex) {
					final Method m = _renderer.getClass()
						.getMethod("render", new Class<?>[] {Treeitem.class, Object.class});
					m.setAccessible(true);
					m.invoke(_renderer, new Object[] {item, node});
				}
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
	 * with {@link #getItemRenderer}.
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
	 * with {@link #getItemRenderer}.
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
			//Note item already rendered, so no need:
			//renderer.render(item, node);
		}else{
			if (tc != null) tc.getChildren().clear(); //just in case
			else {
				tc = new Treechildren();
				tc.setParent(item);
			}

			renderChildren(renderer, tc, node);
		}

		Object v = item.getAttribute("org.zkoss.zul.model.renderAs");
		if (v != null) //a new item is created to replace the existent one
			(item = (Treeitem)v).setOpen(false);
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
				renderer.renderChangedItem(item, node, item.getIndex()); //re-render
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
	}

	/** Renders the specified {@link Treeitem} if not loaded yet,
	 * with {@link #getItemRenderer}.
	 *
	 * <p>It does nothing if {@link #getModel} returns null.
	 * <p>To unload treeitem, with {@link Treeitem#unload()}.
	 * @see #renderItem
	 * @since 3.0.0
	 */
	public void renderItems(Set<? extends Treeitem> items) {
		if (_model == null) return;

		if (items.isEmpty())
			return; //nothing to do

		final Renderer renderer = new Renderer();
		try {
			for (final Treeitem item: items){
				renderItem0(renderer, item);
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
		return _model.getChild(getTreeitemPath(t,ti));
	}

	/**
	 * return the path which is from ZK Component root to ZK Component lastNode
	 */
	/*package*/ int[] getTreeitemPath(Component root, Component lastNode){
		List<Integer> l = new ArrayList<Integer>();
		Component curNode = lastNode;
		while(!root.equals(curNode)){
			if(curNode instanceof Treeitem){
				l.add(0, new Integer(((Treeitem)curNode).getIndex()));
			}
			curNode = curNode.getParent();
		}
		final Integer[] objs = l.toArray(new Integer[l.size()]);
		final int[] path = new int[objs.length];
		for (int i = 0; i < objs.length; i++)
			path[i] = objs[i].intValue();
		return path;
	}

	/** Load the treeitems by the given node.
	 * This method must be used with a tree model, and the node is
	 * one of the value returned by {@link TreeModel#getChild}.
	 * <p>Notice that this method has to search the model one-by-one.
	 * The performance might not be good, so use {@link #renderItemByPath}
	 * if possible.
	 * @exception IllegalStateException if no model is assigned ({@link #setModel}).
	 * @return the treeitem that is associated with the give node, or null
	 * no treeitem is associated (including the give node is the root).
	 * @since 5.0.6
	 * @since #getChildByNode
	 */
	public Treeitem renderItemByNode(Object node) {
		return renderItemByPath(_model.getPath(node));
	}
	
	/**
	 * Load the treeitems by giving a path of the treeitems top open.
	 * <br>Note: By using this method, all treeitems in path will be rendered
	 * and opened ({@link Treeitem#setOpen}). If you want to visit the rendered
	 * item in paging mold, please invoke {@link #setActivePage(Treeitem)}.
	 * @param path - an index path. The first element is the index at the first level
	 * of the tree structure.
	 * @return the treeitem from tree by given path
	 * @since 3.0.0
	 */
	public Treeitem renderItemByPath(int[] path) {
		if (path == null || path.length == 0)
			return null;
		// Start from root-Tree
		Treeitem ti = null;
		List<Component> children = this.getTreechildren().getChildren();
		/*
		 * Go through each stop in path and render corresponding treeitem
		 */
		for (int i = 0; i < path.length; i++) {
			if (path[i] < 0 || path[i] > children.size())
				return null;
			ti = (Treeitem) children.get(path[i]);

			if (i < path.length - 1)
				ti.setOpen(true);

			if (ti.getTreechildren() != null) {
				children = ti.getTreechildren().getChildren();
			} else {
				if (i != path.length - 1) {
					return null;
				}
			}
		}
		return ti;
	}

	protected void redrawChildren(Writer out) throws IOException {
		super.redrawChildren(out);
		if (inPagingMold()) {
			removeAttribute(Attributes.RENDERED_ITEM_COUNT);
			removeAttribute(Attributes.VISITED_ITEM_COUNT);
			removeAttribute(Attributes.VISITED_ITEM_TOTAL);
		}
	}

	// AREA JEFF ADDED END
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "name", _name);
		if (_rows > 0)
			renderer.render("rows", getRows());

		render(renderer, "multiple", isMultiple());
		render(renderer, "checkmark", isCheckmark());
		render(renderer, "vflex", isVflex());

		if (_model != null)
			render(renderer, "model", true);

		if (_nonselTags != null)
			renderer.render("nonselectableTags", _nonselTags);
		if (isCheckmarkDeselectOther())
			renderer.render("_cdo", true);
		if (!isRightSelect())
			renderer.render("rightSelect", false);
		if (_pgi != null && _pgi instanceof Component)
			renderer.render("paginal", _pgi);
		
		if (_currentTop != 0)
			renderer.render("_currentTop", _currentTop);
		if (_currentLeft != 0)
			renderer.render("_currentLeft", _currentLeft);

		if (_anchorTop != 0)
			renderer.render("_anchorTop", _anchorTop);
		if (_anchorLeft != 0)
			renderer.render("_anchorLeft", _anchorLeft);
		
		
	}
	/** Returns whether to toggle a list item selection on right click
	 */
	private boolean isRightSelect() {
		return Utils.testAttribute(this, "org.zkoss.zul.tree.rightSelect", true, true);
	}
	/** Returns whether to sort all of item when model or sort direction be changed.
	 * @since 5.0.7
	 */
	/*package*/ boolean isAutosort() {
		String attr = "org.zkoss.zul.tree.autoSort";
		Object val = getAttribute(attr, true);
		if (val == null)
			val = Library.getProperty(attr);
		return val instanceof Boolean ? ((Boolean)val).booleanValue():
			val != null ? "true".equals(val) || "ignore.change".equals(val): false;
	}
	
	/** Returns whether to sort all of item when model or sort direction be changed.
	 * @since 5.0.7
	 */
	private boolean isIgnoreSortWhenChanged() {
		String attr = "org.zkoss.zul.tree.autoSort";
		Object val = getAttribute(attr, true);
		if (val == null)
			val = Library.getProperty(attr);
		return val == null ? true: "ignore.change".equals(val);
	}
	/** Returns whether to toggle the selection if clicking on a list item
	 * with a checkmark.
	 */
	private static boolean isCheckmarkDeselectOther() {
		if (_ckDeselectOther == null) //ok to race
			_ckDeselectOther = Boolean.valueOf(
				"true".equals(Library.getProperty("org.zkoss.zul.tree.checkmarkDeselectOthers")));
		return _ckDeselectOther.booleanValue();
	}
	private static Boolean _ckDeselectOther;

	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_SELECT)) {
			SelectEvent<Treeitem, ?> evt = SelectEvent.getSelectEvent(request, 
					new SelectEvent.SelectedObjectHandler<Treeitem>() {
				public Set<Object> getObjects(Set<Treeitem> items) {
					if (items == null || items.isEmpty() || _model == null)
						return null;
					Set<Object> objs = new LinkedHashSet<Object>();
					for (Treeitem i : items)
						objs.add(_model.getChild(getTreeitemPath(Tree.this, i)));
					return objs;
				}
			});
			Set<Treeitem> selItems = evt.getSelectedItems();
			disableClientUpdate(true);
			try {
				if (AuRequests.getBoolean(request.getData(), "clearFirst")) {
					clearSelection();
					if (_model instanceof TreeSelectableModel)
						((TreeSelectableModel)_model).clearSelection();
				}
				
				final boolean paging = inPagingMold();
				if (!_multiple || (!paging && (selItems == null || selItems.size() <= 1))) {
					final Treeitem item =
						selItems != null && selItems.size() > 0 ?
							selItems.iterator().next(): null;
					selectItem(item);
					if (_model instanceof TreeSelectableModel) {
						TreeSelectableModel tsm = (TreeSelectableModel) _model;
						tsm.clearSelection();
						if (item != null)
							tsm.addSelectionPath(getTreeitemPath(this, item));
					}
					
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

					// B50-ZK-547: SelectEvent.getSelectItems() does not return multiple selected TreeItems.
					Set<Treeitem> oldSelItems = new LinkedHashSet<Treeitem>(_selItems);
					for (Treeitem item : selItems)
						if (!_selItems.contains(item)) {
							addItemToSelection(item);
							if (_model instanceof TreeSelectableModel)
								((TreeSelectableModel)_model).addSelectionPath(getTreeitemPath(this, item));
						}
					for (Treeitem item : oldSelItems)
						if (!selItems.contains(item)) {
							final int index = getVisibleIndexOfItem(item);
							if (!paging || (index >= from && index < to)) {
								removeItemFromSelection(item);
								if (_model instanceof TreeSelectableModel)
									((TreeSelectableModel)_model).removeSelectionPath(getTreeitemPath(this, item));
							}
						}
				}
			} finally {
				disableClientUpdate(false);
			}

			Events.postEvent(evt);
		} else if (inPagingMold() && cmd.equals(ZulEvents.ON_PAGE_SIZE)) { //since 5.0.2
			final Map<String, Object> data = request.getData();
			final int oldsize = getPageSize();
			int size = AuRequests.getInt(data, "size", oldsize);
			if (size != oldsize) {
				int begin = getActivePage() * oldsize;
				int end = begin + oldsize;
				end = Math.min(getPaginal().getTotalSize(), end);
				Treeitem item = getSelectedItem();
				int sel = getVisibleIndexOfItem(item);
				if (sel < 0 || sel < begin || sel >= end) { //not in selection range
					sel = size > oldsize ? (end-1) : begin;
				}
				int newpg = sel / size;
				setPageSize(size);
				setActivePage(newpg);
				// Bug: B50-3204965: onChangePageSize is not fired in autopaging scenario
				Events.postEvent(new PageSizeEvent(cmd, this, pgi(), size));
			}
		} else if (cmd.equals("onInnerWidth")) {
			final String width = AuRequests.getInnerWidth(request);
			_innerWidth = width == null ? "100%": width;
		} else if (cmd.equals("onScrollPos")) {
			final Map<String, Object> data = request.getData();
			_currentTop = AuRequests.getInt(data, "top", 0);
			_currentLeft = AuRequests.getInt(data, "left", 0);
		} else if(cmd.equals("onAnchorPos")){
			final Map<String, Object> data = request.getData();
			_anchorTop = AuRequests.getInt(data, "top", 0);
			_anchorLeft = AuRequests.getInt(data, "left", 0);
		} else if (cmd.equals(Events.ON_RENDER)) {
			final RenderEvent<Treeitem> event = RenderEvent.getRenderEvent(request);
			final Set<Treeitem> items = event.getItems();

			int cnt = items.size();
			if (cnt == 0) return; //nothing to do
			
			int preloadsz = preloadSize();
			
			final Renderer renderer = new Renderer();
			try {
				Treeitem maxItem = null;
				int maxIndex = -1;
				for (Treeitem ti : items) {
					if (ti.isRendered())
						continue;
					int i = ti.getIndex();
					if (maxItem == null) {
						maxItem = ti;
						maxIndex = i;
					}
					if (i > maxIndex) {
						maxItem = ti;
						maxIndex = i;
					}
					
					ti.getChildren().clear();
					Treechildren parent = (Treechildren) ti.getParent();
					Object childNode = getAssociatedNode(ti, this);
					renderChildren0(renderer, parent, ti, childNode, i);
				}
				if (preloadsz > 0) {
					while (maxItem != null && preloadsz-- > 0) {
						maxItem = (Treeitem) maxItem.getNextSibling();
						if (maxItem != null) {
							if (maxItem.isRendered())
								continue;

							maxItem.getChildren().clear();
							Treechildren parent = (Treechildren) maxItem.getParent();
							Object childNode = getAssociatedNode(maxItem, this);
							renderChildren0(renderer, parent, maxItem, childNode, maxItem.getIndex());
						}
					}
				}
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
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
				if (o instanceof Treecols || o instanceof Auxhead) {
					_it.previous();
					return true;
				}
			}
			return false;
		}
		public Component next() {
			for (;;) {
				Component o = _it.next();
				if (o instanceof Treecols || o instanceof Auxhead)
					return o;
			}
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
