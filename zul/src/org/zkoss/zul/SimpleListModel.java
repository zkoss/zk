/* SimpleListModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 15:40:14     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

import org.zkoss.util.ArraysX;
import org.zkoss.zul.event.ListDataEvent;

/**
 * A simple implementation of {@link ListModel}.
 * Note: It assumes the content is immutable. If not, use {@link ListModelList}
 * or {@link ListModelArray} instead.
 * In additions, it stores the data in the array format, so if the original
 * data is not an array. It is better not to use this class.
 *
 * @author tomyeh
 * @see ListModelArray
 * @see ListModelSet
 * @see ListModelList
 * @see ListModelMap
 * @see ListSubModel (since 3.0.2)
 */
public class SimpleListModel extends AbstractListModel
implements ListModelExt, ListSubModel, java.io.Serializable {
    private static final long serialVersionUID = 20060707L;

	private final Object[] _data;

	/** Constructor.
	 *
	 * @param data the array to represent
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified list.
	 * If false, the content of the specified list is copied.
	 * If true, this object is a 'facade' of the specified list,
	 * i.e., when you add or remove items from this ListModelList,
	 * the inner "live" list would be changed accordingly.
	 *
	 * However, it is not a good idea to modify <code>data</code>
	 * once it is passed to this method with live is true,
	 * since {@link Listbox} is not smart enough to hanle it.
	 * @since 2.4.1
	 */
	public SimpleListModel(Object[] data, boolean live) {
		if (data == null)
			throw new NullPointerException();
		_data = live ? data: (Object[])ArraysX.clone(data);
	}
	/** Constructor.
	 * It made a copy of the specified array (<code>data</code>).
	 */
	public SimpleListModel(Object[] data) {
		this(data, false);
	}

	/** Constructor.
	 * Notice the data will be converted to an array, so the performance
	 * is not good if the data is huge. Use {@link ListModelList} instead
	 * if the data is huge.
	 * @since 2.4.1
	 */
	public SimpleListModel(List data) {
		_data = data.toArray(new Object[data.size()]);
	}

	//-- ListModel --//
	public int getSize() {
		return _data.length;
	}
	public Object getElementAt(int j) {
		return _data[j];
	}

	//-- ListModelExt --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmprt to compare.
	 */
	public void sort(Comparator cmpr, final boolean ascending) {
		Arrays.sort(_data, cmpr);
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}
	
	/**
	 * Returns the subset of the list model data that matches
	 * the specified value.
	 * It is ususally used for implmentation of auto-complete.
	 *
	 * <p>The implementation uses {@link #inSubModel} to check if
	 * the returned object of {@link #getElementAt} shall be in
	 * the sub model.
	 * 
	 * <p>Notice the maximal allowed number of items is decided by
	 * {@link #getMaxNumberInSubModel}, which, by default, returns 15
	 * if nRows is negative.
	 *
	 * @param value the value to retrieve the subset of the list model.
	 * It is the key argument when invoking {@link #inSubModel}.
	 * this string.
	 * @param nRows the maximal allowed number of matched items.
	 * If negative, it means the caller allows any number, but the implementation
	 * usually limits to a certain number (for better performance).
	 * @see #inSubModel
	 * @see #getMaxNumberInSubModel
	 * @since 3.0.2
	 */
	public ListModel getSubModel(Object value, int nRows) {
		final LinkedList data = new LinkedList();
		nRows = getMaxNumberInSubModel(nRows);
		for (int i = 0; i < _data.length; i++) {
			if (inSubModel(value, _data[i])) {
				data.add(_data[i]);
				if (--nRows <= 0) break; //done
			}
		}
		return new SimpleListModel(data);
	}
	/** Returns the maximal allowed number of matched items in the sub-model
	 * returned by {@link #getSubModel}.
	 * <p>Default: <code>nRows < 0 ? 15: nRows</code>.
	 * @since 5.0.4
	 */
	protected int getMaxNumberInSubModel(int nRows) {
		return nRows < 0 ? 15: nRows;
	}
	/** Compares if the given value shall belong to the submodel represented
	 * by the key.
	 * <p>Default: converts both key and value to String objects and
	 * then return true if the String object of value starts with
	 * the String object
	 * @param key the key representing the submodel. In autocomplete,
	 * it is the value entered by user.
	 * @param value the value in this model.
	 * @see #getSubModel
	 * @since 5.0.4
	 */
	protected boolean inSubModel(Object key, Object value) {
		String idx = objectToString(key);
		return idx.length() > 0 && objectToString(value).startsWith(idx);
	}
	
	/**
	 * @deprecated As of release 5.0.4, replaced with {@link #inSubModel}.
	 */
	protected String objectToString(Object value) {
		final String s = value != null ? value.toString(): "";
		return s != null ? s: "";
	}
}
