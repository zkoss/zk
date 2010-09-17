/* ListModelArray.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 26 17:02:14     2007, Created by henrichen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zk.ui.UiException;

import org.zkoss.lang.Objects;
import org.zkoss.util.ArraysX;
import static org.zkoss.lang.Generics.cast;

import java.util.Comparator;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This is the {@link ListModel} as an Object array to be used with {@link Listbox}.
 * Change the contents of this model as an Object array would cause the associated Listbox to 
 * change accordingly.</p> 
 *
 * @author Henri Chen
 * @see ListModel
 * @see ListModelList
 * @see ListModelMap
 */
public class ListModelArray<E> extends AbstractListModel<E>
implements ListModelExt<E>, java.io.Serializable {
	private static final long serialVersionUID = 20070226L;

	protected final Object[] _array;

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
	 * since {@link Listbox} is not smart enough to hanle it.
	 * Instead, modify it thru this object.
	 * @since 2.4.0
	 */
	public ListModelArray(E[] array, boolean live) {
		_array = live ? array: ArraysX.clone(array);
	}

	/**
	 * Constructor.
	 * It mades a copy of the specified array (i.e., not live).
	 * @param src the source array used to initialize this ListModelArray.
	 */
	public ListModelArray(E[] src) {
		_array = ArraysX.clone(src);
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
		for(int j = 0; j < _array.length; ++j) {
			if (Objects.equals(elm, _array[j])) {
				return j;
			}
		}
		return -1;
	}

	//-- ListModel --//
	public int getSize() {
		return _array.length;
	}
	@SuppressWarnings("unchecked")
	public E getElementAt(int j) {
		return (E)_array[j];
	}
	
	//-- ListModelExt --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	@SuppressWarnings("unchecked")
	public void sort(Comparator<E> cmpr, final boolean ascending) {
		Arrays.sort(_array, (Comparator)cmpr);
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}

	//Object//
	public boolean equals(Object o) {
		return _array.equals(o instanceof ListModelArray ? ((ListModelArray)o)._array: o);
	}
	public int hashCode() {
		return _array.hashCode();
	}
	public String toString() {
		return Objects.toString(_array);
	}
}
