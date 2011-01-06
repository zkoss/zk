/* DefaultTreeNode.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan  5 17:36:42 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.zkoss.util.CollectionsX.ArrayCollection;

/**
 * A general-purpose node in a tree data structure.
 *
 * <p>Notice that if a node is added to another (i.e., become a child of another node), it will
 * be removed from the previous parent automatically.
 *
 * @author tomyeh
 * @since 5.0.6
 */
public class DefaultTreeNode implements TreeNode, java.io.Serializable  {
	private DefaultTreeModel _model;
	private DefaultTreeNode _parent;
	/** List<DefaultTreeNode> */
	private ArrayList _children;
	private Object _data;
	private final boolean _leaf;

	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(Object)} instead.
	 */
	public DefaultTreeNode(Object data, Collection children) {
		_data = data;
		_leaf = false;
		if (children != null)
			for (Iterator it = children.iterator(); it.hasNext();)
				add((DefaultTreeNode)it.next());
	}
	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(Object)} instead.
	 */
	public DefaultTreeNode(Object data, DefaultTreeNode[] children) {
		this(data, new ArrayCollection(children));
	}
	/** Creates a leaf node, i.e., it won't allow any children.
	 */
	public DefaultTreeNode(Object data) {
		_data = data;
		_leaf = true;
	}

	/** Removes the receiver from its parent.
	 */
	public void removeFromParent() {
		if (_parent != null)
			_parent.remove(this);
	}

	//@Override
	public DefaultTreeModel getModel() {
		return _parent != null ? _parent.getModel(): _model;
	}
	//@Override
	public void setModel(DefaultTreeModel model) {
		if (model != null && _parent != null)
			throw new IllegalStateException("Only root allowed, "+this);
		_model = model;
	}

	//@Override
	public Object getData() {
		return _data;
	}
	//@Override
	public void setData(Object data) {
		_data = data;
		//TODO: getModel().fireEvent(...)
	}

	//@Override
	public TreeNode getChildAt(int childIndex) {
		return childIndex >= 0 && childIndex < getChildCount() ?
			(TreeNode)_children.get(childIndex): null;
	}
	//@Override
	public int getChildCount() {
		return _children != null ? _children.size(): 0;
	}
	//@Override
	public TreeNode getParent() {
		return _parent;
	}
	/** Sets the parent.
	 * It is called automatically when {@link #insert}, {@link #add} or {@link #remove} is
	 * called.
	 * The deriving class rarely needs to overide it.
	 */
	protected void setParent(DefaultTreeNode parent) {
		_parent = parent;
	}

	//@Override
	public int getIndex(TreeNode node) {
		return _children != null ? _children.indexOf(node): -1;
	}

	//@Override
	public boolean isLeaf() {
		return _leaf;
	}

	//@Override
	/** Adds child to this node at the given index.
     * @exception IndexOutOfBoundsException	if <code>index</code> is out of bounds
     * @exception IllegalArgumentException if <code>child</code> is an ancestor of this node 
     * @exception IllegalStateException if this node does not allow children
     * @exception NullPointerException if <code>child</code> is null
	 */
	public void insert(TreeNode child, int index) {
		if (isLeaf())
			throw new IllegalStateException("child not allowed");
		if (isAncestor(child, this))
			throw new IllegalArgumentException("new child is an ancestor");

		TreeNode oldp = child.getParent();
		if (oldp != null)
			oldp.remove(child);
		if (_children == null)
			_children = new ArrayList();
		_children.add(index, child);
		((DefaultTreeNode)child).setParent(this);
		//TODO: getModel().fireEvent(...)
	}
	//@Override
	/** Adds a child to this node at the end.
     * @exception IllegalArgumentException if <code>child</code> is an ancestor of this node 
     * @exception IllegalStateException if this node does not allow children
     * @exception NullPointerException if <code>child</code> is null
	 */
	public void add(TreeNode child) {
		insert(child, getChildCount());
	}
	private static boolean isAncestor(TreeNode p, TreeNode c) {
		do {
			if (p == c)
				return true;
		} while ((c = c.getParent()) != null);
		return false;
	}

	//@Override
	/**
     * Removes the child at the specified index from this node's children,
     * and sets that node's parent to null.
     *
     * @param index the index in this node's child array of the child to remove
     * @exception IndexOutOfBoundsException	if <code>index</code> is out of bounds
     */
	public void remove(int index) {
		DefaultTreeNode child = (DefaultTreeNode)_children.remove(index);
		child.setParent(null);
		//TODO: getModel().fireEvent(...)
	}
	//@Override
	/**
     * Removes <code>child</code> from this node's children, giving it a null parent.
     *
     * @param child a child of this node to remove
     * @exception IllegalArgumentException if <code>child</code> is not a child of this node
     */
	public void remove(TreeNode child) {
		if (!_children.remove(child))
			throw new IllegalArgumentException("not a child");
		((DefaultTreeNode)child).setParent(null);
		//TODO: getModel().fireEvent(...)
	}
}
