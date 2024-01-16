/* PartialSelectable.java

        Purpose:

        Description:

        History:
                Fri Dec 22 12:05:16 CST 2023, Created by jamson

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.ext;

import java.util.Set;

import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;

/**
 * Indicate a tree model that supports partial selection. It is typically used with
 * {@link TreeSelectableModel} {@link TreeModel} and {@link Tree}.
 *
 * @author Jamson Chan
 * @since 10.0.0
 */
public interface TreePartialSelectableModel<E> {

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

	/**
	 * Adds path to the current partial selection. If path is not currently in the
	 * selection the TreeDataListeners are notified. This has no effect if
	 * <code>path</code> is null.
	 *
	 * @param path the new path to add to the current partial selection
	 * @return whether it is added successfully
	 */
	public boolean addPartialPath(int[] path);

	/**
	 * Adds paths to the current partial selection. If any of the paths in paths are not
	 * currently in the partial selection the TreeDataListeners are notified. This has
	 * no effect if <code>paths</code> is null.
	 *
	 * @param paths the new paths to add to the current partial selection
	 * @return whether it is added successfully
	 */
	public boolean addPartialPaths(int[][] paths);

	/**
	 * Removes path from the partial selection. If path is in the partial selection The
	 * TreeDataListeners are notified. This has no effect if <code>path</code>
	 * is null.
	 *
	 * @param path
	 *            the path to remove from the partial selection
	 * @return true if it was unselected successfully
	 */
	public boolean removePartialPath(int[] path);

	/**
	 * Removes paths from the partial selection. If any of the paths in
	 * <code>paths</code> are in the partial selection, the TreeDataListeners are
	 * notified. This method has no effect if <code>paths</code> is null.
	 *
	 * @param paths
	 *            the path to remove from the partial selection
	 * @return true if one of the paths was unselected successfully
	 */
	public boolean removePartialPaths(int[][] paths);

	/**
	 * Returns true if the path, <code>path</code>, is in the current partial selection.
	 */
	public boolean isPathPartial(int[] path);

	/**
	 * Returns the first path in the partial selection. How first is defined is up to
	 * implementors.
	 */
	public int[] getPartialPath();

	/**
	 * Returns the paths in the partial selection. This will return null (or an empty
	 * array) if nothing is currently partial selected or tristate mode is disabled.
	 */
	public int[][] getPartialPaths();

	/**
	 * Returns the number of paths that are partially selected.
	 */
	public int getPartialCount();

}
