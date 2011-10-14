/* TreeNode.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 31 19:25:46 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.util.List;

/**
 * Defines the requirements for a tree node object that can change --
 * by adding or removing child nodes, or by changing the contents of
 * an application-specific data ({@link #setData}) stored in the node. 
 * It is designed to be used with {@link DefaultTreeModel}.
 *
 * @author tomyeh
 * @since 5.0.6
 */
public interface TreeNode<E> {
	/** Returns the tree model it belongs to.
	 */
	public DefaultTreeModel<E> getModel();
	/** Sets the tree model it belongs to.
	 * It can be called only if this node is a root. If a node has a parent,
	 * its model shall be the same as its parent.
	 * <p>This method is invoked automatically if {@link DefaultTreeModel},
	 * so you don't have to invoke it.
	 * @exception IllegalArgumentException if the model is null.
	 * @exception IllegalStateException if the node is not a root.
	 */
	public void setModel(DefaultTreeModel<E> model);

	/**
	 * Returns the application-specific data of this node.
	 */
	public E getData();
	/**
	 * Sets the application-specific data associated with this node.
	 */
	public void setData(E data);

	/**
	 * Return children of the receiver
	 * @return children of the receiver. If the node is a leaf, null is returned.
	 */
	public List<? extends TreeNode<E>> getChildren();
	
	/**
	 * Returns the child <code>TreeNode</code> at index 
	 * <code>childIndex</code>.
	 */
	public TreeNode<E> getChildAt(int childIndex);
	/**
	 * Returns the number of children <code>TreeNode</code>s this node
	 * contains.
	 */
	public int getChildCount();
	/**
	 * Returns the parent <code>TreeNode</code> of this node.
	 */
	public TreeNode<E> getParent();
	/**
	 * Returns the index of <code>node</code> in this node's children.
	 * If this node does not contain <code>node</code>, -1 will be
	 * returned.
	 */
	public int getIndex(TreeNode<E> node);

	/**
	 * Returns true if this node is a leaf.
	 * Notice that not all non-leaf nodes have children.
	 * In file-system terminology, a leaf node is a file, while a non-leaf node is a folder.
	 */
	public boolean isLeaf();

	/** Adds child to this node at the given index.
	 * @exception UnsupportedOperationException if the tree structure is not mutable,
	 * or this node does not allow children
     * @exception IndexOutOfBoundsException	if <code>index</code> is out of bounds
     * @exception IllegalArgumentException if <code>child</code> is an ancestor of this node 
     * @exception NullPointerException if <code>child</code> is null
	 */
	public void insert(TreeNode<E> child, int index);
	/** Adds a child to this node at the end.
	 * @exception UnsupportedOperationException if the tree structure is not mutable,
	 * or this node does not allow children
     * @exception IllegalArgumentException if <code>child</code> is an ancestor of this node 
     * @exception NullPointerException if <code>child</code> is null
	 */
	public void add(TreeNode<E> child);
	/** Removes the child at index from this node.
	 * @exception UnsupportedOperationException if the tree structure is not mutable,
	 * or this node does not allow children
     * @exception IndexOutOfBoundsException	if <code>index</code> is out of bounds
	 */
	public void remove(int index);
	/** Removes the child from this node.
	 * @exception UnsupportedOperationException if the tree structure is not mutable
	 * or this node does not allow children
     * @exception IllegalArgumentException if <code>child</code> is not a child of this node
	 */
	public void remove(TreeNode<E> child);
}
