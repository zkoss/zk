/* RowComparator.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 23 09:38:24     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Comparator;


/**
 * A comparator used to compare {@link Row}, if not live data,
 * or the data themselves, if live data.
 *
 * @author jumperchen
 * @since 3.6.0
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
	/** Whether to treat null as the maximum value. */
	private boolean _maxnull;

	/** Compares with {@link Label#getValue}.
	 *
	 * <p>It assumes the value returned by the label indexing in {@link Row#getChildren}.
	 *
	 * <p>Note: It assumes the ascending order and case-insensitive.
	 * If not, use {@link #RowComparator(int, boolean, boolean)}
	 * instead.
	 */
	public RowComparator() {
		this(-1, true, true, false);
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
		this(index, true, true, false);
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
	 * @param index which column to compare. If -1, {@link Label#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public RowComparator(int index, boolean ascending,
	boolean ignoreCase) {
		this(index, ascending, ignoreCase, false);
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
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public RowComparator(int index, boolean ascending,
	boolean ignoreCase, boolean nullAsMax) {
		_column = null;
		_index = index;
		_asc = ascending;
		_igcase = ignoreCase;
		_maxnull = nullAsMax;
	}

	/** Compares with the column.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public RowComparator(Column header, boolean ascending,
	boolean ignoreCase, boolean nullAsMax) {
		_column = header;
		_index = -1; //not decided yet
		_asc = ascending;
		_igcase = ignoreCase;
		_maxnull = nullAsMax;
	}
	/** Returns the column that this comparator is associated with, or null
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

	//Comparator//
	public int compare(Object o1, Object o2) {
		if (_index < 0 && _column != null) //decide the index
			_index = _column.getParent().getChildren().indexOf(_column);
		
		Object v1, v2;
		if (o1 instanceof Row) { //not live data
			final Row r1 = (Row)o1, r2 = (Row)o2;
			if (_index < 0) {
				v1 = handleCase(r1.getValue());
				v2 = handleCase(r2.getValue());
			} else {
				List rcs1 = r1.getChildren();
				if (_index >= rcs1.size()) v1 = null;
				else {
					v1 = handleCase(rcs1.get(_index));
				}
				List rcs2 = r2.getChildren();
				if (_index >= rcs2.size()) v2 = null;
				else {
					v2 = handleCase(rcs2.get(_index));
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
		if (c instanceof Label)
			c = ((Label) c).getValue();
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
