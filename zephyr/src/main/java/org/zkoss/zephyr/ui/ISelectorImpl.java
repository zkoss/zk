/* ISelectorImpl.java

	Purpose:
		
	Description:
		
	History:
		11:15 AM 2022/8/18, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.zkoss.util.Pair;
import org.zkoss.zephyr.zpr.IChildable;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IComposite;
import org.zkoss.zephyr.zpr.ISingleChildable;

/**
 * A selector context to hold a root {@link IComponent} for seeking icomponents.
 * @author jumperchen
 */
/*package*/ class ISelectorImpl implements ISelector {
	private final IComponent<?> _root;

	protected ISelectorImpl(IComponent<?> root) {
		_root = root;
	}

	/**
	 * Returns the target icomponent by its given id if matched.
	 * @param id the id of the icomponent for seeking
	 */
	public <T extends IComponent<?>> T get(String id) {
		for (DFSIComponentIterator it = new DFSIComponentIterator(_root); it.hasNext(); ) {
			Pair<IComponent<?>, List<Integer>> pair = it.next();
			IComponent<?> icmp = pair.getX();
			if (id.equals(icmp.getId())) {
				return (T) icmp;
			}
		}
		return null;
	}

	/**
	 * Returns the target icomponent by its given path if matched.
	 * @param path the path of the icomponent for seeking
	 */
	public <T extends IComponent<?>> T get(int[] path) {
		if (path.length == 0) return (T) _root;
		IComponent<?> current = _root;
		for (int index = 0, j = path.length; index < j && current != null; index++) {
			int i = path[index];
			List<IComponent<?>> children = getChildren(current);
			if (children.size() > i) {
				current = children.get(i);
			}
		}
		return (T) current;
	}

	/**
	 * Returns all the ancestor from top to bottom for the given path.
	 * @param path the path of the icomponent for seeking
	 */
	public <T extends IComponent<?>> List<T> getAncestor(int[] path) {
		List<T> result = new ArrayList<T>();
		while (path != null && path.length > 0) {
			int[] parentPath = new int[path.length - 1];
			System.arraycopy(path, 0, parentPath, 0, parentPath.length);
			result.add(get(parentPath));
			path = parentPath;
		}
		if (result.isEmpty()) return result;

		Collections.reverse(result);

		return result;
	}

	/**
	 * Returns all the ancestor from top to bottom for the given ichild.
	 * @param ichild the icomponent for seeking
	 */
	public <T extends IComponent<?>> List<T> getAncestor(IComponent ichild) {
		return getAncestor(getPath(ichild));
	}

	/**
	 * Returns the target parent.
	 * <p>Note: If not found, {@code null} is assumed</p>
	 * @param ichild the icomponent for seeking
	 */
	public <T extends IComponent<?>> T getParent(IComponent ichild) {
		int[] path = getPath(ichild);
		if (path != null) {
			if (path.length <= 1) {
				return (T) _root;
			}
			int[] parentPath = new int[path.length - 1];
			System.arraycopy(path, 0, parentPath, 0, parentPath.length);
			return get(parentPath);
		}
		return null;
	}

	/**
	 * Returns the target path.
	 * <p>Note: If not found, {@code null} is assumed</p>
	 * @param ichild the icomponent for seeking
	 */
	public int[] getPath(IComponent ichild) {
		if (ichild == _root) return new int[0];
		for (DFSIComponentIterator it = new DFSIComponentIterator(_root); it.hasNext(); ) {
			Pair<IComponent<?>, List<Integer>> pair = it.next();
			IComponent<?> icmp = pair.getX();
			if (ichild.equals(icmp)) {
				return pair.getY().stream().mapToInt(i -> i).toArray();
			}
		}
		return null;
	}

	private static List<IComponent<?>> getChildren(IComponent<?> parent) {
		if (parent instanceof IComposite) {
			return ((IComposite<IComponent<?>>) parent).getAllComponents();
		} else if (parent instanceof IChildable) {
			return ((IChildable) parent).getChildren();
		} else if (parent instanceof ISingleChildable) {
			IComponent child = ((ISingleChildable) parent).getChild();
			if (child != null) {
				return Arrays.asList(child);
			}
		}
		return Collections.emptyList();
	}

	private static class DFSIComponentIterator implements Iterator<Pair<IComponent<?>, List<Integer>>> {
		private Map<IComponent, Boolean> _visited;
		private final Deque<Pair<IComponent<?>, List<Integer>>> _deque;
		private final Deque<Iterator<IComponent<?>>> _levels;
		private final List<Integer> _currentPath;
		private DFSIComponentIterator(IComponent<?> root) {
			_deque = new ArrayDeque<>();
			_deque.push(new Pair<>(root, new ArrayList<>()));
			_visited = new HashMap<>();
			_levels = new ArrayDeque<>();
			_currentPath = new ArrayList<>();
		}
		public boolean hasNext() {
			if (_deque.isEmpty()) {
				return false;
			}
			Pair<IComponent<?>, List<Integer>> peek = _deque.peek();

			// if not empty, we need to iterator the next item.
			if (!_levels.isEmpty()) {
				Iterator<IComponent<?>> levelsPeek = _levels.peek();
				if (levelsPeek.hasNext()) {
					int endIndex = _currentPath.size() - 1;
					_currentPath.set(endIndex, _currentPath.get(endIndex) + 1);
					_deque.push(new Pair<>(levelsPeek.next(), new ArrayList<>(_currentPath)));
				} else {
					_levels.pop();
					_currentPath.remove(_currentPath.size() - 1);
				}
			}
			peek = _deque.peek();
			while (_visited.putIfAbsent(peek.getX(), Boolean.TRUE) == null) {
				List<IComponent<?>> children = getChildren(peek.getX());
				Iterator<IComponent<?>> iterator = children.iterator();
				if (iterator.hasNext()) {
					_levels.push(iterator);
					_currentPath.add(0);
					peek = new Pair<>(iterator.next(), new ArrayList<>(_currentPath));
					_deque.push(peek);
					continue;
				}
				break;
			}
			return true;
		}

		public Pair<IComponent<?>, List<Integer>> next() {
			if (_deque.isEmpty()) {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
			}
			return _deque.pop();
		}
	}
}
