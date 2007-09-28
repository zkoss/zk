/* RowComparator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu August 2 13:01:55     2007, Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.yuiext.grid;

import java.util.List;
import java.util.Comparator;


/**
 * A comparator used to compare {@link Row}, if not live data,
 * or the data themselves, if live data.
 *
 * @author jumperchen
 */
public class RowComparator implements Comparator {

	/** The column (optinal). */
	private final Column _column;
	/** Column index. */
	private int _index;
	/** Ascending. */
	private final boolean _asc;
	/** Ignore case. */
	private boolean _igcase;
	/** Compares by native object (instead of label) */
	private boolean _bynat;
	/** Whether to treat null as the maximum value. */
	private boolean _maxnull;

	/** Compares with {@link Row#getValue}.
	 *
	 * <p>It assumes the value returned by {@link Row#getValue}
	 * implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order and case-insensitive.
	 * If not, use {@link #RowComparator(int, boolean, boolean)}
	 * instead.
	 */
	public RowComparator() {
		this(-1, true, true, false, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Row#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order, case-insensitive and
	 * comparing the returned values of {@link Label#getValue}.
	 * If not, use {@link #RowComparator(int, boolean, boolean, boolean)}
	 * instead.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param index which column to compare. If -1, {@link Row#getValue}
	 * is used.
	 */
	public RowComparator(int index) {
		this(index, true, true, false, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Row#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: it compares the returned value of {@link Label#getValue}.
	 * If you want to compare {@link  Label#getValue}.,
	 * use {@link #RowComparator(int, boolean, boolean, boolean)}
	 * instead.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param index which column to compare. If -1, {@link Label#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public RowComparator(int index, boolean ascending,
	boolean ignoreCase) {
		this(index, ascending, ignoreCase, false, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Row#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param index which column to compare. If -1, {@link Row#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byNativeValue whether to compare {@link Label#getNativeObject}.
	 * If false, it compares {@link Label#getValue}.
	 * If true, it assumes the value returned by {@link Label#getNativeObject}
	 * implements Comparable.
	 * It is ignored if the index is -1.
	 */
	public RowComparator(int index, boolean ascending,
	boolean ignoreCase, boolean byNativeValue) {
		this(index, ascending, ignoreCase, byNativeValue, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Row#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * @param index which column to compare. If -1, {@link Row#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byNativeValue whether to compare {@link Label#getNativeObject}.
	 * If false, it compares {@link Label#getValue}.
	 * If true, it assumes the value returned by {@link Label#getNativeObject}
	 * implements Comparable.
	 * It is ignored if the index is -1.
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public RowComparator(int index, boolean ascending,
	boolean ignoreCase, boolean byNativeValue, boolean nullAsMax) {
		_column = null;
		_index = index;
		_asc = ascending;
		_igcase = ignoreCase;
		_bynat = byNativeValue;
		_maxnull = nullAsMax;
	}
	/** Compares with the column which the list header is at.
	 *
	 * <p>Note: it compares the returned value of {@link Label#getValue}.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public RowComparator(Column column, boolean ascending,
	boolean ignoreCase) {
		this(column, ascending, ignoreCase, false, false);
	}
	/** Compares with the column which the list header is at.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byNativeValue whether to compare {@link Label#getNativeObject}.
	 * If false, it compares {@link Label#getValue}.
	 */
	public RowComparator(Column column, boolean ascending,
	boolean ignoreCase, boolean byNativeValue) {
		this(column, ascending, ignoreCase, byNativeValue, false);
	}
	/** Compares with the column which the list header is at.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byNativeValue whether to compare {@link Label#getNativeObject}.
	 * If false, it compares {@link Listcell#getValue}.
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public RowComparator(Column column, boolean ascending,
	boolean ignoreCase, boolean byNativeValue, boolean nullAsMax) {
		_column = column;
		_index = -1; //not decided yet
		_asc = ascending;
		_igcase = ignoreCase;
		_bynat = byNativeValue;
		_maxnull = nullAsMax;
	}
	/** Returns the listheader that this comparator is associated with, or null
	 * if not available.
	 */
	public Column getColumn() {
		return _column;
	}
	/** Returns whether the order is ascending.
	 */
	public boolean isAscending() {
		return _asc;
	}
	/** Returns whether to ignore case.
	 */
	public boolean shallIgnoreCase() {
		return _igcase;
	}
	/** Returns whether to compare the returned value of {@link Label#getValue}
	 */
	public boolean byNativeValue() {
		return _bynat;
	}

	//Comparator//
	public int compare(Object o1, Object o2) {
		if (_index < 0 && _column != null) //decide the index
			_index = _column.getParent().getChildren().indexOf(_column);
		
		Object v1, v2;
		if (o1 instanceof Row) { //not live data
			final Row r1 = (Row)o1, r2 = (Row)o2;
			if (_index < 0) {
				v1 = handleCase((Comparable)r1.getValue());
				v2 = handleCase((Comparable)r2.getValue());
			} else {
				List rcs1 = r1.getChildren();
				if (_index >= rcs1.size()) v1 = null;
				else {
					final Label lc = (Label)rcs1.get(_index);
					v1 = handleCase(_bynat ? lc.getNativeObject(): lc.getValue());
				}
				List rcs2 = r2.getChildren();
				if (_index >= rcs2.size()) v2 = null;
				else {
					final Label lc = (Label)rcs2.get(_index);
					v2 = handleCase(_bynat ? lc.getNativeObject(): lc.getValue());
				}
			}
		} else { //live data
			v1 = handleCase(o1);
			v2 = handleCase(o2);
		}

		if (v1 == null) return v2 == null ? 0: _maxnull ? 1: -1;
		if (v2 == null) return _maxnull ? -1: 1;
		final int v = ((Comparable)v1).compareTo(v2);
		return _asc ? v: -v;
	}
	private Object handleCase(Object c) {
		if (_igcase) {
			if (c instanceof String)
				return ((String)c).toUpperCase();
			if (c instanceof Character)
				return new Character(Character.toUpperCase(
					((Character)c).charValue()));
		}
		return c;
	}

	public boolean equals(Object o) {
		if (!(o instanceof RowComparator))
			return false;
		final RowComparator c = (RowComparator)o;
		return c._index == _index && c._asc == _asc && c._igcase == _igcase;
	}
	public int hashCode() {
		return _index ^ (_asc ? 1: 5) ^ (_igcase ? 9: 3);
	}
	public String toString() {
		return "[Compare "+(_index < 0 ? "value": _index+"-th column")+']';
	}
}
