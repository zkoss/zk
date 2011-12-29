/* DefaultTreeNode.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan  5 17:36:42 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
	private final List<TreeNode<E>> _children;
	private E _data;
	/** Whether to treat null as the maximum value. */
	private boolean _maxnull;

	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(Object)} instead.
	 */
	public DefaultTreeNode(E data, Collection<? extends TreeNode<E>> children) {
		this(data, children, false);
	}
	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(Object)} instead.
	 */
	public DefaultTreeNode(E data, Collection<? extends TreeNode<E>> children, boolean nullAsMax) {
		_data = data;
		_children = new TreeNodeChildrenList();
		if (children != null)
			for (TreeNode<E> node: children)
				add(node);
		_maxnull = nullAsMax;
	}
	/** Creates a branch (non-leaf) node.
	 * @param children a collection of children (they must be {@link DefaultTreeNode} too).
	 * If null or empty, it means
	 * no children at all. However, it still allows to add children.
	 * If it is not allowed, please use {@link #DefaultTreeNode(Object)} instead.
	 */
	public DefaultTreeNode(E data, TreeNode<E>[] children) {
		this(data, new ArrayCollection<TreeNode<E>>(children));
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
		_children = null;
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
	public void setData(E data) {
		_data = data;
		DefaultTreeModel<E> model = getModel();
		TreeNode<E> parent = getParent();
		if (model != null && parent != null) {
			int index = parent.getIndex(this);
			model.fireEvent(parent, index, index, TreeDataEvent.CONTENTS_CHANGED);
		}
	}
	
	//@Override
	public List<? extends TreeNode<E>> getChildren(){
		return isLeaf() ? null : _children;
	}

	//@Override
	public TreeNode<E> getChildAt(int childIndex) {
		return childIndex >= 0 && childIndex < getChildCount() ?
			_children.get(childIndex): null;
	}
	//@Override
	public int getChildCount() {
		return isLeaf() ? 0 : _children.size();
	}
	//@Override
	public TreeNode<E> getParent() {
		return _parent;
	}
	/** Sets the parent.
	 * It is called automatically when {@link #insert}, {@link #add} or {@link #remove} is
	 * called.
	 * The deriving class rarely needs to override it.
	 */
	protected void setParent(DefaultTreeNode<E> parent) {
		_parent = parent;
	}

	//@Override
	public int getIndex(TreeNode<E> node) {
		return isLeaf() ? -1 : _children.indexOf(node);
	}

	//@Override
	public boolean isLeaf() {
		return _children == null;
	}

	//@Override
	public void insert(TreeNode<E> child, int index) {
		if (isLeaf())
			throw new UnsupportedOperationException("Child is not allowed in leaf node");
		_children.add(index, child);
	}
	//@Override
	public void add(TreeNode<E> child) {
		insert(child, getChildCount());
	}
	/** Checks if p is one of ancestors of this node. */
	private static boolean isAncestor(TreeNode p, TreeNode c) {
		do {
			if (p == c)
				return true;
		} while ((c = c.getParent()) != null);
		return false;
	}

	//@Override
	public void remove(int index) {
		if (isLeaf())
			throw new UnsupportedOperationException("Child is not allowed in leaf node");
		_children.remove(index);
	}
	//@Override
	public void remove(TreeNode<E> child) {
		if (isLeaf())
			throw new UnsupportedOperationException("Child is not allowed in leaf node");
		if(!_children.remove(child))
			throw new IllegalArgumentException("not a child of this node");
	}
	@SuppressWarnings("unchecked")
	public int compareTo(DefaultTreeNode<E> node) {
		if (_data == null) 
			return node == null ? 0: 
				node.getData() == null? 0: _maxnull ? 1: -1;
		if (node == null) return _maxnull ? -1: 1;
		return ((Comparable)_data).compareTo(node.getData());
	}
	
	protected class TreeNodeChildrenList extends AbstractList<TreeNode<E>> implements java.io.Serializable {
		
		protected final ArrayList<TreeNode<E>> _list = new ArrayList<TreeNode<E>>();
		
		// required implementation by spec: get, size, add, remove, set
		// set is not supported
		public TreeNode<E> get(int index) {
			return _list.get(index);
		}
		
		public int size() {
			return _list.size();
		}
		
		public void add(int index, TreeNode<E> child) {
			if (isAncestor(child, DefaultTreeNode.this))
				throw new IllegalArgumentException("New child is an ancestor");
			
			TreeNode<E> oldp = child.getParent();
			if (oldp != null)
				oldp.remove(child);
			
			_list.add(index, child);
			
			if (child instanceof DefaultTreeNode)
				((DefaultTreeNode<E>) child).setParent(DefaultTreeNode.this);
			
			DefaultTreeModel<E> model = getModel();
			if (model != null)
				model.fireEvent(DefaultTreeNode.this, index, index, TreeDataEvent.INTERVAL_ADDED);
			
		}
		
		public TreeNode<E> remove(int index) {
			TreeNode<E> child = _list.remove(index);
			
			if (child instanceof DefaultTreeNode)
				((DefaultTreeNode<E>)child).setParent(null);
			
			DefaultTreeModel<E> model = getModel();
			if (model != null) {
				model.fireEvent(DefaultTreeNode.this, index, index, TreeDataEvent.INTERVAL_REMOVED);
				int[] path = Tree.getPath(model, model.getRoot(), child);
				model.removeSelectionPath(path);
				model.removeOpenPath(path);
			}
			
			return child;
		}
		
		public boolean remove(Object child) {
			int index = _list.indexOf(child);
			if (index < 0)
				return false;
			remove(index);
			return true;
		}

		/** Used only internally by DefaultTreeModel.sort0(). it won't fire event INTERVAL_ADDED or INTERVAL_REMOVED */
		// B50-ZK-566: Set sortDirection to treecol will show an error
		@SuppressWarnings("unchecked")
		/*package*/ void sort(Comparator cmpr) {
			Collections.sort(_list, cmpr);
		}
	}
	
}
