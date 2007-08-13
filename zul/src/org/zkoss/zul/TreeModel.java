/* TreeModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Web Aug 10  2007, Created by Jeff Liu
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;

import org.zkoss.zul.event.TreeDataListener;

/**
 * This interface defines the methods that component like {@link Tree}
 * use to get the content of items.
 *
 */
public interface TreeModel {
	
	/**
	 * Returns true if node is a leaf.
	 * @param node - a node in the tree, obtained from this data source
	 * @return true if node is a leafs
	 */
	public boolean isLeaf(Object node);

	/**
	 * Returns the child of parent at index index in the parent's child array.
	 * @param parent - a node in the tree, obtained from this data source
	 * @return the child of parent at index index
	 */
	public Object getChild(Object parent, int index);
	
	/**
	 * Returns the number of children of parent.
	 * @param parent - a node in the tree, obtained from this data source
	 * @return the number of children of the node parent
	 */
	public int getChildCount(Object parent);
	
	/**
	 * Returns the root of the tree.
	 * @return the root of Tree.
	 */
	public Object getRoot();
	
	/**
	 * Add a listener to the tree that's notified each time a change to the data model occurs
	 * @param l - the listener to add
	 */
	public void addTreeDataListener(TreeDataListener l);
	
	/**
	 * Remove a listener to the tree that's notified each time a change to the data model occurs
	 * @param l - the listener to remove
	 */
	public void removeTreeDataListener(TreeDataListener l);
	
	/**
	 * Return a node which is an associated Treeitem ti in a Tree tree 
	 * @param ti - The Treeitem is associated 
	 * @param tree - Tree which Treeitem is belonged to
	 * @return a node which is an associated Treeitem ti in a Tree tree
	 */
	public Object getChild(Treeitem ti, Tree tree);
	
	/**
	 * Constructs a new TreePath, which is the path identified by root ending in node.
	 * @param node - The destination of path
	 * @return The tree path
	 */
	public ArrayList getPath(Object node);
	
}
