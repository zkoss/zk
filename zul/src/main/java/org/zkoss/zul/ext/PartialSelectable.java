/* PartialSelectable.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 22 12:06:40 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.ext;

import java.util.Set;

import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;

/**
 * Indicate a partial selectable collection or component. Generally used with
 * {@link Selectable} {@link TreePartialSelectableModel} {@link TreeSelectableModel}
 * {@link TreeModel} and {@link Tree}.
 * @author Jamson Chan
 * @since 10.0.0
 */
public interface PartialSelectable<E> {

	/**
	 * Returns the current partial selection.
	 * It is readonly. Don't modify it directly
	 * @return the current partial selection.
	 */
	public Set<E> getPartial();

	/**
	 * Returns whether an object is partially selected.
	 */
	public boolean isPartial(Object obj);

	/**
	 * Returns true if the partial selection is currently empty.
	 */
	public boolean isPartialEmpty();

	/**
	 * Add the specified object into partial selection.
	 * <p>If this represents a change to the
	 * current partial selection then notify each TreeDataListener, including UI.
	 * @param obj the object to be as partial selection.
	 * @return true if it is added successfully; false if <code>obj</code>
	 * is not part of the data, or was already partial selected.
	 */
	public boolean addToPartial(E obj);

	/**
	 * Remove the specified object from partial selection.
	 * <p>If this represents a change to the
	 * current partial selection then notify each TreeDataListener, including UI.
	 * @param obj the object to be remove from partial selection.
	 * @return whether it is removed successfully
	 */
	public boolean removeFromPartial(Object obj);

	/**
	 * Change the partial selection to the empty set.
	 * <p>If this represents a change to the
	 * current partial selection then notify each ListDataListener, including UI.
	 */
	public void clearPartial();

	/**
	 * Sets the selection mode to be tristate.
	 */
	public void setTristate(boolean tristate);

	/**
	 * Returns whether the current selection mode is tristate.
	 * @see #setTristate
	 */
	public boolean isTristate();

}
