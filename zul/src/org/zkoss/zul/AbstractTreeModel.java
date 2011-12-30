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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.io.Serializables;

import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.ext.TreeOpenableModel;
import org.zkoss.zul.ext.TreeSelectionModel;

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
		TreeSelectionModel, TreeOpenableModel<E>, java.io.Serializable {
	/**
	 * The root object to be return by method {@link #getRoot()}.
	 */
	private E _root;

	private boolean _multiple;

	private transient List<TreeDataListener> _listeners = new LinkedList<TreeDataListener>();

	private HashMap<E, Boolean> _opens = new HashMap<E, Boolean>();

	private HashMap<E, Boolean> _selections = new HashMap<E, Boolean>();

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

	// -- TreeModel --//
	public void addTreeDataListener(TreeDataListener l) {
		_listeners.add(l);
	}

	// -- TreeModel --//
	public void removeTreeDataListener(TreeDataListener l) {
		_listeners.remove(l);
	}

	/**
	 * Returns the selections set.
	 */
	public Set<E> getSelection() {
		HashSet<E> selected = new HashSet<E>();
		int[][] paths = getSelectionPaths();
		for (int i = 0; i < paths.length; i++) {
			selected.add(getChild(paths[i]));
		}
		return selected;
	}
	
	/**
	 * Add the specified object into selection.
	 * @param obj the object to be as selection.
	 */	
	public void addSelection(E obj) {
		int[] path = Tree.getPath(this, getRoot(), obj);
		if (path != null && path.length > 0)
			addSelectionPath(path);
	}
	
	/**
	 * Remove the specified object from selection.
	 * @param obj the object to be remove from selection.	 * 
	 */
	public void removeSelection(E obj) {
		int[] path = Tree.getPath(this, getRoot(), obj);
		if (path != null && path.length > 0)
			addSelectionPath(path);
	}
	/**
	 * Sets the specified object into open.
	 * @param obj the object to be as open.
	 * @param open whether be opened
	 */
	public void setOpen(E obj, boolean open) {
		int[] path = Tree.getPath(this, getRoot(), obj);
		if (path != null && path.length > 0) {
			if (open)
				addOpenPath(path);
			else
				removeOpenPath(path);
		}
	}
	
	/**
	 * Returns whether the specified object be opened.
	 * @param obj
	 */
	public boolean isOpen(E obj) {
		int[] path = Tree.getPath(this, getRoot(), obj);
		if (path != null && path.length > 0) {
			return isPathOpened(path);
		}
		return false;
	}
	
	// TreeOpenableModel
	@Override
	public void addOpenPath(int[] path) {
		if (path != null && path.length > 0) {
			int[][] paths = new int[1][path.length];
			paths[0] = path;
			addOpenPaths(paths);
		}
	}

	@Override
	public void addOpenPaths(int[][] paths) {
		int newPathLength = paths != null ? paths.length : 0;
		if (newPathLength > 0) {
			for (E e : getNodesByPath(paths)) {
				if (!_opens.containsKey(e)) {
					_opens.put(e, Boolean.TRUE);
					fireOpenChanged(e);
				}
			}
		}
	}

	@Override
	public void removeOpenPath(int[] path) {
		if (path != null && path.length > 0) {
			int[][] paths = new int[1][path.length];
			paths[0] = path;
			removeOpenPaths(paths);
		}
	}

	@Override
	public void removeOpenPaths(int[][] paths) {
		int newPathLength = paths != null ? paths.length : 0;
		if (newPathLength > 0 && !_opens.isEmpty()) {
			for (E e : getNodesByPath(paths)) {
				if (_opens.remove(e)) {
					fireOpenChanged(e);
				}
			}
		}
	}

	@Override
	public boolean isPathOpened(int[] path) {
		if (path != null && !_opens.isEmpty()) {
			E e = getChild(path);
			if (e != null)
				return _opens.containsKey(e);
		}
		return false;
	}

	@Override
	public int[] getOpenPath() {
		if (!_opens.isEmpty()) {
			return Tree.getPath(this, getRoot(), _opens.keySet().iterator()
					.next());
		} else return null;
	}

	@Override
	public int[][] getOpenPaths() {
		if (!_opens.isEmpty()) {
			List<int[]> paths = new ArrayList<int[]>();
			E root = getRoot();
			for (E e : _opens.keySet()) {
				int[] path = Tree.getPath(this, root, e);
				if (path != null)
					paths.add(path);
			}
			return (int[][]) paths.toArray();
		} else return null;
	}

	@Override
	public int getOpenCount() {
		return _opens.size();
	}

	@Override
	public boolean isOpenEmpty() {
		return _opens.isEmpty();
	}

	@Override
	public void clearOpen() {
		if (!_opens.isEmpty()) {
			for (E e : new ArrayList<E>(_opens.keySet())) {
				_opens.remove(e);
				fireOpenChanged(e);
			}
		}
	}

	// Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new LinkedList<TreeDataListener>();
		Serializables.smartRead(s, _listeners);
	}

	// -TreeModel-//
	/**
	 * Returns the path from a node
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public int[] getPath(Object parent, Object lastNode) {
		return Tree.getPath((TreeModel) this, parent, lastNode);
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
		clone._selections = new HashMap<E, Boolean>(_selections);
		clone._opens = new HashMap<E, Boolean>(_opens);

		return clone;
	}

	// Selectable//Selectable
	@Override
	public void setMultiple(boolean multiple) {
		_multiple = multiple;
	}

	@Override
	public boolean isMultiple() {
		return _multiple;
	}

	@Override
	public void addSelectionPath(int[] path) {
		if (path != null && path.length > 0) {
			int[][] paths = new int[1][path.length];
			paths[0] = path;
			addSelectionPaths(paths);
		}
	}

	@Override
	public void addSelectionPaths(int[][] paths) {
		int newPathLength = paths != null ? paths.length : 0;
		if (newPathLength > 0) {
			if (!isMultiple()) {
				List<E> newSelection = getNodesByPath(paths);
				if (!newSelection.isEmpty()) {
					E e = newSelection.get(0);
					if (!_selections.containsKey(e)) {
						_selections.clear();
						_selections.put(e, Boolean.TRUE);
						fireSelectionChanged(e);
					}
				}
			} else {
				for (E e : getNodesByPath(paths)) {
					if (!_selections.containsKey(e)) {
						_selections.put(e, Boolean.TRUE);
						fireSelectionChanged(e);
					}
				}
			}
		}
	}

	@Override
	public void removeSelectionPath(int[] path) {
		if (path != null && path.length > 0) {
			int[][] paths = new int[1][path.length];
			paths[0] = path;
			removeSelectionPaths(paths);
		}
	}

	@Override
	public void removeSelectionPaths(int[][] paths) {
		int newPathLength = paths != null ? paths.length : 0;
		if (newPathLength > 0 && !_selections.isEmpty()) {
			for (E e : getNodesByPath(paths)) {
				if (_selections.remove(e)) {
					fireSelectionChanged(e);
				}
				if (!isMultiple())
					break;
			}
		}
	}

	@Override
	public boolean isPathSelected(int[] path) {
		if (path != null && !_selections.isEmpty()) {
			E e = getChild(path);
			if (e != null)
				return _selections.containsKey(e);
		}
		return false;
	}

	@Override
	public int[] getSelectionPath() {
		if (!_selections.isEmpty()) {
			return Tree.getPath(this, getRoot(), _selections.keySet()
					.iterator().next());
		} else return null;
	}

	@Override
	public int[][] getSelectionPaths() {
		if (!_selections.isEmpty()) {
			List<int[]> paths = new ArrayList<int[]>();
			E root = getRoot();
			for (E e : _selections.keySet()) {
				int[] path = Tree.getPath(this, root, e);
				if (path != null)
					paths.add(path);
			}
			return (int[][]) paths.toArray();
		} else return null;
	}

	@Override
	public int getSelectionCount() {
		return _selections.size();
	}

	@Override
	public boolean isSelectionEmpty() {
		return _selections.isEmpty();
	}

	@Override
	public void clearSelection() {
		if (!_selections.isEmpty()) {
			for (E e : new ArrayList<E>(_selections.keySet())) {
				_selections.remove(e);
				fireSelectionChanged(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<E> getNodesByPath(int[][] paths) {
		if (paths == null)
			return Collections.EMPTY_LIST;
		List<E> list = new ArrayList<E>();
		for (int[] path : paths) {
			E node = getChild(path);
			if (node != null)
				list.add(node);
		}
		return list;
	}
	
	/**
	 * Returns the child of parent at path where the path indicates the node is
	 * placed in the whole tree.
	 * @param path the tree path
	 * @return the child of parent at path
	 */
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
}
