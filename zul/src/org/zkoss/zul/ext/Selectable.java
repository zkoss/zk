package org.zkoss.zul.ext;

import java.util.Collection;
import java.util.Set;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

/* Selectable.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 24, 2009 12:15:21 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

/**
 * Indicate a selectable collection or component. Generally used with {@link ListModel}
 * and {@link Listbox}.
 * @author henrichen
 * @author tomyeh
 * @see ListModel
 * @see Listbox
 */
public interface Selectable<E> {
	/**
	 * Returns the current selection.
	 * It is readonly. Don't modify it directly
	 * @return the current selection.
	 */
	public Set<E> getSelection();
	/**
	 * Replace the current selection with the given set.
	 * <p>If this represents a change to the
	 * current selection then notify each ListDataListener, including UI.
	 * @since 6.0.0
	 */
	public void setSelection(Collection<? extends E> selection);
	/** Returns whether an object is selected.
	 * @since 6.0.0
	 */
	public boolean isSelected(Object obj);
	
	/**
	 * Returns true if the selection is currently empty.
	 * @since 6.0.0
	 */
	public boolean isSelectionEmpty();
	/**
	 * Add the specified object into selection.
	 * <p>If this represents a change to the
	 * current selection then notify each ListDataListener, including UI.
	 * @param obj the object to be as selection.
	 * @return true if it is added successfully; fasle if <code>obj</code>
	 * is not part of the data, or was already selected.
	 * @since 6.0.0
	 */
	public boolean addToSelection(E obj);
	/**
	 * Remove the specified object from selection.
	 * <p>If this represents a change to the
	 * current selection then notify each ListDataListener, including UI.
	 * @param obj the object to be remove from selection.
	 * @return whether it is removed successfully
	 * @since 6.0.0
	 */
	public boolean removeFromSelection(Object obj);
	/**
	 * Change the selection to the empty set.
	 * <p>If this represents a change to the
	 * current selection then notify each ListDataListener, including UI.
	 */
	public void clearSelection();

	/**
	 * Sets the selection mode to be multiple.
	 * @since 6.0.0
	 */
	public void setMultiple(boolean multiple);
	/**
	 * Returns whether the current selection mode is multiple.
	 * @see #setMultiple
	 * @since 6.0.0
	 */
	public boolean isMultiple();
}
