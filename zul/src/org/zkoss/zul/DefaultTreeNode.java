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
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A general-purpose node in a tree data structure.
 *
 * <p>Notice that if a node is added to another (i.e., become a child of another node), it will
 * be removed from the previous parent automatically.
 *
 * @author tomyeh
 * @since 5.0.6
 */
public class DefaultTreeNode<E> implements TreeNode<E>, Comparable<DefaultTreeNode<E>>, java.io.Serializable  {
	private DefaultTreeModel<E> _model;
	private DefaultTreeNode<E> _parent;
	/** List<DefaultTreeNode> */
	private List<DefaultTreeNode<E>> _children;
	private E _data;
	private final boolean _leaf;
	/** Whether to treat null as the maximum value. */
	private boolean _maxnull;

	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(E)} instead.
	 */
	public DefaultTreeNode(E data, Collection<DefaultTreeNode<E>> children) {
		this(data, children, false);
	}
	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(Object)} instead.
	 */
	public DefaultTreeNode(E data, Collection<DefaultTreeNode<E>> children, boolean nullAsMax) {
		_data = data;
		_leaf = false;
		if (children != null)
			for (DefaultTreeNode<E> node: children)
				add(node);
		_maxnull = nullAsMax;
	}
	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(E)} instead.
	 */
	public DefaultTreeNode(E data, DefaultTreeNode<E>[] children) {
		this(data, new ArrayCollection<DefaultTreeNode<E>>(children));
	}
	/** Creates a leaf node, i.e., it won't allow any children.
	 */
	public DefaultTreeNode(E data) {
		this(data, false);
	}
	
	/** Creates a leaf node, i.e., it won't allow any children.
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public DefaultTreeNode(E data, boolean nullAsMax) {
		_data = data;
		_leaf = true;
		_maxnull = nullAsMax;
	}

	/** Removes the receiver from its parent.
	 */
	public void removeFromParent() {
		if (_parent != null)
			_parent.remove(this);
	}

	//@Override
	public DefaultTreeModel<E> getModel() {
		return _parent != null ? _parent.getModel(): _model;
	}
	//@Override
	public void setModel(DefaultTreeModel<E> model) {
		if (model != null && _parent != null)
			throw new IllegalStateException("Only root allowed, "+this);
		_model = model;
	}

	//@Override
	public E getData() {
		return _data;
	}
	//@Override
	@SuppressWarnings("unchecked")
	public void setData(E data) {
		_data = data;
		DefaultTreeModel model = getModel();
		TreeNode parent = getParent();
		if (model != null && parent != null) {
			int index = parent.getIndex(this);
			model.fireEvent(parent, index, index, TreeDataEvent.CONTENTS_CHANGED);
		}
	}
	
	//@Override
	public List<? extends TreeNode<E>> getChildren(){
		return _children;
	}

	//@Override
	public TreeNode<E> getChildAt(int childIndex) {
		return childIndex >= 0 && childIndex < getChildCount() ?
			_children.get(childIndex): null;
	}
	//@Override
	public int getChildCount() {
		return _children != null ? _children.size(): 0;
	}
	//@Override
	public TreeNode<E> getParent() {
		return _parent;
	}
	/** Sets the parent.
	 * It is called automatically when {@link #insert}, {@link #add} or {@link #remove} is
	 * called.
	 * The deriving class rarely needs to overide it.
	 */
	protected void setParent(DefaultTreeNode<E> parent) {
		_parent = parent;
	}

	//@Override
	public int getIndex(TreeNode<E> node) {
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
	@SuppressWarnings("unchecked")
	public void insert(TreeNode<E> child, int index) {
		if (isLeaf())
			throw new IllegalStateException("child not allowed");
		if (isAncestor(child))
			throw new IllegalArgumentException("new child is an ancestor");

		TreeNode<E> oldp = child.getParent();
		if (oldp != null)
			oldp.remove(child);
		if (_children == null)
			_children = new ArrayList<DefaultTreeNode<E>>();
		_children.add(index, (DefaultTreeNode<E>)child);
		((DefaultTreeNode<E>)child).setParent(this);
		
		DefaultTreeModel model = getModel();
		if (model != null)
			model.fireEvent(this, index, index, TreeDataEvent.INTERVAL_ADDED);
	}
	//@Override
	/** Adds a child to this node at the end.
     * @exception IllegalArgumentException if <code>child</code> is an ancestor of this node 
     * @exception IllegalStateException if this node does not allow children
     * @exception NullPointerException if <code>child</code> is null
	 */
	public void add(TreeNode<E> child) {
		insert(child, getChildCount());
	}
	/** Checks if p is one of ancestors of this node. */
	private boolean isAncestor(TreeNode<E> p) {
		TreeNode<E> c = this;
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
	@SuppressWarnings("unchecked")
	public void remove(int index) {
		DefaultTreeNode<E> child = _children.remove(index);
		child.setParent(null);
		
		DefaultTreeModel model = getModel();
		if (model != null)
			model.fireEvent(this, index, index, TreeDataEvent.INTERVAL_REMOVED);
	}
	//@Override
	/**
     * Removes <code>child</code> from this node's children, giving it a null parent.
     *
     * @param child a child of this node to remove
     * @exception IllegalArgumentException if <code>child</code> is not a child of this node
     */
	@SuppressWarnings("unchecked")
	public void remove(TreeNode<E> child) {
		int index = _children.indexOf(child);
		if (!_children.remove(child))
			throw new IllegalArgumentException("not a child");
		((DefaultTreeNode<E>)child).setParent(null);
		
		DefaultTreeModel model = getModel();
		if (model != null)
			model.fireEvent(this, index, index, TreeDataEvent.INTERVAL_REMOVED);
	}
	@SuppressWarnings("unchecked")
	public int compareTo(DefaultTreeNode<E> node) {
		if (_data == null) 
			return node == null ? 0: 
				node.getData() == null? 0: _maxnull ? 1: -1;
		if (node == null) return _maxnull ? -1: 1;
		return ((Comparable)_data).compareTo(node.getData());
	}
}
