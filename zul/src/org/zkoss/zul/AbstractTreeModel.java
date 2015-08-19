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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.io.Serializables;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.ext.Openable;
import org.zkoss.zul.ext.Pageable;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.ext.SelectionControl;
import org.zkoss.zul.ext.TreeOpenableModel;
import org.zkoss.zul.ext.TreeSelectableModel;

/**
 * A skeletal implementation for {@link TreeModel}.
 * 
 * <p>{@link AbstractTreeModel} implements both {@link TreeSelectableModel}
 * and {@link TreeOpenableModel}. In other words, it stores the selection
 * and open states, such that {@link Tree} and other UI can interact with.
 *
 * <p>In additions, {@link AbstractTreeModel} also implements
 * {@link Selectable} and {@link Openable} to simplify the access
 * (and provides backward compatibility to ZK 5 and earlier).
 * However, these two interfaces are optional and designed for application.
 * {@link Tree} and all ZK core don't access it at all.
 *
 * <p>For introduction, please refer to <a href=
 * "http://books.zkoss.org/wiki/ZK_Developer's_Reference/MVC/Model/Tree_Model"
 * >ZK Developer's Reference: Tree Model</a>.
 * 
 * @author jumperchen
 * @author tomyeh
 * @since 3.0.0
 */
abstract public class AbstractTreeModel<E> implements TreeModel<E>,
TreeSelectableModel, TreeOpenableModel, Selectable<E>, Openable<E>,
java.io.Serializable, Pageable {
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

	private SelectionControl<E> _ctrl;
	public void setSelectionControl(SelectionControl ctrl) {
		_ctrl = ctrl;
	}

	public SelectionControl getSelectionControl() {
		return _ctrl;
	}
	/**
	 * Creates a {@link AbstractTreeModel}.
	 * 
	 * @param root
	 *            root of tree
	 */
	public AbstractTreeModel(E root) {
		_root = root;
		//ZK-2611: only for TreeDataEvent.INTERVAL_REMOVED and INTERVAL_ADDED:
		addTreeDataListener(new TreeDataListener() {
			public void onChange(TreeDataEvent event) {
				updatePath(event);
				invalidatePageCount();
			}
		});
		_ctrl = new DefaultSelectionControl(this);
	}
	
	private void updatePath(TreeDataEvent event) {
		final int type = event.getType();
		final int[] affectedPath = event.getAffectedPath();
		if (affectedPath == null || affectedPath.length < 1) return;
		switch (type) {
		case TreeDataEvent.INTERVAL_REMOVED:
			internalDataChange(_opens, affectedPath, true);
			internalDataChange(_selection, affectedPath, true);
			break;
		case TreeDataEvent.INTERVAL_ADDED:
			internalDataChange(_opens, affectedPath, false);
			internalDataChange(_selection, affectedPath, false);
			break;
		}
	}
	
	private void internalDataChange(Set<Path> openOrSelect, int[] affectedPath, boolean isRemoved) {
		List<Path> list = new LinkedList<Path>(openOrSelect);
		int update = isRemoved ? -1 : 1;
		for (Iterator<Path> it = list.iterator(); it.hasNext();) {
			Path p = it.next();
			// p.verifyPrefix should be execute anyway
			if (p.verifyPrefix(affectedPath, update) && isRemoved) {
				it.remove();
			}
		}
		openOrSelect.clear();
		openOrSelect.addAll(list);
	}

	/**
	 * Return the root of the tree model.
	 */
	public E getRoot() {
		return _root;
	}
	/** Sets the root of the tree model.
	 */
	/*package*/ void setRootDirectly(E root) {
		_root = root;
	}

	/** @deprecated As of release 6.0.0, replaced with {@link #fireEvent(int, int[], int, int)}.
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
	 * Has the same functionality with {@link #fireEvent(int, int[], int, int)},
	 * while this is used for node removal only
	 * 
	 * @since 7.0.5
	 */
	public void fireEvent(int evtType, int[] path, int indexFrom, int indexTo, int[] affectedPath) {
		final TreeDataEvent evt =
			new TreeDataEvent(this, evtType, path, indexFrom, indexTo, affectedPath);
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
		final int cnt = _childCount(parent);
		for (int j = 0; j < cnt; ++j)
			if (Objects.equals(child, getChild(parent, j)))
				return j;
		return -1;
	}

	
	public E getChild(int[] path) {
		E node = getRoot();		
		for (int childCount = 0, i = 0; i < path.length && node != null; i++) {
			if (path[i] < 0 || path[i] > (childCount = _childCount(node)))
				return null;
			node = getChild(node, path[i]);
		}
		return node;
	}
	//Retrieves the child count. Note: it won't call getChildCount if isLeaf
	private int _childCount(E parent) {
		return isLeaf(parent) ? 0: getChildCount(parent);
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

		for (int i = 0, size = _childCount(node); i< size; i++)
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
	
	public void removeTreeDataListener(TreeDataListener l) {
		_listeners.remove(l);
	}

	//TreeSelectableModel//
	
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
	
	public boolean isMultiple() {
		return _multiple;
	}

	// TreeSelectableModel
	
	public boolean addSelectionPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			return addSelectionPaths(paths);
		}
		return false;
	}

	
	public boolean addSelectionPaths(int[][] paths) {
		boolean added = false;
		final int len = paths != null ? paths.length : 0;
		final boolean multiple = isMultiple();
		for (int j = 0; j < len; ++j)
			if (paths[j] != null) {
				final Path path = new Path(paths[j]);
				if (multiple) {
					if (_selection.add(path)) {
						added = true;
						fireSelectionChanged(path.path);
					}
				} else {
					if (!_selection.contains(path)) {
						added = true;
						_selection.clear();
						_selection.add(path);
						fireSelectionChanged(path.path);
					}
					break; //done
				}
			}
		return added;
	}

	
	public boolean removeSelectionPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			return removeSelectionPaths(paths);
		}
		return false;
	}

	
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

	
	public boolean isPathSelected(int[] path) {
		return path != null && _selection.contains(new Path(path));
	}

	
	public int[] getSelectionPath() {
		return _selection.isEmpty() ? null: _selection.iterator().next().path;
	}

	
	public int[][] getSelectionPaths() {
		if (_selection.isEmpty())
			return null;

		final int[][] paths = new int[_selection.size()][];
		int j = 0;
		for (final Path path : _selection)
			paths[j++] = path.path;
		return paths;
	}

	
	public int getSelectionCount() {
		return _selection.size();
	}

	
	public boolean isSelectionEmpty() {
		return _selection.isEmpty();
	}

	
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
	
	public boolean addOpenPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			return addOpenPaths(paths);
		}
		return false;
	}
	
	
	public boolean addOpenPaths(int[][] paths) {
		boolean added = false;
		final int len = paths != null ? paths.length : 0;
		for (int j = 0; j < len; ++j) {
			if (paths[j] != null) {
				final Path path = new Path(paths[j]);
				if (_opens.add(path)) {
					added = true;
					fireOpenChanged(path.path);
				}
			}
		}
		return added;
	}

	
	public boolean removeOpenPath(int[] path) {
		if (path != null && path.length > 0) {
			final int[][] paths = new int[1][path.length];
			paths[0] = path;
			return removeOpenPaths(paths);
		}
		return false;
	}

	
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

	
	public boolean isPathOpened(int[] path) {
		return path != null && _opens.contains(new Path(path));
	}

	
	public int[] getOpenPath() {
		return _opens.isEmpty() ? null: _opens.iterator().next().path;
	}

	
	public int[][] getOpenPaths() {
		if (_opens.isEmpty())
			return null;

		final int[][] paths = new int[_opens.size()][];
		int j = 0;
		for (final Path path : _opens)
			paths[j++] = path.path;
		return paths;
	}

	
	public int getOpenCount() {
		return _opens.size();
	}

	
	public boolean isOpenEmpty() {
		return _opens.isEmpty();
	}

	
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

	/** A utility that the deriving class can call to save the states
	 * before sorting the model.
	 * <p>Default: saves the selection and open states.
	 * <p>For example, {@link DefaultTreeModel#sort} invokes it to preserve
	 * the selection to the same objects (rather than the same paths).
	 * @since 6.0.0
	 */
	protected Object beforeSort() {
		final States<E> states = new States<E>();
		for (final Path path : _selection)
			states.selection.add(getChild(path.path));
		for (final Path path : _opens)
			states.opens.add(getChild(path.path));
		return states;
	}
	/** A utility that the deriving class can call to restore the states
	 * saved by {@link #beforeSort}
	 * @since 6.0.0
	 */
	protected void afterSort(Object ctx) {
		if (ctx instanceof States) {
			@SuppressWarnings("unchecked")
			final States<E> states = (States)ctx;
			_selection.clear();
			for (final E node: states.selection)
				_selection.add(new Path(getPath(node)));
			_opens.clear();
			for (final E node: states.opens)
				_opens.add(new Path(getPath(node)));
		}
	}

	//Selectable//
	
	public Set<E> getSelection() {
		final Set<E> selected = new LinkedHashSet<E>();
		int[][] paths = getSelectionPaths();
		if (paths != null)
			for (int i = 0; i < paths.length; i++)
				selected.add(getChild(paths[i]));
		return selected;
	}
	
	public void setSelection(Collection<? extends E> selection) {
		if (isSelectionChanged(selection)) {
			clearSelection();
			for (final E node: selection)
				addToSelection(node);
		}
	}
	private boolean isSelectionChanged(Collection<? extends E> selection) {
		if (_selection.size() != selection.size())
			return true;

		for (final E e: selection)
			if (!_selection.contains(e))
				return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	
	public boolean isSelected(Object child) {
		final int[] path = getPath((E)child);
		if (path != null && path.length > 0)
			return isPathSelected(path);
		return false;
	}
	
	public boolean addToSelection(E child) {
		final int[] path = getPath(child);
		if (path != null && path.length > 0)
			return addSelectionPath(path);
		return false;
	}
	@SuppressWarnings("unchecked")
	
	public boolean removeFromSelection(Object child) {
		final int[] path = getPath((E)child);
		if (path != null && path.length > 0)
			return removeSelectionPath(path);
		return false;
	}

	//Openable//
	
	public Set<E> getOpenObjects() {
		final Set<E> opened = new LinkedHashSet<E>();
		int[][] paths = getOpenPaths();
		if (paths != null)
			for (int i = 0; i < paths.length; i++)
				opened.add(getChild(paths[i]));
		return opened;
	}
	
	public void setOpenObjects(Collection<? extends E> opened) {
		clearOpen();
		for (final E node: opened)
			addOpenObject(node);
	}
	@SuppressWarnings("unchecked")
	
	public boolean isObjectOpened(Object child) {
		final int[] path = getPath((E)child);
		if (path != null && path.length > 0)
			return isPathOpened(path);
		return false;
	}
	
	public boolean addOpenObject(E child) {
		final int[] path = getPath(child);
		if (path != null && path.length > 0)
			return addOpenPath(path);
		return false;
	}
	@SuppressWarnings("unchecked")
	
	public boolean removeOpenObject(Object child) {
		final int[] path = getPath((E)child);
		if (path != null && path.length > 0)
			return removeOpenPath(path);
		return false;
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
	protected static class Path implements java.io.Serializable, Comparable {
		public final int[] path;
		protected Path(int[] path) {
			this.path = path;
		}
		
		protected Path(Path p) {
			int length = p.path.length;
			this.path = new int[length];
			for (int i = 0; i < length; i++) {
				this.path[i] = p.path[i];
			}
		}
		
		public int hashCode() {
			return Objects.hashCode(path);
		}
		
		public boolean equals(Object o) {
			return o instanceof Path && Objects.equals(path, ((Path)o).path);
		}

		public int compareTo(Object o) {
			if (!(o instanceof Path))
				throw new WrongValueException(o + " is not Path object");
			int length = path.length;
			Path toCompared = (Path)o;
			int[] toPath = toCompared.path;
			int toLength = toCompared.path.length;

			int smaller = (length < toLength? length : toLength);
			for (int i = 0; i < smaller; i++) {
				if (path[i] != toPath[i])
					return path[i] - toPath[i];
			}
			return length - toLength;
		}
		
		private boolean verifyPrefix(int[] path) {
			return verifyPrefix(path, 0);
		}
		
		/**
		 * Test if this object's path match the given path when removal or addition,
		 * and there are three situations:
		 * 1. if this.path is fully matched, then it should be removed or updated
		 * 2. if this.path match the given path except the last digit, they are
		 * sibling, and we have to update the value by "update"
		 * value of update:
		 * 0 -> not update
		 * 1 -> add one if behind prefix path (when model added)
		 * -1 -> remove one if behind prefix path (when model removed
		 * 3. if it failed matching before last digit, they are not sibling,
		 * and just ignore this.path
		 * 
		 * Note: Because it will update this.path, be careful to use this function.
		 * @param path the path to be matched
		 * @param update 
		 * @return boolean
		 * @since 7.0.5
		 */
		private boolean verifyPrefix(int[] path, int update) {
			if (path.length > this.path.length)
				return false;
			int i = 0;
			for (; i < path.length; i++) {
				if (path[i] != this.path[i])
					break;
			}
			
			if (i == path.length) { //fully match prefix, should be removed
				if (update == 1) //only for add case
					this.path[i-1] += update;
				return true;
			} else if (i == path.length - 1) { //different on last digit, sibling
				if (this.path[i] > path[i]) //this.path is later
					this.path[i] += update;
				return false;
			} else {
				return false;
			}		
		}
		
		public String toString() {
			String result = "[";
			for (int i = 0; i < path.length; i++) {
				result += path[i] + ", ";
			}
			result = result.substring(0, result.length() - 2) + "]";
			return result;
		}
	}
	private static class States<E> {
		private final Set<E> selection = new LinkedHashSet<E>();
		private final Set<E> opens = new LinkedHashSet<E>();
	}

	// Pageable //
	private int _pageSize = 20; // same default as paging
	private int _activePage = 0; // same default as paging
	private int _pageCount = -1; // pending calculation

	// Pageable //
	public int getPageSize() {
		return _pageSize;
	}

	public void setPageSize(int size) throws WrongValueException {
		if (size < 0) {
			throw new WrongValueException("expecting positive non zero value, got: " + size);
		}
		_pageSize = size;
		invalidatePageCount();
	}

	/**
	 * {@inheritDoc}
	 * <br><br>
	 * Note: the entire tree will be traversed once to count the number of tree nodes, 
	 * although the result will be cached until the model has changed (open/add/remove/etc.), 
	 * it is still a VERY EXPENSIVE operation, please @Override to provide your 
	 * own implementation for better performance
	 * 
	 * @return number of pages, or 1 if the model is empty
	 * @since 8.0.0 
	 */
	public int getPageCount() {
		if (_pageCount < 1) { // dirty/invalid value, re-calculation required
			if (_root != null) {
				int count = getChildNodeCount(_root);
				if (count <= 0) { // got no child, return one page anyway
					_pageCount = 1;
				} else {
					int pageCount = count / _pageSize;
					if (count % _pageSize == 0) {
						_pageCount = pageCount;
					} else {
						_pageCount = pageCount + 1;
					}
				}
			} else {
				_pageCount = 1; // will always have at least one page
			}
		}
		return _pageCount;
	}

	// count the number of child nodes, will traverse into any opened child 
	// nodes, depth-first style
	private int getChildNodeCount(E node) {
		int c = getChildCount(node);
		int count = c;
		for (int i = 0; i < c; i++) {
			E child = getChild(node, i);
			if (isPathOpened(getPath(child))) {
				count += getChildNodeCount(child);
			}
		}
		return count;
	}
	
	//ZK-1696: set to a invalid value to trigger re-calculation when getPageCount() is called
	private void invalidatePageCount() {
		_pageCount = -1;
	}

	public int getActivePage() {
		return _activePage;
	}

	public void setActivePage(int pg) throws WrongValueException {
		if (pg < 0) {
			throw new WrongValueException("expecting positive non zero value, got: " + pg);
		}
		_activePage = pg;
	}

	private List<E> getAllNodes() {
		E root = getRoot();
		List all = new LinkedList();
		if (root != null) {
			getChildNodes(all, root);
		}
		return all;
	}

	private void getChildNodes(List<E> all, E parent) {
		for (int i = 0, j = getChildNodeCount(parent); i < j; i++) {
			E child = getChild(parent, i);
			if (child != null) {
				all.add(child);
				getChildNodes(all, child);
			}
		}
	}
	/**
	 * A default selection control implementation for {@link AbstractTreeModel},
	 * by default it assumes all elements are selectable.
	 * <p>Note: the implementation is not used for a huge data model, if in this case,
	 * please implement your own one to speed up.</p>
	 * @since 8.0.0
	 */
	public static class DefaultSelectionControl<E> implements SelectionControl<E> {
		private AbstractTreeModel model;
		public DefaultSelectionControl(AbstractTreeModel model) {
			this.model = model;
		}
		public boolean isSelectable(E e) {
			return true;
		}
		public void setSelectAll(boolean selectAll) {
			if (selectAll) {
				List all = new LinkedList();
				List<E> allNodes = model.getAllNodes();
				for (E o : allNodes) {
					if (isSelectable(o)) // check whether it can be selectable or not
						all.add(o);
				}

				// avoid scroll into view at client side.
				model.fireEvent(TreeDataEvent.DISABLE_CLIENT_UPDATE, null, -1, -1);

				if (model instanceof AbstractTreeModel)
					try {
						((Selectable)model).setSelection(all);
					} finally {
						model.fireEvent(TreeDataEvent.ENABLE_CLIENT_UPDATE, null, -1, -1);
					}
			} else {
				((Selectable)model).clearSelection();
			}
		}
		public boolean isSelectAll() {
			List<E> allNodes = model.getAllNodes();
			for (E o : allNodes) {
				if (isSelectable(o) && !((Selectable)model).isSelected(o))
					return false;
			}
			return true;
		}
	}
}
