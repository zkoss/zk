/* ListModelArray.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 26 17:02:14     2007, Created by henrichen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.util.ArraysX;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.ext.Sortable;

/**
 * <p>This is the {@link ListModel} as an Object array to be used with {@link Listbox}.
 * Change the contents of this model as an Object array would cause the associated Listbox to 
 * change accordingly.</p> 
 *
 * <p> The class implements the {@link Selectable} interface, updating
 * the selection status after sorted. (since 6.0.0)
 * 
 * @author Henri Chen
 * @see ListModel
 * @see ListModelList
 * @see ListModelMap
 */
public class ListModelArray<E> extends AbstractListModel<E> implements Sortable<E>, java.io.Serializable {
	private static final long serialVersionUID = 20070226L;

	protected Object[] _array;

	private Comparator<E> _sorting;

	private boolean _sortDir;

	/**
	 * Constructor
	 *
	 * @param array the array to represent
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified array.
	 * If false, the content of the specified array is copied.
	 * If true, this object is a 'facade' of the specified array,
	 * i.e., when you add or remove items from this {@link ListModelArray},
	 * the inner "live" array would be changed accordingly.
	 *
	 * However, it is not a good idea to modify <code>array</code>
	 * if it is passed to this method with live is true,
	 * since {@link Listbox} is not smart enough to handle it.
	 * Instead, modify it thru this object.
	 * @since 2.4.0
	 */
	public ListModelArray(E[] array, boolean live) {
		_array = live ? array : ArraysX.duplicate(array);
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified array (i.e., not live).
	 *
	 * <p>Notice that if the data is static or not shared, it is better to
	 * use <code>ListModelArray(array, true)</code> instead, since
	 * making a copy is slower.
	 */
	public ListModelArray(E[] array) {
		_array = ArraysX.duplicate(array);
	}

	/**
	 * Constructor.
	 * @param size the array size.
	 */
	public ListModelArray(int size) {
		_array = new Object[size];
	}

	/**
	 * Constructor.
	 * It mades a copy of the specified list (i.e., not live).
	 * @since 2.4.1
	 */
	public ListModelArray(List<? extends E> list) {
		_array = list.toArray(new Object[list.size()]);
	}

	/** Get the value of this ListModelArray at specified index.
	 * @param index the array index to be get value.
	 */
	public E get(int index) {
		return getElementAt(index);
	}

	/** Change content of the Array at specified index.
	 * @param index the array index to be set the new value.
	 */
	public void set(int index, E value) {
		_array[index] = value;
		fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
	}

	/**
	 * Get the inner real Object[].
	 * @since 2.4.0
	 */
	public Object[] getInnerArray() {
		return _array;
	}

	/** Returns the index of the specified element.
	 */
	public int indexOf(Object elm) {
		for (int j = 0; j < _array.length; ++j) {
			if (Objects.equals(elm, _array[j])) {
				return j;
			}
		}
		return -1;
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

	//-- ListModel --//
	public int getSize() {
		return _array.length;
	}

	@SuppressWarnings("unchecked")
	public E getElementAt(int j) {
		return (E) _array[j];
	}

	//-- Sortable --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmpr to compare.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sort(Comparator<E> cmpr, final boolean ascending) {
		_sorting = cmpr;
		_sortDir = ascending;
		Arrays.sort(_array, (Comparator) cmpr);
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

	//Object//
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof ListModelArray) {
			return Arrays.equals(_array, ((ListModelArray<?>) o)._array);
		}
		return false;
	}

	public int hashCode() {
		return Arrays.hashCode(_array);
	}

	public String toString() {
		return Objects.toString(_array);
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		ListModelArray<E> clone = (ListModelArray<E>) super.clone();
		if (_array != null)
			clone._array = ArraysX.duplicate(_array);
		return clone;
	}

	protected void fireSelectionEvent(E e) {
		fireEvent(ListDataEvent.SELECTION_CHANGED, indexOf(e), -1);
	}
}
