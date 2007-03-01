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
implements java.io.Serializable {
	private static final long serialVersionUID = 20070226L;

	protected final Object[] _data;

	/**
	 * new an instance which accepts a "live" Object array as its inner array. Any change to this
	 * ListModelArray will change to the passed in "live" array.
	 * @param array the inner array storage
	 */
	public static ListModelArray instance(Object[] data) {
		return new ListModelArray(data, 0);
	}

	/**
	 * <p>Constructor, unlike other constructor, the passed in Object array is a "live" array inside
	 * this ListModelArray; i.e., when you change the contents from this ListModelArray,
	 * the inner "live" array would be changed accordingly.</p>
	 * @param array the inner "live" Object array that would be modified accordingly
	 * when you modify to this ListModelArray.
	 */
	protected ListModelArray(Object[] data, int dummy) {
		if (data == null)
			throw new NullPointerException();
		_data = data;
	}

	/**
	 * Constructor.
	 * @param src the source array used to initialize this ListModelArray.
	 */
	public ListModelArray(Object[] src) {
		if (src == null)
			throw new NullPointerException();
		_data = new Object[src.length];
		System.arraycopy(src, 0, _data, 0, src.length);
	}
	
	/**
	 * Constructor.
	 * @param size the array size.
	 */
	public ListModelArray(int size) {
		_data = new Object[size];
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
		_data[index] = value;
		fireEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
	}
		
	//-- ListModel --//
	public int getSize() {
		return _data.length;
	}
	
	public Object getElementAt(int j) {
		return _data[j];
	}
}
