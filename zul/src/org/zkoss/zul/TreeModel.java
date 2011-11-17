/* TreeModel.java

	Purpose:
		
	Description:
		
	History:
		Web Aug 10  2007, Created by Jeff Liu

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * This interface defines the methods that component like {@link Tree}
 * use to get the content of items.
  *
 * <p>Note: changing a render will not cause the tree to re-render.
 * If you want it to re-render, you could assign the same model again 
 * (i.e., setModel(getModel())), or fire an {@link TreeDataEvent} event.
 *
 * <p>For introduction, please refer to
 * <a href="http://books.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model">ZK Developer's Reference: Tree Model</a>.
 *
 * @author Jeff Liu
 * @since 3.0.0
 *
 */
public interface TreeModel<E> {
	/**
	 * Returns true if node is a leaf.
	 * Notice that not all non-leaf nodes have children.
	 * In file-system terminology, a leaf node is a file, while a non-leaf node is a folder.
	 * @param node a node in the tree, obtained from this data source
	 * @return true if node is a leaf.
	 */
	public boolean isLeaf(E node);

	/**
	 * Returns the child of parent at index index in the parent's child array.
	 * @param parent a node in the tree, obtained from this data source
	 * @return the child of parent at index index
	 */
	public E getChild(E parent, int index);
	
	/**
	 * Returns the number of children of parent.
	 * @param parent a node in the tree, obtained from this data source
	 * @return the number of children of the node parent
	 */
	public int getChildCount(E parent);

	/**
	 * Returns the index of child in parent.
	 * If either parent or child is null, returns -1. If either parent or child don't belong to this tree model, returns -1. 
	 * @param parent a node in the tree, obtained from this data source
     * @param child the node we are interested in 
	 * @return the index of the child in the parent, or -1 if either child or parent are null or don't belong to this tree model
	 * @since 5.0.6
	 */
	public int getIndexOfChild(E parent, E child);

	/**
	 * Returns the root of the tree.
	 * @return the root of Tree.
	 */
	public E getRoot();
	
	/**
	 * Add a listener to the tree that's notified each time a change to the data model occurs
	 * @param l the listener to add
	 */
	public void addTreeDataListener(TreeDataListener l);
	
	/**
	 * Remove a listener to the tree that's notified each time a change to the data model occurs
	 * @param l the listener to remove
	 */
	public void removeTreeDataListener(TreeDataListener l);

	/**
	 * @deprecated As of release 5.0.6, it was replaced by {@link #getIndexOfChild}.
	 * You don't have to implement this method if you extends from
	 * {@link AbstractTreeModel}.
	 * If you implement {@link TreeModel} from scratch, you could implement
	 * this method by just returning null, since none of ZK's code depends on it.
	 */
	public int[] getPath(Object parent, Object lastNode);
}
