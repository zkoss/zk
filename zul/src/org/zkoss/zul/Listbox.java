/* Listbox.java

	Purpose:

	Description:

	History:
		Wed Jun 15 17:25:00     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import static org.zkoss.lang.Generics.cast;

import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.io.Serializables;
import org.zkoss.json.JSONAware;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.CloneableEventListener;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.ext.Blockable;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zul.event.DataLoadingEvent;
import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.event.GroupsDataListener;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.PageSizeEvent;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.PagingListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.GroupsSelectableModel;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.ext.SelectionControl;
import org.zkoss.zul.ext.Sortable;
import org.zkoss.zul.impl.DataLoader;
import org.zkoss.zul.impl.GroupsListModel;
import org.zkoss.zul.impl.ListboxDataLoader;
import org.zkoss.zul.impl.MeshElement;
import org.zkoss.zul.impl.Padding;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A listbox.
 *
 * <p>
 * Event:
 * <ol>
 * <li>{@link org.zkoss.zk.ui.event.SelectEvent} is sent when user changes the
 * selection.</li>
 * <li>onAfterRender is sent when the model's data has been rendered.(since 5.0.4)</li>
 * <li>onCheckSelectAll is sent when user click on selectAll checkbox.(since 6.5.5)</li>
 * </ol>
 *
 * <p>
 * See <a href="package-summary.html">Specification</a>.
 * </p>
 *
 * <p>
 * Besides creating {@link Listitem} programmatically, you could assign a data
 * model (a {@link ListModel} or {@link GroupsModel} instance) to a listbox via
 * {@link #setModel(ListModel)} or {@link #setModel(GroupsModel)} and then the
 * listbox will retrieve data via {@link ListModel#getElementAt} when necessary.
 *
 * <p>
 * Besides assign a list model, you could assign a renderer (a
 * {@link ListitemRenderer} instance) to a listbox, such that the listbox will
 * use this renderer to render the data returned by
 * {@link ListModel#getElementAt}. If not assigned, the default renderer, which
 * assumes a label per list item, is used. In other words, the default renderer
 * adds a label to a Listitem by calling toString against the object returned by
 * {@link ListModel#getElementAt}</p>
 *
 * <p>To retrieve what are selected in Listbox with a {@link Selectable}
 * {@link ListModel}, you shall use {@link Selectable#getSelection} to get what
 * is currently selected object in {@link ListModel} rather than using
 * {@link Listbox#getSelectedItems}. That is, you shall operate on the item of
 * the {@link ListModel} rather than on the {@link Listitem} of the {@link Listbox}
 * if you use the {@link Selectable} {@link ListModel}.</p>
 *
 * <pre><code>
 * Set selection = ((Selectable)getModel()).getSelection();
 * </code></pre>
 * 
 * <p>[Since 6.0.0] If a model is set, whether the listbox allows
 * the multiple selection depends on {@link Selectable#setMultiple}.
 * In other words, the application shall not access listbox directly if
 * a model is assigned. Rather, the application shall access the model
 * directly.
 * 
 * <p>
 * There are two ways to handle long content: scrolling and paging. If
 * {@link #getMold} is "default", scrolling is used if {@link #setHeight} is
 * called and too much content to display. If {@link #getMold} is "paging",
 * paging is used if two or more pages are required. To control the number of
 * items to display in a page, use {@link #setPageSize}.
 *
 * <p>
 * If paging is used, the page controller is either created automatically or
 * assigned explicitly by {@link #setPaginal}. The paging controller specified
 * explicitly by {@link #setPaginal} is called the external page controller. It
 * is useful if you want to put the paging controller at different location
 * (other than as a child component), or you want to use the same controller to
 * control multiple listboxes.
 *
 * <p>
 * Default {@link #getZclass}: z-listbox.(since 3.5.0)
 *
 * <p>
 * To have a list box without stripping, you can specify a non-existent style
 * class to {@link #setOddRowSclass}.
 *
 * <h3>Clustering and Serialization</h3>
 *
 * <p>
 * When used in a clustering environment, you have to make
 * {@link ListitemRenderer} ({@link #setItemRenderer}) and {@link ListModel} (
 * {@link #setModel}) either serializable or re-assign them when
 * {@link #sessionDidActivate} is called.
 *
 * <h3>Render on Demand (rod)</h3>
 * [ZK EE]
 * [Since 5.0.0]
 *
 * <p>For huge data, you can turn on Listbox's ROD to request ZK engine to load from
 * {@link ListModel} only the required data chunk and create only the required
 * {@link Listitem}s in memory and render only the required DOM elements in
 * browser. So it saves both the memory and the processing time in both server
 * and browser for huge data. If you don't use the {@link ListModel} with the
 * Listbox, turn on the ROD will still have ZK engine to render only a chunk of
 * DOM elements in browser so it at least saves the memory and processing time
 * in browser. Note that ROD works only if the Listbox is configured to has a
 * limited "view port" height. That is, either the Listbox is in the "paging"
 * mold or you have to {@link #setHeight(String)},{@link #setVflex(String)},
 * or {@link #setRows(int)} of the Listbox to make ROD works.</p>
 *
 * <p>You can turn on/off ROD for all Listboxes in the application or only
 * for a specific Listbox. To turn on ROD for all Listboxes in the application,
 * you have to specify the Library Property "org.zkoss.zul.listbox.rod" to
 * "true" in WEB-INF/zk.xml. If you did not specify the Library Property,
 * default is false.</p>
 *
 * <pre><code>
 *	<library-property>
 *		<name>org.zkoss.zul.listbox.rod</name>
 *		<value>true</value>
 *	</library-property>
 * </code></pre>
 *
 * <p>To turn on ROD for a specific Listbox, you have to specify the Listbox's
 * attribute map with key "org.zkoss.zul.listbox.rod" to true. That is, for
 * example, if in a zul file, you shall specify &lt;custom-attributes> of the
 * Listbox like this:</p>
 *
 * <pre><code>
 *	<listbox ...>
 *    <custom-attributes org.zkoss.zul.listbox.rod="true"/>
 *  </listbox>
 * </code></pre>
 *
 * <p>You can mix the Library Property and &lt;custom-attributes> ways together.
 * The &lt;custom-attributes> way always takes higher priority. So you
 * can turn OFF ROD in general and turn ON only some specific Listbox component.
 * Or you can turn ON ROD in general and turn OFF only some specific Listbox
 * component.</P>
 *
 * <p>Since only partial {@link Listitem}s are created and rendered in the
 * Listbox if you turn the ROD on, there will be some limitations on accessing
 * {@link Listitem}s. For example, if you call
 * <pre><code>
 * Listitem itemAt100 = (Listitem) getItemAtIndex(100);
 * </code></pre>
 * <p>The {@link Listitem} in index 100 is not necessary created yet if it is
 * not in the current "view port" and you will get "null" instead.</p>
 *
 * <p>And it is generally a bad idea to "cache" the created {@link Listitem}
 * in your application if you turn the ROD on because Listitems might be removed
 * later. Basically, you shall operate on the item of the {@link ListModel}
 * rather than on the {@link Listitem} of the {@link Listbox} if you use the
 * {@link ListModel} and ROD.</p>
 *
 * <h3>Custom Attributes</h3>
 * <dl>
 * <dt>org.zkoss.zul.listbox.rightSelect</dt>
 * <dd>Specifies whether the selection shall be toggled when user right clicks on
 * item, if the checkmark ({@link #isCheckmark}) is enabled.</br>
 * Notice that you could specify this attribute in any of its ancestor's attributes.
 * It will be inherited.</dd>
 * <dt>org.zkoss.zul.listbox.rod</dt>
 * <dd>Specifies whether to enable ROD (render-on-demand).</br>
 * Notice that you could specify this attribute in any of its ancestor's attributes.
 * It will be inherited.</dd>
 * <dt>org.zkoss.zul.listbox.autoSort</dt>.(since 5.0.7) 
 * <dd>Specifies whether to sort the model when the following cases:</br>
 * <ol>
 * <li>{@link #setModel} is called and {@link Listheader#setSortDirection} is set.</li>
 * <li>{@link Listheader#setSortDirection} is called.</li>
 * <li>Model receives {@link ListDataEvent} and {@link Listheader#setSortDirection} is set.</li>
 * </ol>
 * If you want to ignore sort when receiving {@link ListDataEvent}, 
 * you can specifies the value as "ignore.change".</br>
 * Notice that you could specify this attribute in any of its ancestor's attributes.
 * It will be inherited.</dd>
 * </dl>
 * <dt>org.zkoss.zul.listbox.groupSelect</dt>
 * <dd>Specifies whether Listgroups under this Listbox are selectable. Notice that 
 * you could specify this attribute in any of its ancestor's attributes. It will 
 * be inherited. Default value is false.</dd>
 * 
 * <dt>org.zkoss.zul.listbox.preloadSize</dt>.(since 5.0.8) 
 * <dd>Specifies the number of items to preload when receiving
 * the rendering request from the client.
 * <p>It is used only if live data ({@link #setModel(ListModel)} and
 * not paging ({@link #getPagingChild}).</dd>
 * 
 * <dt>org.zkoss.zul.listbox.initRodSize</dt>.(since 5.0.8) 
 * <dd>Specifies the number of items rendered when the Listbox first render.
 * <p>
 * It is used only if live data ({@link #setModel(ListModel)} and not paging
 * ({@link #getPagingChild}).</dd>
 * 
 * <dt>org.zkoss.zul.listbox.selectOnHighlight.disabled</dt>.(since 7.0.4)
 * <dd>Sets whether to disable select functionality when highlighting text 
 * content with mouse dragging or not.</dd>
 * 
 * @author tomyeh
 * @see ListModel
 * @see ListitemRenderer
 * @see ListitemRendererExt
 */
public class Listbox extends MeshElement {
	private static final long serialVersionUID = 2009111111L;
	public static final String LOADING_MODEL = "org.zkoss.zul.loadingModel";
	public static final String SYNCING_MODEL = "org.zkoss.zul.syncingModel";

	private static final Logger log = LoggerFactory.getLogger(Listbox.class);
	private static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.onInitLaterPosted";
	private static final String ATTR_ON_PAGING_INIT_RENDERER_POSTED = "org.zkoss.zul.onPagingInitPosted";
	private static final int INIT_LIMIT = 50;

	private transient DataLoader _dataLoader;
	private transient List<Listitem> _items;
	private transient List<int[]> _groupsInfo;
	private transient List<Listgroup> _groups;
	/** A list of selected items. */
	private transient Set<Listitem> _selItems;
	/** A readonly copy of {@link #_selItems}. */
	private transient Set<Listitem> _roSelItems;
	private int _maxlength;
	private int _rows, _jsel = -1;
	private transient Listhead _listhead;
	private transient Listfoot _listfoot;
	private transient Frozen _frozen;
	private transient ListModel<?> _model;
	private transient ListitemRenderer<?> _renderer;
	private transient ListDataListener _dataListener;
	private transient GroupsDataListener _groupsDataListener;
	private transient Collection<Component> _heads;
	private int _hdcnt;
	private String _innerWidth = "100%";
	/** The name. */
	private String _name;
	/** The paging controller, used only if mold = "paging". */
	private transient Paginal _pgi;
	private transient boolean _isReplacingItem;
	private transient int _focusIndex = -1;

	/**
	 * The paging controller, used only if mold = "paging" and user doesn't
	 * assign a controller via {@link #setPaginal}. If exists, it is the last
	 * child
	 */
	private transient Paging _paging;
	private EventListener<Event> _pgListener, _pgImpListener, _modelInitListener;
	/** The style class of the odd row. */
	private String _scOddRow = null;
	/** the # of rows to preload. */
	private int _preloadsz = 50;
	/** maintain the number of the visible item in Paging mold. */
	private int _visibleItemCount;
	private int _currentTop = 0; // since 5.0.0 scroll position
	private int _currentLeft = 0;
	private int _topPad; // since 5.0.0 top padding
	private String _nonselTags; //since 5.0.5 for non-selectable tags

	private int _anchorTop = 0; //since ZK 5.0.11 , 6.0.0 anchor position
	private int _anchorLeft = 0;

	private boolean _multiple;
	private boolean _disabled, _checkmark;
	private boolean _renderAll; //since 5.0.0

	private transient boolean _rod;
	/** whether to ignore ListDataEvent.SELECTION_CHANGED */
	private transient boolean _ignoreDataSelectionEvent;
	/** the message to display when there are no items */
	private String _emptyMessage;
	//ZK-3103: if setSelectedIndex is called, should force scroll into view
	private boolean _shallSyncSelInView = false;
	//ZK-3982: opened group scrolled out of view
	private boolean _shallUpdateScrollPos = false;

	static {
		addClientEvent(Listbox.class, Events.ON_RENDER, CE_DUPLICATE_IGNORE | CE_IMPORTANT | CE_NON_DEFERRABLE);
		addClientEvent(Listbox.class, "onInnerWidth", CE_DUPLICATE_IGNORE | CE_IMPORTANT);

		//ZK-925 We can't use CE_DUPLICATE_IGNORE in "onSelect" event since we need to sync the status when multiple select in ROD.
		addClientEvent(Listbox.class, Events.ON_SELECT, CE_IMPORTANT);
		addClientEvent(Listbox.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Listbox.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
		addClientEvent(Listbox.class, "onScrollPos", CE_DUPLICATE_IGNORE | CE_IMPORTANT); // since 5.0.0
		addClientEvent(Listbox.class, "onTopPad", CE_DUPLICATE_IGNORE); // since
		// 5.0.0
		addClientEvent(Listbox.class, "onDataLoading", CE_DUPLICATE_IGNORE | CE_IMPORTANT | CE_NON_DEFERRABLE); // since 5.0.0
		addClientEvent(Listbox.class, ZulEvents.ON_PAGE_SIZE, CE_DUPLICATE_IGNORE | CE_IMPORTANT | CE_NON_DEFERRABLE); //since 5.0.2

		// since 6.0.0, F60-ZK-715
		addClientEvent(Listbox.class, "onAcrossPage", CE_DUPLICATE_IGNORE | CE_IMPORTANT | CE_NON_DEFERRABLE);

		// since 6.0.0/5.0.11, B50-ZK-798
		addClientEvent(Listbox.class, "onAnchorPos", CE_DUPLICATE_IGNORE | CE_IMPORTANT);
		// since 6.5.5 F65-ZK-2014
		addClientEvent(Listbox.class, "onCheckSelectAll", CE_DUPLICATE_IGNORE | CE_IMPORTANT);
	}

	public Listbox() {
		init();
	}

	private void init() {
		_items = new AbstractSequentialList<Listitem>() {
			public ListIterator<Listitem> listIterator(int index) {
				return new ItemIter(index);
			}

			public Listitem get(int j) {
				final Component o = Listbox.this.getChildren().get(j + _hdcnt);
				if (o instanceof Listitem)
					return (Listitem) o;
				throw new IndexOutOfBoundsException("Wrong index: " + j);
			}

			public int size() {
				int sz = getChildren().size() - _hdcnt;
				if (_listfoot != null)
					--sz;
				if (_paging != null)
					--sz;
				if (_frozen != null)
					--sz;
				return sz;
			}

			/**
			 * override for Listgroup
			 *
			 * @since 3.5.1
			 */
			protected void removeRange(int fromIndex, int toIndex) {
				ListIterator<Listitem> it = listIterator(toIndex);
				for (int n = toIndex - fromIndex; --n >= 0 && it.hasPrevious();) {
					it.previous();
					it.remove();
				}
			}

			/**
			 * Override to remove unnecessary Listitem re-indexing (when ROD is on, clear() is called frequently). 
			 */
			public void clear() {
				final boolean oldFlag = setReplacingItem(true);
				try {
					//Bug ZK-1834: if there are selected items, clear first.
					if (getSelectedCount() > 0) {
						clearSelection();

						// Bug ZK-1842 Listbox scroll bug listheader sort 
						_anchorLeft = _anchorTop = 0;
					}
					super.clear();
				} finally {
					setReplacingItem(oldFlag);
				}
			}
		};
		_selItems = new LinkedHashSet<Listitem>(4);
		_roSelItems = Collections.unmodifiableSet(_selItems);

		_heads = new AbstractCollection<Component>() {
			public int size() {
				return _hdcnt;
			}

			public Iterator<Component> iterator() {
				return new Iter();
			}
		};
		_groupsInfo = new LinkedList<int[]>();
		_groups = new AbstractList<Listgroup>() {
			public int size() {
				return getGroupCount();
			}

			public Iterator<Listgroup> iterator() {
				return new IterGroups();
			}

			public Listgroup get(int index) {
				return (Listgroup) getItemAtIndex(_groupsInfo.get(index)[0]);
			}
		};
	}

	private int getRealIndex(int index) {
		final int offset = _model != null && !_renderAll ? getDataLoader().getOffset() : 0;
		return index - (offset < 0 ? 0 : offset);
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> List<T> getChildren() {
		return (List<T>) new Children();
	}

	protected class Children extends AbstractComponent.Children {
		protected void removeRange(int fromIndex, int toIndex) {
			ListIterator<Component> it = listIterator(toIndex);
			for (int n = toIndex - fromIndex; --n >= 0 && it.hasPrevious();) {
				it.previous();
				it.remove();
			}
		}
	}

	/**
	 * Initializes _dataListener and register the listener to the model
	 */
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					onListDataChange(event);
				}
			};
		_model.addListDataListener(_dataListener);

		// ZK-3088: for updating group status
		if (_model instanceof GroupsListModel) {
			if (_groupsDataListener == null) {
				_groupsDataListener = new GroupsDataListener() {
					public void onChange(GroupsDataEvent event) {
						onGroupsDataChange(event);
					}
				};
			}
			((GroupsListModel) _model).getGroupsModel().addGroupsDataListener(_groupsDataListener);
		}
	}

	/**
	 * @deprecated since 5.0.0, use {@link #setSizedByContent}(!fixedLayout)
	 *             instead
	 * @param fixedLayout
	 *            true to outline this listbox by browser
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

	/**
	 * Returns {@link Listhead} belonging to this listbox, or null if no list
	 * headers at all.
	 */
	public Listhead getListhead() {
		return _listhead;
	}

	/**
	 * Returns {@link Listfoot} belonging to this listbox, or null if no list
	 * footers at all.
	 */
	public Listfoot getListfoot() {
		return _listfoot;
	}

	/**
	 * Returns the frozen child.
	 *
	 * @since 5.0.0
	 */
	public Frozen getFrozen() {
		return _frozen;
	}

	/**
	 * Returns a collection of heads, including {@link #getListhead} and
	 * auxiliary heads ({@link Auxhead}) (never null).
	 *
	 * @since 3.0.0
	 */
	public Collection<Component> getHeads() {
		return _heads;
	}

	/**
	 * Returns whether the HTML's select tag is used.
	 */
	/* package */ boolean inSelectMold() {
		return "select".equals(getMold());
	}

	/**
	 * Returns whether the check mark shall be displayed in front of each item.
	 * <p>
	 * Default: false.
	 */
	public boolean isCheckmark() {
		return _checkmark;
	}

	/**
	 * Sets whether the check mark shall be displayed in front of each item.
	 * <p>
	 * The check mark is a checkbox if {@link #isMultiple} returns true. It is a
	 * radio button if {@link #isMultiple} returns false.
	 */
	public void setCheckmark(boolean checkmark) {
		if (_checkmark != checkmark) {
			_checkmark = checkmark;
			smartUpdate("checkmark", checkmark);
		}
	}

	/**
	 * Sets the inner width of this component. The inner width is the width of
	 * the inner table. By default, it is 100%. That is, it is the same as the
	 * width of this component. However, it is changed when the user is sizing
	 * the column's width.
	 *
	 * <p>
	 * Application developers rarely call this method, unless they want to
	 * preserve the widths of sizable columns changed by the user. To preserve
	 * the widths, the developer have to store the widths of all columns and the
	 * inner width ({@link #getInnerWidth}), and then restore them when
	 * re-creating this component.
	 *
	 * @param innerWidth
	 *            the inner width. If null, "100%" is assumed.
	 * @since 3.0.0
	 */
	public void setInnerWidth(String innerWidth) {
		if (innerWidth == null)
			innerWidth = "100%";
		if (!_innerWidth.equals(innerWidth)) {
			_innerWidth = innerWidth;
			smartUpdate("innerWidth", innerWidth);
		}
	}

	/**
	 * Returns the inner width of this component. The inner width is the width
	 * of the inner table.
	 * <p>
	 * Default: "100%"
	 *
	 * @see #setInnerWidth
	 * @since 3.0.0
	 */
	public String getInnerWidth() {
		return _innerWidth;
	}

	/**
	 * Returns whether to grow and shrink vertical to fit their given space, so
	 * called vertical flexibility.
	 *
	 * <p>
	 * Note: this attribute is ignored if {@link #setRows} is specified
	 *
	 * <p>
	 * Default: false.
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

	/**
	 * Sets whether to grow and shrink vertical to fit their given space, so
	 * called vertical flexibility.
	 *
	 * <p>
	 * Note: this attribute is ignored if {@link #setRows} is specified
	 */
	public void setVflex(boolean vflex) {
		if (isVflex() != vflex) {
			setVflex(String.valueOf(vflex));
		}
	}

	@Override
	public void setVflex(String flex) { //ZK-4296: Error indicating incorrect usage when using both vflex and rows
		if (_rows != 0)
			throw new UiException("Not allowed to set vflex and rows at the same time");

		super.setVflex(flex);
	}

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/**
	 * Sets whether it is disabled.
	 * <p>Note that it is only applied when mold is "select".
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/**
	 * Returns the rows. Zero means no limitation.
	 * <p>
	 * Default: 0.
	 */
	public int getRows() {
		return _rows;
	}

	/**
	 * Sets the rows.
	 * <p>
	 * Note: Not allowed to set rows and height/vflex at the same time
	 */
	public void setRows(int rows) throws WrongValueException {
		checkBeforeSetRows();

		if (rows < 0) {
			throw new WrongValueException("Illegal rows: " + rows);
		}
		if (_rows != rows) {
			_rows = rows;
			smartUpdate("rows", _rows);
		}
	}

	@Override
	public void setHeight(String height) {
		if (_rows != 0)
			throw new UiException("Not allowed to set height and rows at the same time");

		super.setHeight(height);
	}

	/**
	 * Returns the seltype.
	 * <p>
	 * Default: "single".
	 */
	public String getSeltype() {
		return _multiple ? "multiple" : "single";
	}

	/**
	 * Sets the seltype.
	 * Allowed values:single,multiple
	 * 
	 */
	public void setSeltype(String seltype) throws WrongValueException {
		if ("single".equals(seltype))
			setMultiple(false);
		else if ("multiple".equals(seltype))
			setMultiple(true);
		else
			throw new WrongValueException("Unknown seltype: " + seltype);
	}

	/**
	 * Returns whether multiple selections are allowed.
	 * <p>
	 * Default: false.
	 */
	public boolean isMultiple() {
		return _multiple;
	}

	/**
	 * Sets whether multiple selections are allowed.
	 * <p>Notice that, if a model is assigned, it will change the model's
	 * state (by {@link Selectable#setMultiple}).
	 */
	@SuppressWarnings("rawtypes")
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			if (!_multiple && _selItems.size() > 1) {
				final Listitem item = getSelectedItem();
				for (Iterator<Listitem> it = _selItems.iterator(); it.hasNext();) {
					final Listitem li = it.next();
					if (li != item) {
						li.setSelectedDirectly(false);
						it.remove();
					}
				}
				// No need to update selId because multiple will do the job at
				// client
			}
			if (_model != null)
				((Selectable) _model).setMultiple(multiple);
			smartUpdate("multiple", _multiple);
		}
	}

	/**
	 * Returns the maximal length of each item's label.
	 * <p>
	 * It is meaningful only for the select mold.
	 */
	public int getMaxlength() {
		return _maxlength;
	}

	/**
	 * Sets the maximal length of each item's label.
	 * <p>
	 * It is meaningful only for the select mold.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0)
			maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			smartUpdate("maxlength", maxlength);
		}
	}

	/**
	 * Returns the name of this component.
	 * <p>
	 * Default: null.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Sets the name of this component.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 *
	 * @param name
	 *            the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0)
			name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", name);
		}
	}

	/** Sets a list of HTML tag names that shall <i>not</i> cause the list item
	 * being selected if they are clicked.
	 * <p>Default: null (it means button, input, textarea and a). If you want
	 * to select no matter which tag is clicked, please specify an empty string.
	 * @param tags a list of HTML tag names that will <i>not</i> cause the list item
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

	/** Returns a list of HTML tag names that shall <i>not</i> cause the list item
	 * being selected if they are clicked.
	 * <p>Refer to {@link #setNonselectableTags} for details.
	 * @since 5.0.5
	 */
	public String getNonselectableTags() {
		return _nonselTags;
	}

	/**
	 * Returns a live list of all {@link Listitem}. By live we mean you can add
	 * or remove them directly with the List interface. In other words, you
	 * could add or remove an item by manipulating the returned list directly.
	 */
	public List<Listitem> getItems() {
		return _items;
	}

	/**
	 * Returns the number of items.
	 */
	public int getItemCount() {
		return _items.size();
	}

	/**
	 * Returns the item at the specified index.
	 *
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 */
	public Listitem getItemAtIndex(int index) {
		final int realindex = getRealIndex(index);
		return realindex < 0 || realindex >= _items.size() ? null : _items.get(realindex);
	}

	/**
	 * Returns the index of the specified item, or -1 if not found.
	 */
	public int getIndexOfItem(Listitem item) {
		return item == null ? -1 : item.getIndex();
	}

	/**
	 * Returns the index of the selected item (-1 if no one is selected).
	 */
	public int getSelectedIndex() {
		return _jsel;
	}

	/* package */boolean isLoadingModel() {
		return getAttribute(LOADING_MODEL) != null;
	}

	/**
	 * Deselects all of the currently selected items and selects the item with
	 * the given index.
	 */
	@SuppressWarnings("rawtypes")
	public void setSelectedIndex(int jsel) {
		final int isz = _items.size();
		final int tsz = _model != null ? _model.getSize() : isz;
		if (jsel >= tsz)
			throw new UiException("Out of bound: " + jsel + " while size=" + tsz);

		if (jsel < -1)
			jsel = -1;
		if (jsel < 0) { // unselect all
			clearSelection();
		} else if (jsel != _jsel || (_multiple && _selItems.size() > 1) || !_selItems.contains(getItemAtIndex(_jsel))) {
			for (Listitem item : _selItems) {
				item.setSelectedDirectly(false);
			}
			_selItems.clear();
			_jsel = jsel;
			Listitem item = getItemAtIndex(_jsel);

			if (item == null) { // to be selected item is not there
				if (inPagingMold()) {
					final int offset = _jsel - _jsel % getPageSize();
					final int limit = getPageSize();
					getDataLoader().syncModel(offset, limit); // force reloading
					if (_jsel != jsel) //Bug ZK-1537: _jsel changed after syncModel if model is never synchronized
						_jsel = jsel;
					//ZK-3103: this will be triggered once with model, scrolling selection into view
					_shallSyncSelInView = true;
				} else {
					smartUpdate("selInView_", _jsel);
				}
			} else {
				if (!item.isDisabled()) { // Bug ZK-1715: not select item if disabled.
					item.setSelectedDirectly(true);
					_selItems.add(item);
					//ZK-3103: should scroll selection into view
					//if model is present, this will be erroneously triggered twice
					if (_model == null || !_rod)
						_shallSyncSelInView = true;
				}
			}

			if (inSelectMold()) {
				smartUpdate("selectedIndex", _jsel);
			} else if (item != null)
				smartUpdate("selectedItem", item);
			// Bug 1734950: don't count on index (since it may change)
			// On the other hand, it is OK with select-mold since
			// it invalidates if items are added or removed
		}

		if (_jsel >= 0 && inPagingMold()) {
			final Listitem item = getItemAtIndex(_jsel);
			int size = getDataLoader().getOffset();
			for (Iterator it = new VisibleChildrenIterator(true); it.hasNext(); size++)
				if (item.equals(it.next()))
					break;

			final int pg = size / getPageSize();
			if (pg != getActivePage())
				setActivePage(pg);
		}
	}

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #setSelectedItem}.
	 *
	 * @param item
	 *            the item to select. If null, all items are deselected.
	 */
	public void selectItem(Listitem item) {
		if (item == null) {
			setSelectedIndex(-1);
		} else {
			if (item.getParent() != this)
				throw new UiException("Not a child: " + item);
			if (_multiple || !item.isSelected())
				setSelectedIndex(item.getIndex());
		}
	}

	/**
	 * Selects the given item, without deselecting any other items that are
	 * already selected..
	 *
	 * <p>Notice that if you assign a model to a listbox ({@link #setModel}),
	 * you shall not invoke this method directly. Rather, use {@link Selectable}
	 * instead.
	 */
	public void addItemToSelection(Listitem item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: " + item);

		if (!item.isSelected()) {
			if (!_multiple) {
				selectItem(item);
			} else {
				if (item.getIndex() < _jsel || _jsel < 0) {
					_jsel = item.getIndex();
					// ZK-866
					// update the change of selected index
					if (inSelectMold()) {
						smartUpdate("selectedIndex", _jsel);
					} else if (item != null)
						smartUpdate("selectedItem", item);
				}
				if (!item.isDisabled()) { // Bug ZK-1715: not select item if disabled.
					item.setSelectedDirectly(true);
					_selItems.add(item);
				}

				// ZK-2113: should be same as normal mold
				//				if (inSelectMold()) {
				//					item.smartUpdate("selected", true);
				//				} else {
				//				}

				smartUpdateSelection();
			}
		}
	}

	/**
	 * Deselects the given item without deselecting other items.
	 *
	 * <p>Notice that if you assign a model to a listbox ({@link #setModel}),
	 * you shall not invoke this method directly. Rather, use {@link Selectable}
	 * instead.
	 */
	public void removeItemFromSelection(Listitem item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: " + item);

		if (item.isSelected()) {
			if (!_multiple) {
				clearSelection();
			} else {
				item.setSelectedDirectly(false);
				_selItems.remove(item);
				fixSelectedIndex(0);

				// ZK-2113: should be same as normal mold
				//				if (inSelectMold()) {
				//					item.smartUpdate("selected", false);
				//				} else {
				//				}

				smartUpdateSelection();
			}
		}
	}

	/**
	 * Note: we have to update all selection at once, since addItemToSelection
	 * and removeItemFromSelection might be called interchangeably.
	 */
	private void smartUpdateSelection() {
		final StringBuffer sb = new StringBuffer(80);
		for (Listitem item : _selItems) {
			if (sb.length() > 0)
				sb.append(',');
			sb.append(item.getUuid());
		}
		smartUpdate("chgSel", sb.toString());
	}

	/**
	 * If the specified item is selected, it is deselected. If it is not
	 * selected, it is selected. Other items in the list box that are selected
	 * are not affected, and retain their selected state.
	 */
	public void toggleItemSelection(Listitem item) {
		if (item.isSelected())
			removeItemFromSelection(item);
		else
			addItemToSelection(item);
	}

	/**
	 * Clears the selection.
	 */
	public void clearSelection() {
		if (!_selItems.isEmpty()) {
			for (Listitem item : _selItems) {
				item.setSelectedDirectly(false);
			}
			_selItems.clear();
			_jsel = -1;
			if (inSelectMold())
				smartUpdate("selectedIndex", -1);
			else
				smartUpdate("selectedItem", null);
			// Bug 1734950: don't count on index (since it may change)
		}
	}

	/**
	 * Selects all items.
	 */
	public void selectAll() {
		if (!_multiple)
			throw new UiException("Appliable only to the multiple seltype: " + this);

		if (_items.size() != _selItems.size()) {
			for (Listitem item : _items) {
				_selItems.add(item);
				item.setSelectedDirectly(true);
			}
			_jsel = _items.isEmpty() ? -1 : 0;
			smartUpdate("selectAll", true);
		}
	}

	/**
	 * Returns the selected item.
	 *
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 */
	public Listitem getSelectedItem() {
		return _jsel >= 0 ? _jsel > 0 && _selItems.size() == 1 ? // optimize for performance
				_selItems.iterator().next() : getItemAtIndex(_jsel) : null;
	}

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #selectItem}.
	 */
	public void setSelectedItem(Listitem item) {
		selectItem(item);
	}

	/**
	 * Selects the given listitems.
	 *
	 * @since 3.6.0
	 */
	public void setSelectedItems(Set<Listitem> listItems) {
		if (!isMultiple())
			throw new WrongValueException("Listbox must allow multiple selections.");
		for (Iterator<Listitem> it = listItems.iterator(); it.hasNext();) {
			addItemToSelection(it.next());
		}
	}

	/**
	 * Returns all selected items.
	 *
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 */
	public Set<Listitem> getSelectedItems() {
		return _roSelItems;
	}

	/**
	 * Returns the number of items being selected.
	 */
	public int getSelectedCount() {
		return _selItems.size();
	}

	/**
	 * Appends an item.
	 *
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 */
	public Listitem appendItem(String label, String value) {
		final Listitem item = new Listitem(label, value);
		item.applyProperties();
		item.setParent(this);
		return item;
	}

	/**
	 * Removes the child item in the list box at the given index.
	 *
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 *
	 * @return the removed item.
	 */
	public Listitem removeItemAt(int index) {
		final Listitem item = getItemAtIndex(index);
		removeChild(item);
		return item;
	}

	// --Paging--//
	/**
	 * Returns the paging controller, or null if not available. Note: the paging
	 * controller is used only if {@link #getMold} is "paging".
	 *
	 * <p>
	 * If mold is "paging", this method never returns null, because a child
	 * paging controller is created automatically (if not specified by
	 * developers with {@link #setPaginal}).
	 *
	 * <p>
	 * If a paging controller is specified (either by {@link #setPaginal}, or by
	 * {@link #setMold} with "paging"), the listbox will rely on the paging
	 * controller to handle long-content instead of scrolling.
	 */
	public Paginal getPaginal() {
		return _pgi;
	}

	/**
	 * Specifies the paging controller. Note: the paging controller is used only
	 * if {@link #getMold} is "paging".
	 *
	 * <p>It is OK, though without any effect, to specify a paging controller
	 * even if mold is not "paging".
	 *
	 * @param pgi the paging controller. If null and {@link #getMold} is
	 * "paging", a paging controller is created automatically as a child
	 * component (see {@link #getPagingChild}).
	 */
	public void setPaginal(Paginal pgi) {
		if (!Objects.equals(pgi, _pgi)) {
			final Paginal old = _pgi;
			_pgi = pgi; // assign before detach paging, since removeChild
			// assumes it

			if (inPagingMold()) {
				if (old != null)
					removePagingListener(old);
				if (_pgi == null) {
					if (_paging != null) {
						_pgi = _paging;
					} else
						newInternalPaging();
				} else { // _pgi != null
					if (_pgi != _paging) {
						if (_paging != null)
							_paging.detach();
						_pgi.setTotalSize(getDataLoader().getTotalSize());
						addPagingListener(_pgi);
						if (_pgi instanceof Component)
							smartUpdate("paginal", _pgi);
					}
				}
				// Bug ZK-1696: model also preserves paging information
				if (_model instanceof Pageable) {
					Pageable m = (Pageable) _model;
					m.setPageSize(_pgi.getPageSize());
					m.setActivePage(_pgi.getActivePage());
				}
			}
		}
	}

	/**
	 * Creates the internal paging component.
	 */
	private void newInternalPaging() {
		final Paging paging = new InternalPaging();
		paging.setDetailed(true);
		paging.applyProperties();
		//min page size is 1
		if (_model instanceof Pageable && ((Pageable) _model).getPageSize() > 0) {
			paging.setPageSize(((Pageable) _model).getPageSize());
		}
		paging.setTotalSize(getDataLoader().getTotalSize());
		//min page index is 0
		if (_model instanceof Pageable && ((Pageable) _model).getActivePage() >= 0) {
			paging.setActivePage(((Pageable) _model).getActivePage());
		}
		paging.setParent(this);

		if (_pgi != null)
			addPagingListener(_pgi);
	}

	@SuppressWarnings("serial")
	private class PGListener implements PagingListener {
		public void onEvent(Event event) {
			//Bug ZK-1622: reset anchor position after changing page
			_anchorTop = 0;
			_anchorLeft = 0;
			if (event instanceof PagingEvent) {
				PagingEvent pe = (PagingEvent) event;
				int pgsz = pe.getPageable().getPageSize();
				int actpg = pe.getActivePage();
				if (PageableModel.INTERNAL_EVENT.equals(event.getName())) {
					if (pgsz > 0) //min page size is 1
						_pgi.setPageSize(pgsz);
					if (actpg >= 0) //min page index is 0
						_pgi.setActivePage(actpg);
				} else if (_model instanceof Pageable) {
					// Bug ZK-1696: model also preserves paging information
					// additional check to avoid infinite loop
					((Pageable) _model).setActivePage(actpg);
				}
				Events.postEvent(new PagingEvent(event.getName(), Listbox.this, pe.getPageable(), actpg));
			}
		}

		public Object willClone(Component comp) {
			return null; // skip to clone
		}
	}

	@SuppressWarnings("serial")
	private class PGImpListener implements PagingListener {
		public void onEvent(Event event) {
			if (_model != null && inPagingMold()) {
				final Paginal pgi = getPaginal();
				int pgsz = pgi.getPageSize();
				int ofs = pgi.getActivePage() * pgsz;
				if (event instanceof PagingEvent) {
					// Bug ZK-1696: PagingEvent have the newest paging information
					pgsz = ((PagingEvent) event).getPageable().getPageSize();
					ofs = ((PagingEvent) event).getActivePage() * pgsz;
				}
				if (_model instanceof Pageable) {
					((Pageable) _model).setPageSize(pgsz);
					((Pageable) _model).setActivePage(pgi.getActivePage());
				}
				if (_rod) {
					getDataLoader().syncModel(ofs, pgsz);
				}
				postOnPagingInitRender();
			}
			invalidate();
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
		if (_model instanceof PageableModel) {
			((PageableModel) _model).addPagingEventListener((PagingListener) _pgListener);
		}
		if (_pgImpListener == null)
			_pgImpListener = new PGImpListener();

		pgi.addEventListener("onPagingImpl", _pgImpListener);
	}

	/** Removes the event listener for the onPaging event. */
	private void removePagingListener(Paginal pgi) {
		if (_model instanceof PageableModel) {
			((PageableModel) _model).removePagingEventListener((PagingListener) _pgListener);
		}
		pgi.removeEventListener(ZulEvents.ON_PAGING, _pgListener);
		pgi.removeEventListener("onPagingImpl", _pgImpListener);
	}

	/**
	 * Returns the child paging controller that is created automatically, or
	 * null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 *
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

	/**
	 * Sets the active page in which the specified item is. The active page will
	 * become the page that contains the specified item.
	 *
	 * @param item
	 *            the item to show. If the item is null or doesn't belong to
	 *            this listbox, nothing happens.
	 * @since 3.0.4
	 * @see #setActivePage(int)
	 */
	public void setActivePage(Listitem item) {
		if (item != null && item.getParent() == this) {
			final int pg = item.getIndex() / getPageSize();
			if (pg != getActivePage())
				setActivePage(pg);
		}
	}

	@Override
	public void setActivePage(int pg) throws WrongValueException {
		// Bug ZK-1696: model also preserves paging information
		if (_model instanceof Pageable) {
			((Pageable) _model).setActivePage(pg);
		}
		super.setActivePage(pg);
	}

	/**
	 * Returns whether this listbox is in the paging mold.
	 */
	/* package */boolean inPagingMold() {
		return "paging".equals(getMold());
	}

	/**
	 * Returns the number of visible descendant {@link Listitem}.
	 *
	 * @since 3.5.1
	 */
	public int getVisibleItemCount() {
		return _visibleItemCount;
	}

	/* package */void addVisibleItemCount(int count) {
		if (count != 0) {
			_visibleItemCount += count;
			if (inPagingMold()) {
				final Paginal pgi = getPaginal();
				int totalSize = getDataLoader().getTotalSize();
				if (count < 0 && _model instanceof Pageable) {
					Pageable p = (Pageable) _model;
					int actpg = p.getActivePage();
					int maxPageIndex = p.getPageCount() - 1;
					if (actpg > maxPageIndex) {
						p.setActivePage(maxPageIndex);
					}
				}
				pgi.setTotalSize(totalSize);
				invalidate(); // the set of visible items might change
			} else if (((Cropper) getDataLoader()).isCropper()) {
				getDataLoader().updateModelInfo();
			} else {
				smartUpdate("visibleItemCount", _visibleItemCount);
			}
		}
	}

	/**
	 * Returns the style class for the odd rows.
	 * <p>
	 * Default: {@link #getZclass()}-odd. (since 3.5.0)
	 *
	 * @since 3.0.0
	 */
	public String getOddRowSclass() {
		return _scOddRow == null ? getZclass() + "-odd" : _scOddRow;
	}

	/**
	 * Sets the style class for the odd rows. If the style class doesn't exist,
	 * the striping effect disappears. You can provide different effects by
	 * providing the proper style classes.
	 *
	 * @since 3.0.0
	 */
	public void setOddRowSclass(String scls) {
		if (scls != null && scls.length() == 0)
			scls = null;
		if (!Objects.equals(_scOddRow, scls)) {
			_scOddRow = scls;
			smartUpdate("oddRowSclass", scls);
		}
	}

	/**
	 * Returns the number of listgroup
	 *
	 * @since 3.5.0
	 */
	public int getGroupCount() {
		return _groupsInfo.size();
	}

	/**
	 * Returns a list of all {@link Listgroup}.
	 *
	 * @since 3.5.0
	 */
	public List<Listgroup> getGroups() {
		return _groups;
	}

	/**
	 * Returns whether listgroup exists.
	 *
	 * @since 3.5.0
	 */
	public boolean hasGroup() {
		return !_groupsInfo.isEmpty();
	}

	/** Sets true to avoid unnecessary Listitem re-indexing when render template.
	 * @param b true to skip
	 * @return original true/false status
	 * @see Renderer#render
	 */
	/* package */boolean setReplacingItem(boolean b) {
		final boolean old = _isReplacingItem;
		if (_model != null) // B60-ZK-898: only apply when model is used.
			_isReplacingItem = b;
		return old;
	}

	/* package */void fixItemIndices(int j, int to, boolean infront) {
		int realj = getRealIndex(j);
		if (realj < 0) {
			realj = 0;
		}
		if (realj < _items.size()) {
			final int beginning = j;
			for (Iterator<Listitem> it = _items.listIterator(realj); it.hasNext() && (to < 0 || j <= to); ++j) {
				Listitem o = it.next();
				o.setIndexDirectly(j);

				if (_isReplacingItem) //@see Renderer#render
					break; //set only the first Listitem, skip handling GroupInfo

				// if beginning is a group, we don't need to change its groupInfo,
				// because
				// it is not reliable when infront is true.
				if ((!infront || beginning != j) && o instanceof Listgroup) {
					int[] g = getLastGroupsInfoAt(j + (infront ? -1 : 1));
					if (g != null) {
						g[0] = j;
						if (g[2] != -1)
							g[2] += (infront ? 1 : -1);
					}
				}
			}
		}
	}

	/**
	 * Fix Childitem._index since j-th item.
	 *
	 * @param j
	 *            the start index (inclusion)
	 * @param to
	 *            the end index (inclusion). If -1, up to the end.
	 */
	private void fixItemIndices(int j, int to) {
		int realj = getRealIndex(j);
		if (realj < 0)
			realj = 0;
		if (realj < _items.size()) {
			for (Iterator<Listitem> it = _items.listIterator(realj); it.hasNext() && (to < 0 || j <= to); ++j)
				it.next().setIndexDirectly(j);
		}
	}
	
	/* package */Listgroup getListgroupAt(int index) {
		if (_groupsInfo.isEmpty())
			return null;
		final int[] g = getGroupsInfoAt(index);
		if (g != null) {
			return (Listgroup) getItemAtIndex(g[0]);
		}
		return null;
	}

	/**
	 * Returns the group index which matches with the ListModel index.
	 *
	 * @param index
	 *            the list item index
	 * @return the associated group index of the list item index.
	 */
	/* package */int getGroupIndex(int index) {
		int j = 0, gindex = -1;
		int[] g = null;
		for (Iterator<int[]> it = _groupsInfo.iterator(); it.hasNext(); ++j) {
			g = it.next();
			if (index == g[0])
				gindex = j;
			else if (index < g[0])
				break;
		}
		return gindex != -1 ? gindex
				: g != null && index < (g[0] + g[1]) ? (j - 1)
						: g != null && index == (g[0] + g[1]) && g[2] == -1 ? (j - 1) : gindex;
	}

	/* package */int[] getGroupsInfoAt(int index) {
		return getGroupsInfoAt(index, false);
	}
	
	/**
	 * Returns an int array that it has two length, one is an index of
	 * listgroup, and the other is the number of items of listgroup(inclusive).
	 */
	/* package */int[] getGroupsInfoAt(int index, boolean isListgroup) {
		for (int[] g : _groupsInfo) {
			if (isListgroup) {
				if (index == g[0])
					return g;
			} else if ((index > g[0] && index <= g[0] + g[1]))
				return g;
		}
		return null;
	}

	/**
	 * Returns the last groups info which matches with the same index. Because
	 * dynamically maintain the index of the groups will occur the same index at
	 * the same time in the loop.
	 */
	/* package */int[] getLastGroupsInfoAt(int index) {
		int[] rg = null;
		for (int[] g : _groupsInfo) {
			if (index == g[0])
				rg = g;
			else if (index < g[0])
				break;
		}
		return rg;
	}

	public void beforeChildAdded(Component newChild, Component refChild) {
		if (newChild instanceof Listitem) {
			if (newChild instanceof Listgroupfoot) {
				if (!hasGroup())
					throw new UiException("Listgroupfoot cannot exist alone, you have to add a Listgroup first");
				if (refChild == null) {
					if (getLastChild() instanceof Listgroupfoot)
						throw new UiException("Only one Listgroupfoot is allowed per Listgroup");
				}
			}
		} else if (newChild instanceof Listhead) {
			if (_listhead != null && _listhead != newChild)
				throw new UiException("Only one listhead is allowed: " + this);
		} else if (newChild instanceof Frozen) {
			if (_frozen != null && _frozen != newChild)
				throw new UiException("Only one frozen child is allowed: " + this);
			if (inSelectMold())
				log.warn("Mold select ignores frozen");
		} else if (newChild instanceof Listfoot) {
			if (_listfoot != null && _listfoot != newChild)
				throw new UiException("Only one listfoot is allowed: " + this);
			if (inSelectMold())
				log.warn("Mold select ignores listfoot");
		} else if (newChild instanceof Paging) {
			if (_paging != null && _paging != newChild)
				throw new UiException("Only one paging is allowed: " + this);
			if (_pgi != null)
				throw new UiException("External paging cannot coexist with child paging");
			if (!inPagingMold())
				throw new UiException("The child paging is allowed only in the paging mold");
		} else if (!(newChild instanceof Auxhead)) {
			throw new UiException("Unsupported child for Listbox: " + newChild);
		}
		super.beforeChildAdded(newChild, refChild);
	}

	private boolean hasGroupsModel() {
		return _model instanceof GroupsListModel;
	}

	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Listitem) {
			final boolean isReorder = newChild.getParent() == this;
			//bug #3051305: Active Page not update when drag & drop item to the end
			if (isReorder) {
				checkInvalidateForMoved((Listitem) newChild, true);
			}
			fixGroupsInfoBeforeInsert(newChild, refChild, isReorder);
			// first: listhead or auxhead
			// last two: listfoot and paging
			if (refChild != null && refChild.getParent() != this)
				refChild = null; // Bug 1649625: it becomes the last child
			if (refChild != null && (refChild == _listhead || refChild instanceof Auxhead))
				refChild = getChildren().size() > _hdcnt ? getChildren().get(_hdcnt) : null;

			refChild = fixRefChildBeforeFoot(refChild);
			final Listitem newItem = (Listitem) newChild;
			final int jfrom = newItem.getParent() == this ? newItem.getIndex() : -1;

			if (super.insertBefore(newChild, refChild)) {
				// Maintain _items
				final int jto = refChild instanceof Listitem ? ((Listitem) refChild).getIndex() : -1,
						fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto : jfrom;
				// jfrom < 0: use jto
				// jto < 0: use jfrom
				// otherwise: use min(jfrom, jto)
				if (fixFrom < 0)
					newItem.setIndexDirectly(_items.size() - 1 + getDataLoader().getOffset());
				else
					fixItemIndices(fixFrom, jfrom >= 0 && jto >= 0 ? jfrom > jto ? jfrom : jto : -1, !isReorder);

				// Maintain selected
				final int newIndex = newItem.getIndex();
				if (newItem.isSelected()) {
					if (_jsel < 0) {
						_jsel = newIndex;
						_selItems.add(newItem);
						smartUpdateSelection();
					} else if (_multiple) {
						if (_jsel > newIndex) {
							_jsel = newIndex;
						}
						_selItems.add(newItem);
						smartUpdateSelection();
					} else { // deselect
						newItem.setSelectedDirectly(false);
					}
				} else {
					if (jfrom < 0) { // no existent child
						if (!isLoadingModel() && _jsel >= newIndex)
							++_jsel;
					} else if (_jsel >= 0) { // any selected
						if (jfrom > _jsel) { // from below
							if (jto >= 0 && jto <= _jsel)
								++_jsel;
						} else { // from above
							if (jto < 0 || jto > _jsel)
								--_jsel;
						}
					}
				}

				fixGroupsInfoAfterInsert(newItem);

				//bug #3049167: Totalsize increase when drag & drop in paging Listbox/Grid
				if (!isReorder) { //if reorder, not an insert
					afterInsert(newChild);
				}

				return true;
			} // insert
		} else if (newChild instanceof Listhead) {
			final boolean added = _listhead == null;
			refChild = fixRefChildForHeader(refChild);
			if (super.insertBefore(newChild, refChild)) {
				_listhead = (Listhead) newChild;
				if (added)
					++_hdcnt; // it may be moved, not inserted
				return true;
			}
		} else if (newChild instanceof Auxhead) {
			final boolean added = newChild.getParent() != this;
			refChild = fixRefChildForHeader(refChild);
			if (super.insertBefore(newChild, refChild)) {
				if (added)
					++_hdcnt; // it may be moved, not inserted
				return true;
			}
		} else if (newChild instanceof Frozen) {
			refChild = _paging; // the last two: listfoot and paging
			if (super.insertBefore(newChild, refChild)) {
				_frozen = (Frozen) newChild;
				return true;
			}
		} else if (newChild instanceof Listfoot) {
			// the last two: listfoot and paging
			if (_frozen != null)
				refChild = _frozen;
			else
				refChild = _paging;
			if (super.insertBefore(newChild, refChild)) {
				_listfoot = (Listfoot) newChild;
				return true;
			}
		} else if (newChild instanceof Paging) {
			refChild = null; // the last: paging
			if (super.insertBefore(newChild, refChild)) {
				_pgi = _paging = (Paging) newChild;
				return true;
			}
		} else {
			return super.insertBefore(newChild, refChild);
			// impossible but to make it more extensible
		}
		return false;
	}

	private Component fixRefChildForHeader(Component refChild) {
		if (refChild != null && refChild.getParent() != this)
			refChild = null;

		// try the first listitem
		if (refChild == null || (refChild != _listhead && !(refChild instanceof Auxhead)))
			refChild = getChildren().size() > _hdcnt ? getChildren().get(_hdcnt) : null;

		// try listfoot or paging if no listem
		refChild = fixRefChildBeforeFoot(refChild);
		return refChild;
	}

	private Component fixRefChildBeforeFoot(Component refChild) {
		if (refChild == null) {
			if (_listfoot != null)
				refChild = _listfoot;
			else if (_frozen != null)
				refChild = _frozen;
			else
				refChild = _paging;
		} else if (refChild == _paging) {
			if (_listfoot != null)
				refChild = _listfoot;
			else if (_frozen != null)
				refChild = _frozen;
		}
		return refChild;
	}

	/**
	 * If the child is a listgroup, its listgroupfoot will be removed at the
	 * same time.
	 */
	public boolean removeChild(Component child) {
		if (_paging == child && _pgi == child && inPagingMold())
			throw new IllegalStateException(
					"The paging component cannot be removed manually. It is removed automatically when changing the mold");
		// Feature 1906110: prevent developers from removing it accidently

		if (child instanceof Listitem && child.getParent() == this)
			beforeRemove(child);

		if (!super.removeChild(child))
			return false;

		if (_listhead == child) {
			_listhead = null;
			--_hdcnt;
		} else if (_listfoot == child) {
			_listfoot = null;
		} else if (_frozen == child) {
			_frozen = null;
		} else if (child instanceof Listitem) {
			// maintain items
			final Listitem item = (Listitem) child;
			final int index = item.getIndex();
			item.setIndexDirectly(-1); // mark

			// Maintain selected
			if (item.isSelected()) {
				_selItems.remove(item);
				if (_jsel == index) {
					fixSelectedIndex(index);
				}

				smartUpdateSelection();
			} else {
				if (!isLoadingModel() && _jsel >= index) {
					--_jsel;
				}
			}
			fixGroupsInfoAfterRemove(child, index);
		} else if (_paging == child) {
			_paging = null;
			if (_pgi == child)
				_pgi = null;
		} else if (child instanceof Auxhead) {
			--_hdcnt;
		}

		if (((Cropper) getDataLoader()).isCropper()) {
			getDataLoader().updateModelInfo();
		}

		return true;
	}

	/**
	 * Callback if a list item has been inserted.
	 * <p>
	 * Note: it won't be called if other kind of child is inserted.
	 * <p>
	 * When this method is called, the index is correct.
	 * <p>
	 * Default: invalidate if it is the paging mold and it affects the view of
	 * the active page.
	 *
	 * @since 3.0.5
	 */
	protected void afterInsert(Component comp) {
		if (_isReplacingItem) //@see Renderer#render
			return; //called by #insertBefore(), skip handling GroupInfo

		updateVisibleCount((Listitem) comp, false);
		checkInvalidateForMoved((Listitem) comp, false);
	}

	/**
	 * Callback if a list item will be removed (not removed yet). Note: it won't
	 * be called if other kind of child is removed.
	 * <p>
	 * Default: invalidate if it is the paging mold and it affects the view of
	 * the active page.
	 *
	 * @since 3.0.5
	 */
	protected void beforeRemove(Component comp) {
		if (_isReplacingItem) //@see Renderer#render
			return; //called by #removeChild(), skip handling GroupInfo

		updateVisibleCount((Listitem) comp, true);
		checkInvalidateForMoved((Listitem) comp, true);
	}

	/**
	 * Update the number of the visible item before it is removed or after it is
	 * added.
	 */
	private void updateVisibleCount(Listitem item, boolean isRemove) {
		if (item instanceof Listgroup || item.isVisible()) {
			final Listgroup g = getListgroupAt(item.getIndex());

			// We shall update the number of the visible item in the following
			// cases.
			// 1) If the item is a type of Listgroupfoot, it is always shown.
			// 2) If the item is a type of Listgroup, it is always shown.
			// 3) If the item doesn't belong to any group.
			// 4) If the group of the item is open.
			if (item instanceof Listgroupfoot || item instanceof Listgroup || g == null || g.isOpen())
				addVisibleItemCount(isRemove ? -1 : 1);

			if (item instanceof Listgroup) {
				final Listgroup group = (Listgroup) item;

				// If the previous group exists, we shall update the number of
				// the visible item from the number of the visible item of the
				// current group.
				if (item.getPreviousSibling() instanceof Listitem) {
					final Listitem preRow = (Listitem) item.getPreviousSibling();
					if (preRow == null) {
						if (!group.isOpen()) {
							addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
						}
					} else {
						final Listgroup preGroup = preRow instanceof Listgroup ? (Listgroup) preRow
								: getListgroupAt(preRow.getIndex());
						if (preGroup != null) {
							if (!preGroup.isOpen() && group.isOpen())
								addVisibleItemCount(
										isRemove ? -group.getVisibleItemCount() : group.getVisibleItemCount());
							else if (preGroup.isOpen() && !group.isOpen())
								addVisibleItemCount(
										isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
						} else {
							if (!group.isOpen())
								addVisibleItemCount(
										isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
						}
					}
				} else if (!group.isOpen()) {
					addVisibleItemCount(isRemove ? group.getVisibleItemCount() : -group.getVisibleItemCount());
				}
			}
		}
		if (inPagingMold()) {
			int totalSize = getDataLoader().getTotalSize();
			if (isRemove && _model instanceof Pageable) {
				Pageable p = (Pageable) _model;
				int actpg = p.getActivePage();
				int maxPageIndex = p.getPageCount() - 1;
				if (actpg > maxPageIndex) {
					p.setActivePage(maxPageIndex);
				}
			}
			getPaginal().setTotalSize(totalSize);
		}
	}

	/**
	 * Checks whether to invalidate, when a child has been added or or will be
	 * removed.
	 *
	 * @param bRemove
	 *            if child will be removed
	 */
	private void checkInvalidateForMoved(Listitem child, boolean bRemove) {
		// No need to invalidate if
		// 1) act == last and child in act
		// 2) act != last and child after act
		// Except removing last elem which in act and act has only one elem
		if (inPagingMold() && !isInvalidated()) {
			final int j = child.getIndex(), pgsz = getPageSize(), n = (getActivePage() + 1) * pgsz;
			if (j >= n)
				return; // case 2

			final int cnt = getItems().size(), n2 = n - pgsz;
			if (j >= n2 && cnt <= n && (!bRemove || cnt > n2 + 1))
				return; // case 1

			invalidate();
		}
	}

	/**
	 * An iterator used by visible children.
	 */
	@SuppressWarnings("rawtypes")
	private class VisibleChildrenIterator implements Iterator {
		private final ListIterator<Listitem> _it = getItems().listIterator();
		private int _count = 0;
		private boolean _isBeginning = false;

		private VisibleChildrenIterator() {
		}

		private VisibleChildrenIterator(boolean isBeginning) {
			_isBeginning = isBeginning;
		}

		public boolean hasNext() {
			if (!inPagingMold())
				return _it.hasNext();

			if (!_isBeginning && _count >= getPaginal().getPageSize()) {
				return false;
			}

			if (_count == 0 && !_isBeginning) {
				final Paginal pgi = getPaginal();
				int begin = pgi.getActivePage() * pgi.getPageSize();
				for (int i = 0; i < begin && _it.hasNext();) {
					getVisibleRow((Listitem) _it.next());
					i++;
				}
			}
			return _it.hasNext();
		}

		private Listitem getVisibleRow(Listitem item) {
			if (item instanceof Listgroup) {
				final Listgroup g = (Listgroup) item;
				if (!g.isOpen()) {
					for (int j = 0, len = g.getItemCount(); j < len && _it.hasNext(); j++)
						_it.next();
				}
			}
			while (!item.isVisible() && _it.hasNext())
				item = (Listitem) _it.next();
			return item;
		}

		public Object next() {
			if (!inPagingMold())
				return _it.next();
			_count++;
			final Listitem item = (Listitem) _it.next();
			return _it.hasNext() ? getVisibleRow(item) : item;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Fix the selected index, _jsel, assuming there are no selected one before
	 * (and excludes) j-the item.
	 */
	private void fixSelectedIndex(int j) {
		if (!_selItems.isEmpty()) {
			int realj = getRealIndex(j);
			if (realj < 0)
				realj = 0;
			if (realj < _items.size()) {
				for (Iterator<Listitem> it = _items.listIterator(realj); it.hasNext(); ++j) {
					final Listitem item = it.next();
					if (item.isSelected()) {
						_jsel = j;
						return;
					}
				}
			}
		}
		_jsel = -1;
	}

	private void fixGroupsInfoBeforeInsert(Component newChild, Component refChild, boolean isReorder) {
		if (_isReplacingItem) //@see Renderer#render
			return; //called by #insertBefore(), skip handling GroupInfo

		if (newChild instanceof Listgroupfoot) {
			if (refChild == null) {
				if (isReorder) {
					final int idx = ((Listgroupfoot) newChild).getIndex();
					final int[] ginfo = getGroupsInfoAt(idx);
					if (ginfo != null) {
						ginfo[1]--;
						ginfo[2] = -1;
					}
				}
				final int[] g = _groupsInfo.get(getGroupCount() - 1);

				g[2] = getItems().get(getItems().size() - 1).getIndex();
			} else if (refChild instanceof Listitem) {
				final int idx = ((Listitem) refChild).getIndex();
				final int[] g = getGroupsInfoAt(idx);
				if (g == null)
					throw new UiException("Listgroupfoot cannot exist alone, you have to add a Listgroup first");
				if (g[2] != -1)
					throw new UiException("Only one Listgroupfoot is allowed per Listgroup");
				if (idx != (g[0] + g[1]))
					throw new UiException("Listgroupfoot must be placed after the last Row of the Listgroup");
				g[2] = idx - 1;
				if (isReorder) {
					final int nindex = ((Listgroupfoot) newChild).getIndex();
					final int[] ginfo = getGroupsInfoAt(nindex);
					if (ginfo != null) {
						ginfo[1]--;
						ginfo[2] = -1;
					}
				}
			} else {
				final Component preRefChild = refChild.getPreviousSibling();
				if (preRefChild instanceof Listitem) {
					final int idx = ((Listitem) preRefChild).getIndex();
					//bug 2936019: Execption when Listbox insertBefore() group + groupfoot
					final int[] g = getGroupsInfoAt(idx, preRefChild instanceof Listgroup);
					if (g == null)
						throw new UiException("Listgroupfoot cannot exist alone, you have to add a Listgroup first");
					if (g[2] != -1)
						throw new UiException("Only one Listgroupfoot is allowed per Listgroup");
					if (idx + 1 != (g[0] + g[1]))
						throw new UiException("Listgroupfoot must be placed after the last Row of the Listgroup");
					g[2] = idx;
					if (isReorder) {
						final int nindex = ((Listgroupfoot) newChild).getIndex();
						final int[] ginfo = getGroupsInfoAt(nindex);
						if (ginfo != null) {
							ginfo[1]--;
							ginfo[2] = -1;
						}
					}
				}
			}
		}
	}

	private void fixGroupsInfoAfterInsert(Listitem newItem) {
		if (_isReplacingItem) //@see Renderer#render
			return; //called by #insertBefore(), skip handling GroupInfo

		if (newItem instanceof Listgroup) {
			Listgroup lg = (Listgroup) newItem;
			if (_groupsInfo.isEmpty())
				_groupsInfo.add(new int[] { lg.getIndex(), getItemCount() - lg.getIndex(), -1 });
			else {
				int idx = 0;
				int[] prev = null, next = null;
				for (int[] g : _groupsInfo) {
					if (g[0] <= lg.getIndex()) {
						prev = g;
						idx++;
					} else {
						next = g;
						break;
					}
				}
				if (prev != null) {
					int index = lg.getIndex(), leng = index - prev[0], size = prev[1] - leng + 1;
					prev[1] = leng;
					_groupsInfo.add(idx, new int[] { index, size, size > 1 && prev[2] >= index ? prev[2] + 1 : -1 });
					if (size > 1 && prev[2] > index)
						prev[2] = -1; // reset listgroupfoot
				} else if (next != null) {
					_groupsInfo.add(idx, new int[] { lg.getIndex(), next[0] - lg.getIndex(), -1 });
				}
			}
		} else if (!_groupsInfo.isEmpty()) {
			int index = newItem.getIndex();
			final int[] g = getGroupsInfoAt(index);
			if (g != null) {
				g[1]++;
				if (g[2] != -1 && (g[2] >= index || newItem instanceof Listgroupfoot))
					g[2] = g[0] + g[1] - 1;
			}
		}
	}

	private void fixGroupsInfoAfterRemove(Component child, int index) {
		if (!_isReplacingItem) { //@see Renderer#render
			//called by #removeChild(), handling GroupInfo if !isReplcingItem
			if (child instanceof Listgroup) {
				int[] prev = null, remove = null;
				for (int[] g : _groupsInfo) {
					if (g[0] == index) {
						remove = g;
						break;
					}
					prev = g;
				}
				if (prev != null && remove != null) {
					prev[1] += remove[1] - 1;
				}
				fixItemIndices(index, -1, false);
				if (remove != null) {
					_groupsInfo.remove(remove);
					final int idx = remove[2];
					if (idx != -1) {
						final int realIndex = getRealIndex(idx) - 1;
						if (realIndex >= 0 && realIndex < getItemCount())
							removeChild(getChildren().get(realIndex));
					}
				}
			} else if (!_groupsInfo.isEmpty()) {
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1)
						g[2]--;
					fixItemIndices(index, -1, false);
				} else
					fixItemIndices(index, -1, false);

				if (child instanceof Listgroupfoot) {
					final int[] g1 = getGroupsInfoAt(index);
					if (g1 != null)
						g1[2] = -1;
				}
			} else
				fixItemIndices(index, -1);
		}

		if (hasGroupsModel() && getItemCount() <= 0) { // remove to empty,
			// reset _groupsInfo
			_groupsInfo = new LinkedList<int[]>();
		}
		//bug 3057288
		//getDataLoader().updateModelInfo(); //itemsInvalidate after really removed
		//return true;
	}


	// -- ListModel dependent codes --//
	/**
	 * Returns the model associated with this list box, or null if this list box
	 * is not associated with any list data model.
	 *
	 * <p>
	 * Note: if {@link #setModel(GroupsModel)} was called with a groups model,
	 * this method returns an instance of {@link ListModel} encapsulating it.
	 *
	 * @see #setModel(ListModel)
	 * @see #setModel(GroupsModel)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ListModel<T> getModel() {
		return (ListModel) _model;
	}

	/**
	 * Returns the list model associated with this list box, or null if this
	 * list box is associated with a {@link GroupsModel} or not associated with
	 * any list data model.
	 *
	 * @since 3.5.0
	 * @see #setModel(ListModel)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ListModel<T> getListModel() {
		return _model instanceof GroupsListModel ? null : (ListModel) _model;
	}

	/**
	 * Returns the groups model associated with this list box, or null if this
	 * list box is associated with a {@link ListModel} or not associated with
	 * any list data model.
	 *
	 * @since 3.5.0
	 * @see #setModel(GroupsModel)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <D, G, F> GroupsModel<D, G, F> getGroupsModel() {
		return _model instanceof GroupsListModel ? ((GroupsListModel) _model).getGroupsModel() : null;
	}

	/**
	 * Sets the list model associated with this listbox. If a non-null model is
	 * assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 *
	 * @param model the list model to associate, or null to dis-associate any
	 * previous model. If not null, it must implement {@link Selectable}.
	 * @exception UiException if failed to initialize with the model
	 * @see #getListModel
	 * @see #setModel(GroupsModel)
	 */
	public void setModel(ListModel<?> model) {
		//ZK-3514: speed up
		if (_model != null && _model != model) {
			int threshold = Utils.getIntAttribute(this, "org.zkoss.zul.invalidateThreshold", 10, true);
			int diff = Math.abs((model != null ? model.getSize() : 0) - _model.getSize());
			if (diff > threshold)
				invalidate();
		}

		if (model != null) {
			if (!(model instanceof Selectable))
				throw new UiException(model.getClass() + " must implement " + Selectable.class);

			if (model instanceof GroupsSelectableModel) {
				((GroupsSelectableModel) model).setGroupSelectable(isListgroupSelectable());
			}
			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
					GroupsModel groupsModel = getGroupsModel();
					if (groupsModel != null) {
						((GroupsListModel) _model).cleanInternalListener();
						groupsModel.removeGroupsDataListener(_groupsDataListener);
					}
					/* Bug ZK-1512: should clear listitem anyway
					if (_model instanceof GroupsListModel)
						getItems().clear();*/

					resetDataLoader(); // Bug 3357641

					if (!isAutosort()) {
						Listhead hds = getListhead();
						if (hds != null) {
							for (Component listheader : hds.getChildren()) {
								((Listheader) listheader).setSortDirection("natural");
							}
						}
					}
				}
				getItems().clear(); // Bug 1807414, ZK-1512
				_visibleItemCount = 0; //Bug ZK-3735: should clear _visibleItemCount before syncModel.

				if (!inSelectMold()) {
					smartUpdate("model",
							model instanceof GroupsListModel || model instanceof GroupsModel ? "group" : true);
				}
				_model = model;
				initDataListener();
				setAttribute(Attributes.BEFORE_MODEL_ITEMS_RENDERED, Boolean.TRUE);
				//ZK-3173: move the block here to avoid modifying pgi "again" before PagingEvent is handled
				if (inPagingMold()) {
					//B30-2129667, B36-2782751, (ROD) exception when zul applyProperties
					//must update paginal totalSize or exception in setActivePage
					final Paginal pgi = getPaginal();
					Pageable m = _model instanceof Pageable ? (Pageable) _model : null;
					//if pageable model contain non-default values, sync from model to pgi
					//otherwise, sync from pgi to model
					if (m != null) {
						if (m.getPageSize() > 0) { //min page size is 1
							pgi.setPageSize(m.getPageSize());
						} else {
							m.setPageSize(pgi.getPageSize());
						}
					}
					pgi.setTotalSize(getDataLoader().getTotalSize());
					if (m != null) {
						if (m.getActivePage() >= 0) { //min page index is 0
							pgi.setActivePage(m.getActivePage());
						} else {
							m.setActivePage(pgi.getActivePage());
						}
					}
				}
			}

			final Execution exec = Executions.getCurrent();
			final boolean defer = exec == null ? false
					: exec.getAttribute("zkoss.Listbox.deferInitModel_" + getUuid()) != null;
			final boolean rod = evalRod();
			//Always syncModel because it is easier for user to enfore reload
			if (!defer || !rod) { //if attached and rod, defer the model sync
				getDataLoader().syncModel(-1, -1);
				removeAttribute(Attributes.BEFORE_MODEL_ITEMS_RENDERED);
			}
			if (!doSort(this))
				postOnInitRender();
			// Since user might setModel and setItemRender separately or
			// repeatedly,
			// we don't handle it right now until the event processing phase
			// such that we won't render the same set of data twice
			// --
			// For better performance, we shall load the first few row now
			// (to save a roundtrip)
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
			getItems().clear();
			if (!inSelectMold())
				smartUpdate("model", false);
			getDataLoader().updateModelInfo();
		}
	}
	
	/**
	 * Sets the groups model associated with this list box. If a non-null model
	 * is assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 *
	 * <p>
	 * The groups model is used to represent a list of data with grouping.
	 *
	 * @param model
	 *            the groups model to associate, or null to dis-associate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 * @since 3.5.0
	 * @see #setModel(ListModel)
	 * @see #getGroupsModel()
	 */
	@SuppressWarnings("rawtypes")
	public void setModel(GroupsModel<?, ?, ?> model) {
		if (model instanceof AbstractGroupsModel) {
			((AbstractGroupsModel) model).setGroupSelectable(isListgroupSelectable());
		}
		setModel((ListModel) (model != null ? GroupsListModel.toListModel(model) : null));
	}

	private static boolean doSort(Listbox listbox) {
		Listhead hds = listbox.getListhead();
		if (!listbox.isAutosort() || hds == null)
			return false;
		for (Iterator<Component> it = hds.getChildren().iterator(); it.hasNext();) {
			final Listheader hd = (Listheader) it.next();
			String dir = hd.getSortDirection();
			if (!"natural".equals(dir)) {
				return hd.doSort("ascending".equals(dir));
			}
		}
		return false;
	}

	/**
	 * Returns the renderer to render each item, or null if the default renderer
	 * is used.
	 */
	@SuppressWarnings("unchecked")
	public <T> ListitemRenderer<T> getItemRenderer() {
		return (ListitemRenderer<T>) _renderer;
	}

	/**
	 * Sets the renderer which is used to render each item if {@link #getModel}
	 * is not null.
	 *
	 * <p>
	 * Note: changing a render will not cause the listbox to re-render. If you
	 * want it to re-render, you could assign the same model again (i.e.,
	 * setModel(getModel())), or fire an {@link ListDataEvent} event.
	 *
	 * @param renderer
	 *            the renderer, or null to use the default.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setItemRenderer(ListitemRenderer<?> renderer) {
		if (_renderer != renderer) {
			_renderer = renderer;

			if (_model != null) {
				if ((renderer instanceof ListitemRendererExt) || (_renderer instanceof ListitemRendererExt)) {
					// bug# 2388345, a new renderer that might new own Listitem,
					// shall clean all Listitems first
					getItems().clear();
					getDataLoader().syncModel(-1, -1); // we have to recreate
					// all
				} else if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
					getDataLoader().syncModel(-1, -1); // have to recreate all
				} else {
					//bug# 3039282, we need to resyncModel if not in a defer mode
					final Execution exec = Executions.getCurrent();
					final boolean defer = exec == null ? false
							: exec.getAttribute("zkoss.Listbox.deferInitModel_" + getUuid()) != null;
					final boolean rod = evalRod();
					if (!defer || !rod)
						getDataLoader().syncModel(-1, -1);
				}
			}
		}
	}

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 */
	@SuppressWarnings("rawtypes")
	public void setItemRenderer(String clsnm) throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setItemRenderer((ListitemRenderer) Classes.newInstanceByThread(clsnm));
	}

	/** @deprecated As of release 5.0.8, use custom attributes (org.zkoss.zul.listbox.preloadSize) instead.
	 * Returns the number of items to preload when receiving the rendering
	 * request from the client.
	 *
	 * <p>
	 * Default: 50. (Since 6.0.1)
	 *
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 *
	 * <p>
	 * Note: if the "pre-load-size" attribute of component is specified, it's
	 * prior to the original value.(@since 3.0.4)
	 *
	 * @since 2.4.1
	 */
	public int getPreloadSize() {
		final String size = (String) getAttribute("pre-load-size");
		return size != null ? Integer.parseInt(size) : _preloadsz;
	}

	/** @deprecated As of release 5.0.8, use custom attributes (org.zkoss.zul.listbox.preloadSize) instead.
	 * Sets the number of items to preload when receiving the rendering request
	 * from the client.
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 *
	 * @param sz
	 *            the number of items to preload. If zero, no preload at all.
	 * @exception UiException
	 *                if sz is negative
	 * @since 2.4.1
	 */
	public void setPreloadSize(int sz) {
		if (sz < 0)
			throw new UiException("nonnegative is required: " + sz);
		_preloadsz = sz;
		// no need to update client since paging done at server
	}

	/**
	 * Handles a private event, onInitRender. It is used only for
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
		removeAttribute(ATTR_ON_PAGING_INIT_RENDERER_POSTED);
		doInitRenderer();
	}

	@SuppressWarnings("rawtypes")
	private void doInitRenderer() {
		if (getPage() == null)
			return;
		// sync the multiple status from model
		if (_model != null)
			setMultiple(((Selectable) _model).isMultiple());

		final Renderer renderer = new Renderer();
		try {
			int pgsz, ofs;
			if (inPagingMold()) {
				pgsz = _pgi.getPageSize();
				ofs = _pgi.getActivePage() * pgsz;
			} else {
				pgsz = inSelectMold() ? getItemCount() : getDataLoader().getLimit();
				ofs = inSelectMold() ? 0 : getDataLoader().getOffset();
				// we don't know # of visible rows, so a 'smart' guess
				// It is OK since client will send back request if not enough
			}
			final int cnt = getItemCount() + getDataLoader().getOffset();
			if (ofs >= cnt) { // not possible; just in case
				ofs = cnt - pgsz;
				if (ofs < 0)
					ofs = 0;
			}

			int j = 0;
			int realOfs = ofs - getDataLoader().getOffset();
			if (realOfs < 0)
				realOfs = 0;
			boolean open = true;
			for (Listitem item = getItems().size() <= realOfs ? null : getItems().get(realOfs), nxt; j < pgsz
					&& item != null; item = nxt, j++) {
				nxt = nextListitem(item); //retrieve first since it might be changed

				if (item.isVisible() && (open || item instanceof Listgroupfoot || item instanceof Listgroup)) {
					renderer.render(item, j + ofs);
				}
				if (item instanceof Listgroup)
					open = ((Listgroup) item).isOpen();
			}

			getDataLoader().updateModelInfo();
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null); // notify the listbox when items have been rendered.
		removeAttribute(Attributes.BEFORE_MODEL_ITEMS_RENDERED);
	}

	private static Listitem nextListitem(Listitem item) {
		final Component c = item.getNextSibling();
		return c instanceof Listitem ? (Listitem) c : null;
		//listitem must be placed contineously
	}

	private void postOnInitRender() {
		// 20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			// Bug ZL-1696: manipulate list from api might happen before list 
			// render, use sendEvent instead of postEvent to render list first
			Events.postEvent("onInitRender", this, null);
		}
	}

	private void postOnPagingInitRender() {
		if (getAttribute(ATTR_ON_PAGING_INIT_RENDERER_POSTED) == null
				&& getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) { // B50-ZK-345
			setAttribute(ATTR_ON_PAGING_INIT_RENDERER_POSTED, Boolean.TRUE);
			Events.postEvent("onPagingInitRender", this, null);
		}
	}

	private void onGroupsDataChange(GroupsDataEvent event) {
		getDataLoader().doGroupsDataChange(event);
	}

	/**
	 * Handles when the list model's content changed.
	 */
	@SuppressWarnings("rawtypes")
	private void onListDataChange(ListDataEvent event) {
		//sort when add
		int type = event.getType();
		if ((type == ListDataEvent.INTERVAL_ADDED || type == ListDataEvent.CONTENTS_CHANGED)
				&& !isIgnoreSortWhenChanged()) {
			if (doSort(this)) {
				getDataLoader().updateModelInfo();
			} else {
				getDataLoader().doListDataChange(event);
				postOnInitRender(); // to improve performance
			}
		} else if (type == ListDataEvent.SELECTION_CHANGED) {
			if (!_ignoreDataSelectionEvent) {
				if (event.getIndex0() > -1) {
					setSelectedIndex(event.getIndex0());
					// auto scrollIntoView, if item is not rendered
				}
				doSelectionChanged();
			}
		} else if (type == ListDataEvent.MULTIPLE_CHANGED) {
			setMultiple(((Selectable) _model).isMultiple());
		} else if (type == ListDataEvent.DISABLE_CLIENT_UPDATE) {
			disableClientUpdate(true);
		} else if (type == ListDataEvent.ENABLE_CLIENT_UPDATE) {
			disableClientUpdate(false);
		} else {
			if (getAttribute(Attributes.BEFORE_MODEL_ITEMS_RENDERED) != null
					&& (type == ListDataEvent.INTERVAL_ADDED || type == ListDataEvent.INTERVAL_REMOVED))
				return;
			getDataLoader().doListDataChange(event);
			postOnInitRender(); // to improve performance

			// TODO: We have to skip the synchronization of the target component
			// when the event is fired from it, i.e. No need to sync the sorting
			// status here.
			if (event.getType() == ListDataEvent.STRUCTURE_CHANGED && _model instanceof Sortable && _listhead != null) { //ZK-1705 added null check for _listhead
				Sortable<Object> smodel = cast(_model);
				List<Listheader> headers = cast(_listhead.getChildren());
				boolean found = false;
				for (Listheader col : headers) {
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
			if (_model.getSize() == 0) { // Bug ZK-1834: model is empty
				resetDataLoader(true);
			}
		}
	}

	/** Called when SELECTION_CHANGED is received. */
	private void doSelectionChanged() {
		final Selectable<Object> smodel = getSelectableModel();
		final Set<Object> selObjs = smodel.getSelection();
		final int expSelCnt = selObjs.size();
		if (expSelCnt == 0) {
			clearSelection();
			return;
		}

		//Optimize the single-selection case
		if (!_multiple && _jsel >= 0) {
			if (smodel.isSelected(_model.getElementAt(_jsel)))
				return; //nothing changed
			getSelectedItem().setSelected(false);
		}

		//Unfortunately, we have to scan all list items either ROD or not,
		//because the selection is meaningful only if a listitem is loaded
		//The performance is not good but it rarely happens (since it is called
		//if the selection is modified by app directly)
		int selCnt = 0;
		for (final Listitem item : _items) {
			if (item.isLoaded()) { //selected is meaningful only if loaded
				boolean sel = selObjs.contains(_model.getElementAt(item.getIndex()));
				if (sel)
					++selCnt;
				item.setSelected(sel);
			}
			if (selCnt == expSelCnt && selCnt >= _selItems.size())
				break; //done (all selected items are sync)
		}
	}

	@SuppressWarnings("unchecked")
	private Selectable<Object> getSelectableModel() {
		return (Selectable<Object>) _model;
	}

	/** Used to render listitem if _model is specified. */
	/* package */class Renderer { // use package for easy to call (if override)
		@SuppressWarnings("rawtypes")
		private final ListitemRenderer _renderer;
		private boolean _rendered, _ctrled;

		/* package */@SuppressWarnings("rawtypes")
		Renderer() {
			_renderer = (ListitemRenderer) getDataLoader().getRealRenderer();
		}

		/* package */@SuppressWarnings({ "unchecked", "rawtypes" })
		void render(Listitem item, int index) throws Throwable {
			if (item.isLoaded()) {
				return; // nothing to do
			}

			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl) _renderer).doTry();
				_ctrled = true;
			}

			final Listcell cell = (Listcell) item.getFirstChild();
			if (!(_renderer instanceof ListitemRendererExt)
					|| (((ListitemRendererExt) _renderer).getControls() & ListitemRendererExt.DETACH_ON_RENDER) != 0) { // detach
				// (default)
				cell.detach();
			}

			//bug #3039843: Paging Listbox without rod, ListModel shall not fully loaded
			//check if the item is a selected item and add into selected set
			final Object value = _model.getElementAt(index);

			final SelectionControl ctrl = getSelectableModel().getSelectionControl();
			final boolean selectable = ctrl == null ? true : ctrl.isSelectable(value);

			//bug #ZK-675: Selection was lost if a render replace the listitem
			final boolean selected = ((Selectable) _model).isSelected(value);
			final boolean oldFlag = setReplacingItem(true); //skipFixItemIndices when rendering
			try {
				try {
					_renderer.render(item, value, index);
				} catch (AbstractMethodError ex) {
					final Method m = _renderer.getClass().getMethod("render",
							new Class<?>[] { Listitem.class, Object.class });
					m.setAccessible(true);
					m.invoke(_renderer, new Object[] { item, value });
				}
				Object v = item.getAttribute(Attributes.MODEL_RENDERAS);
				if (v != null) //a new listitem is created to replace the existent one
					item = (Listitem) v;
			} catch (Throwable ex) {
				try {
					item.setLabel(Exceptions.getMessage(ex));
				} catch (Throwable t) {
					log.error("", t);
				}
				item.setLoaded(true);
				throw ex;
			} finally {
				setReplacingItem(oldFlag);
				if (item.getChildren().isEmpty())
					cell.setParent(item);
			}

			//bug #ZK-675: Selection was lost if a render replace the listitem
			//Also we have to add it to selection when rendered
			if (selected)
				addItemToSelection(item);

			if (ctrl != null)
				item.setSelectable(selectable);

			item.setLoaded(true);
			_rendered = true;
		}

		/* package */void doCatch(Throwable ex) {
			if (_ctrled) {
				try {
					((RendererCtrl) _renderer).doCatch(ex);
				} catch (Throwable t) {
					throw UiException.Aide.wrap(t);
				}
			} else {
				throw UiException.Aide.wrap(ex);
			}
		}

		/* package */void doFinally() {
			if (_ctrled)
				((RendererCtrl) _renderer).doFinally();
		}
	}

	/**
	 * Renders the specified {@link Listitem} if not loaded yet, with
	 * {@link #getItemRenderer}.
	 *
	 * <p>
	 * It does nothing if {@link #getModel} returns null. In other words, it is
	 * meaningful only if live data model is used.
	 *
	 * @see #renderItems
	 * @see #renderAll
	 * @return the list item being passed to this method
	 */
	public Listitem renderItem(Listitem li) {
		if (_model != null && li != null && !li.isLoaded()) {
			final Renderer renderer = new Renderer();
			try {
				renderer.render(li, li.getIndex());
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
		return li;
	}

	/**
	 * Renders all {@link Listitem} if not loaded yet, with
	 * {@link #getItemRenderer}.
	 *
	 * @see #renderItem
	 * @see #renderItems
	 */
	public void renderAll() {
		if (_model == null)
			return;

		_renderAll = true;
		getDataLoader().setLoadAll(_renderAll);

		final Renderer renderer = new Renderer();
		if (!getItems().isEmpty())
			try {
				Listitem item = getItems().get(0);
				int index = item.getIndex();
				for (Listitem nxt; item != null; item = nxt) {
					nxt = nextListitem(item); //retrieve first since it might be changed
					renderer.render(item, index++);
				}
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
	}

	/** Renders the given set of list items.
	 */
	public void renderItems(Set<? extends Listitem> items) {
		if (_model == null)
			return;

		if (items.isEmpty())
			return; // nothing to do

		final Renderer renderer = new Renderer();
		try {
			for (final Listitem item : items)
				renderer.render(item, item.getIndex());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/** Sets the mold to render this component.
	 *
	 * @param mold the mold. If null or empty, "default" is assumed.
	 * Allowed values: default, select, paging 
	 * @see org.zkoss.zk.ui.metainfo.ComponentDefinition
	 */
	public void setMold(String mold) {
		final String old = getMold();
		if (!Objects.equals(old, mold)) {
			super.setMold(mold);
			// we have to change model before detaching paging,
			// since removeChild assumes it

			if ("paging".equals(old)) { // change from paging
				if (_paging != null) {
					removePagingListener(_paging);
					_paging.detach();
				} else if (_pgi != null) {
					removePagingListener(_pgi);
				}
				if (getModel() != null) {
					getDataLoader().syncModel(0, initRodSize()); // change offset back to 0
					postOnInitRender();
				}
				invalidate(); // paging mold -> non-paging mold
			} else if (inPagingMold()) { // change to paging
				if (_pgi != null)
					addPagingListener(_pgi);
				else
					newInternalPaging();
				_topPad = 0;
				_currentTop = 0;
				_currentLeft = 0;
				// enforce a page loading
				// B50-ZK-345: speed up onPagingImpl to surpass onInitRender
				Events.postEvent(10001, new PagingEvent("onPagingImpl", (Component) _pgi, _pgi.getActivePage()));
				invalidate(); // non-paging mold -> paging mold
			}
		}
	}

	/**
	 * Returns the message to display when there are no items
	 * @return String
	 * @since 5.0.7
	 */
	public String getEmptyMessage() {
		return _emptyMessage;
	}

	/**
	 * Sets the message to display when there are no items
	 * @param emptyMessage
	 * @since 5.0.7
	 */
	public void setEmptyMessage(String emptyMessage) {
		if (!Objects.equals(emptyMessage, _emptyMessage)) {
			this._emptyMessage = emptyMessage;
			smartUpdate("emptyMessage", this._emptyMessage);
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-listbox" : _zclass;
	}

	@SuppressWarnings("serial")
	private class ItemIter implements ListIterator<Listitem>, java.io.Serializable {
		private ListIterator<Listitem> _it;
		private int _j;
		private boolean _bNxt;

		private ItemIter(int index) {
			_j = index;
		}

		public void add(Listitem o) {
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

		public Listitem next() {
			if (!hasNext()) //use _items.size() to control if reach listfoot
				throw new NoSuchElementException();

			prepare();
			final Listitem o = _it.next();
			++_j;
			_bNxt = true;
			return o;
		}

		public Listitem previous() {
			if (!hasPrevious()) //use _j >= 0 to control if reach listhead
				throw new NoSuchElementException();

			prepare();
			final Listitem o = _it.previous();
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
			if (_it == null)
				throw new IllegalStateException();
			_it.remove();
			if (_bNxt)
				--_j;
		}

		public void set(Listitem o) {
			if (_it == null)
				throw new IllegalStateException();
			_it.set(o);
		}

		private void prepare() {
			if (_it == null)
				_it = cast(getChildren().listIterator(_j + _hdcnt));
		}
	}

	private boolean evalRod() {
		return Utils.testAttribute(this, "org.zkoss.zul.listbox.rod", false, true)
				&& !(_model instanceof GroupsListModel);
		//TODO: performance enhancement: support GroupsModel in ROD
	}

	private void setFocusIndex(int index) {
		// F60-ZK-715: notify Listbox widget to set _focusItem
		if (index != _focusIndex) {
			_focusIndex = index;
			smartUpdate("focusIndex", index);
		}
	}

	/* package */DataLoader getDataLoader() {
		if (_dataLoader == null) {
			_rod = evalRod();
			final String loadercls = Library.getProperty("org.zkoss.zul.listbox.DataLoader.class");
			try {
				_dataLoader = _rod && loadercls != null ? (DataLoader) Classes.forNameByThread(loadercls).newInstance()
						: new ListboxDataLoader();
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
			_dataLoader.init(this, 0, initRodSize());
		}
		return _dataLoader;
	}

	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (oldpage == null) { // mark as a new attached Listbox
			final Execution exec = Executions.getCurrent();
			exec.setAttribute("zkoss.Listbox.deferInitModel_" + getUuid(), Boolean.TRUE);
			exec.setAttribute("zkoss.Listbox.attached_" + getUuid(), Boolean.TRUE);
			// prepare a right moment to init Listbox (must be as early as possible)
			this.addEventListener("onInitModel", _modelInitListener = new ModelInitListener());
			Events.postEvent(20000, new Event("onInitModel", this)); //first event to be called
		}
		GroupsModel groupsModel = getGroupsModel();
		if (_model != null || groupsModel != null) {
			getDataLoader().syncModel(-1, -1);
			postOnInitRender();
		}
		if (_model != null && _dataListener != null) {
			_model.removeListDataListener(_dataListener);
			_model.addListDataListener(_dataListener);
		}
		if (groupsModel != null && _groupsDataListener != null) {
			groupsModel.removeGroupsDataListener(_groupsDataListener);
			groupsModel.addGroupsDataListener(_groupsDataListener);
		}
	}

	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		if (_model != null && _dataListener != null)
			_model.removeListDataListener(_dataListener);
		GroupsModel groupsModel = getGroupsModel();
		if (groupsModel != null && _groupsDataListener != null)
			groupsModel.removeGroupsDataListener(_groupsDataListener);
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
			_anchorTop = 0;
			_anchorLeft = 0;
			_topPad = 0;
		}
	}

	@SuppressWarnings("serial")
	private class ModelInitListener implements SerializableEventListener<Event>, CloneableEventListener<Event> {
		public void onEvent(Event event) throws Exception {
			if (_modelInitListener != null) {
				Listbox.this.removeEventListener("onInitModel", _modelInitListener);
				_modelInitListener = null;
			}
			// initialize data loader
			// Tricky! might has been initialized when apply
			// properties
			if (_dataLoader != null) {
				final boolean rod = evalRod();
				if (_rod != rod || getItems().isEmpty()) {
					if (_model != null) { // so has to recreate
						// list items
						getItems().clear();
						resetDataLoader(); // enforce recreate the dataloader
						initModel(); //init the model
					} else {
						resetDataLoader(); // enforce recreate the dataloader
						// dataloader

						// Bug ZK-1895
						//The attribute shall be removed, otherwise DataLoader will not syncModel when setModel
						Executions.getCurrent().removeAttribute("zkoss.Listbox.deferInitModel_" + getUuid());
					}
				}
			} else if (_model != null) { //items in model not init yet
				initModel(); //init the model
			} else {
				//The attribute shall be removed, otherwise DataLoader will not syncModel when setModel
				Executions.getCurrent().removeAttribute("zkoss.Listbox.deferInitModel_" + getUuid());
			}
			final DataLoader loader = getDataLoader();

			// initialize paginal if any
			Paginal pgi = getPaginal();
			if (pgi != null)
				pgi.setTotalSize(loader.getTotalSize());
		}

		private void initModel() {
			Executions.getCurrent().removeAttribute("zkoss.Listbox.deferInitModel_" + getUuid());
			setModel(_model); //init the model
		}

		public Object willClone(Component comp) {
			return null; // skip to clone
		}
	}

	// Cloneable//
	@SuppressWarnings("rawtypes")
	public Object clone() {
		final Listbox clone = (Listbox) super.clone();
		clone.init();

		// remove cached listeners
		clone._pgListener = null;
		clone._pgImpListener = null;

		//recreate the DataLoader
		final int offset = clone.getDataLoader().getOffset();

		clone.afterUnmarshal(offset);

		// after _pgi ready, and then getLimit() will work
		final int limit = clone.getDataLoader().getLimit();
		clone.resetDataLoader(false); // no need to reset, it will reset the old reference.
		clone.getDataLoader().init(clone, offset, limit);

		if (clone._model != null) {
			if (clone._model instanceof ComponentCloneListener) {
				final ListModel model = (ListModel) ((ComponentCloneListener) clone._model).willClone(clone);
				if (model != null)
					clone._model = model;
			}
			// we use the same data model but we have to create a new listener
			clone._dataListener = null;
			clone.initDataListener();

			// As the bug in tree - B30-1892446.zul, the component clone won't
			// clone the posted event, so we need to remove the attributes here.
			clone.removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
			clone.removeAttribute(ATTR_ON_PAGING_INIT_RENDERER_POSTED);
			clone.getDataLoader().setLoadAll(_renderAll);
		}

		clone._groupsInfo.addAll(_groupsInfo);

		return clone;
	}

	private void afterUnmarshal(int index) {
		for (Iterator<Component> it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Listitem) {
				final Listitem li = (Listitem) child;
				li.setIndexDirectly(index++); // since Listitem.clone() resets
				// index
				if (li.isSelected()) {
					_selItems.add(li);
				}
			} else if (child instanceof Listhead) {
				_listhead = (Listhead) child;
			} else if (child instanceof Listfoot) {
				_listfoot = (Listfoot) child;
			} else if (child instanceof Frozen) {
				_frozen = (Frozen) child;
			} else if (child instanceof Paging) {
				_pgi = _paging = (Paging) child;
				addPagingListener(_pgi);
			}
		}
	}

	// -- Serializable --//
	// NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
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

		int size = _groupsInfo.size();
		s.writeInt(size);
		if (size > 0)
			s.writeObject(_groupsInfo);
	}

	@SuppressWarnings("rawtypes")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_model = (ListModel) s.readObject();
		didDeserialize(_model);
		_renderer = (ListitemRenderer) s.readObject();
		didDeserialize(_renderer);

		init();

		int offset = s.readInt();
		afterUnmarshal(offset);

		int limit = s.readInt();
		resetDataLoader(false); // no need to reset, it will reset the old reference.
		getDataLoader().init(this, offset, limit);

		if (_model != null) {
			initDataListener();
			getDataLoader().setLoadAll(_renderAll);

			// Map#Entry cannot be serialized, we have to restore them
			if (_model instanceof ListModelMap) {
				for (Listitem item : getItems())
					item.setValue(_model.getElementAt(item.getIndex()));
			}
		}

		int size = s.readInt();
		if (size > 0) {
			List groupsInfo = (List) s.readObject();
			for (int i = 0; i < size; i++)
				_groupsInfo.add((int[]) groupsInfo.get(i));
		}
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

	// -- ComponentCtrl --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		if (_rows > 0)
			renderer.render("rows", getRows());

		render(renderer, "name", _name);

		render(renderer, "emptyMessage", _emptyMessage);

		if (inSelectMold()) {
			render(renderer, "multiple", isMultiple());
			render(renderer, "disabled", isDisabled());
			if (_maxlength > 0)
				renderer.render("maxlength", _maxlength);
		} else {
			render(renderer, "oddRowSclass", _scOddRow);

			render(renderer, "checkmark", isCheckmark());
			render(renderer, "multiple", isMultiple());

			if (_model != null) {
				render(renderer, "model",
						_model instanceof GroupsListModel || _model instanceof GroupsModel ? "group" : true);

				if (isMultiple()) {
					SelectionControl selectionControl = getSelectableModel().getSelectionControl();
					if (selectionControl != null) {
						// cannot use render(renderer) here, we have to send a false state
						// to client.
						renderer.render("$$selectAll", selectionControl.isSelectAll());
					}
				}
			}
			if (!"100%".equals(_innerWidth))
				render(renderer, "innerWidth", _innerWidth);
			if (_currentTop != 0)
				renderer.render("_currentTop", _currentTop);
			if (_currentLeft != 0)
				renderer.render("_currentLeft", _currentLeft);

			//ZK-798
			if (_anchorTop != 0)
				renderer.render("_anchorTop", _anchorTop);
			if (_anchorLeft != 0)
				renderer.render("_anchorLeft", _anchorLeft);

			renderer.render("_topPad", _topPad);
			renderer.render("_totalSize", getDataLoader().getTotalSize());
			renderer.render("_offset", getDataLoader().getOffset());

			if (_rod && !_renderAll) {
				if (((Cropper) getDataLoader()).isCropper())//bug #2936064
					renderer.render("_listbox$rod", true);
				int sz = initRodSize();
				if (sz != INIT_LIMIT)
					renderer.render("initRodSize", initRodSize());
				if (!inPagingMold() && _jsel >= 0)
					renderer.render("_selInView", _jsel); // B50-ZK-56
			}
			if (_nonselTags != null)
				renderer.render("nonselectableTags", _nonselTags);
			if (isCheckmarkDeselectOther())
				renderer.render("_cdo", true);
			if (!isRightSelect())
				renderer.render("rightSelect", false);
			if (isListgroupSelectable())
				renderer.render("groupSelect", true);
			if (isSelectOnHighlightDisabled()) // F70-ZK-2433
				renderer.render("selectOnHighlightDisabled", true);
		}
		if (_pgi != null && _pgi instanceof Component) {
			renderer.render("paginal", _pgi);

			// ZK 8, if no model used in paging mold, we don't support select all in this case
			if (_model == null) {
				renderer.render("_listbox$noSelectAll", true); // B50-ZK-873, separate the select all condition and isCropper
			}
			
			//ZK-3103: only true when setSelectedIndex is called
			if (_shallSyncSelInView) {
				renderer.render("_listbox$shallSyncSelInView", true);
				_shallSyncSelInView = false;
			}
		}
		if (_focusIndex > -1)
			renderer.render("focusIndex", _focusIndex); // F60-ZK-715

		if (_shallUpdateScrollPos) {
			renderer.render("_listbox$shallUpdateScrollPos", true);
			_shallUpdateScrollPos = false;
		}
	}

	/** Returns whether to toggle a list item selection on right click
	 */
	private boolean isRightSelect() {
		return Utils.testAttribute(this, "org.zkoss.zul.listbox.rightSelect", true, true);
	}

	protected boolean isAutohidePaging() {
		return Utils.testAttribute(this, "org.zkoss.zul.listbox.autohidePaging", true, true);
	}

	/** Returns whether to sort all of item when model or sort direction be changed.
	 * @since 5.0.7
	 */
	/*package*/ boolean isAutosort() {
		String attr = "org.zkoss.zul.listbox.autoSort";
		Object val = getAttribute(attr, true);
		if (val == null)
			val = Library.getProperty(attr);
		return val instanceof Boolean ? ((Boolean) val).booleanValue()
				: val != null ? "true".equals(val) || "ignore.change".equals(val) : false;
	}

	protected boolean isSelectOnHighlightDisabled() {
		return Utils.testAttribute(this, "org.zkoss.zul.listbox.selectOnHighlight.disabled", false, true);
	}

	/** 
	 * Returns the number of items to preload when receiving the rendering
	 * request from the client.
	 * <p>
	 * Default: 50. (Since 6.0.1)
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 */
	private int preloadSize() {
		final String size = (String) getAttribute("pre-load-size");
		int sz = size != null ? Integer.parseInt(size) : _preloadsz;

		if ((sz = Utils.getIntAttribute(this, "org.zkoss.zul.listbox.preloadSize", sz, true)) < 0)
			throw new UiException("nonnegative is required: " + sz);
		return sz;
	}

	/** 
	 * Returns the number of items rendered when the Listbox first render.
	 *  <p>
	 * Default: 50. (Since 6.0.1)
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingChild}.
	 */
	private int initRodSize() {
		int sz = Utils.getIntAttribute(this, "org.zkoss.zul.listbox.initRodSize", INIT_LIMIT, true);
		if ((sz) < 0)
			throw new UiException("nonnegative is required: " + sz);
		return sz;
	}

	/** Returns whether to sort all of item when model or sort direction be changed.
	 * @since 5.0.7
	 */
	private boolean isIgnoreSortWhenChanged() {
		String attr = "org.zkoss.zul.listbox.autoSort";
		Object val = getAttribute(attr, true);
		if (val == null)
			val = Library.getProperty(attr);
		return val == null ? true : "ignore.change".equals(val);
	}

	/** Returns whether to toggle the selection if clicking on a list item
	 * with a checkmark.
	 */
	private boolean isCheckmarkDeselectOther() {
		if (_ckDeselectOther == null) //ok to race
			_ckDeselectOther = Boolean
					.valueOf("true".equals(Library.getProperty("org.zkoss.zul.listbox.checkmarkDeselectOthers")));
		return Utils.testAttribute(this, "org.zkoss.zul.listbox.checkmarkDeselectOthers", _ckDeselectOther.booleanValue(), true);
	}

	private static Boolean _ckDeselectOther;

	/**
	 * Returns whether Listgroup is selectable.
	 */
	private boolean isListgroupSelectable() {
		return Utils.testAttribute(this, "org.zkoss.zul.listbox.groupSelect", false, true);
	}

	private <T> Set<T> collectUnselectedObjects(Set<T> previousSelection, Set<T> currentSelection) {
		Set<T> prevSeldItems = previousSelection != null ? new LinkedHashSet<T>(previousSelection)
				: new LinkedHashSet<T>();
		if (currentSelection != null && prevSeldItems.size() > 0)
			prevSeldItems.removeAll(currentSelection);
		return prevSeldItems;
	}

	/**
	 * Processes an AU request.
	 *
	 * <p>
	 * Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onSelect.
	 *
	 * @since 5.0.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals("onDataLoading")) {
			if (_rod) {
				Executions.getCurrent().setAttribute("zkoss.zul.listbox.onDataLoading." + this.getUuid(), Boolean.TRUE); //indicate doing dataloading
			}
			Events.postEvent(DataLoadingEvent.getDataLoadingEvent(request, preloadSize()));
		} else if (inPagingMold() && cmd.equals(ZulEvents.ON_PAGE_SIZE)) { //since 5.0.2
			final Map<String, Object> data = request.getData();
			final int oldsize = getPageSize();
			int size = AuRequests.getInt(data, "size", oldsize);
			if (size != oldsize) {
				int begin = getActivePage() * oldsize;
				int end = begin + oldsize;
				end = Math.min(getPaginal().getTotalSize(), end);
				int sel = getSelectedIndex();
				if (sel < 0 || sel < begin || sel >= end) { //not in selection range
					sel = size > oldsize ? (end - 1) : begin;
				}
				int newpg = sel / size;
				setPageSize(size);
				setActivePage(newpg);
				// Bug: B50-3204965: onChangePageSize is not fired in autopaging scenario
				Events.postEvent(new PageSizeEvent(cmd, this, pgi(), size));
			}
		} else if (cmd.equals("onScrollPos")) {
			final Map<String, Object> data = request.getData();
			_currentTop = AuRequests.getInt(data, "top", 0);
			_currentLeft = AuRequests.getInt(data, "left", 0);
		} else if (cmd.equals("onAnchorPos")) {
			final Map<String, Object> data = request.getData();
			_anchorTop = AuRequests.getInt(data, "top", 0);
			_anchorLeft = AuRequests.getInt(data, "left", 0);
		} else if (cmd.equals("onTopPad")) {
			_topPad = AuRequests.getInt(request.getData(), "topPad", 0);
		} else if (cmd.equals(Events.ON_SELECT)) {
			if (_rod && Executions.getCurrent()
					.getAttribute("zkoss.zul.listbox.onDataLoading." + this.getUuid()) != null) //indicate doing dataloading
				return; //skip all onSelect event after the onDataLoading

			Desktop desktop = request.getDesktop();
			Map data = request.getData();
			List<String> sitems = cast((List) data.get("items"));
			boolean selectAll = Boolean.parseBoolean(data.get("selectAll") + "");
			boolean paging = inPagingMold();
			Set<Listitem> prevSeldItems = new LinkedHashSet<Listitem>(_selItems);
			Set<Listitem> curSeldItems = AuRequests.convertToItems(desktop, sitems);
			Set<Listitem> realPrevSeldItems = new LinkedHashSet<Listitem>(prevSeldItems);
			Set<Object> prevSeldObjects = _model != null
					? new LinkedHashSet<Object>(getSelectableModel().getSelection()) : new LinkedHashSet<Object>();
			// fine tune with B50-ZK-547.
			Selectable<Object> smodel = _model != null ? getSelectableModel() : null;

			// ZK-2089: prevSeldItems should skip listgroup if listgroup is not selectable
			if (!isListgroupSelectable() && prevSeldItems.size() > 0) {
				// use toArray() to prevent java.util.ConcurrentModificationException
				for (Object item : prevSeldItems.toArray()) {
					if (item instanceof Listgroup) {
						prevSeldItems.remove(item);
						realPrevSeldItems.remove(item);
					}
				}
			}

			int from, to;
			Paginal pgi = getPaginal();
			if (pgi != null) {
				int pgsz = pgi.getPageSize();
				from = pgi.getActivePage() * pgsz;
				to = from + pgsz; // excluded
			} else {
				from = 0;
				to = 0;
			}

			// remove the selection in other page
			if (paging && (!isCheckmarkDeselectOther() || selectAll)) {
				// use toArray() to prevent java.util.ConcurrentModificationException
				for (Object item : realPrevSeldItems.toArray()) {
					int index = ((Listitem) item).getIndex();
					if (index >= to || index < from)
						realPrevSeldItems.remove(item);
				}
			}

			if (curSeldItems == null)
				curSeldItems = new HashSet<Listitem>(); //just in case

			SelectionControl ctrl = smodel != null ? smodel.getSelectionControl() : null;
			int start = -1;
			int end = -1;
			if (_rod) { // Bug: ZK-592
				Map<String, Object> m = cast((Map) data.get("range"));
				if (m != null) {
					curSeldItems.addAll(_selItems); // keep other selected items.
					start = AuRequests.getInt(m, "start", -1);
					end = AuRequests.getInt(m, "end", -1);
					for (Iterator<Listitem> it = _items.iterator(); it.hasNext();) {
						Listitem item = it.next();
						int index = item.getIndex();
						if (index >= start && index <= end) {
							// the same logic come from JS file (SelectWidget)
							// for Bug: 2030986
							if (!item.isDisabled() && item.isSelectable())
								curSeldItems.add(item);
						}
					}
				}
			}

			disableClientUpdate(true);
			final boolean oldIDSE = _ignoreDataSelectionEvent;
			_ignoreDataSelectionEvent = true;

			try {
				if (AuRequests.getBoolean(request.getData(), "clearFirst")) {
					clearSelection();
					if (_model != null)
						((Selectable) _model).clearSelection();
				}

				if (!_multiple || (_model == null && !_rod && !paging && curSeldItems.size() <= 1)) {
					//If _model, selItems is only a subset (so we can't optimize it)
					final Listitem item = curSeldItems.size() > 0 ? curSeldItems.iterator().next() : null;
					selectItem(item);
					if (_model != null) {
						final List<Object> selObjs = new ArrayList<Object>();
						if (item != null) {
							Object ele = _model.getElementAt(item.getIndex());
							if (ctrl == null || ctrl.isSelectable(ele))
								selObjs.add(ele);
						}
						getSelectableModel().setSelection(selObjs);
					}
				} else {
					for (final Listitem item : curSeldItems) {
						if (!_selItems.contains(item)) {
							addItemToSelection(item);
							if (smodel != null) { //still have to add selection if not multiple select
								Object ele = _model.getElementAt(item.getIndex());
								if (ctrl == null || ctrl.isSelectable(ele))
									smodel.addToSelection(ele);
							}
						}
					}
					if (smodel != null) {
						while (start >= 0 && end >= 0 && start <= end) {
							//ZK-2804: add those items not in _items as selected
							Object ele = _model.getElementAt(start++);
							if (ctrl == null || ctrl.isSelectable(ele))
								smodel.addToSelection(ele);
						}
					}
					for (final Listitem item : prevSeldItems) {
						if (!curSeldItems.contains(item)) {
							final int index = item.getIndex();
							if (!paging || (index >= from && index < to)) {
								removeItemFromSelection(item);
								if (smodel != null)
									smodel.removeFromSelection(_model.getElementAt(index));
							}
						}
					}
				}
			} finally {
				_ignoreDataSelectionEvent = oldIDSE;
				disableClientUpdate(false);
			}

			Set<Listitem> unselectedItems;
			if (_model != null && paging) {
				prevSeldItems = null;
				unselectedItems = null;
			} else {
				unselectedItems = collectUnselectedObjects(realPrevSeldItems, curSeldItems);
			}

			Set<Object> unselectedObjects;
			Set<Object> selectedObjects = new LinkedHashSet<Object>();
			if (_model == null) {
				prevSeldObjects = null;
				unselectedObjects = null;
			} else {
				for (Listitem i : curSeldItems)
					selectedObjects.add(_model.getElementAt(i.getIndex()));
				unselectedObjects = collectUnselectedObjects(prevSeldObjects, smodel.getSelection());
			}
			if (ctrl != null) {
				if (selectAll)
					ctrl.setSelectAll(true);
			}
			if (sitems == null || sitems.isEmpty() || _model == null)
				selectedObjects = null;
			SelectEvent evt = new SelectEvent(Events.ON_SELECT, this, curSeldItems, prevSeldItems, unselectedItems,
					selectedObjects, prevSeldObjects, unselectedObjects,
					desktop.getComponentByUuidIfAny((String) data.get("reference")), null, AuRequests.parseKeys(data));
			Events.postEvent(evt);
		} else if (cmd.equals("onInnerWidth")) {
			final String width = AuRequests.getInnerWidth(request);
			_innerWidth = width == null ? "100%" : width;
		} else if (cmd.equals(Events.ON_RENDER)) {
			final Set<Listitem> items = AuRequests.convertToItems(request.getDesktop(), getRequestData(request));
			int cnt = items.size();
			if (cnt == 0)
				return; // nothing to do

			// B85-ZK-3572 get size using preloadSize()
			int size = preloadSize();
			cnt = size - cnt;
			if (cnt > 0 && size > 0) { // Feature 1740072: pre-load
				if (cnt > size)
					cnt = size; // at most 8 more to load

				// 1. locate the first item found in items
				final List<Listitem> toload = new LinkedList<Listitem>();
				Iterator<Listitem> it = _items.iterator();
				while (it.hasNext()) {
					final Listitem li = it.next();
					if (items.contains(li)) // found
						break;
					if (!li.isLoaded())
						toload.add(0, li); // reverse order
				}

				// 2. add unload items before the found one
				if (!toload.isEmpty()) {
					int bfcnt = cnt / 3;
					for (Iterator<Listitem> e = toload.iterator(); bfcnt > 0 && e.hasNext(); --bfcnt, --cnt) {
						items.add(e.next());
					}
				}

				// 3. add unloaded after the found one
				while (cnt > 0 && it.hasNext()) {
					final Listitem li = it.next();
					if (!li.isLoaded() && items.add(li))
						--cnt;
				}
			}

			Listbox.this.renderItems(items);

		} else if (cmd.equals("onAcrossPage")) { // F60-ZK-715
			final Map<String, Object> data = request.getData();
			int page = AuRequests.getInt(data, "page", 0);
			int offset = AuRequests.getInt(data, "offset", 0);
			int shift = AuRequests.getInt(data, "shift", 0);
			int pageSize = getPageSize();
			int index = page * pageSize + offset;
			int from = shift < 0 ? index + shift : index;
			int to = shift > 0 ? index + shift : index;

			//Update UI
			final int tsz = getItemCount();
			final int toUI = Math.min(to, tsz - 1); // capped by size
			if (!isMultiple() || shift == 0) {

				// B65-ZK-1969 and B65-1715
				if ((_model == null && index >= tsz) || (_model != null && index >= _model.getSize()))
					index = tsz - 1;
				setSelectedIndex(index);
				setFocusIndex(offset < 0 ? pageSize - 1 : offset);
			} else {
				Set<Listitem> items = new HashSet<Listitem>();
				for (int i = from; i <= toUI; i++)
					items.add(_items.get(i));
				setSelectedItems(items);
				setActivePage(index / pageSize);
				setFocusIndex(offset);
			}

			//Update Model
			if (_model != null) {
				final boolean oldIDSE = _ignoreDataSelectionEvent;
				_ignoreDataSelectionEvent = true;
				try {
					to = Math.min(to, _model.getSize() - 1); // capped by size
					final Selectable<Object> smodel = getSelectableModel();
					if (!smodel.isMultiple() || shift == 0) {
						if (!smodel.isMultiple())
							from = to;
						smodel.clearSelection();
					}
					while (from <= to)
						smodel.addToSelection(_model.getElementAt(from++));
				} finally {
					_ignoreDataSelectionEvent = oldIDSE;
				}
			}

			// B60-ZK-815: simulate onSelect event when going across page
			SelectEvent<Listitem, Object> evt = new SelectEvent<Listitem, Object>("onSelect", this, getSelectedItems(),
					getItemAtIndex(index), shift != 0 ? SelectEvent.SHIFT_KEY : 0);
			Events.postEvent(evt);
		} else if (cmd.equals("onCheckSelectAll")) { // F65-ZK-2014
			CheckEvent evt = CheckEvent.getCheckEvent(request);
			if (_model != null) {
				final Selectable<Object> selectableModel = getSelectableModel();
				SelectionControl selectionControl = selectableModel.getSelectionControl();
				if (selectionControl == null)
					throw new IllegalStateException(
							"SelectionControl cannot be null, please implement SelectionControl interface for SelectablModel");
				selectionControl.setSelectAll(evt.isChecked());
			}
			Events.postEvent(evt);
		} else if (cmd.equals("onUpdateSelectAll")) {
			if (_model != null) {
				final SelectionControl selectionControl = getSelectableModel().getSelectionControl();
				if (selectionControl != null) {
					Clients.response(new AuInvoke(this, "$doService", new Object[] { cmd, new JSONAware() {
						public String toJSONString() {
							return String.valueOf(selectionControl.isSelectAll());
						}
					} }));
				}
			}
		} else
			super.service(request, everError);
	}

	@SuppressWarnings("unchecked")
	private List<String> getRequestData(org.zkoss.zk.au.AuRequest request) {
		return (List<String>) request.getData().get("items");
	}

	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/**
	 * A utility class to implement {@link #getExtraCtrl}. It is used only by
	 * component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Cropper, Padding, Blockable {
		// --Padding--//
		public int getHeight() {
			return _topPad;
		}

		public void setHeight(int height) {
			_topPad = height;
		}

		// --Cropper--//
		public boolean isCropper() {
			return ((Cropper) getDataLoader()).isCropper();
		}

		public Component getCropOwner() {
			return Listbox.this;
		}

		public Set<? extends Component> getAvailableAtClient() {
			return ((Cropper) getDataLoader()).getAvailableAtClient();
		}

		public boolean shallBlock(AuRequest request) {
			return isDisabled() || !Components.isRealVisible(Listbox.this);
		}
	}

	/**
	 * An iterator used by _heads.
	 */
	private class Iter implements Iterator<Component> {
		private final Iterator<Component> _it = getChildren().iterator();
		private int _j;

		public boolean hasNext() {
			return _j < _hdcnt;
		}

		public Component next() {
			final Component o = _it.next();
			++_j;
			return o;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * An iterator used by _groups.
	 */
	private class IterGroups implements Iterator<Listgroup> {
		private final Iterator<int[]> _it = _groupsInfo.iterator();
		private int _j;

		public boolean hasNext() {
			return _j < getGroupCount();
		}

		public Listgroup next() {
			final Listgroup o = (Listgroup) getItemAtIndex(_it.next()[0]);
			++_j;
			return o;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void setPageSize(int pgsz) throws WrongValueException {
		// Bug ZK-1696: model also preserves paging information
		if (_model instanceof Pageable) {
			((Pageable) _model).setPageSize(pgsz);
		}
		super.setPageSize(pgsz);
	}
	
	public void onAfterRender() {
		if (inPagingMold() && _model instanceof Pageable) {
			Pageable m = (Pageable) _model;
			if (m.getPageSize() > 0) { //min page size is 1
				_pgi.setPageSize(m.getPageSize());
			} else {
				m.setPageSize(_pgi.getPageSize());
			}
			_pgi.setTotalSize(getDataLoader().getTotalSize());
			if (m.getActivePage() >= 0) { //min page index is 0
				_pgi.setActivePage(m.getActivePage());
			} else {
				m.setActivePage(_pgi.getActivePage());
			}
		}
	}
	
	/**
	 * Scroll to the specified item by the given index.
	 * @param index the index of item
	 * @since 8.5.2
	 */
	public void scrollToIndex(int index) {
		ListModel<Object> model = getModel();
		int itemCount = model != null ? model.getSize() : getItemCount();
		if (index < 0 || index > itemCount - 1) {
			throw new IndexOutOfBoundsException("Illegal index: " + index);
		}
		response(new AuInvoke(this, "scrollToIndex", index, (double) index / itemCount));
	}

	/**
	 * Sets whether to update the scroll position on init
	 * <p>Default: false.
	 * <p>Note: internal use only
	 * @param shallUpdateScrollPos whether update the scroll position on init
	 * @since 8.6.0
	 */
	public void shallUpdateScrollPos(boolean shallUpdateScrollPos) {
		_shallUpdateScrollPos = shallUpdateScrollPos;
	}
}
