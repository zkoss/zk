/* TreeSelectionModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 27, 2011 3:53:20 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zul.ext;

import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;

/**
 * Indicate a tree model that supports selection. It is typically used with
 * {@link TreeModel} and {@link Tree}.
 * 
 * @author jumperchen
 * @since 6.0.0
 */
public interface TreeSelectionModel {

	/**
	 * Sets the selection mode to be multiple.
	 */
	public void setMultiple(boolean multiple);

	/**
	 * Returns whether the current selection mode is multiple.
	 * 
	 * @see #setMultiple
	 */
	public boolean isMultiple();

	/**
	 * Adds path to the current selection. If path is not currently in the
	 * selection the TreeDataListeners are notified. This has no effect if
	 * <code>path</code> is null.
	 * 
	 * @param path
	 *            the new path to add to the current selection
	 */
	public void addSelectionPath(int[] path);

	/**
	 * Adds paths to the current selection. If any of the paths in paths are not
	 * currently in the selection the TreeDataListeners are notified. This has
	 * no effect if <code>paths</code> is null.
	 * 
	 * @param paths
	 *            the new paths to add to the current selection
	 */
	public void addSelectionPaths(int[][] paths);

	/**
	 * Removes path from the selection. If path is in the selection The
	 * TreeDataListeners are notified. This has no effect if <code>path</code>
	 * is null.
	 * 
	 * @param path
	 *            the path to remove from the selection
	 * @return true if it was selected and unselected successfully
	 */
	public boolean removeSelectionPath(int[] path);

	/**
	 * Removes paths from the selection. If any of the paths in
	 * <code>paths</code> are in the selection, the TreeDataListeners are
	 * notified. This method has no effect if <code>paths</code> is null.
	 * 
	 * @param paths
	 *            the path to remove from the selection
	 * @return true if at least one of the path was selected and unselected successfully
	 */
	public boolean removeSelectionPaths(int[][] paths);

	/**
	 * Returns true if the path, <code>path</code>, is in the current selection.
	 */
	public boolean isPathSelected(int[] path);

	/**
	 * Returns true if the selection is currently empty.
	 */
	public boolean isSelectionEmpty();

	/**
	 * Empties the current selection. If this represents a change in the current
	 * selection, the selection listeners are notified.
	 */
	public void clearSelection();

	/**
	 * Returns the first path in the selection. How first is defined is up to
	 * implementors.
	 */
	public int[] getSelectionPath();

	/**
	 * Returns the paths in the selection. This will return null (or an empty
	 * array) if nothing is currently selected.
	 */
	public int[][] getSelectionPaths();

	/**
	 * Returns the number of paths that are selected.
	 */
	public int getSelectionCount();

}
