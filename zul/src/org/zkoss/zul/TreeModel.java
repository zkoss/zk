/**
 * 
 */
package org.zkoss.zul;

import java.util.ArrayList;

import org.zkoss.zul.event.TreeDataListener;

/**
 * @author Jeff
 *
 */

public interface TreeModel {
	
	/**
	 * @param node
	 * @return
	 */
	public boolean isLeaf(Object node);
	
	 
	
	/**
	 * Return a child at index index in parent's child array 
	 * @param parent
	 * @param _index
	 * @return
	 */
	public Object getChild(Object parent, int index);
	
	/**
	 * Return the number of children of parent.
	 * @param parent
	 * @return
	 */
	public int getChildCount(Object parent);
	
	/**
	 * Return the root of Tree.
	 * @return the root of Tree.
	 */
	public Object getRoot();
	
	/**
	 * Add a listener to the tree that's notified each time a change to the data model occurs
	 * @param l
	 */
	public void addTreeDataListener(TreeDataListener l);
	
	/**
	 * Remove a listener to the tree that's notified each time a change to the data model occurs
	 * @param l
	 */
	public void removeTreeDataListener(TreeDataListener l);


	// TODO BETA
	/*
	 * Return a node with an associated Treeitem ti from a Tree tree 
	 */
	public Object getChild(Treeitem ti, Tree tree);
	
	// TODO BETA
	/*
	 * Return the path which is from root to node
	 */
	public ArrayList getPath(Object node);
	
}
