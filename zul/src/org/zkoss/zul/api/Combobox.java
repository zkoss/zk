/* Combobox.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.List;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;//for javadoc

/**
 * A combo box.
 * 
 * <p>
 * Non-XUL extension. It is used to replace XUL menulist. This class is more
 * flexible than menulist, such as {@link #setAutocomplete} {@link #setAutodrop}.
 * 
 * <p>
 * Default {@link #getZclass}: z-combobox.(since 3.5.0)
 * 
 * <p>
 * Events: onOpen, onSelect<br/>
 * Developers can listen to the onOpen event and initializes it when
 * {@link org.zkoss.zk.ui.event.OpenEvent#isOpen} is true, and/or clean up if
 * false.
 * 
 * *
 * <p>
 * Besides assign a list model, you could assign a renderer (a
 * {@link ComboitemRenderer} instance) to a combobox, such that the combobox
 * will use this renderer to render the data returned by
 * {@link ListModel#getElementAt}. If not assigned, the default renderer, which
 * assumes a label per combo item, is used. In other words, the default renderer
 * adds a label to a row by calling toString against the object returned by
 * {@link ListModel#getElementAt}.
 * 
 * <p>
 * Note: to have better performance, onOpen is sent only if a non-deferrable
 * event listener is registered (see {@link org.zkoss.zk.ui.event.Deferrable}).
 * 
 * <p>
 * Like {@link Datebox}, the value of a read-only comobobox ({@link #isReadonly}
 * ) can be changed by dropping down the list and selecting an combo item
 * (though users cannot type anything in the input box).
 * 
 * @author tomyeh
 * @see Comboitem
 * @since 3.5.2
 */
public interface Combobox extends Textbox {

	/**
	 * Returns the list model associated with this combobox, or null if this
	 * combobox is not associated with any list data model.
	 * <p>
	 * Note: for implementation of auto-complete, the result of
	 * {@link #getItemCount()} is a subset of model. So, if the model
	 * implemented {@link org.zkoss.zul.ListSubModel} interface, you can't use
	 * the index of model to find the comboitem by
	 * {@link #getItemAtIndexApi(int)}.
	 * 
	 * @see org.zkoss.zul.ListSubModel#getSubModel(Object, int)
	 */
	public ListModel getModel();

	/**
	 * Sets the list model associated with this combobox. If a non-null model is
	 * assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 * 
	 * @param model
	 *            the list model to associate, or null to dis-associate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setModel(ListModel model);

	/**
	 * Returns the renderer to render each row, or null if the default renderer
	 * is used.
	 * 
	 */
	public ComboitemRenderer getItemRenderer();

	/**
	 * Sets the renderer which is used to render each row if {@link #getModel}
	 * is not null.
	 * 
	 * <p>
	 * Note: changing a render will not cause the combobox to re-render. If you
	 * want it to re-render, you could assign the same model again (i.e.,
	 * setModel(getModel())), or fire an {@link ListDataEvent} event.
	 * 
	 * @param renderer
	 *            the renderer, or null to use the default.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setItemRenderer(ComboitemRenderer renderer);

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 * 
	 */
	public void setItemRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException;

	/**
	 * Returns whether to automatically drop the list if users is changing this
	 * text box.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutodrop();

	/**
	 * Sets whether to automatically drop the list if users is changing this
	 * text box.
	 */
	public void setAutodrop(boolean autodrop);

	/**
	 * Returns whether to automatically complete this text box by matching the
	 * nearest item ({@link Comboitem}.
	 * 
	 * <p>
	 * Default: false.
	 * 
	 * <p>
	 * If true, the nearest item will be searched and the text box is updated
	 * automatically. If false, user has to click the item or use the DOWN or UP
	 * keys to select it back.
	 * 
	 * <p>
	 * Note: this feature is reserved and not yet implemented. Don't confuse it
	 * with the auto-completion feature mentioned by other framework. Such kind
	 * of auto-completion is supported well by listening to the onChanging
	 * event.
	 */
	public boolean isAutocomplete();

	/**
	 * Sets whether to automatically complete this text box by matching the
	 * nearest item ({@link Comboitem}.
	 */
	public void setAutocomplete(boolean autocomplete);

	/**
	 * Drops down or closes the list of combo items ({@link Comboitem}.
	 * 
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open);

	/**
	 * Drops down the list of combo items ({@link Comboitem}. It is the same as
	 * setOpen(true).
	 * 
	 */
	public void open();

	/**
	 * Closes the list of combo items ({@link Comboitem} if it was dropped down.
	 * It is the same as setOpen(false).
	 * 
	 */
	public void close();

	/**
	 * Returns whether the button (on the right of the textbox) is visible.
	 * <p>
	 * Default: true.
	 */
	public boolean isButtonVisible();

	/**
	 * Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible);

	/**
	 * Returns a 'live' list of all {@link Comboitem}. By live we mean you can
	 * add or remove them directly with the List interface.
	 * 
	 * <p>
	 * Currently, it is the same as {@link #getChildren}. However, we might add
	 * other kind of children in the future.
	 */
	public List getItems();

	/**
	 * Returns the number of items.
	 */
	public int getItemCount();

	/**
	 * Returns the item at the specified index.
	 */
	public org.zkoss.zul.api.Comboitem getItemAtIndexApi(int index);

	/**
	 * Appends an item.
	 */
	public org.zkoss.zul.api.Comboitem appendItemApi(String label);

	/**
	 * Removes the child item in the list box at the given index.
	 * 
	 * @return the removed item.
	 */
	public org.zkoss.zul.api.Comboitem removeItemAtApi(int index);

	/**
	 * Returns the selected item.
	 * 
	 */
	public org.zkoss.zul.api.Comboitem getSelectedItemApi();

	/**
	 * Deselects the currently selected items and selects the given item.
	 * <p>
	 * Note: if the label of comboitem has the same more than one, the first
	 * comboitem will be selected at client side, it is a limitation of
	 * {@link Combobox} and it is different from {@link Listbox}.
	 * </p>
	 * 
	 */
	public void setSelectedItemApi(Comboitem item);

	/**
	 * Deselects the currently selected items and selects the item with the
	 * given index.
	 * <p>
	 * Note: if the label of comboitem has the same more than one, the first
	 * comboitem will be selected at client side, it is a limitation of
	 * {@link Combobox} and it is different from {@link Listbox}.
	 * </p>
	 * 
	 */
	public void setSelectedIndex(int jsel);

	/**
	 * Returns the index of the selected item, or -1 if not selected.
	 * 
	 */
	public int getSelectedIndex();

}
