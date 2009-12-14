/* Listbox.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun 15 17:25:00     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.zkoss.lang.Classes;
import org.zkoss.lang.D;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zul.event.DataLoadingEvent;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Paginated;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.impl.DataLoader;
import org.zkoss.zul.impl.GroupsListModel;
import org.zkoss.zul.impl.ListboxDataLoader;
import org.zkoss.zul.impl.Padding;
import org.zkoss.zul.impl.XulElement;

/**
 * A listbox.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.SelectEvent is sent when user changes the
 * selection.</li>
 * </ol>
 * 
 * <p>
 * See <a href="package-summary.html">Specification</a>.
 * </p>
 * 
 * <p>
 * Besides creating {@link Listitem} programmingly, you could assign a data
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
 * {@link ListModel#getElementAt}
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
 * [Enterprise Edition]
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
 * <p>To retrieve what are selected in ROD Listbox, you shall use 
 * {@link Selectable#getSelection} to get what is currently selected object in
 * {@link ListModel} rather than using {@link Listbox#getSelectedItems}. That is, 
 * you shall operate on the item of the {@link ListModel} rather than on the 
 * {@link Listitem} of the {@link Listbox} if you use the {@link ListModel} and
 * ROD.</p>
 * 
 * <pre><code>
 * Set selection = ((Selectable)getModel()).getSelection();
 * </code></pre>  
 * 
 * @author tomyeh
 * @see ListModel
 * @see ListitemRenderer
 * @see ListitemRendererExt
 */
public class Listbox extends XulElement implements Paginated,
		org.zkoss.zul.api.Listbox {
	public static final String LOADING_MODEL = "org.zkoss.zul.loadingModel";
	public static final String SYNCING_MODEL = "org.zkoss.zul.syncingModel";

	private static final Log log = Log.lookup(Listbox.class);
	private static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.onInitLaterPosted";

	private transient DataLoader _dataLoader;
	private transient List _items, _groupsInfo, _groups;
	/** A list of selected items. */
	private transient Set _selItems;
	/** A readonly copy of {@link #_selItems}. */
	private transient Set _roSelItems;
	private int _maxlength;
	private int _rows, _jsel = -1;
	private transient Listhead _listhead;
	private transient Listfoot _listfoot;
	private transient Frozen _frozen;
	private transient ListModel _model;
	private transient ListitemRenderer _renderer;
	private transient ListDataListener _dataListener;
	private transient Collection _heads;
	private int _hdcnt;
	private String _innerWidth = "100%";

	private String _pagingPosition = "bottom";
	/** The name. */
	private String _name;
	/** The paging controller, used only if mold = "paging". */
	private transient Paginal _pgi;
	/**
	 * The paging controller, used only if mold = "paging" and user doesn't
	 * assign a controller via {@link #setPaginal}. If exists, it is the last
	 * child
	 */
	private transient Paging _paging;
	private transient EventListener _pgListener, _pgImpListener,
			_listboxInitListener;
	/** The style class of the odd row. */
	private String _scOddRow = null;
	private int _tabindex = -1;
	/** the # of rows to preload. */
	private int _preloadsz = 7;
	private boolean _multiple;
	private boolean _disabled, _checkmark;
	private boolean _vflex;
	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;
	private boolean _sizedByContent;
	/** maintain the number of the visible item in Paging mold. */
	private int _visibleItemCount;
	private int _currentTop = 0; // since 5.0.0 scroll position
	private int _currentLeft = 0;
	private int _topPad; // since 5.0..0 top padding

	private transient boolean _rod;

	static {
		addClientEvent(Listbox.class, Events.ON_RENDER, CE_DUPLICATE_IGNORE
				| CE_IMPORTANT | CE_NON_DEFERRABLE);
		addClientEvent(Listbox.class, "onInnerWidth", CE_DUPLICATE_IGNORE
				| CE_IMPORTANT);
		addClientEvent(Listbox.class, Events.ON_SELECT, CE_IMPORTANT);
		addClientEvent(Listbox.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Listbox.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
		addClientEvent(Listbox.class, "onScrollPos", CE_DUPLICATE_IGNORE
				| CE_IMPORTANT); // since 5.0.0
		addClientEvent(Listbox.class, "onTopPad", CE_DUPLICATE_IGNORE); // since
		// 5.0.0
		addClientEvent(Listbox.class, "onDataLoading", CE_DUPLICATE_IGNORE
				| CE_IMPORTANT | CE_NON_DEFERRABLE); // since 5.0.0
	}

	public Listbox() {
		init();
	}

	private void init() {
		_items = new AbstractSequentialList() {
			public ListIterator listIterator(int index) {
				return new ItemIter(index);
			}

			public Object get(int j) {
				final Object o = Listbox.this.getChildren().get(j + _hdcnt);
				if (!(o instanceof Listitem))
					throw new IndexOutOfBoundsException("Wrong index: " + j);
				return o;
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
				ListIterator it = listIterator(toIndex);
				for (int n = toIndex - fromIndex; --n >= 0 && it.hasPrevious();) {
					it.previous();
					it.remove();
				}
			}
		};
		_selItems = new LinkedHashSet(5);
		_roSelItems = Collections.unmodifiableSet(_selItems);

		_heads = new AbstractCollection() {
			public int size() {
				return _hdcnt;
			}

			public Iterator iterator() {
				return new Iter();
			}
		};
		_groupsInfo = new LinkedList();
		_groups = new AbstractList() {
			public int size() {
				return getGroupCount();
			}

			public Iterator iterator() {
				return new IterGroups();
			}

			public Object get(int index) {
				return getItemAtIndex(((int[]) _groupsInfo.get(index))[0]);
			}
		};
	}

	private int getRealIndex(int index) {
		final int offset = _model != null ? getDataLoader().getOffset() : 0;
		return index - (offset < 0 ? 0 : offset);
	}

	protected List newChildren() {
		return new Children();
	}

	protected class Children extends AbstractComponent.Children {
		protected void removeRange(int fromIndex, int toIndex) {
			ListIterator it = listIterator(toIndex);
			for (int n = toIndex - fromIndex; --n >= 0 && it.hasPrevious();) {
				it.previous();
				it.remove();
			}
		}
	};

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
	 * Sets whether sizing listbox column width by its content. Default is
	 * false, i.e. the outline of listbox is dependent on browser. It means, we
	 * don't calculate the width of each cell. if set to true, the outline will
	 * count on the content of body. In other words, the outline of listbox will be
	 * like ZK version 2.4.1 that the header's width is only for reference.
	 * 
	 * <p>
	 * You can also specify the "sized-by-content" attribute of component in
	 * lang-addon.xml directly, it will then take higher priority.
	 * 
	 * @param byContent
	 * @since 5.0.0
	 */
	public void setSizedByContent(boolean byContent) {
		if (_sizedByContent != byContent) {
			_sizedByContent = byContent;
			smartUpdate("sizedByContent", byContent);
		}
	}

	/**
	 * Returns whether sizing listbox column width by its content. Default is
	 * false.
	 * <p>
	 * Note: if the "sized-by-content" attribute of component is specified, it's
	 * prior to the original value.
	 * 
	 * @since 5.0.0
	 * @see #setSizedByContent
	 */
	public boolean isSizedByContent() {
		String s = (String) getAttribute("sized-by-content");
		if (s == null) {
			s = (String) getAttribute("fixed-layout");
			return s != null ? !"true".equalsIgnoreCase(s) : _sizedByContent;
		} else
			return "true".equalsIgnoreCase(s);
	}

	/**
	 * Returns {@link Listhead} belonging to this listbox, or null if no list
	 * headers at all.
	 */
	public Listhead getListhead() {
		return _listhead;
	}

	/**
	 * Returns {@link Listhead} belonging to this listbox, or null if no list
	 * headers at all.
	 * 
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listhead getListheadApi() {
		return getListhead();
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
	 * Returns {@link Listfoot} belonging to this listbox, or null if no list
	 * footers at all.
	 * 
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listfoot getListfootApi() {
		return getListfoot();
	}

	/**
	 * Returns a collection of heads, including {@link #getListhead} and
	 * auxiliary heads ({@link Auxhead}) (never null).
	 * 
	 * @since 3.0.0
	 */
	public Collection getHeads() {
		return _heads;
	}

	/**
	 * Returns whether the HTML's select tag is used.
	 */
	/* package */final boolean inSelectMold() {
		return "select".equals(getMold());
	}

	/**
	 * Returns whether the check mark shall be displayed in front of each item.
	 * <p>
	 * Default: false.
	 */
	public final boolean isCheckmark() {
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
	 * called vertial flexibility.
	 * 
	 * <p>
	 * Note: this attribute is ignored if {@link #setRows} is specified
	 * 
	 * <p>
	 * Default: false.
	 */
	public final boolean isVflex() {
		return _vflex;
	}

	/**
	 * Sets whether to grow and shrink vertical to fit their given space, so
	 * called vertial flexibility.
	 * 
	 * <p>
	 * Note: this attribute is ignored if {@link #setRows} is specified
	 */
	public void setVflex(boolean vflex) {
		if (_vflex != vflex) {
			_vflex = vflex;
			smartUpdate("flex", _vflex);
		}
	}

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 */
	public final boolean isDisabled() {
		return _disabled;
	}

	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/**
	 * Returns the tab order of this component.
	 * <p>
	 * Currently, only the "select" mold supports this property.
	 * <p>
	 * Default: -1 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _tabindex;
	}

	/**
	 * Sets the tab order of this component.
	 * <p>
	 * Currently, only the "select" mold supports this property.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if (_tabindex != tabindex) {
			_tabindex = tabindex;
			smartUpdate("tabindex", _tabindex);
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
	 * Note: if both {@link #setHeight} is specified with non-empty,
	 * {@link #setRows} is ignored
	 */
	public void setRows(int rows) throws WrongValueException {
		if (rows < 0)
			throw new WrongValueException("Illegal rows: " + rows);

		if (_rows != rows) {
			_rows = rows;
			smartUpdate("rows", _rows);
		}
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
	 */
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			if (!_multiple && _selItems.size() > 1) {
				final Listitem item = getSelectedItem();
				for (Iterator it = _selItems.iterator(); it.hasNext();) {
					final Listitem li = (Listitem) it.next();
					if (li != item) {
						li.setSelectedDirectly(false);
						it.remove();
					}
				}
				// No need to update selId because multiple will do the job at
				// client
			}

			smartUpdate("multiple", _multiple);
		}
	}

	/**
	 * Returns the UUID of the selected item (it is stored as the selId
	 * attribute of the listbox).
	 */
	private String getSelUuid() {
		final Listitem sel = getSelectedItem();
		return sel != null ? sel.getUuid() : null;
	}

	/**
	 * Returns the maximal length of each item's label.
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

	/**
	 * Returns a live list of all {@link Listitem}. By live we mean you can add
	 * or remove them directly with the List interface. In other words, you
	 * could add or remove an item by manipulating the returned list directly.
	 */
	public List getItems() {
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
		return realindex < 0 || realindex >= _items.size() ? null
				: (Listitem) _items.get(realindex);
	}

	/**
	 * Returns the item at the specified index.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 * 
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listitem getItemAtIndexApi(int index) {
		return getItemAtIndex(index);
	}

	/**
	 * Returns the index of the specified item, or -1 if not found.
	 */
	public int getIndexOfItem(Listitem item) {
		return item == null ? -1 : item.getIndex();
	}

	/**
	 * Returns the index of the specified item, or -1 if not found.
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 */
	public int getIndexOfItemApi(org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		return getIndexOfItem(item);
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
	public void setSelectedIndex(int jsel) {
		final int isz = _items.size();
		final int tsz = _model != null ? _model.getSize() : isz;
		if (jsel >= tsz)
			throw new UiException("Out of bound: " + jsel + " while size="
					+ tsz);

		if (jsel < -1)
			jsel = -1;
		if (jsel < 0) { // unselect all
			clearSelection();
		} else if (jsel != _jsel || (_multiple && _selItems.size() > 1) || !_selItems.contains(getItemAtIndex(_jsel))) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Listitem item = (Listitem) it.next();
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
				} else {
					smartUpdate("jumpTo_", _jsel);
				}
			} else {
				item.setSelectedDirectly(true);
				_selItems.add(item);
			}

			if (_model instanceof Selectable) {
				if (!isLoadingModel()) {
					((Selectable) _model).clearSelection();
				}
				((Selectable) _model).addSelection(_model.getElementAt(_jsel));
			}

			if (inSelectMold()) {
				smartUpdate("selectedIndex", _jsel);
			} else if (item != null)
				smartUpdate("selectedItem", item.getUuid());
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
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #setSelectedItem}.
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 */
	public void selectItemApi(org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		selectItem(item);
	}

	/**
	 * Selects the given item, without deselecting any other items that are
	 * already selected..
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
					if (!inSelectMold())
						smartUpdate("selectedItem", getSelUuid());
				}
				item.setSelectedDirectly(true);
				_selItems.add(item);
				if (_model instanceof Selectable) {
					((Selectable) _model).addSelection(_model.getElementAt(item
							.getIndex()));
				}
				if (inSelectMold()) {
					item.smartUpdate("selected", true);
				} else {
					smartUpdateSelection();
				}
			}
		}
	}

	/**
	 * Selects the given item, without deselecting any other items that are
	 * already selected..
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 */
	public void addItemToSelectionApi(org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		addItemToSelection(item);
	}

	/**
	 * Deselects the given item without deselecting other items.
	 */
	public void removeItemFromSelection(Listitem item) {
		if (item.getParent() != this)
			throw new UiException("Not a child: " + item);

		if (item.isSelected()) {
			if (!_multiple) {
				clearSelection();
			} else {
				if (_model instanceof Selectable && !isLoadingModel()) {
					((Selectable) _model).removeSelection(_model
							.getElementAt(item.getIndex()));
				}

				final int oldSel = _jsel;
				item.setSelectedDirectly(false);
				_selItems.remove(item);
				fixSelectedIndex(0);
				if (inSelectMold()) {
					item.smartUpdate("selected", false);
				} else {
					smartUpdateSelection();
					if (oldSel != _jsel)
						smartUpdate("selectedItem", getSelUuid());
				}
			}
		}
	}

	/**
	 * Deselects the given item without deselecting other items.
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 */
	public void removeItemFromSelectionApi(org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		removeItemFromSelection(item);
	}

	/**
	 * Note: we have to update all selection at once, since addItemToSelection
	 * and removeItemFromSelection might be called interchangeably.
	 */
	private void smartUpdateSelection() {
		final StringBuffer sb = new StringBuffer(80);
		for (Iterator it = _selItems.iterator(); it.hasNext();) {
			if (sb.length() > 0)
				sb.append(',');
			sb.append(((Listitem) it.next()).getUuid());
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
	 * If the specified item is selected, it is deselected. If it is not
	 * selected, it is selected. Other items in the list box that are selected
	 * are not affected, and retain their selected state.
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 */
	public void toggleItemSelectionApi(org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		toggleItemSelection(item);
	}

	/**
	 * Clears the selection.
	 */
	public void clearSelection() {
		if (!_selItems.isEmpty()) {
			for (Iterator it = _selItems.iterator(); it.hasNext();) {
				final Listitem item = (Listitem) it.next();
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
		if (_model instanceof Selectable && !isLoadingModel()) {
			((Selectable) _model).clearSelection();
		}
	}

	/**
	 * Selects all items.
	 */
	public void selectAll() {
		if (!_multiple)
			throw new UiException("Appliable only to the multiple seltype: "
					+ this);

		if (_items.size() != _selItems.size()) {
			for (Iterator it = _items.iterator(); it.hasNext();) {
				final Listitem item = (Listitem) it.next();
				_selItems.add(item);
				item.setSelectedDirectly(true);
			}
			_jsel = _items.isEmpty() ? -1 : 0;
			smartUpdate("selectAll", true);
		}
		if (_model instanceof Selectable) {
			if (_model.getSize() != ((Selectable) _model).getSelection().size()) {
				for (int j = _model.getSize(); --j >= 0;) {
					((Selectable) _model).addSelection(_model.getElementAt(j));
				}
			}
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
		return _jsel >= 0 ? _jsel > 0 && _selItems.size() == 1 ? // optimize for
		// performance
		(Listitem) _selItems.iterator().next()
				: getItemAtIndex(_jsel) : null;
	}

	/**
	 * Returns the selected item.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 * 
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listitem getSelectedItemApi() {
		return getSelectedItem();
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
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #selectItem}.
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 */
	public void setSelectedItemApi(org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		selectItem(item);
	}

	/**
	 * Selects the given listitems.
	 * 
	 * @since 3.6.0
	 */
	public void setSelectedItems(Set listItems) {
		if (!isMultiple())
			throw new WrongValueException(
					"Listbox must allow multiple selections.");
		for (Iterator it = listItems.iterator(); it.hasNext();) {
			addItemToSelection((Listitem) it.next());
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
	public Set getSelectedItems() {
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
	 * Appends an item.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 * 
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listitem appendItemApi(String label, String value) {
		return appendItem(label, value);
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

	/**
	 * Removes the child item in the list box at the given index.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItem}.
	 * 
	 * @since 3.5.2
	 * @return the removed item.
	 */
	public org.zkoss.zul.api.Listitem removeItemAtApi(int index) {
		return removeItemAt(index);
	}

	// --Paging--//
	/**
	 * Sets how to position the paging of listbox at the client screen. It is
	 * meaningless if the mold is not in "paging".
	 * 
	 * @param pagingPosition
	 *            how to position. It can only be "bottom" (the default), or
	 *            "top", or "both".
	 * @since 3.0.4
	 */
	public void setPagingPosition(String pagingPosition) {
		if (pagingPosition == null
				|| (!pagingPosition.equals("top")
						&& !pagingPosition.equals("bottom") && !pagingPosition
						.equals("both")))
			throw new WrongValueException("Unsupported position : "
					+ pagingPosition);
		if (!Objects.equals(_pagingPosition, pagingPosition)) {
			_pagingPosition = pagingPosition;
			smartUpdate("pagingPosition", pagingPosition);
		}
	}

	/**
	 * Returns how to position the paging of listbox at the client screen. It is
	 * meaningless if the mold is not in "paging".
	 * 
	 * @since 3.0.4
	 */
	public String getPagingPosition() {
		return _pagingPosition;
	}

	/**
	 * Returns the paging controller, or null if not available. Note: the paging
	 * controller is used only if {@link #getMold} is "paging".
	 * 
	 * <p>
	 * If mold is "paging", this method never returns null, because a child
	 * paging controller is created automcatically (if not specified by
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

	/*
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
					if (_paging != null)
						_pgi = _paging;
					else
						newInternalPaging();
				} else { // _pgi != null
					if (_pgi != _paging) {
						if (_paging != null)
							_paging.detach();
						_pgi.setTotalSize(getDataLoader().getTotalSize());
						addPagingListener(_pgi);
					}
				}
			}
		}
	}

	/**
	 * Creates the internal paging component.
	 */
	private void newInternalPaging() {
		assert D.OFF || inPagingMold() : "paging mold only";
		assert D.OFF || (_paging == null && _pgi == null);

		final Paging paging = new Paging();
		paging.setAutohide(true);
		paging.setDetailed(true);
		paging.setTotalSize(getDataLoader().getTotalSize());
		paging.setParent(this);
		addPagingListener(_pgi);
	}

	/** Adds the event listener for the onPaging event. */
	private void addPagingListener(Paginal pgi) {
		if (_pgListener == null)
			_pgListener = new EventListener() {
				public void onEvent(Event event) {
					final PagingEvent evt = (PagingEvent) event;
					Events.postEvent(new PagingEvent(evt.getName(),
							Listbox.this, evt.getPageable(), evt
									.getActivePage()));
				}
			};
		pgi.addEventListener(ZulEvents.ON_PAGING, _pgListener);

		if (_pgImpListener == null)
			_pgImpListener = new EventListener() {
				public void onEvent(Event event) {
					if (_model != null && inPagingMold()) {
						final Paginal pgi = getPaginal();
						int pgsz = pgi.getPageSize();
						final int ofs = pgi.getActivePage() * pgsz;
						getDataLoader().syncModel(ofs, pgsz);
						postOnInitRender();
					}
					invalidate();
				}
			};
		pgi.addEventListener("onPagingImpl", _pgImpListener);
	}

	/** Removes the event listener for the onPaging event. */
	private void removePagingListener(Paginal pgi) {
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

	/**
	 * Returns the child paging controller that is created automatically, or
	 * null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 * 
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Paging getPagingChildApi() {
		return getPagingChild();
	}

	/**
	 * Returns the page size, aka., the number items per page.
	 * 
	 * @exception IllegalStateException
	 *                if {@link #getPaginal} returns null, i.e., mold is not
	 *                "paging" and no external controller is specified.
	 */
	public int getPageSize() {
		return pgi().getPageSize();
	}

	/**
	 * Sets the page size, aka., the number items per page.
	 * 
	 * @exception IllegalStateException
	 *                if {@link #getPaginal} returns null, i.e., mold is not
	 *                "paging" and no external controller is specified.
	 */
	public void setPageSize(int pgsz) throws WrongValueException {
		pgi().setPageSize(pgsz);
	}

	/**
	 * Returns the number of pages. Note: there is at least one page even no
	 * item at all.
	 * 
	 * @since 3.0.4
	 */
	public int getPageCount() {
		return pgi().getPageCount();
	}

	/**
	 * Returns the active page (starting from 0).
	 * 
	 * @since 3.0.4
	 */
	public int getActivePage() {
		return pgi().getActivePage();
	}

	/**
	 * Sets the active page (starting from 0).
	 * 
	 * @since 3.0.4
	 * @see #setActivePage(Listitem)
	 */
	public void setActivePage(int pg) throws WrongValueException {
		pgi().setActivePage(pg);
	}

	private Paginal pgi() {
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

	/**
	 * Sets the active page in which the specified item is. The active page will
	 * become the page that contains the specified item.
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 * @see #setActivePage(int)
	 */
	public void setActivePage(org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		setActivePage(item);
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
				pgi.setTotalSize(getDataLoader().getTotalSize());
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
	public List getGroups() {
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

	// -- Component --//
	protected void smartUpdate(String attr, Object value) {
		if (!_noSmartUpdate)
			super.smartUpdate(attr, value);
	}

	/* package */void fixGroupIndex(int j, int to, boolean infront) {
		int realj = getRealIndex(j);
		if (realj < 0) {
			realj = 0;
		}
		final int beginning = j;
		for (Iterator it = _items.listIterator(realj); it.hasNext()
				&& (to < 0 || j <= to); ++j) {
			Object o = it.next();
			((Listitem) o).setIndexDirectly(j);

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
		for (Iterator it = _groupsInfo.iterator(); it.hasNext(); ++j) {
			int[] g = (int[]) it.next();
			if (index == g[0])
				gindex = j;
			else if (index < g[0])
				break;
		}
		return gindex;
	}

	/* package */int[] getGroupsInfoAt(int index) {
		return getGroupsInfoAt(index, false);
	}

	/**
	 * Returns the last groups info which matches with the same index. Because
	 * dynamically maintain the index of the groups will occur the same index at
	 * the same time in the loop.
	 */
	/* package */int[] getLastGroupsInfoAt(int index) {
		int[] rg = null;
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[]) it.next();
			if (index == g[0])
				rg = g;
			else if (index < g[0])
				break;
		}
		return rg;
	}

	/**
	 * Returns an int array that it has two length, one is an index of
	 * listgroup, and the other is the number of items of listgroup(inclusive).
	 */
	/* package */int[] getGroupsInfoAt(int index, boolean isListgroup) {
		for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
			int[] g = (int[]) it.next();
			if (isListgroup) {
				if (index == g[0])
					return g;
			} else if ((index > g[0] && index <= g[0] + g[1]))
				return g;
		}
		return null;
	}

	public void beforeChildAdded(Component newChild, Component refChild) {
		if (newChild instanceof Listitem) {
			if (newChild instanceof Listgroup && inSelectMold())
				throw new UnsupportedOperationException(
						"Unsupported Listgroup in Select mold!");
			if (newChild instanceof Listgroupfoot) {
				if (!hasGroup())
					throw new UiException(
							"Listgroupfoot cannot exist alone, you have to add a Listgroup first");
				if (refChild == null) {
					if (getLastChild() instanceof Listgroupfoot)
						throw new UiException(
								"Only one Listgroupfoot is allowed per Listgroup");
				}
			}
		} else if (newChild instanceof Listhead) {
			if (_listhead != null && _listhead != newChild)
				throw new UiException("Only one listhead is allowed: " + this);
		} else if (newChild instanceof Frozen) {
			if (_frozen != null && _frozen != newChild)
				throw new UiException("Only one frozen child is allowed: "
						+ this);
			if (inSelectMold())
				log.warning("Mold select ignores frozen");
		} else if (newChild instanceof Listfoot) {
			if (_listfoot != null && _listfoot != newChild)
				throw new UiException("Only one listfoot is allowed: " + this);
			if (inSelectMold())
				log.warning("Mold select ignores listfoot");
		} else if (newChild instanceof Paging) {
			if (_paging != null && _paging != newChild)
				throw new UiException("Only one paging is allowed: " + this);
			if (_pgi != null)
				throw new UiException(
						"External paging cannot coexist with child paging");
			if (!inPagingMold())
				throw new UiException(
						"The child paging is allowed only in the paging mold");
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
			if (_rod && hasGroupsModel()) {
				if (_groupsInfo.isEmpty())
					_groupsInfo = ((GroupsListModel) getModel())
							.getGroupsInfo();
				refChild = fixRefChildBeforeFoot(refChild);
				if (super.insertBefore(newChild, refChild)) {
					afterInsert(newChild);
					return true;
				}
				return false;
			}
			final boolean isReorder = newChild.getParent() == this;
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
					final int[] g = (int[]) _groupsInfo
							.get(getGroupCount() - 1);
					
					g[2] = ((Listitem) getItems().get(
							getItems().size() - 1))
							.getIndex();
				} else if (refChild instanceof Listitem) {
					final int idx = ((Listitem) refChild).getIndex();
					final int[] g = getGroupsInfoAt(idx);
					if (g == null)
						throw new UiException(
								"Listgroupfoot cannot exist alone, you have to add a Listgroup first");
					if (g[2] != -1)
						throw new UiException(
								"Only one Listgroupfoot is allowed per Listgroup");
					if (idx != (g[0] + g[1]))
						throw new UiException(
								"Listgroupfoot must be placed after the last Row of the Listgroup");
					g[2] = idx - 1;
					if (isReorder) {
						final int nindex = ((Listgroupfoot) newChild)
								.getIndex();
						final int[] ginfo = getGroupsInfoAt(nindex);
						if (ginfo != null) {
							ginfo[1]--;
							ginfo[2] = -1;
						}
					}
				} else if (refChild.getPreviousSibling() instanceof Listitem) {
					final int idx = ((Listitem) refChild.getPreviousSibling())
							.getIndex();
					final int[] g = getGroupsInfoAt(idx);
					if (g == null)
						throw new UiException(
								"Listgroupfoot cannot exist alone, you have to add a Listgroup first");
					if (g[2] != -1)
						throw new UiException(
								"Only one Listgroupfoot is allowed per Listgroup");
					if (idx + 1 != (g[0] + g[1]))
						throw new UiException(
								"Listgroupfoot must be placed after the last Row of the Listgroup");
					g[2] = idx;
					if (isReorder) {
						final int nindex = ((Listgroupfoot) newChild)
								.getIndex();
						final int[] ginfo = getGroupsInfoAt(nindex);
						if (ginfo != null) {
							ginfo[1]--;
							ginfo[2] = -1;
						}
					}
				}
			}
			// first: listhead or auxhead
			// last two: listfoot and paging
			if (refChild != null && refChild.getParent() != this)
				refChild = null; // Bug 1649625: it becomes the last child
			if (refChild != null
					&& (refChild == _listhead || refChild instanceof Auxhead))
				refChild = getChildren().size() > _hdcnt ? (Component) getChildren()
						.get(_hdcnt)
						: null;

			refChild = fixRefChildBeforeFoot(refChild);
			final Listitem newItem = (Listitem) newChild;
			final int jfrom = newItem.getParent() == this ? newItem.getIndex()
					: -1;

			if (super.insertBefore(newChild, refChild)) {
				// Maintain _items
				final int jto = refChild instanceof Listitem ? ((Listitem) refChild)
						.getIndex()
						: -1, fixFrom = jfrom < 0 || (jto >= 0 && jfrom > jto) ? jto
						: jfrom;
				// jfrom < 0: use jto
				// jto < 0: use jfrom
				// otherwise: use min(jfrom, jto)
				if (fixFrom < 0)
					newItem.setIndexDirectly(_items.size() - 1
							+ getDataLoader().getOffset());
				else
					fixGroupIndex(fixFrom,
							jfrom >= 0 && jto >= 0 ? jfrom > jto ? jfrom : jto
									: -1, !isReorder);

				// Maintain selected
				final int newIndex = newItem.getIndex();
				if (newItem.isSelected()) {
					if (_jsel < 0) {
						_jsel = newIndex;
						if (!inSelectMold())
							smartUpdate("selectedItem", getSelUuid());
						_selItems.add(newItem);
						if (_model instanceof Selectable) {
							((Selectable) _model).addSelection(_model
									.getElementAt(_jsel));
						}
					} else if (_multiple) {
						if (_jsel > newIndex) {
							_jsel = newIndex;
							if (!inSelectMold())
								smartUpdate("selectedItem", getSelUuid());
						}
						_selItems.add(newItem);
						if (_model instanceof Selectable) {
							((Selectable) _model).addSelection(_model
									.getElementAt(_jsel));
						}
					} else { // deselect
						newItem.setSelectedDirectly(false);
					}
				} else {
					final int oldjsel = _jsel;
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

					if (oldjsel != _jsel && !inSelectMold())
						smartUpdate("selectedItem", getSelUuid());
				}

				if (newChild instanceof Listgroup) {
					Listgroup lg = (Listgroup) newChild;
					if (_groupsInfo.isEmpty())
						_groupsInfo.add(new int[] { lg.getIndex(),
								getItemCount() - lg.getIndex(), -1 });
					else {
						int idx = 0;
						int[] prev = null, next = null;
						for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
							int[] g = (int[]) it.next();
							if (g[0] <= lg.getIndex()) {
								prev = g;
								idx++;
							} else {
								next = g;
								break;
							}
						}
						if (prev != null) {
							int index = lg.getIndex(), leng = index - prev[0], size = prev[1]
									- leng + 1;
							prev[1] = leng;
							_groupsInfo
									.add(
											idx,
											new int[] {
													index,
													size,
													size > 1 && prev[2] > index ? prev[2]
															: -1 });
							if (size > 1 && prev[2] > index)
								prev[2] = -1; // reset listgroupfoot
						} else if (next != null) {
							_groupsInfo.add(idx, new int[] { lg.getIndex(),
									next[0] - lg.getIndex(), -1 });
						}
					}
				} else if (!_groupsInfo.isEmpty()) {
					int index = newItem.getIndex();
					final int[] g = getGroupsInfoAt(index);
					if (g != null) {
						g[1]++;
						if (g[2] != -1
								&& (g[2] >= index || newItem instanceof Listgroupfoot))
							g[2]++;
					}
				}
				afterInsert(newChild);
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
				invalidate();
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
				invalidate();
				// we place listfoot and treeitem at different div, so...
				_listfoot = (Listfoot) newChild;
				return true;
			}
		} else if (newChild instanceof Paging) {
			refChild = null; // the last: paging
			if (super.insertBefore(newChild, refChild)) {
				invalidate();
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
		if (refChild == null
				|| (refChild != _listhead && !(refChild instanceof Auxhead)))
			refChild = getChildren().size() > _hdcnt ? (Component) getChildren()
					.get(_hdcnt)
					: null;

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
					if (!inSelectMold())
						smartUpdate("selectedItem", getSelUuid());
				}
			} else {
				if (!isLoadingModel() && _jsel >= index) {
					--_jsel;
					if (!inSelectMold())
						smartUpdate("selectedItem", getSelUuid());
				}
			}
			if (child instanceof Listgroup) {
				int[] prev = null, remove = null;
				for (Iterator it = _groupsInfo.iterator(); it.hasNext();) {
					int[] g = (int[]) it.next();
					if (g[0] == index) {
						remove = g;
						break;
					}
					prev = g;
				}
				if (prev != null && remove != null) {
					prev[1] += remove[1] - 1;
				}
				fixGroupIndex(index, -1, false);
				if (remove != null) {
					_groupsInfo.remove(remove);
					final int idx = remove[2];
					if (idx != -1) {
						final int realIndex = getRealIndex(idx) - 1;
						if (realIndex >= 0 && realIndex < getItemCount())
							removeChild((Component) getChildren()
									.get(realIndex));
					}
				}
			} else if (!_groupsInfo.isEmpty()) {
				final int[] g = getGroupsInfoAt(index);
				if (g != null) {
					g[1]--;
					if (g[2] != -1)
						g[2]--;
					fixGroupIndex(index, -1, false);
				} else
					fixGroupIndex(index, -1, false);

				if (child instanceof Listgroupfoot) {
					final int[] g1 = getGroupsInfoAt(index);
					g1[2] = -1;
				}
			} else
				fixItemIndices(index, -1);

			if (hasGroupsModel() && getItemCount() <= 0) { // remove to empty,
				// reset _groupsInfo
				_groupsInfo = new LinkedList();
			}
			getDataLoader().updateModelInfo(); //itemsInvalidate after really removed
			return true;
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
	
	private boolean isSyncingModel() {
		return getAttribute(SYNCING_MODEL) != null;
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
		if (comp instanceof Listitem && _model instanceof Selectable && (isLoadingModel() || isSyncingModel())) {
			final Listitem item = (Listitem) comp;
			if (((Selectable) _model).getSelection().contains(
					_model.getElementAt(item.getIndex()))) {
				addItemToSelection(item);
			}
		}
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
			if (item instanceof Listgroupfoot || item instanceof Listgroup
					|| g == null || g.isOpen())
				addVisibleItemCount(isRemove ? -1 : 1);

			if (item instanceof Listgroup) {
				final Listgroup group = (Listgroup) item;

				// If the previous group exists, we shall update the number of
				// the visible item from the number of the visible item of the
				// current group.
				if (item.getPreviousSibling() instanceof Listitem) {
					final Listitem preRow = (Listitem) item
							.getPreviousSibling();
					if (preRow == null) {
						if (!group.isOpen()) {
							addVisibleItemCount(isRemove ? group
									.getVisibleItemCount() : -group
									.getVisibleItemCount());
						}
					} else {
						final Listgroup preGroup = preRow instanceof Listgroup ? (Listgroup) preRow
								: getListgroupAt(preRow.getIndex());
						if (preGroup != null) {
							if (!preGroup.isOpen() && group.isOpen())
								addVisibleItemCount(isRemove ? -group
										.getVisibleItemCount() : group
										.getVisibleItemCount());
							else if (preGroup.isOpen() && !group.isOpen())
								addVisibleItemCount(isRemove ? group
										.getVisibleItemCount() : -group
										.getVisibleItemCount());
						} else {
							if (!group.isOpen())
								addVisibleItemCount(isRemove ? group
										.getVisibleItemCount() : -group
										.getVisibleItemCount());
						}
					}
				} else if (!group.isOpen()) {
					addVisibleItemCount(isRemove ? group.getVisibleItemCount()
							: -group.getVisibleItemCount());
				}
			}
		}
		if (inPagingMold())
			getPaginal().setTotalSize(getDataLoader().getTotalSize());
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
			final int j = child.getIndex(), pgsz = getPageSize(), n = (getActivePage() + 1)
					* pgsz;
			if (j >= n)
				return; // case 2

			final int cnt = getItems().size(), n2 = n - pgsz;
			if (j >= n2 && cnt <= n && (!bRemove || cnt > n2 + 1))
				return; // case 1

			invalidate();
		}
	}

	/**
	 * Returns an iterator to iterate thru all visible children. Unlike
	 * {@link #getVisibleItemCount}, it handles only the direct children.
	 * Component developer only.
	 * 
	 * @since 3.5.1
	 */
	public Iterator getVisibleChildrenIterator() {
		return new VisibleChildrenIterator();
	}

	/**
	 * An iterator used by visible children.
	 */
	private class VisibleChildrenIterator implements Iterator {
		private final ListIterator _it = getItems().listIterator();
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
					for (int j = 0, len = g.getItemCount(); j < len
							&& _it.hasNext(); j++)
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
			for (Iterator it = _items.listIterator(realj); it.hasNext(); ++j) {
				final Listitem item = (Listitem) it.next();
				if (item.isSelected()) {
					_jsel = j;
					return;
				}
			}
		}
		_jsel = -1;
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
		for (Iterator it = _items.listIterator(realj); it.hasNext()
				&& (to < 0 || j <= to); ++j)
			((Listitem) it.next()).setIndexDirectly(j);
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
	public ListModel getModel() {
		return _model;
	}

	/**
	 * Returns the list model associated with this list box, or null if this
	 * list box is associated with a {@link GroupsModel} or not associated with
	 * any list data model.
	 * 
	 * @since 3.5.0
	 * @see #setModel(ListModel)
	 */
	public ListModel getListModel() {
		return _model instanceof GroupsListModel ? null : _model;
	}

	/**
	 * Returns the groups model associated with this list box, or null if this
	 * list box is associated with a {@link ListModel} or not associated with
	 * any list data model.
	 * 
	 * @since 3.5.0
	 * @see #setModel(GroupsModel)
	 */
	public GroupsModel getGroupsModel() {
		return _model instanceof GroupsListModel ? ((GroupsListModel) _model)
				.getGroupsModel() : null;
	}

	/**
	 * Sets the list model associated with this listbox. If a non-null model is
	 * assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 * 
	 * @param model
	 *            the list model to associate, or null to dis-associate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 * @see #getListModel
	 * @see #setModel(GroupsModel)
	 */
	public void setModel(ListModel model) {
		if (model != null) {
			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
					if (_model instanceof GroupsListModel)
						getItems().clear();
				} else {
					getItems().clear(); // Bug 1807414
					if (!inSelectMold())
						smartUpdate("model", true);
				}

				_model = model;
				initDataListener();
			}

			final boolean defer = Executions.getCurrent().getAttribute("zkoss.Listbox.deferInitModel_"+getUuid()) != null;
			final boolean rod = evalRod();
			//Always syncModel because it is easier for user to enfore reload
			if (!defer || !rod) { //if attached and rod, defer the model sync
				getDataLoader().syncModel(-1, -1);
				postOnInitRender();
			}
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
	public void setModel(GroupsModel model) {
		setModel((ListModel) (model != null ? new GroupsListModel(model) : null));
	}

	/**
	 * Returns the renderer to render each item, or null if the default renderer
	 * is used.
	 */
	public ListitemRenderer getItemRenderer() {
		return _renderer;
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
	public void setItemRenderer(ListitemRenderer renderer) {
		if (_renderer != renderer) {
			_renderer = renderer;

			if (_model != null) {
				if ((renderer instanceof ListitemRendererExt)
						|| (_renderer instanceof ListitemRendererExt)) {
					// bug# 2388345, a new renderer that might new own Listitem,
					// shall clean all Listitems first
					getItems().clear();
					getDataLoader().syncModel(-1, -1); // we have to recreate
					// all
				} else if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
					getDataLoader().syncModel(-1, -1); // have to recreate all
				}
			}
		}
	}

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setItemRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setItemRenderer((ListitemRenderer) Classes
					.newInstanceByThread(clsnm));
	}

	/**
	 * Returns the number of items to preload when receiving the rendering
	 * request from the client.
	 * 
	 * <p>
	 * Default: 7.
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

	/**
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

		final Renderer renderer = new Renderer();
		try {
			int pgsz, ofs;
			if (inPagingMold()) {
				pgsz = _pgi.getPageSize();
				ofs = _pgi.getActivePage() * pgsz;
			} else {
				pgsz = inSelectMold() ? getItemCount() : getDataLoader()
						.getLimit();
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
			for (Iterator it = getItems().listIterator(realOfs); j < pgsz
					&& it.hasNext();) {
				final Listitem item = (Listitem) it.next();
				if (item.isVisible()
						&& (open || item instanceof Listgroupfoot || item instanceof Listgroup)) {
					renderer.render(item);
					++j;
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
	}

	private void postOnInitRender() {
		// 20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
		}
	}

	/**
	 * Handles when the list model's content changed.
	 */
	private void onListDataChange(ListDataEvent event) {
		getDataLoader().doListDataChange(event);
		postOnInitRender(); // to improve performance
	}

	/** Used to render listitem if _model is specified. */
	/* package */class Renderer { // use package for easy to call (if override)
		private final ListitemRenderer _renderer;
		private boolean _rendered, _ctrled;

		/* package */Renderer() {
			_renderer = (ListitemRenderer) getDataLoader().getRealRenderer();
		}

		/* package */void render(Listitem item) throws Throwable {
			if (item.isLoaded())
				return; // nothing to do

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
				renderer.render(li);
			} catch (Throwable ex) {
				renderer.doCatch(ex);
			} finally {
				renderer.doFinally();
			}
		}
		return li;
	}

	/**
	 * Renders the specified {@link Listitem} if not loaded yet, with
	 * {@link #getItemRenderer}.
	 * 
	 * <p>
	 * It does nothing if {@link #getModel} returns null. In other words, it is
	 * meaningful only if live data model is used.
	 * 
	 * @param itemApi
	 *            assume as a {@link org.zkoss.zul.Listitem}
	 * @since 3.5.2
	 * @see #renderItems
	 * @see #renderAll
	 * @return the list item being passed to this method
	 */
	public org.zkoss.zul.api.Listitem renderItemApi(
			org.zkoss.zul.api.Listitem itemApi) {
		Listitem item = (Listitem) itemApi;
		return renderItem(item);
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

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = getItems().iterator(); it.hasNext();)
				renderer.render((Listitem) it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	public void renderItems(Set items) {
		if (_model == null)
			return;

		if (items.isEmpty())
			return; // nothing to do

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = items.iterator(); it.hasNext();)
				renderer.render((Listitem) it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	// -- super --//
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
					getDataLoader().syncModel(0, 40); // change offset back to 0
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
				Events.postEvent(new PagingEvent("onPagingImpl",
						(Component) _pgi, _pgi.getActivePage()));
				invalidate(); // non-paging mold -> paging mold
			}
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-listbox" : _zclass;
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
			if (_it == null)
				throw new IllegalStateException();
			_it.remove();
			if (_bNxt)
				--_j;
		}

		public void set(Object o) {
			if (_it == null)
				throw new IllegalStateException();
			_it.set(o);
		}

		private void prepare() {
			if (_it == null)
				_it = getChildren().listIterator(_j + _hdcnt);
		}
	}

	private boolean evalRod() {
		final String rod1 = org.zkoss.lang.Library.getProperty(
				"org.zkoss.zul.listbox.rod", "false");
		String rod2 = (String) getAttribute("org.zkoss.zul.listbox.rod");
		if (rod2 == null) {
			rod2 = rod1;
		}
		return "true".equals(rod2);
	}

	/* package */DataLoader getDataLoader() {
		if (_dataLoader == null) {
			_rod = evalRod();
			final String loadercls = (String) getAttribute("listbox-dataloader");
			try {
				_dataLoader = _rod && loadercls != null ? (DataLoader) Classes
						.forNameByThread(loadercls).newInstance()
						: new ListboxDataLoader();
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
			_dataLoader.init(this, 0, 40);
		}
		return _dataLoader;
	}

	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (oldpage == null) { // mark as a new attached Listbox
			Executions.getCurrent().setAttribute("zkoss.Listbox.deferInitModel_"+getUuid(), Boolean.TRUE);
			Executions.getCurrent().setAttribute("zkoss.Listbox.attached_"+getUuid(), Boolean.TRUE);
			// prepare a right moment to init Listbox (must be as late as
			// possible)
			this.addEventListener("onInitListbox",
					_listboxInitListener = new EventListener() {
						public void onEvent(Event event) throws Exception {
							if (_listboxInitListener != null) {
								Listbox.this.removeEventListener(
										"onInitListbox", _listboxInitListener);
								_listboxInitListener = null;
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
										_dataLoader = null; // enforce recreate
										Executions.getCurrent().removeAttribute("zkoss.Listbox.deferInitModel_"+getUuid());
										setModel(_model);
									} else {
										_dataLoader = null; // enforce recreate
										// dataloader
									}
								}
							}
							final DataLoader loader = getDataLoader();

							// initialize paginal if any
							Paginal pgi = getPaginal();
							if (pgi != null)
								pgi.setTotalSize(loader.getTotalSize());
						}
					});
			Events.postEvent(-50000, new Event("onInitListbox", this));
		}
	}

	// Cloneable//
	public Object clone() {
		final Listbox clone = (Listbox) super.clone();
		clone.init();
		clone.afterUnmarshal();
		if (clone._model != null) {
			// we use the same data model but we have to create a new listener
			clone._dataListener = null;
			clone.initDataListener();
		}
		return clone;
	}

	private void afterUnmarshal() {
		//recreate the DataLoader 
		final int offset = getDataLoader().getOffset(); 
		final int limit = getDataLoader().getLimit();
		_dataLoader = null;
		getDataLoader().init(this, offset, limit);
		
		int index = offset;
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Listitem) {
				final Listitem li = (Listitem) child;
				li.setIndexDirectly(index++); // since Listitem.clone() resets
				// index
				if (li.isSelected()) {
					_selItems.add(li);
					if (_model instanceof Selectable) {
						((Selectable) _model).addSelection(_model
								.getElementAt(index - 1));
					}
				}
			} else if (child instanceof Listhead) {
				_listhead = (Listhead) child;
			} else if (child instanceof Listfoot) {
				_listfoot = (Listfoot) child;
			} else if (child instanceof Frozen) {
				_frozen = (Frozen) child;
			} else if (child instanceof Paging) {
				_pgi = _paging = (Paging) child;
			}
		}
	}

	// -- Serializable --//
	// NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.defaultWriteObject();

		willSerialize(_model);
		s.writeObject(_model instanceof java.io.Serializable
				|| _model instanceof java.io.Externalizable ? _model : null);
		willSerialize(_renderer);
		s.writeObject(_renderer instanceof java.io.Serializable
				|| _renderer instanceof java.io.Externalizable ? _renderer
				: null);
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_model = (ListModel) s.readObject();
		didDeserialize(_model);
		_renderer = (ListitemRenderer) s.readObject();
		didDeserialize(_renderer);

		init();
		afterUnmarshal();
		// TODO: how to marshal _pgi if _pgi != _paging
		// TODO: re-register event listener for onPaging

		if (_model != null)
			initDataListener();
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
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);

		if (_rows > 0)
			renderer.render("rows", getRows());

		if (inSelectMold()) {
			render(renderer, "name", _name);
			render(renderer, "multiple", isMultiple());
			render(renderer, "disabled", isDisabled());
			if (_tabindex >= 0)
				renderer.render("tabindex", getTabindex());
			if (_maxlength > 0)
				renderer.render("maxlength", getMaxlength());
		} else {
			render(renderer, "oddRowSclass", _scOddRow);

			if (isSizedByContent())
				renderer.render("sizedByContent", true);

			render(renderer, "vflex", _vflex);

			render(renderer, "checkmark", isCheckmark());
			render(renderer, "multiple", isMultiple());

			if (_model != null)
				render(renderer, "model", true);

			if (!"bottom".equals(_pagingPosition))
				render(renderer, "pagingPosition", _pagingPosition);
			if (!"100%".equals(_innerWidth))
				render(renderer, "innerWidth", _innerWidth);
			if (_currentTop != 0)
				renderer.render("_currentTop", _currentTop);
			if (_currentLeft != 0)
				renderer.render("_currentLeft", _currentLeft);

			renderer.render("_topPad", _topPad);
			renderer.render("_totalSize", getDataLoader().getTotalSize());
			renderer.render("_offset", getDataLoader().getOffset());

			if (_rod) {
				renderer.render("_listbox$rod", true);
			}
		}
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
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals("onDataLoading")) {
			if (_rod)
				Executions.getCurrent().setAttribute("zkoss.zul.listbox.onDataLoading."+this.getUuid(), Boolean.TRUE); //indicate doing dataloading
			Events.postEvent(DataLoadingEvent.getDataLoadingEvent(request,
					getPreloadSize()));
		} else if (cmd.equals("onScrollPos")) {
			final Map data = request.getData();
			_currentTop = AuRequests.getInt(data, "top", 0);
			_currentLeft = AuRequests.getInt(data, "left", 0);
		} else if (cmd.equals("onTopPad")) {
			_topPad = AuRequests.getInt(request.getData(), "topPad", 0);
		} else if (cmd.equals(Events.ON_SELECT)) {
			if (_rod && Executions.getCurrent().getAttribute("zkoss.zul.listbox.onDataLoading."+this.getUuid()) != null) //indicate doing dataloading
				return; //skip all onSelect event after the onDataLoading
			SelectEvent evt = SelectEvent.getSelectEvent(request);
			Set selItems = evt.getSelectedItems();
			_noSmartUpdate = true;
			try {
				if (AuRequests.getBoolean(request.getData(), "clearFirst"))
					clearSelection();
				final boolean paging = inPagingMold();
				if (!_multiple
						|| (!paging && (selItems == null || selItems.size() <= 1))) {
					final Listitem item = selItems != null
							&& selItems.size() > 0 ? (Listitem) selItems
							.iterator().next() : null;
					selectItem(item);
				} else {
					int from, to;
					if (paging) {
						final Paginal pgi = getPaginal();
						int pgsz = pgi.getPageSize();
						from = pgi.getActivePage() * pgsz;
						to = from + pgsz; // excluded
					} else {
						from = to = 0;
					}

					int j = 0;
					for (Iterator it = _items.iterator(); it.hasNext(); ++j) {
						final Listitem item = (Listitem) it.next();
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

			Events.postEvent(evt);
		} else if (cmd.equals("onInnerWidth")) {
			final String width = AuRequests.getInnerWidth(request);
			_innerWidth = width == null ? "100%" : width;
		} else if (cmd.equals(Events.ON_RENDER)) {
			final Set items = AuRequests.convertToItems(request.getDesktop(),
					(List) request.getData().get("items"));
			int cnt = items.size();
			if (cnt == 0)
				return; // nothing to do

			cnt = 20 - cnt;
			if (cnt > 0 && _preloadsz > 0) { // Feature 1740072: pre-load
				if (cnt > _preloadsz)
					cnt = _preloadsz; // at most 8 more to load

				// 1. locate the first item found in items
				final List toload = new LinkedList();
				Iterator it = _items.iterator();
				while (it.hasNext()) {
					final Listitem li = (Listitem) it.next();
					if (items.contains(li)) // found
						break;
					if (!li.isLoaded())
						toload.add(0, li); // reverse order
				}

				// 2. add unload items before the found one
				if (!toload.isEmpty()) {
					int bfcnt = cnt / 3;
					for (Iterator e = toload.iterator(); bfcnt > 0
							&& e.hasNext(); --bfcnt, --cnt) {
						items.add(e.next());
					}
				}

				// 3. add unloaded after the found one
				while (cnt > 0 && it.hasNext()) {
					final Listitem li = (Listitem) it.next();
					if (!li.isLoaded() && items.add(li))
						--cnt;
				}
			}

			Listbox.this.renderItems(items);
		} else
			super.service(request, everError);
	}

	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	/**
	 * A utility class to implement {@link #getExtraCtrl}. It is used only by
	 * component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements Cropper,
			Padding {
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

		public Set getAvailableAtClient() {
			return ((Cropper) getDataLoader()).getAvailableAtClient();
		}
	}

	/**
	 * An iterator used by _heads.
	 */
	private class Iter implements Iterator {
		private final Iterator _it = getChildren().iterator();
		private int _j;

		public boolean hasNext() {
			return _j < _hdcnt;
		}

		public Object next() {
			final Object o = _it.next();
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
	private class IterGroups implements Iterator {
		private final Iterator _it = _groupsInfo.iterator();
		private int _j;

		public boolean hasNext() {
			return _j < getGroupCount();
		}

		public Object next() {
			final Object o = getItemAtIndex(((int[]) _it.next())[0]);
			++_j;
			return o;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
