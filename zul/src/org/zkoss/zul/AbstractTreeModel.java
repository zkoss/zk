/* AbstractTreeModel.java

	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.lang.Objects;
import org.zkoss.io.Serializables;

import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

/**
 * A skeletal implementation for {@link TreeModel}.
 * 
 * <p>
 * For introduction, please refer to <a href=
 * "http://books.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model"
 * >ZK Developer's Reference: Tree Model</a>.
 * 
 * @author Jeff Liu
 * @author jumperchen
 * @since 3.0.0
 */
abstract public class AbstractTreeModel<E> implements TreeModel<E>,
		java.io.Serializable {
	/**
	 * The root object to be return by method {@link #getRoot()}.
	 */
	private E _root;

	private transient List<TreeDataListener> _listeners = new LinkedList<TreeDataListener>();


	/**
	 * Creates a {@link AbstractTreeModel}.
	 * 
	 * @param root
	 *            root of tree
	 */
	public AbstractTreeModel(E root) {
		_root = root;
	}

	/**
	 * Return the root of tree
	 * 
	 * @return the root of tree
	 */
	public E getRoot() {
		return _root;
	}

	/**
	 * Fires a {@link TreeDataEvent} for all registered listener
	 * 
	 * <p>
	 * Note: you can invoke this method only in an event listener.
	 */
	public void fireEvent(E node, int indexFrom, int indexTo, int evtType) {
		final TreeDataEvent<E> evt = new TreeDataEvent<E>(this, evtType, node,
				indexFrom, indexTo);
		for (TreeDataListener l : _listeners)
			l.onChange(evt);
	}

	/**
	 * Fires a {@link TreeDataEvent} for all registered listener when selection
	 * status has changed.
	 * 
	 * @since 6.0.0
	 */
	protected void fireSelectionChanged(E node) {
		final TreeDataEvent<E> evt = new TreeDataEvent<E>(this,
				TreeDataEvent.SELECTION_CHANGED, node, 0, 1);
		for (TreeDataListener l : _listeners)
			l.onChange(evt);
	}

	/**
	 * Fires a {@link TreeDataEvent} for all registered listener when open
	 * status has changed.
	 * 
	 * @since 6.0.0
	 */
	protected void fireOpenChanged(E node) {
		final TreeDataEvent<E> evt = new TreeDataEvent<E>(this,
				TreeDataEvent.OPEN_CHANGED, node, 0, 1);
		for (TreeDataListener l : _listeners)
			l.onChange(evt);
	}

	/**
	 * Returns the index of child in parent. If either parent or child is null,
	 * returns -1. If either parent or child don't belong to this tree model,
	 * returns -1.
	 * <p>
	 * The default implementation iterates through all children of
	 * <code>parent</code> by invoking, and check if <code>child</code> is part
	 * of them. You could override it if you have a better algorithm.
	 * {@link #getChild}
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @param child
	 *            the node we are interested in
	 * @return the index of the child in the parent, or -1 if either child or
	 *         parent are null or don't belong to this tree model
	 * @since 5.0.6
	 */
	public int getIndexOfChild(E parent, E child) {
		final int cnt = getChildCount(parent);
		for (int j = 0; j < cnt; ++j)
			if (Objects.equals(child, getChild(parent, j)))
				return j;
		return -1;
	}

	@Override
	public E getChild(int[] path) {
		E parent = getRoot();
		E node = null;
		int childCount = getChildCount(parent);
		for (int i = 0; i < path.length; i++) {
			if (path[i] < 0 || path[i] > childCount)
				return null;
			node = getChild(parent, path[i]);

			if (node != null && (childCount = getChildCount(node)) > 0) {
				parent = node;
			} else if (i != path.length - 1) {
				return null;
			}
		}
		return node;
	}


	/**
	 * Returns the path from the specified child.
	 * This implementation looks for the child by traversing every possible
	 * child (deep-first).
	 * It is suggested to override this method for better performance,
	 * if there is a better algorithm.
	 * @since 6.0.0 
	 */
	public int[] getPath(E child) {
		final List<Integer> path = new ArrayList<Integer>();
		dfSearch(path, getRoot(), child);

		final int[] ipath = new int[path.size()];
		for (int j = 0; j < ipath.length; j++)
			ipath[j] = path.get(j);
		return ipath;
	}
	private boolean dfSearch(List<Integer> path, E node, E target){
		if (node.equals(target))
			return true;
		if (isLeaf(node))
			return false;

		for (int i = 0, size = getChildCount(node); i< size; i++)
			if (dfSearch(path, getChild(node, i), target)){
				path.add(0, new Integer(i));
				return true;
			}
		return false;
	}

	// -- TreeModel --//
	public void addTreeDataListener(TreeDataListener l) {
		_listeners.add(l);
	}

	// -- TreeModel --//
	public void removeTreeDataListener(TreeDataListener l) {
		_listeners.remove(l);
	}	

	// Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}

	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new LinkedList<TreeDataListener>();
		Serializables.smartRead(s, _listeners);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		final AbstractTreeModel clone;
		try {
			clone = (AbstractTreeModel) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		clone._listeners = new LinkedList<TreeDataListener>();

		return clone;
	}
	
}
