/* ListModelSet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 01 14:38:43     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

/**
 * <p>This is the {@link ListModel} as a {@link java.util.Set} to be used with {@link Listbox}.
 * Add or remove the contents of this model as a Set would cause the associated Listbox to change accordingly.</p> 
 *
 * @author Henri Chen
 * @see ListModel
 * @see ListModelList
 * @see ListModelMap
 */
public class ListModelSet<E> extends AbstractListModel<E> implements Sortable<E>, Set<E>, java.io.Serializable {
	private static final long serialVersionUID = 20120206122849L;

	protected Set<E> _set;

	private Comparator<E> _sorting;

	private boolean _sortDir;

	/**
	 * Constructor
	 *
	 * @param set the set to represent
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified set.
	 * If false, the content of the specified set is copied.
	 * If true, this object is a 'facade' of the specified set,
	 * i.e., when you add or remove items from this {@link ListModelSet},
	 * the inner "live" set would be changed accordingly.
	 *
	 * However, it is not a good idea to modify <code>set</code>
	 * if it is passed to this method with live is true,
	 * since {@link Listbox} is not smart enough to handle it.
	 * Instead, modify it thru this object.
	 * @since 2.4.0
	 */
	public ListModelSet(Set<E> set, boolean live) {
		_set = live ? set : new LinkedHashSet<E>(set);
	}

	/**
	 * Constructor.
	 */
	public ListModelSet() {
		_set = new LinkedHashSet<E>();
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified collection (i.e., not live).
	 *
	 * <p>Notice that if the data is static or not shared, it is better to
	 * use <code>ListModelSet(set, true)</code> instead, since
	 * making a copy is slower.
	 */
	public ListModelSet(Collection<? extends E> c) {
		_set = new LinkedHashSet<E>(c);
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified array (i.e., not live).
	 * @since 2.4.1
	 */
	public ListModelSet(E[] array) {
		_set = new LinkedHashSet<E>(array.length);
		for (int j = 0; j < array.length; ++j)
			_set.add(array[j]);
	}

	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelSet.
	 */
	public ListModelSet(int initialCapacity) {
		_set = new LinkedHashSet<E>(initialCapacity);
	}

	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelMap.
	 * @param loadFactor the loadFactor to increase capacity of this ListModelMap.
	 */
	public ListModelSet(int initialCapacity, float loadFactor) {
		_set = new LinkedHashSet<E>(initialCapacity, loadFactor);
	}

	/**
	 * Get the inner real set.
	 */
	public Set<E> getInnerSet() {
		return _set;
	}

	//-- ListModel --//
	public int getSize() {
		return _set.size();
	}

	public E getElementAt(int j) {
		if (j < 0 || j >= _set.size())
			throw new IndexOutOfBoundsException("" + j);

		for (Iterator<E> it = _set.iterator();;) {
			final E o = it.next();
			if (--j < 0)
				return o;
		}
	}

	/**
	 * Notifies a change of the same element to trigger an event of {@link ListDataEvent#CONTENTS_CHANGED}.
	 * @param element
	 * @return true if the element exists
	 * @since 8.0.0
	 */
	public boolean notifyChange(E element) {
		int i = indexOf(element);
		if (i >= 0) {
			fireEvent(ListDataEvent.CONTENTS_CHANGED, i, i);
			return true;
		}
		return false;
	}

	//-- Set --//
	/**
	 * This implementation optimized on the LinkedHashSet(which guarantees the sequence of the added item). 
	 * Other implementation needs one more linear search.
	 */
	public boolean add(E o) {
		if (!_set.contains(o)) {
			boolean ret = _set.add(o);
			//After add, the position can change if not LinkedHashSet
			//bug #1819318 Problem while using SortedSet with Databinding
			//bug #1839634 Problem while using HashSet with Databinding
			if (_set instanceof LinkedHashSet) {
				//bug  1870996 Exception when use ListModelSet with SimpleListModelSharer
				//java.lang.IndexOutOfBoundsException: 1, interval added index should be _set.size() - 1
				final int i1 = _set.size() - 1;
				fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
			} else if (_set instanceof SortedSet) {
				final int i1 = indexOf(o);
				fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
			} else { //bug #1839634, HashSet, not sure the iteration sequence 
					//of the HashSet, must resync
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			}
			return ret;
		}
		return false;
	}

	/**
	 * This implementation optimized on the LinkedHashSet(which 
	 * guarantees the sequence of the added item). 
	 * Other implementation needs one more linear search.
	 */
	public boolean addAll(Collection<? extends E> c) {
		if (_set instanceof LinkedHashSet) {
			int begin = _set.size();
			int added = 0;
			for (final Iterator<? extends E> it = c.iterator(); it.hasNext();) {
				E o = it.next();
				if (_set.contains(o)) {
					continue;
				}
				_set.add(o);
				++added;
			}
			if (added > 0) {
				fireEvent(ListDataEvent.INTERVAL_ADDED, begin, begin + added - 1);
				return true;
			}
			return false;
		} else { //bug #1819318 Problem while using SortedSet with Databinding
				//bug #1839634 Problem while using HashSet with Databinding
			final boolean ret = _set.addAll(c);
			fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			return ret;
		}
	}

	public void clear() {
		int i2 = _set.size() - 1;
		if (i2 < 0) {
			return;
		}
		clearSelection();
		_set.clear();
		fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
	}

	public boolean contains(Object elem) {
		return _set.contains(elem);
	}

	public boolean containsAll(Collection<?> c) {
		return _set.containsAll(c);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		return _set.equals(o instanceof ListModelSet ? ((ListModelSet<?>) o)._set : o);
	}

	public int hashCode() {
		return _set.hashCode();
	}

	public boolean isEmpty() {
		return _set.isEmpty();
	}

	public String toString() {
		return _set.toString();
	}

	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Iterator<E> _it = _set.iterator();
			private E _current = null;
			private int _nextIndex;

			public boolean hasNext() {
				return _it.hasNext();
			}

			public E next() {
				_current = _it.next();
				++_nextIndex;
				return _current;
			}

			public void remove() {
				_it.remove();
				removeFromSelection(_current);
				--_nextIndex;
				//bug #1819318 Problem while using SortedSet with Databinding
				if (_set instanceof LinkedHashSet || _set instanceof SortedSet) {
					fireEvent(ListDataEvent.INTERVAL_REMOVED, _nextIndex, _nextIndex);
				} else { //bug #1839634 Problem while using HashSet with Databinding
					_it.remove();
					fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
				}
			}
		};
	}

	public boolean remove(Object o) {
		boolean ret = false;
		if (_set.contains(o)) {
			//bug #1819318 Problem while using SortedSet with Databinding
			removeFromSelection(o);
			if (_set instanceof LinkedHashSet || _set instanceof SortedSet) {
				final int index = indexOf(o);
				ret = _set.remove(o);
				fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			} else { //bug #1839634 Problem while using HashSet with Databinding
				ret = _set.remove(o);
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			}
		}
		return ret;
	}

	/** Returns the index of the specified object, or -1 if not found.
	 */
	public int indexOf(Object o) {
		int j = 0;
		for (Iterator<E> it = _set.iterator(); it.hasNext(); ++j) {
			if (Objects.equals(o, it.next()))
				return j;
		}
		return -1;
	}

	public boolean removeAll(Collection<?> c) {
		if (_set == c || this == c) { //special case
			clear();
			return true;
		}
		//bug #1819318 Problem while using SortedSet with Databinding
		if (_set instanceof LinkedHashSet || _set instanceof SortedSet) {
			// B60-ZK-1126.zul
			// Remove all items may cause exception
			return removePartial(c, true);
		} else { //bug #1839634 Problem while using HashSet with Databinding
			removeAllSelection(c);
			final boolean ret = _set.removeAll(c);
			if (ret) {
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			}
			return ret;
		}
	}

	public boolean retainAll(Collection<?> c) {
		if (_set == c || this == c) { //special case
			return false;
		}
		//bug #1819318 Problem while using SortedSet with Databinding
		if (_set instanceof LinkedHashSet || _set instanceof SortedSet) {
			return removePartial(c, false);
		} else { //bug #1839634 Problem while using HashSet with Databinding
			retainAllSelection(c);
			final boolean ret = _set.retainAll(c);
			if (ret) {
				fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
			}
			return ret;
		}
	}

	private boolean removePartial(Collection<?> c, boolean isRemove) {
		int sz = c.size();
		int removed = 0;
		int retained = 0;
		int index = 0;
		int begin = -1;
		// B60-ZK-1126.zul
		// Remember the selections to be cleared
		List<E> selected = new ArrayList<E>();
		for (final Iterator<E> it = _set.iterator(); it.hasNext() && (!isRemove || removed < sz)
				&& (isRemove || retained < sz); ++index) {
			E item = it.next();
			if (c.contains(item) == isRemove) {
				if (begin < 0) {
					begin = index;
				}
				++removed;
				it.remove();
				// B60-ZK-1126.zul
				// Removed item has been selected; remember and remove the selection later
				if (_selection.contains(item)) {
					selected.add(item);
				}
			} else {
				++retained;
				if (begin >= 0) {
					fireEvent(ListDataEvent.INTERVAL_REMOVED, begin, index - 1);
					index = begin; //this range removed, the index is reset to begin
					begin = -1;
				}
			}
		}
		// B60-ZK-1126.zul
		// Clear the selected items that were removed
		if (!selected.isEmpty()) {
			removeAllSelection(selected);
		}
		if (begin >= 0) {
			fireEvent(ListDataEvent.INTERVAL_REMOVED, begin, index - 1);
		}

		return removed > 0;
	}

	public int size() {
		return _set.size();
	}

	public Object[] toArray() {
		return _set.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return _set.toArray(a);
	}

	//-- Sortable --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmpr to compare.
	 */
	public void sort(Comparator<E> cmpr, final boolean ascending) {
		final List<E> copy = new ArrayList<E>(_set);
		_sorting = cmpr;
		_sortDir = ascending;
		Collections.sort(copy, cmpr);
		_set.clear();
		_set.addAll(copy);
		fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
	}

	public void sort() {
		if (_sorting == null)
			throw new UiException("The sorting comparator is not assigned, please use sort(Comparator cmpr, final boolean ascending)");
		sort(_sorting, _sortDir);
	}

	public String getSortDirection(Comparator<E> cmpr) {
		if (Objects.equals(_sorting, cmpr))
			return _sortDir ? "ascending" : "descending";
		return "natural";
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		ListModelSet<E> clone = (ListModelSet<E>) super.clone();
		if (_set != null)
			clone._set = new LinkedHashSet<E>(_set);
		return clone;
	}

	protected void fireSelectionEvent(E e) {
		fireEvent(ListDataEvent.SELECTION_CHANGED, indexOf(e), -1);
	}

}