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
import java.util.Set;
import java.util.LinkedHashSet;
import org.zkoss.lang.Objects;
import org.zkoss.io.Serializables;

import org.zkoss.zul.ext.TreeOpenableModel;
import org.zkoss.zul.ext.TreeSelectableModel;
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
 * @author jumperchen
 * @author tomyeh
 * @since 3.0.0
 */
abstract public class AbstractTreeModel<E> implements TreeModel<E>,
TreeSelectableModel, TreeOpenableModel, java.io.Serializable {
	/**
	 * The root object to be return by method {@link #getRoot()}.
	 */
	private E _root;
	private transient List<TreeDataListener> _listeners = new LinkedList<TreeDataListener>();
	/** The selection. */
	protected Set<Path> _selection = new LinkedHashSet<Path>();
	/** The open information. */
	protected Set<Path> _opens = new LinkedHashSet<Path>();
	private boolean _multiple;

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

	/** @deprecated As of release 6.0.0, replaced with {@link fireEvent(int, int[], int, int)}.
	 */
	public void fireEvent(E node, int indexFrom, int indexTo, int evtType) {
		fireEvent(evtType, getPath(node), indexFrom, indexTo);
	}
	/**
	 * Fires a {@link TreeDataEvent} for all registered listener
	 * 
	 * <p>
	 * Note: you can invoke this method only in an event listener.
	 * @since 6.0.0
	 */
	public void fireEvent(int evtType, int[] path, int indexFrom, int indexTo) {
		final TreeDataEvent evt =
			new TreeDataEvent(this, evtType, path, indexFrom, indexTo);
		for (TreeDataListener l : _listeners)
			l.onChange(evt);
	}
	/**
	 * Fires a {@link TreeDataEvent} for all registered listener when selection
	 * status has changed.
	 * 
	 * @since 6.0.0
	 */
	protected void fireSelectionChanged(int[] path) {
		final TreeDataEvent evt = new TreeDataEvent(this,
				TreeDataEvent.SELECTION_CHANGED, path, 0, 1);
		for (TreeDataListener l : _listeners)
			l.onChange(evt);
	}
	/**
	 * Fires a {@link TreeDataEvent} for all registered listener when open
	 * status has changed.
	 * 
	 * @since 6.0.0
	 */
	protected void fireOpenChanged(int[] path) {
		final TreeDataEvent evt = new TreeDataEvent(this,
				TreeDataEvent.OPEN_CHANGED, path, 0, 1);
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
		if (path.length == 0) return parent;
		
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
	@Override
	public void addTreeDataListener(TreeDataListener l) {
		_listeners.add(l);
	}
	@Override
	public void removeTreeDataListener(TreeDataListener l) {
		_listeners.remove(l);
	}

	//TreeSelectableModel//
	@Override
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			fireEvent(TreeDataEvent.MULTIPLE_CHANGED, null, -1, -1);

			if (!multiple && _selection.size() > 1) {
				final List<Path> sels = new LinkedList<Path>();
				sels.addAll(_selection);
				final Path sel = sels.remove(0);
				_selection.clear();
				_selection.add(sel);

				for (final Path path: sels)
					fireSelectionChanged(path.path);
			}
		}
	}
	@Override
	public boolean isMultiple() {
		return _multiple;
	}

	// TreeSelectableModel
	@Override
	public void addSelectionPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			addSelectionPaths(paths);
		}
	}

	@Override
	public void addSelectionPaths(int[][] paths) {
		final int len = paths != null ? paths.length : 0;
		final boolean multiple = isMultiple();
		for (int j = 0; j < len; ++j)
			if (paths[j] != null) {
				final Path path = new Path(paths[j]);
				if (multiple) {
					if (_selection.add(path))
						fireSelectionChanged(path.path);
				} else {
					if (!_selection.contains(path)) {
						_selection.clear();
						_selection.add(path);
						fireSelectionChanged(path.path);
					}
					break; //done
				}
			}
	}

	@Override
	public boolean removeSelectionPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			return removeSelectionPaths(paths);
		}
		return false;
	}

	@Override
	public boolean removeSelectionPaths(int[][] paths) {
		boolean found = false;
		final int len = paths != null ? paths.length : 0;
		for (int j = 0; j < len && !_selection.isEmpty(); ++j) {
			final Path path = new Path(paths[j]);
			if (_selection.remove(path)) {
				found = true;
				fireSelectionChanged(path.path);
			}
			if (!isMultiple())
				break;
		}
		return found;
	}

	@Override
	public boolean isPathSelected(int[] path) {
		return path != null && _selection.contains(new Path(path));
	}

	@Override
	public int[] getSelectionPath() {
		return _selection.isEmpty() ? null: _selection.iterator().next().path;
	}

	@Override
	public int[][] getSelectionPaths() {
		if (_selection.isEmpty())
			return null;

		final int[][] paths = new int[_selection.size()][];
		int j = 0;
		for (final Path path : _selection)
			paths[j++] = path.path;
		return paths;
	}

	@Override
	public int getSelectionCount() {
		return _selection.size();
	}

	@Override
	public boolean isSelectionEmpty() {
		return _selection.isEmpty();
	}

	@Override
	public void clearSelection() {
		if (!_selection.isEmpty()) {
			final int[][] paths = getSelectionPaths();
			if (paths != null) {
				_selection.clear();
				for (int j = 0; j < paths.length;  ++j)
					fireSelectionChanged(paths[j]);
			}
		}
	}

	// TreeOpenableModel //
	@Override
	public void addOpenPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			addOpenPaths(paths);
		}
	}
	
	@Override
	public void addOpenPaths(int[][] paths) {
		final int len = paths != null ? paths.length : 0;
		for (int j = 0; j < len; ++j) {
			if (paths[j] != null) {
				final Path path = new Path(paths[j]);
				if (_opens.add(path))
					fireOpenChanged(path.path);
			}
		}
	}

	@Override
	public boolean removeOpenPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			return removeOpenPaths(paths);
		}
		return false;
	}

	@Override
	public boolean removeOpenPaths(int[][] paths) {
		boolean found = false;
		final int len = paths != null ? paths.length : 0;
		for (int j = 0; j < len && !_opens.isEmpty(); ++j) {
			final Path path = new Path(paths[j]);
			if (_opens.remove(path)) {
				found = true;
				fireOpenChanged(path.path);
			}
		}
		return found;
	}

	@Override
	public boolean isPathOpened(int[] path) {
		return path != null && _opens.contains(new Path(path));
	}

	@Override
	public int[] getOpenPath() {
		return _opens.isEmpty() ? null: _opens.iterator().next().path;
	}

	@Override
	public int[][] getOpenPaths() {
		if (_opens.isEmpty())
			return null;

		final int[][] paths = new int[_opens.size()][];
		int j = 0;
		for (final Path path : _opens)
			paths[j++] = path.path;
		return paths;
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
			final int[][] paths = getOpenPaths();
			if (paths != null) {
				_opens.clear();
				for (int j = 0; j < paths.length;  ++j)
					fireOpenChanged(paths[j]);
			}
		}
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
		clone._opens = new LinkedHashSet<Path>(_opens);
		clone._selection = new LinkedHashSet<Path>(_selection);
		return clone;
	}

	/** Represents a tree path.
	 * @since 6.0.0
	 */
	protected static class Path implements java.io.Serializable {
		public final int[] path;
		protected Path(int[] path) {
			this.path = path;
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(path);
		}
		@Override
		public boolean equals(Object o) {
			return o instanceof Path && Objects.equals(path, ((Path)o).path);
		}
	}
}
