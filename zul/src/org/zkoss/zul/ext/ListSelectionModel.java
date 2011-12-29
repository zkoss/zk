/* ListSelectionModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 27, 2011 11:19:27 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zul.ext;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

/**
 * Indicate a data model that supports selection. It is typically used with
 * {@link ListModel} and {@link Listbox}.
 * 
 * @author jumperchen
 * @since 6.0.0
 */
public interface ListSelectionModel {

	/**
	 * Changes the selection to be the set union of the current selection and
	 * the indices between {@code index0} and {@code index1} inclusive.
	 * {@code index0} doesn't have to be less than or equal to {@code index1}.
	 * <p>
	 * In single selection mode, only the second index is used.
	 * <p>
	 * If this represents a change to the current selection, then each
	 * {@code ListDataListener} is notified of the change.
	 * 
	 * @param index0
	 *            one end of the interval.
	 * @param index1
	 *            other end of the interval
	 */
	public void addSelectionInterval(int index0, int index1);

	/**
	 * Changes the selection to be the set difference of the current selection
	 * and the indices between {@code index0} and {@code index1} inclusive.
	 * {@code index0} doesn't have to be less than or equal to {@code index1}.
	 * <p>
	 * If this represents a change to the current selection, then each
	 * {@code ListDataListener} is notified of the change.
	 * 
	 * @param index0
	 *            one end of the interval.
	 * @param index1
	 *            other end of the interval
	 */
	public void removeSelectionInterval(int index0, int index1);

	/**
	 * Returns the first selected index or -1 if the selection is empty.
	 */
	public int getMinSelectionIndex();

	/**
	 * Returns the last selected index or -1 if the selection is empty.
	 */
	public int getMaxSelectionIndex();

	/**
	 * Returns true if the specified index is selected.
	 */
	public boolean isSelectedIndex(int index);

	/**
	 * Change the selection to the empty set. If this represents a change to the
	 * current selection then notify each ListDataListener.
	 */
	public void clearSelection();

	/**
	 * Returns true if no indices are selected.
	 */
	public boolean isSelectionEmpty();

	/**
	 * Sets the selection mode to be multiple.
	 */
	public void setMultiple(boolean multiple);

	/**
	 * Returns whether the current selection mode is multiple.
	 * @see #setMultiple
	 */
	public boolean isMultiple();
}
