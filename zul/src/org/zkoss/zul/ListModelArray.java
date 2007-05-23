/* ListModelArray.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Feb 26 17:02:14     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zk.ui.UiException;

import org.zkoss.lang.Objects;
import java.util.Comparator;
import java.util.Arrays;

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
	 * new an instance which accepts a "live" Object array as its inner array. Any change to this
	 * ListModelArray will change to the passed in "live" array.
	 * @param array the inner array storage
	 */
	public static ListModelArray instance(Object[] array) {
		return new ListModelArray(array, 0);
	}

	/**
	 * <p>Constructor, unlike other constructor, the passed in Object array is a "live" array inside
	 * this ListModelArray; i.e., when you change the contents from this ListModelArray,
	 * the inner "live" array would be changed accordingly.</p>
	 * @param array the inner "live" Object array that would be modified accordingly
	 * when you modify to this ListModelArray.
	 * @param dummy dummy argument to avoid confuse with consturctor {@link #ListModelArray(Object[])}.
	 */
	protected ListModelArray(Object[] array, int dummy) {
		if (array == null)
			throw new NullPointerException();
		_array = array;
	}

	/**
	 * Constructor.
	 * @param src the source array used to initialize this ListModelArray.
	 */
	public ListModelArray(Object[] src) {
		if (src == null)
			throw new NullPointerException();
		_array = new Object[src.length];
		System.arraycopy(src, 0, _array, 0, src.length);
	}
	
	/**
	 * Constructor.
	 * @param size the array size.
	 */
	public ListModelArray(int size) {
		_array = new Object[size];
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
	 */	
	public Object[] getInnerObjectArray() {
		return _array;
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

}
