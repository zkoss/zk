/* Grid.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.Collection;
import java.util.Set;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Paginated;
import org.zkoss.zul.event.ListDataEvent;//for javadoc

/**
 * A grid is an element that contains both rows and columns elements. It is used
 * to create a grid of elements. Both the rows and columns are displayed at once
 * although only one will typically contain content, while the other may provide
 * size information.
 * 
 * <p>
 * Besides creating {@link Row} programmingly, you can assign a data model (a
 * {@link ListModel} or {@link GroupsModel} instance) to a grid via
 * {@link #setModel(ListModel)} or {@link #setModel(GroupsModel)} and then the
 * grid will retrieve data by calling {@link ListModel#getElementAt} when
 * necessary.
 * 
 * <p>
 * Besides assign a list model, you could assign a renderer (a
 * {@link RowRenderer} instance) to a grid, such that the grid will use this
 * renderer to render the data returned by {@link ListModel#getElementAt}. If
 * not assigned, the default renderer, which assumes a label per row, is used.
 * In other words, the default renderer adds a label to a row by calling
 * toString against the object returned by {@link ListModel#getElementAt}
 * 
 * <p>
 * There are two ways to handle long content: scrolling and paging. If
 * {@link #getMold} is "default", scrolling is used if {@link #setHeight} is
 * called and too much content to display. If {@link #getMold} is "paging",
 * paging is used if two or more pages are required. To control the number of
 * rows to display in a page, use {@link #setPageSize}.
 * 
 * <p>
 * If paging is used, the page controller is either created automatically or
 * assigned explicity by {@link #setPaginal}. The paging controller specified
 * explicitly by {@link #setPaginal} is called the external page controller. It
 * is useful if you want to put the paging controller at different location
 * (other than as a child component), or you want to use the same controller to
 * control multiple grids.
 * 
 * <p>
 * Default {@link #getZclass}: z-grid.(since 3.5.0)
 * 
 * <p>
 * To have a grid without stripping, you can specify a non-existent style class
 * to {@link #setOddRowSclass}.
 * 
 * @since 3.5.2
 * @author tomyeh
 * @see ListModel
 * @see RowRenderer
 * @see org.zkoss.zul.RowRendererExt
 */
public interface Grid extends org.zkoss.zul.impl.api.XulElement, Paginated {
	/**
	 * Sets whether to grow and shrink vertical to fit their given space, so
	 * called vertial flexibility.
	 * 
	 */
	public void setVflex(boolean vflex);

	/**
	 * Sets the outline of grid whether is fixed layout. If true, the outline of
	 * grid will be depended on browser. It means, we don't calculate the width
	 * of each cell. Otherwise, the outline will count on the content of body.
	 * In other words, the outline of grid is like ZK 2.4.1 version that the
	 * header's width is only for reference.
	 * 
	 * <p>
	 * You can also specify the "fixed-layout" attribute of component in
	 * lang-addon.xml directly, it's a top priority.
	 * 
	 */
	public void setFixedLayout(boolean fixedLayout);

	/**
	 * Returns the outline of grid whether is fixed layout.
	 * <p>
	 * Default: false.
	 * <p>
	 * Note: if the "fixed-layout" attribute of component is specified, it's
	 * prior to the original value.
	 * 
	 */
	public boolean isFixedLayout();

	/**
	 * Returns the rows.
	 */
	public org.zkoss.zul.api.Rows getRowsApi();

	/**
	 * Returns the columns.
	 */
	public org.zkoss.zul.api.Columns getColumnsApi();

	/**
	 * Returns the foot.
	 */
	public org.zkoss.zul.api.Foot getFootApi();

	/**
	 * Returns a collection of heads, including {@link #getColumnsApi} and
	 * auxiliary heads ({@link org.zkoss.zul.Auxhead}) (never null).
	 * 
	 */
	public Collection getHeads();

	/**
	 * Returns the specified cell, or null if not available.
	 * 
	 * @param row
	 *            which row to fetch (starting at 0).
	 * @param col
	 *            which column to fetch (starting at 0).
	 */
	public Component getCell(int row, int col);

	/**
	 * Returns the horizontal alignment of the whole grid.
	 * <p>
	 * Default: null (system default: left unless CSS specified).
	 */
	public String getAlign();

	/**
	 * Sets the horizontal alignment of the whole grid.
	 * <p>
	 * Allowed: "left", "center", "right"
	 */
	public void setAlign(String align);

	// --Paging--//
	/**
	 * Sets how to position the paging of grid at the client screen. It is
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
	 * {@link #setMold} with "paging"), the grid will rely on the paging
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
	 * Returns the page size, aka., the number rows per page.
	 * 
	 * @exception IllegalStateException
	 *                if {@link #getPaginal} returns null, i.e., mold is not
	 *                "paging" and no external controller is specified.
	 */
	public int getPageSize();

	/**
	 * Sets the page size, aka., the number rows per page.
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
	 */
	public void setActivePage(int pg) throws WrongValueException;

	// -- ListModel dependent codes --//
	/**
	 * Returns the model associated with this grid, or null if this grid is not
	 * associated with any list data model.
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
	 * Returns the list model associated with this grid, or null if this grid is
	 * associated with a {@link GroupsModel} or not associated with any list
	 * data model.
	 * 
	 * @see #setModel(ListModel)
	 */
	public ListModel getListModel();

	/**
	 * Returns the groups model associated with this grid, or null if this grid
	 * is associated with a {@link ListModel} or not associated with any list
	 * data model.
	 * 
	 * @see #setModel(GroupsModel)
	 */
	public GroupsModel getGroupsModel();

	/**
	 * Sets the list model associated with this grid. If a non-null model is
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
	 * Sets the groups model associated with this grid. If a non-null model is
	 * assigned, no matter whether it is the same as the previous, it will
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
	 * Returns the renderer to render each row, or null if the default renderer
	 * is used.
	 */
	public RowRenderer getRowRenderer();

	/**
	 * Sets the renderer which is used to render each row if {@link #getModel}
	 * is not null.
	 * 
	 * <p>
	 * Note: changing a render will not cause the grid to re-render. If you want
	 * it to re-render, you could assign the same model again (i.e.,
	 * setModel(getModel())), or fire an {@link ListDataEvent} event.
	 * 
	 * @param renderer
	 *            the renderer, or null to use the default.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setRowRenderer(RowRenderer renderer);

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setRowRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException;

	/**
	 * Returns the number of rows to preload when receiving the rendering
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
	 * prior to the original value.
	 * 
	 */
	public int getPreloadSize();

	/**
	 * Sets the number of rows to preload when receiving the rendering request
	 * from the client.
	 * <p>
	 * It is used only if live data ({@link #setModel(ListModel)} and not paging
	 * ({@link #getPagingApi}.
	 * 
	 * @param sz
	 *            the number of rows to preload. If zero, no preload at all.
	 * @exception UiException
	 *                if sz is negative
	 */
	public void setPreloadSize(int sz);

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
	 * Renders the specified {@link Row} if not loaded yet, with
	 * {@link #getRowRenderer}.
	 * 
	 * <p>
	 * It does nothing if {@link #getModel} returns null. In other words, it is
	 * meaningful only if live data model is used.
	 */
	public void renderRowApi(Row row);

	/**
	 * Renders all {@link Row} if not loaded yet, with {@link #getRowRenderer}.
	 */
	public void renderAll();

	/**
	 * Renders a set of specified rows. It is the same as {@link #renderItems}.
	 */
	public void renderRows(Set rows);

	public void renderItems(Set rows);

	/**
	 * Returns the style class for the odd rows.
	 * 
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

}
