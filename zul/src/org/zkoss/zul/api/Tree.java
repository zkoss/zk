/* Tree.java

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

import java.util.Set;
import java.util.Collection;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.event.TreeDataEvent;//for javadoc
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Paginated;

/**
 * A container which can be used to hold a tabular or hierarchical set of rows
 * of elements.
 * 
 * <p>
 * Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.SelectEvent is sent when user changes the
 * selection.</li>
 * </ol>
 * 
 * <p>
 * Default {@link #getZclass}: z-tree, and an other option is z-dottree. (since
 * 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Tree extends org.zkoss.zul.impl.api.XulElement, Paginated {

	/**
	 * Sets how to position the paging of tree at the client screen. It is
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
	 * {@link #setMold} with "paging"), the tree will rely on the paging
	 * controller to handle long-content instead of scrolling.
	 * 
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
	 *            child component (see {@link #getPagingChildApi} ).
	 * 
	 */
	public void setPaginal(Paginal pgi);

	/**
	 * Returns the child paging controller that is created automatically, or
	 * null if mold is not "paging", or the controller is specified externally
	 * by {@link #setPaginal}.
	 * 
	 */
	public org.zkoss.zul.api.Paging getPagingChildApi();

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
	 * <p>
	 * Note: mold is not "paging" and no external controller is specified.
	 * 
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
	 * Returns the treecols that this tree owns (might null).
	 */
	public org.zkoss.zul.api.Treecols getTreecolsApi();

	/**
	 * Returns the treefoot that this tree owns (might null).
	 */
	public org.zkoss.zul.api.Treefoot getTreefootApi();

	/**
	 * Returns the treechildren that this tree owns (might null).
	 */
	public org.zkoss.zul.api.Treechildren getTreechildrenApi();

	/**
	 * Returns a collection of heads, including {@link #getTreecolsApi} and
	 * auxiliary heads ({@link Auxhead}) (never null).
	 * 
	 */
	public Collection getHeads();

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
	 * Sets whether the check mark shall be displayed in front of each item.
	 * <p>
	 * The check mark is a checkbox if {@link #isMultiple} returns true. It is a
	 * radio button if {@link #isMultiple} returns false.
	 */
	public void setCheckmark(boolean checkmark);

	/**
	 * Sets whether to grow and shrink vertical to fit their given space, so
	 * called vertial flexibility.
	 * 
	 * <p>
	 * Note: this attribute is ignored if {@link #setRows} is specified
	 */
	public void setVflex(boolean vflex);

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
	 * Returns the seltype.
	 * <p>
	 * Default: "single".
	 */
	public String getSeltype();

	/**
	 * Sets the seltype. Currently, only "single" is supported.
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
	 * Sets the active page in which the specified item is. The active page will
	 * become the page that contains the specified item.
	 * 
	 * @param item
	 *            the item to show. If the item is null, invisible, or doesn't
	 *            belong to the same tree, nothing happens.
	 */
	public void setActivePageApi(Treeitem item);

	/**
	 * Returns a readonly list of all descending {@link Treeitem} (children's
	 * children and so on).
	 * 
	 * <p>
	 * Note: the performance of the size method of returned collection is no
	 * good.
	 */
	public Collection getItems();

	/**
	 * Returns the number of child {@link Treeitem}. The same as
	 * {@link #getItems}.size().
	 * <p>
	 * Note: the performance of this method is no good.
	 */
	public int getItemCount();

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #setSelectedItemApi}.
	 * 
	 * @param item
	 *            the item to select. If null, all items are deselected.
	 */
	public void selectItemApi(Treeitem item);

	/**
	 * Selects the given item, without deselecting any other items that are
	 * already selected..
	 */
	public void addItemToSelectionApi(Treeitem item);

	/**
	 * Deselects the given item without deselecting other items.
	 */
	public void removeItemFromSelectionApi(Treeitem item);

	/**
	 * If the specified item is selected, it is deselected. If it is not
	 * selected, it is selected. Other items in the tree that are selected are
	 * not affected, and retain their selected state.
	 */
	public void toggleItemSelectionApi(Treeitem item);

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
	 */
	public org.zkoss.zul.api.Treeitem getSelectedItemApi();

	/**
	 * Deselects all of the currently selected items and selects the given item.
	 * <p>
	 * It is the same as {@link #selectItemApi}.
	 */
	public void setSelectedItemApi(Treeitem item);

	/**
	 * Returns all selected items.
	 */
	public Set getSelectedItems();

	/**
	 * Returns the number of items being selected.
	 */
	public int getSelectedCount();

	/**
	 * Clears all child tree items ({@link Treeitem}.
	 * <p>
	 * Note: after clear, {@link #getTreechildrenApi} won't be null, but it has
	 * no child
	 */
	public void clear();

	/**
	 * Sets the tree model associated with this tree.
	 * 
	 * <p>
	 * Note: changing a render will not cause the tree to re-render. If you want
	 * it to re-render, you could assign the same model again (i.e.,
	 * setModel(getModel())), or fire an {@link TreeDataEvent} event.
	 * 
	 * <p>
	 * Author: jeffliu
	 * 
	 * @param model
	 *            the tree model to associate, or null to dis-associate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setModel(TreeModel model);

	// --TreeModel dependent codes--//
	/**
	 * Returns the list model associated with this tree, or null if this tree is
	 * not associated with any tree data model.
	 * <p>
	 * Author: jeffliu
	 * 
	 * @return the list model associated with this tree
	 */
	public TreeModel getModel();

	/**
	 * Sets the renderer which is used to render each item if {@link #getModel}
	 * is not null.
	 * 
	 * <p>
	 * Note: changing a render will not cause the tree to re-render. If you want
	 * it to re-render, you could assign the same model again (i.e.,
	 * setModel(getModel())), or fire an {@link TreeDataEvent} event.
	 * 
	 * <p>
	 * Author: jeffliu
	 * 
	 * @param renderer
	 *            the renderer, or null to use the default.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setTreeitemRenderer(TreeitemRenderer renderer);

	/**
	 * Returns the renderer to render each item, or null if the default renderer
	 * is used.
	 * 
	 * @return the renderer to render each item, or null if the default
	 */
	public TreeitemRenderer getTreeitemRenderer();

	/**
	 * Renders the specified {@link Treeitem}, if not loaded yet, with
	 * {@link #getTreeitemRenderer}.
	 * 
	 * <p>
	 * It does nothing if {@link #getModel} returns null.
	 * <p>
	 * To unload treeitem, use {@link Treeitem#unload()}.
	 * 
	 * @see #renderItems
	 */
	public void renderItemApi(Treeitem item);

	/**
	 * Renders the specified {@link Treeitem}, if not loaded yet, with
	 * {@link #getTreeitemRenderer}.
	 * 
	 * <p>
	 * It does nothing if {@link #getModel} returns null.
	 * 
	 *<p>
	 * Note: Since the corresponding node is given, This method has better
	 * performance than renderItem(Treeitem item) due to not searching for its
	 * corresponding node.
	 * <p>
	 * To unload treeitem, use {@link Treeitem#unload()}.
	 * 
	 * @see #renderItems
	 */
	public void renderItemApi(Treeitem item, Object node);

	/**
	 * Renders the specified {@link Treeitem} if not loaded yet, with
	 * {@link #getTreeitemRenderer}.
	 * 
	 * <p>
	 * It does nothing if {@link #getModel} returns null.
	 * <p>
	 * To unload treeitem, with {@link org.zkoss.zul.Treeitem#unload()}.
	 * 
	 * @see org.zkoss.zul.Tree#renderItem
	 */
	public void renderItems(Set items);

	/**
	 * Load treeitems through path <b>path</b> <br>
	 * Note: By using this method, all treeitems in path will be rendered and
	 * opened ({@link Treeitem#setOpen}). If you want to visit the rendered item
	 * in paging mold, please invoke {@link #setActivePageApi(Treeitem)}.
	 * 
	 * @param path
	 *            - an int[] path, see {@link TreeModel#getPath}
	 * @return the treeitem from tree by given path
	 */
	public org.zkoss.zul.api.Treeitem renderItemByPathApi(int[] path);

	/** Returns the horizontal alignment of the whole grid.
	 * <p>Default: null (system default: left unless CSS specified).
	 * @since 5.0.0
	 */
	public String getAlign();
	/** Sets the horizontal alignment of the whole grid.
	 * <p>Allowed: "left", "center", "right", "justify"
	 * @since 5.0.0
	 */
	public void setAlign(String align);
}
