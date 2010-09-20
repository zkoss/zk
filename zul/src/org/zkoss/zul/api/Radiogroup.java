/* Radiogroup.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.List;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A radio group.
 * 
 * <p>
 * Note: To support the versatile layout, a radio group accepts any kind of
 * children, including {@link Radio}. On the other hand, the parent of a radio,
 * if any, must be a radio group.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Radiogroup extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the orient.
	 * <p>
	 * Default: "horizontal".
	 */
	public String getOrient();

	/**
	 * Sets the orient.
	 * 
	 * @param orient
	 *            either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException;

	/** Returns a readonly list of {@link Radio}.
	 * Note: any update to the list won't affect the state of this radio group.
	 * @since 5.0.4
	 */
	public List<org.zkoss.zul.Radio> getItems();
	/**
	 * Returns the radio button at the specified index.
	 */
	public org.zkoss.zul.api.Radio getItemAtIndexApi(int index);

	/**
	 * Returns the number of radio buttons in this group.
	 */
	public int getItemCount();

	/**
	 * Returns the index of the selected radio button (-1 if no one is
	 * selected).
	 */
	public int getSelectedIndex();

	/**
	 * Deselects all of the currently selected radio button and selects the
	 * radio button with the given index.
	 */
	public void setSelectedIndex(int jsel);

	/**
	 * Returns the selected radio button.
	 */
	public org.zkoss.zul.api.Radio getSelectedItemApi();

	/**
	 * Deselects all of the currently selected radio buttons and selects the
	 * given radio button.
	 */
	public void setSelectedItemApi(org.zkoss.zul.api.Radio item);

	/**
	 * Appends a radio button.
	 */
	public org.zkoss.zul.api.Radio appendItemApi(String label, String value);

	/**
	 * Removes the child radio button in the list box at the given index.
	 * 
	 * @return the removed radio button.
	 */
	public org.zkoss.zul.api.Radio removeItemAtApi(int index);

	/**
	 * Returns the name of this group of radio buttons. All child radio buttons
	 * shared the same name ({@link Radio#getName}).
	 * <p>
	 * Default: automatically generated an unique name
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 */
	public String getName();

	/**
	 * Sets the name of this group of radio buttons. All child radio buttons
	 * shared the same name ({@link Radio#getName}).
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 */
	public void setName(String name);

}
