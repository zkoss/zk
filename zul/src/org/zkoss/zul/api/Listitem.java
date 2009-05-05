/* Listitem.java

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

/**
 * A list item.
 * 
 * <p>
 * Default {@link #getZclass}: z-listitem (since 5.0.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Listitem extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the list box that it belongs to.
	 * <p>
	 * It is the same as {@link #getParent}.
	 */
	public org.zkoss.zul.api.Listbox getListboxApi();

	/**
	 * Returns the listgroup that this item belongs to, or null.
	 */
	public org.zkoss.zul.api.Listgroup getListgroupApi();

	/**
	 * Returns whether it is checkable.
	 * <p>
	 * Default: true.
	 */
	public boolean isCheckable();

	/**
	 * Sets whether it is checkable.
	 * <p>
	 * Default: true.
	 */
	public void setCheckable(boolean checkable);

	/**
	 * Returns the maximal length of each item's label. It is a shortcut of
	 * getParent().getMaxlength(); Thus, it works only if the listbox's mold is
	 * "select".
	 */
	public int getMaxlength();

	/**
	 * Returns the value.
	 * <p>
	 * Default: null.
	 * <p>
	 * Note: the value is application dependent, you can place whatever value
	 * you want.
	 * <p>
	 * If you are using listitem with HTML Form (and with the name attribute),
	 * it is better to specify a String-typed value.
	 */
	public Object getValue();

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value.
	 *            <p>
	 *            Note: the value is application dependent, you can place
	 *            whatever value you want.
	 *            <p>
	 *            If you are using listitem with HTML Form (and with the name
	 *            attribute), it is better to specify a String-typed value.
	 */
	public void setValue(Object value);

	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Returns whether it is selected.
	 * <p>
	 * Default: false.
	 */
	public boolean isSelected();

	/**
	 * Sets whether it is selected.
	 */
	public void setSelected(boolean selected);

	/**
	 * Returns the label of the {@link Listcell} it contains, or null if no such
	 * cell.
	 */
	public String getLabel();

	/**
	 * Sets the label of the {@link Listcell} it contains.
	 * 
	 * <p>
	 * If it is not created, we automatically create it.
	 */
	public void setLabel(String label);

	/**
	 * Returns the image of the {@link Listcell} it contains.
	 */
	public String getImage();

	/**
	 * Sets the image of the {@link Listcell} it contains.
	 * 
	 * <p>
	 * If it is not created, we automatically create it.
	 */
	public void setImage(String image);

	/**
	 * Returns whether the content of this item is loaded. It is meaningful only
	 * if {@link #getListboxApi} is live data, i.e., {@link Listbox#getModel} is
	 * not null.
	 * 
	 */
	public boolean isLoaded();

}
