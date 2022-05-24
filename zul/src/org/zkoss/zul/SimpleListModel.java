/* SimpleListModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 15:40:14     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.util.ArraysX;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

/**
 * A simple implementation of {@link ListModel}.
 * <p>Note: It assumes the content is immutable. If not, use {@link ListModelList}
 * or {@link ListModelArray} instead.
 * In additions, it stores the data in the array format, so if the original
 * data is not an array. It is better not to use this class.
 *
 * <p>Also notice that {@link SimpleListModel} also implements
 * {@link ListSubModel}. It means when it is used with {@link Combobox},
 * only the data that matches what the user typed will be shown.
 * 
 * @author tomyeh
 * @see ListModelArray
 * @see ListModelSet
 * @see ListModelList
 * @see ListModelMap
 * @see ListSubModel (since 3.0.2)
 */
public class SimpleListModel<E> extends AbstractListModel<E>
		implements Sortable<E>, ListSubModel<E>, java.io.Serializable {
	private static final long serialVersionUID = 20060707L;

	private Object[] _data;

	private Comparator<E> _sorting;

	private boolean _sortDir;

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
	 * since {@link Listbox} is not smart enough to handle it.
	 * @since 2.4.1
	 */
	public SimpleListModel(E[] data, boolean live) {
		_data = live ? data : ArraysX.duplicate(data);
	}

	/** Constructor.
	 * It made a copy of the specified array (<code>data</code>).
	 *
	 * <p>Notice that if the data is static or not shared, it is better to
	 * use <code>SimpleListModelMap(data, true)</code> instead, since
	 * making a copy is slower.
	 */
	public SimpleListModel(E[] data) {
		this(data, false);
	}

	/** Constructor.
	 * Notice the data will be converted to an array, so the performance
	 * is not good if the data is huge. Use {@link ListModelList} instead
	 * if the data is huge.
	 * @since 2.4.1
	 */
	public SimpleListModel(List<? extends E> data) {
		_data = data.toArray();
	}

	//-- ListModel --//
	public int getSize() {
		return _data.length;
	}

	@SuppressWarnings("unchecked")
	public E getElementAt(int j) {
		return (E) _data[j];
	}

	//-- Sortable --//
	/** Sorts the data.
	 *
	 * @param cmpr the comparator.
	 * @param ascending whether to sort in the ascending order.
	 * It is ignored since this implementation uses cmpr to compare.
	 */
	@SuppressWarnings("unchecked")
	public void sort(Comparator<E> cmpr, final boolean ascending) {
		_sorting = cmpr;
		_sortDir = ascending;
		Arrays.sort(_data, (Comparator) cmpr);
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

	/**
	 * Returns the subset of the list model data that matches
	 * the specified value.
	 * It is usually used for implementation of auto-complete.
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
	@SuppressWarnings("unchecked")
	public ListModel<E> getSubModel(Object value, int nRows) {
		final List<E> data = new LinkedList<E>();
		nRows = getMaxNumberInSubModel(nRows);
		for (int i = 0; i < _data.length; i++) {
			if (inSubModel(value, _data[i])) {
				data.add((E) _data[i]);
				if (--nRows <= 0)
					break; //done
			}
		}
		return new SimpleListModel<E>(data);
	}

	/** Returns the maximal allowed number of matched items in the sub-model
	 * returned by {@link #getSubModel}.
	 * <p>Default: {@code nRows < 0 ? 15: nRows}.
	 * @since 5.0.4
	 */
	protected int getMaxNumberInSubModel(int nRows) {
		return nRows < 0 ? 15 : nRows;
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
		final String s = value != null ? value.toString() : "";
		return s != null ? s : "";
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		SimpleListModel clone = (SimpleListModel) super.clone();
		if (_data != null)
			clone._data = ArraysX.duplicate(_data);
		return clone;
	}

	protected void fireSelectionEvent(E e) {
		int index = -1;
		for (int j = 0; j < _data.length; ++j) {
			index++;
			if (Objects.equals(e, _data[j])) {
				break;
			}
		}
		fireEvent(ListDataEvent.SELECTION_CHANGED, index, -1);
	}

	//For Backward Compatibility//
	/** @deprecated As of release 6.0.0, replaced with {@link #addToSelection}.
	 */
	public void addSelection(E obj) {
		addToSelection(obj);
	}

	/** @deprecated As of release 6.0.0, replaced with {@link #removeFromSelection}.
	 */
	public void removeSelection(Object obj) {
		removeFromSelection(obj);
	}
}
