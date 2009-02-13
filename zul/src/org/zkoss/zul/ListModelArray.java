/* ListModelArray.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Feb 26 17:02:14     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zk.ui.UiException;

import org.zkoss.lang.Objects;
import org.zkoss.util.ArraysX;
import java.util.Comparator;
import java.util.Arrays;
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
public class ListModelArray extends AbstractListModel
implements ListModelExt, java.io.Serializable {
	private static final long serialVersionUID = 20070226L;

	protected final Object[] _array;

	/**
	 * Creates an instance which accepts a "live" Object array as its inner array.
	 * @param array the inner array storage
	 * @deprecated As of release 2.4.0, replaced by {@link #ListModelArray(Object[],boolean)}
	 */
	public static ListModelArray instance(Object[] array) {
		return new ListModelArray(array, true);
	}
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
	public ListModelArray(Object[] array, boolean live) {
		_array = live ? array: (Object[])ArraysX.clone(array);
	}

	/**
	 * Constructor.
	 * It mades a copy of the specified array (i.e., not live).
	 * @param src the source array used to initialize this ListModelArray.
	 */
	public ListModelArray(Object[] src) {
		_array = (Object[])ArraysX.clone(src);
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
	public ListModelArray(List list) {
		_array = list.toArray(new Object[list.size()]);
	}

	/** Get the value of this ListModelArray at specified index.
	 * @param index the array index to be get value.
	 */
	public Object get(int index) {
		return getElementAt(index);
	}
	
	/** Change content of the Array at specified index.
	 * @param index the array index to be set the new value.
	 */
	public void set(int index, Object value) {
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
	
	public Object getElementAt(int j) {
		return _array[j];
	}
	
	//-- ListModelExt --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator cmpr, final boolean ascending) {
		Arrays.sort(_array, cmpr);
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
