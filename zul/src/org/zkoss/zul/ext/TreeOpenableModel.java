/* TreeOpenableModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 29, 2011 4:47:19 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zul.ext;

import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.event.TreeDataListener;

/**
 * Indicate an openable collection or components. Generally used with {@link TreeModel}
 * and {@link Tree}.
 * @author jumperchen
 * @since 6.0.0
 */
public interface TreeOpenableModel {

	/**
	 * Adds path to the current open. If path is not currently in the
	 * open the TreeDataListeners are notified. This has no effect if
	 * <code>path</code> is null.
	 * 
	 * @param path
	 *            the new path to add to the current open
	 */
	public void addOpenPath(int[] path);

	/**
	 * Adds paths to the current Open. If any of the paths in paths are not
	 * currently in the Open the TreeDataListeners are notified. This has no
	 * effect if <code>paths</code> is null.
	 * 
	 * @param paths
	 *            the new paths to add to the current Open
	 */
	public void addOpenPaths(int[][] paths);

	/**
	 * Removes path from the open. If path is in the open The TreeDataListeners
	 * are notified. This has no effect if <code>path</code> is null.
	 * 
	 * @param path
	 *            the path to remove from the open
	 */
	public void removeOpenPath(int[] path);

	/**
	 * Removes paths from the open. If any of the paths in <code>paths</code>
	 * are in the open, the TreeDataListeners are notified. This method has no
	 * effect if <code>paths</code> is null.
	 * 
	 * @param paths
	 *            the path to remove from the open
	 */
	public void removeOpenPaths(int[][] paths);

	/**
	 * Returns true if the path, <code>path</code>, is in the current open.
	 */
	public boolean isPathOpened(int[] path);

	/**
	 * Returns true if the open is currently empty.
	 */
	public boolean isOpenEmpty();

	/**
	 * Empties the current open path. If this represents a change in the current
	 * open, the {@link TreeDataListener} listeners are notified.
	 */
	public void clearOpen();

	/**
	 * Returns the number of paths that are opened.
	 */
	public int getOpenCount();
	
	/**
	 * Returns the first path in the open. How first is defined is up to
	 * implementors.
	 */
	public int[] getOpenPath();

	/**
	 * Returns the paths in the open. This will return null (or an empty
	 * array) if nothing is currently opened.
	 */
	public int[][] getOpenPaths();

}
