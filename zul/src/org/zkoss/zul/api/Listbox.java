/* Listbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.event.ListDataEvent;//for javadoc
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Paginated;

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
 * adds a label to a row by calling toString against the object returned by
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
 * assigned explicity by {@link #setPaginal}. The paging controller specified
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
 * @author tomyeh
 * @see ListModel
 * @see ListitemRenderer
 * @see org.zkoss.zul.ListitemRendererExt
 * @since 3.5.2
 */
public interface Listbox extends org.zkoss.zul.impl.api.XulElement, Paginated {

	/**
	 * Sets the outline of listbox whether is fixed layout. If true, the outline
	 * of listbox will be depended on browser. It means, we don't calculate the
	 * width of each cell. Otherwise, the outline will count on the content of
	 * body. In other words, the outline of listbox is like ZK 2.4.1 version
	 * that the header's width is only for reference.
	 * 
	 * <p>
	 * You can also specify the "fixed-layout" attribute of component in
	 * lang-addon.xml directly, it's a top priority.
	 * 
	 */
	public void setFixedLayout(boolean fixedLayout);

	/**
	 * Returns the outline of list box whether is fixed layout.
	 * <p>
	 * Default: false.
	 * <p>
	 * Note: if the "fixed-layout" attribute of component is specified, it's
	 * prior to the original value.
	 * 
	 */
	public boolean isFixedLayout();

	/**
	 * Returns {@link Listhead} belonging to this listbox, or null if no list
	 * headers at all.
	 */
	public org.zkoss.zul.api.Listhead getListheadApi();

	/**
	 * Returns {@link Listfoot} belonging to this listbox, or null if no list
	 * footers at all.
	 */
	public org.zkoss.zul.api.Listfoot getListfootApi();

	/**
	 * Returns a collection of heads, including {@link #getListheadApi} and
	 * auxiliary heads ({@link org.zkoss.zul.Auxhead}) (never null).
	 * 
	 */
	public Collection getHeads();

	/**
	 * Sets whether the check mark shall be displayed in front of each item.
	 * <p>
	 * The check mark is a checkbox if {@link #isMultiple} returns true. It is a
	 * radio button if {@link #isMultiple} returns false.
	 */
	public void setCheckmark(boolean checkmark);

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
	 */
	public void setInnerWidth(String innerWidth);

	/**
	 * Returns the inner width of this component. The inner width is the width
	 * of the inner table.
	 * <p>
	 * Default: "100%"
	 * 
	 * @see #setInnerWidth
	 */
	public String getInnerWidth();

	/**
	 * Sets whether to grow and shrink vertical to fit their given space, so
	 * called vertial flexibility.
	 * 
	 * <p>
	 * Note: this attribute is ignored if {@link #setRows} is specified
	 */
	public void setVflex(boolean vflex);

	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns the tab order of this component.
	 * <p>
	 * Currently, only the "select" mold supports this property.
	 * <p>
	 * Default: -1 (means the same as browser's default).
	 */
	public int getTabindex();

	/**
	 * Sets the tab order of this component.
	 * <p>
	 * Currently, only the "select" mold supports this property.
	 */
	public void setTabindex(int tabindex) throws WrongValueException;

	/**
	 * Returns the rows. Zero means no limitation.
	 * <p>
	 * Default: 0.
	 */
	public int getRows();

	/**
	 * Sets the rows.
	 * <p>
	 * Note: if both {@link #setHeight} is specified with non-empty,
	 * {@link #setRows} is ignored
	 */
	public void setRows(int rows) throws WrongValueException;

	/**
	 * Returns the seltype.
	 * <p>
	 * Default: "single".
	 */
	public String getSeltype();

	/**
	 * Sets the seltype.
	 */
	public void setSeltype(String seltype) throws WrongValueException;

	/**
	 * Returns whether multiple selections are allowed.
	 * <p>
	 * Default: false.
	 */
	public boolean isMultiple();

	/**
	 * Sets whether multiple selections are allowed.
	 */
	public void setMultiple(boolean multiple);

	/**
	 * Returns the maximal length of each item's label.
	 */
	public int getMaxlength();

	/**
	 * Sets the maximal length of each item's label.
	 */
	public void setMaxlength(int maxlength);

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
	public String getName();

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
	public void setName(String name);

	/**
	 * Returns a live list of all {@link Listitem}. By live we mean you can add
	 * or remove them directly with the List interface. In other words, you
	 * could add or remove an item by manipulating the returned list directly.
	 */
	public List getItems();

	/**
	 * Returns the number of items.
	 */
	public int getItemCount();

	/**
	 * Returns the item at the specified index.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItemApi}.
	 */
	public org.zkoss.zul.api.Listitem getItemAtIndexApi(int index);

	/**
	 * Returns the index of the specified item, or -1 if not found.
	 */
	public int getIndexOfItemApi(Listitem item);

	/**
	 * Returns the index of the selected item (-1 if no one is selected).
	 */
	public int getSelectedIndex();

	/**
	 * Deselects all of the currently selected items and selects the item with
	 * the given index.
	 */
	public void setSelectedIndex(int jsel);

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #setSelectedItemApi}.
	 * 
	 * @param item
	 *            the item to select. If null, all items are deselected.
	 */
	public void selectItemApi(Listitem item);

	/**
	 * Selects the given item, without deselecting any other items that are
	 * already selected..
	 */
	public void addItemToSelectionApi(Listitem item);

	/**
	 * Deselects the given item without deselecting other items.
	 */
	public void removeItemFromSelectionApi(Listitem item);

	/**
	 * If the specified item is selected, it is deselected. If it is not
	 * selected, it is selected. Other items in the list box that are selected
	 * are not affected, and retain their selected state.
	 */
	public void toggleItemSelectionApi(Listitem item);

	/**
	 * Clears the selection.
	 */
	public void clearSelection();

	/**
	 * Selects all items.
	 */
	public void selectAll();

	/**
	 * Returns the selected item.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItemApi}.
	 */
	public org.zkoss.zul.api.Listitem getSelectedItemApi();

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #selectItemApi}.
	 */
	public void setSelectedItemApi(org.zkoss.zul.api.Listitem item);

	/**
	 * Returns all selected items.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItemApi}.
	 */
	public Set getSelectedItems();

	/**
	 * Returns the number of items being selected.
	 */
	public int getSelectedCount();

	/**
	 * Appends an item.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItemApi}.
	 */
	public org.zkoss.zul.api.Listitem appendItemApi(String label, String value);

	/**
	 * Removes the child item in the list box at the given index.
	 * 
	 * <p>
	 * Note: if live data is used ({@link #getModel} is not null), the returned
	 * item might NOT be loaded yet. To ensure it is loaded, you have to invoke
	 * {@link #renderItemApi}.
	 * 
	 * @return the removed item.
	 */
	public org.zkoss.zul.api.Listitem removeItemAtApi(int index);

	// --Paging--//
	/**
	 * Sets how to position the paging of listbox at the client screen. It is
	 * meaningless if the mold is not in "paging".
	 * 
	 * @param pagingPosition
	 *            how to position. It can only be "bottom" (the default), or
	 *            "top", or "both".
	 */
	public void setPagingPosition(String pagingPosition);

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
	public Paginal getPaginal();

	/**
	 * Specifies the paging controller. Note: the paging controller is used only
	 * if {@link #getMold} is "paging".
	 * 
	 * <p>
	 * It is OK, though without any effect, to specify a paging controller even
	 * if mold is not "paging".
	 * 
	 * @param pgi
	 *            the paging controller. If null and {@link #getMold} is
	 *            "paging", a paging controller is created automatically as a
	 *            child component (see {@link #getPagingApi}).
	 */
	public void setPaginal(Paginal pgi);

	/**
	 * Returns the child paging controller that is created automatically, or
	 * null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 * 
	 */
	public org.zkoss.zul.api.Paging getPagingChildApi();

	public org.zkoss.zul.api.Paging getPagingApi();

	/**
	 * Returns the page size, aka., the number items per page.
	 * 
	 * @exception IllegalStateException
	 *                if {@link #getPaginal} returns null, i.e., mold is not
	 *                "paging" and no external controller is specified.
	 */
	public int getPageSize();

	/**
	 * Sets the page size, aka., the number items per page.
	 * 
	 * @exception IllegalStateException
	 *                if {@link #getPaginal} returns null, i.e., mold is not
	 *                "paging" and no external controller is specified.
	 */
	public void setPageSize(int pgsz) throws WrongValueException;

	/**
	 * Returns the number of pages. Note: there is at least one page even no
	 * item at all.
	 * 
	 */
	public int getPageCount();

	/**
	 * Returns the active page (starting from 0).
	 * 
	 */
	public int getActivePage();

	/**
	 * Sets the active page (starting from 0).
	 * 
	 * @see #setActivePage(Listitem)
	 */
	public void setActivePage(int pg) throws WrongValueException;

	/**
	 * Sets the active page in which the specified item is. The active page will
	 * become the page that contains the specified item.
	 * 
	 * @param item
	 *            the item to show. If the item is null or doesn't belong to
	 *            this listbox, nothing happens.
	 * @see #setActivePage(int)
	 */
	public void setActivePage(Listitem item);

	/**
	 * Returns the number of visible descendant {@link Listitem}.
	 * 
	 */
	public int getVisibleItemCount();

	/**
	 * Returns the style class for the odd rows.
	 * <p>
	 * Default: {@link #getZclass()}-odd. (since 3.5.0)
	 * 
	 */
	public String getOddRowSclass();

	/**
	 * Sets the style class for the odd rows. If the style class doesn't exist,
	 * the striping effect disappears. You can provide different effects by
	 * providing the proper style classes.
	 * 
	 */
	public void setOddRowSclass(String scls);

	/**
	 * Returns the number of listgroup
	 * 
	 */
	public int getGroupCount();

	/**
	 * Returns a list of all {@link Listgroup}.
	 * 
	 */
	public List getGroups();

	/**
	 * Returns whether listgroup exists.
	 * 
	 */
	public boolean hasGroup();

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
	public ListModel getModel();

	/**
	 * Returns the list model associated with this list box, or null if this
	 * list box is associated with a {@link GroupsModel} or not associated with
	 * any list data model.
	 * 
	 * @see #setModel(ListModel)
	 */
	public ListModel getListModel();

	/**
	 * Returns the groups model associated with this list box, or null if this
	 * list box is associated with a {@link ListModel} or not associated with
	 * any list data model.
	 * 
	 * @see #setModel(GroupsModel)
	 */
	public GroupsModel getGroupsModel();

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
	public void setModel(ListModel model);

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
	 * @see #setModel(ListModel)
	 * @see #getGroupsModel()
	 */
	public void setModel(GroupsModel model);

	/**
	 * Returns the renderer to render each item, or null if the default renderer
	 * is used.
	 */
	public ListitemRenderer getItemRenderer();

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
	public void setItemRenderer(ListitemRenderer renderer);

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setItemRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException;

	/**
	 * Returns the number of items to preload when receiving the rendering
	 * request from the client.
	 * 
	 * <p>
	 * Default: 7.
	 * 
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingApi}.
	 * 
	 * <p>
	 * Note: if the "pre-load-size" attribute of component is specified, it's
	 * 
	 */
	public int getPreloadSize();

	/**
	 * Sets the number of items to preload when receiving the rendering request
	 * from the client.
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingApi}.
	 * 
	 * @param sz
	 *            the number of items to preload. If zero, no preload at all.
	 * @exception UiException
	 *                if sz is negative
	 */
	public void setPreloadSize(int sz);

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
	public org.zkoss.zul.api.Listitem renderItemApi(Listitem li);

	/**
	 * Renders all {@link Listitem} if not loaded yet, with
	 * {@link #getItemRenderer}.
	 * 
	 * @see org.zkoss.zul.Listbox#renderItem
	 * @see #renderItems
	 */
	public void renderAll();

	public void renderItems(Set items);

}
