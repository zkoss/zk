/* ListModelList.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 23 17:43:13     2006, Created by Henri Chen
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

/**
 * <p>This is the {@link ListModel} as a {@link java.util.List} to be used with {@link Listbox}.
 * Add or remove the contents of this model as a List would cause the associated Listbox to change accordingly.</p> 
 *
 * <p>For more information, please refer to
 * <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/MVC/Model/List_Model">ZK Developer's Reference: List Model</a>
 *
 * @author Henri Chen
 * @see ListModel
 * @see ListModelList
 * @see ListModelMap
 */
public class ListModelList<E> extends AbstractListModel<E> implements Sortable<E>, List<E>, java.io.Serializable {
	private static final long serialVersionUID = 20120206122641L;

	protected List<E> _list;

	private Comparator<E> _sorting;

	private boolean _sortDir;

	/**
	 * Constructor
	 *
	 * @param list the list to represent
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified list.
	 * If false, the content of the specified list is copied.
	 * If true, this object is a 'facade' of the specified list,
	 * i.e., when you add or remove items from this ListModelList,
	 * the inner "live" list would be changed accordingly.
	 *
	 * However, it is not a good idea to modify <code>list</code>
	 * if it is passed to this method with live is true,
	 * since {@link Listbox} is not smart enough to handle it.
	 * Instead, modify it thru this object.
	 * @since 2.4.0
	 */
	public ListModelList(List<E> list, boolean live) {
		_list = live ? list : new ArrayList<E>(list);
	}

	/**
	 * Constructor.
	 */
	public ListModelList() {
		_list = new ArrayList<E>();
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified collection (i.e., not live).
	 *
	 * <p>Notice that if the data is static or not shared, it is better to
	 * use <code>ListModelList(c, true)</code> instead, since
	 * making a copy is slower.
	 */
	public ListModelList(Collection<? extends E> c) {
		_list = new ArrayList<E>(c);
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified array (i.e., not live).
	 * @since 2.4.1
	 */
	public ListModelList(E[] array) {
		_list = new ArrayList<E>(array.length);
		for (int j = 0; j < array.length; ++j)
			_list.add(array[j]);
	}

	/**
	 * Constructor.
	 * @param initialCapacity the initial capacity for this ListModelList.
	 */
	public ListModelList(int initialCapacity) {
		_list = new ArrayList<E>(initialCapacity);
	}

	/**
	 * Remove from fromIndex(inclusive) to toIndex(exclusive). If fromIndex equals toIndex, 
	 * this methods do nothing.
	 * @param fromIndex the begin index (inclusive) to be removed.
	 * @param toIndex the end index (exclusive) to be removed.
	 */
	public void removeRange(int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new UiException(
					"fromIndex must less than toIndex: fromIndex: " + fromIndex + ", toIndex: " + toIndex);
		}
		if (fromIndex == toIndex) {
			return;
		}
		int sz = _list.size();
		if (sz == fromIndex) {
			return;
		}
		int index = fromIndex;
		//B70-ZK-2447 : IndexOutOfBound exception occurs when using ListModelList.removeRange(0, size) with all items selected
		Set<E> removedObjs = new HashSet<E>();
		for (final Iterator<E> it = _list.listIterator(fromIndex); it.hasNext() && index < toIndex; ++index) {
			final E obj = it.next();
			removedObjs.add(obj);
			it.remove();
		}
		fireEvent(ListDataEvent.INTERVAL_REMOVED, fromIndex, index - 1);
		for (E obj : removedObjs)
			removeFromSelection(obj);
	}

	/**
	 * Get the inner real List.
	 */
	public List<E> getInnerList() {
		return _list;
	}

	//-- ListModel --//
	public int getSize() {
		return _list.size();
	}

	public E getElementAt(int j) {
		return _list.get(j);
	}

	//-- List --//
	public void add(int index, E element) {
		_list.add(index, element);
		fireEvent(ListDataEvent.INTERVAL_ADDED, index, index);
	}
	
	public boolean add(E o) {
		int i1 = _list.size();
		boolean ret = _list.add(o);
		fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i1);
		return ret;
	}

	/**
	 * Notifies a change of the same element to trigger an event of {@link ListDataEvent#CONTENTS_CHANGED}.
	 * @param element
	 * @return true if the element exists
	 * @since 8.0.0
	 */
	public boolean notifyChange(E element) {
		int i = _list.indexOf(element);
		if (i >= 0) {
			fireEvent(ListDataEvent.CONTENTS_CHANGED, i, i);
			return true;
		}
		return false;
	}

	public boolean addAll(Collection<? extends E> c) {
		int sz = c.size();
		if (sz <= 0) {
			return false;
		}
		int i1 = _list.size();
		int i2 = i1 + sz - 1;
		boolean ret = _list.addAll(c);
		fireEvent(ListDataEvent.INTERVAL_ADDED, i1, i2);
		return ret;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		int sz = c.size();
		if (sz <= 0) {
			return false;
		}
		int i2 = index + sz - 1;
		boolean ret = _list.addAll(index, c);
		fireEvent(ListDataEvent.INTERVAL_ADDED, index, i2);
		return ret;
	}

	public void clear() {
		int i2 = _list.size() - 1;
		if (i2 < 0) {
			return;
		}
		clearSelection();
		_list.clear();
		fireEvent(ListDataEvent.INTERVAL_REMOVED, 0, i2);
	}

	public boolean contains(Object elem) {
		boolean ret = _list.contains(elem);
		return ret;
	}

	public boolean containsAll(Collection<?> c) {
		return _list.containsAll(c);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		return _list.equals(o instanceof ListModelList<?> ? ((ListModelList<?>) o)._list : o);
	}

	public E get(int index) {
		return _list.get(index);
	}

	public int hashCode() {
		return _list.hashCode();
	}

	public String toString() {
		return _list.toString();
	}

	public int indexOf(Object elem) {
		return _list.indexOf(elem);
	}

	public boolean isEmpty() {
		return _list.isEmpty();
	}

	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private Iterator<E> _it = _list.iterator();
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
				removeFromSelection(_current);
				_it.remove();
				--_nextIndex;
				fireEvent(ListDataEvent.INTERVAL_REMOVED, _nextIndex, _nextIndex);
			}
		};
	}

	public int lastIndexOf(Object elem) {
		return _list.lastIndexOf(elem);
	}

	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	public ListIterator<E> listIterator(final int index) {
		return new ListIterator<E>() {
			private ListIterator<E> _it = _list.listIterator(index);
			private E _current = null;

			public boolean hasNext() {
				return _it.hasNext();
			}

			public E next() {
				_current = _it.next();
				return _current;
			}

			public void remove() {
				removeFromSelection(_current);
				_it.remove();
				final int index = _it.nextIndex();
				fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
			}

			public void add(E arg0) {
				final int index = _it.nextIndex();
				_it.add(arg0);
				fireEvent(ListDataEvent.INTERVAL_ADDED, index, index);
			}

			public boolean hasPrevious() {
				return _it.hasPrevious();
			}

			public int nextIndex() {
				return _it.nextIndex();
			}

			public E previous() {
				_current = _it.previous();
				return _current;
			}

			public int previousIndex() {
				return _it.previousIndex();
			}

			public void set(E arg0) {
				_it.set(arg0);
				final int index = _it.nextIndex() - 1;
				fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
			}
		};
	}

	public E remove(int index) {
		E ret = _list.get(index);
		removeFromSelection(ret);
		//Bug ZK-1428: should remove the object after removeFromSelection
		//since Listbox.doSelectionChanged will try to get the object again
		_list.remove(index);
		fireEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
		return ret;
	}

	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index >= 0) {
			remove(index);
			return true;
		}
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		if (_list == c || this == c) { // special case
			clearSelection();
			clear();
			return true;
		}
		return removePartial(c, true);
	}

	public boolean retainAll(Collection<?> c) {
		if (_list == c || this == c) { //special case
			return false;
		}
		return removePartial(c, false);
	}

	private boolean removePartial(Collection<?> c, boolean exclude) {
		boolean removed = false;
		int index = 0;
		int begin = -1;
		// B60-ZK-1126.zul
		// Remember the selections to be cleared
		List<E> selected = new ArrayList<E>();
		for (final Iterator<E> it = _list.iterator(); it.hasNext(); ++index) {
			E item = it.next();
			if (c.contains(item) == exclude) {
				if (begin < 0) {
					begin = index;
				}
				removed = true;
				it.remove();
				// B60-ZK-1126.zul
				// Removed item has been selected; remember and remove the selection later
				if (_selection.contains(item)) {
					selected.add(item);
				}
			} else {
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

		return removed;
	}

	public E set(int index, E element) {
		E ret = _list.set(index, element);
		fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
		return ret;
	}

	public int size() {
		return _list.size();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		List<E> list = _list.subList(fromIndex, toIndex);
		//bug 2448987: sublist must be live
		return new ListModelList<E>(list, true);
	}

	public Object[] toArray() {
		return _list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return _list.toArray(a);
	}

	//-- Sortable --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator<E> cmpr, final boolean ascending) {
		_sorting = cmpr;
		_sortDir = ascending;
		Collections.sort(_list, cmpr);
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
		ListModelList<E> clone = (ListModelList<E>) super.clone();
		if (_list != null)
			clone._list = new ArrayList<E>(_list);
		return clone;
	}

	protected void fireSelectionEvent(E e) {
		fireEvent(ListDataEvent.SELECTION_CHANGED, indexOf(e), -1);
	}
}