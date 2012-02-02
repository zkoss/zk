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
	 * @since 6.0.0
	 */
	public void setSelection(Collection<? extends E> selection);
	/** Returns whether an object is selected.
	 * @since 6.0.0
	 */
	public boolean isSelected(E obj);
	
	/**
	 * Returns true if the selection is currently empty.
	 * @since 6.0.0
	 */
	public boolean isSelectionEmpty();
	/**
	 * Add the specified object into selection.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Listbox}).
	 * If it is called by an application, the component's selection status
	 * won't be changed.
	 * @param obj the object to be as selection.
	 * @since 6.0.0
	 */
	public void addToSelection(E obj);
	/**
	 * Remove the specified object from selection.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Listbox}).
	 * If it is called by an application, the component's selection status
	 * won't be changed.
	 * @param obj the object to be remove from selection.
	 * @return whether it is removed successfully
	 * @since 6.0.0
	 */
	public boolean removeFromSelection(Object obj);
	/**
	 * Change the selection to the empty set. If this represents a change to the
	 * current selection then notify each ListDataListener.
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
