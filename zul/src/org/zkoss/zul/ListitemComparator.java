/* ListitemComparator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 25 21:25:55     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.List;
import java.util.Comparator;

/**
 * A comparator used to compare {@link Listitem}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ListitemComparator implements Comparator {
	/** The listheader (optinal). */
	private final Listheader _header;
	/** Column index. */
	private int _index;
	/** Ascending. */
	private final boolean _asc;
	/** Ignore case. */
	private final boolean _igcase;
	/** Compares by value (instead of label) */
	private final boolean _byval;

	/** Compares with {@link Listitem#getValue}.
	 *
	 * <p>It assumes the value returned by {@link Listitem#getValue}
	 * implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order and case-insensitive.
	 * If not, use {@link #ListitemComparator(int, boolean, boolean)}
	 * instead.
	 */
	public ListitemComparator() {
		this(-1, true, true, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Listitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order, case-insensitive and
	 * comparing the returned values of {@link Listcell#getLabel}.
	 * If not, use {@link #ListitemComparator(int, boolean, boolean, boolean)}
	 * instead.
	 *
	 * @param index which column to compare. If -1, {@link Listitem#getValue}
	 * is used.
	 */
	public ListitemComparator(int index) {
		this(index, true, true, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Listitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: it compares the returned value of {@link Listcell#getLabel}.
	 * If you want to compare {@link Listcell#getValue}.,
	 * use {@link #ListitemComparator(int, boolean, boolean, boolean)}
	 * instead.
	 *
	 * @param index which column to compare. If -1, {@link Listitem#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public ListitemComparator(int index, boolean ascending,
	boolean ignoreCase) {
		this(index, ascending, ignoreCase, false);
	}
	/** Compares with the column which the list header is at.
	 *
	 * <p>Note: it compares the returned value of {@link Listcell#getLabel}.
	 * If you want to compare {@link Listcell#getValue}.,
	 * use {@link #ListitemComparator(Listheader, boolean, boolean, boolean)}
	 * instead.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public ListitemComparator(Listheader header, boolean ascending,
	boolean ignoreCase) {
		this(header, ascending, ignoreCase, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Listitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * @param index which column to compare. If -1, {@link Listitem#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byValue whether to compare {@link Listcell#getValue}.
	 * If false, it compares {@link Listcell#getLabel}.
	 * If true, it assumes the value returned by {@link Listcell#getValue}
	 * implements Comparable.
	 * It is ignored if the index is -1.
	 */
	public ListitemComparator(int index, boolean ascending,
	boolean ignoreCase, boolean byValue) {
		_header = null;
		_index = index;
		_asc = ascending;
		_igcase = ignoreCase;
		_byval = byValue;
	}
	/** Compares with the column which the list header is at.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byValue whether to compare {@link Listcell#getValue}.
	 * If false, it compares {@link Listcell#getLabel}.
	 */
	public ListitemComparator(Listheader header, boolean ascending,
	boolean ignoreCase, boolean byValue) {
		_header = header;
		_index = -1; //not decided yet
		_asc = ascending;
		_igcase = ignoreCase;
		_byval = byValue;
	}

	/** Returns the listheader that this comparator is associated with, or null
	 * if not available.
	 */
	public Listheader getListheader() {
		return _header;
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
	/** Returns whether to compare the returned value of {@link Listcell#getValue}
	 */
	public boolean byValue() {
		return _byval;
	}

	//Comparator//
	public int compare(Object o1, Object o2) {
		if (_index < 0 && _header != null) //decide the index
			_index = _header.getColumnIndex();

		final Listitem li1 = (Listitem)o1, li2 = (Listitem)o2;

		Object v1, v2;
		if (_index < 0) {
			v1 = handleCase((Comparable)li1.getValue());
			v2 = handleCase((Comparable)li2.getValue());
		} else {
			List lcs1 = li1.getChildren();
			if (_index >= lcs1.size()) v1 = null;
			else {
				final Listcell lc = (Listcell)lcs1.get(_index);
				v1 = handleCase(_byval ? lc.getValue(): lc.getLabel());
			}
			List lcs2 = li2.getChildren();
			if (_index >= lcs2.size()) v2 = null;
			else {
				final Listcell lc = (Listcell)lcs2.get(_index);
				v2 = handleCase(_byval ? lc.getValue(): lc.getLabel());
			}
		}

		if (v1 == null) return v2 == null ? 0: _asc ? -1: 1;
		if (v2 == null) return _asc ? 1: -1;
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
		if (!(o instanceof ListitemComparator))
			return false;
		final ListitemComparator c = (ListitemComparator)o;
		return c._index == _index && c._asc == _asc && c._igcase == _igcase;
	}
	public int hashCode() {
		return _index ^ (_asc ? 1: 5) ^ (_igcase ? 9: 3);
	}
	public String toString() {
		return "[Compare "+(_index < 0 ? "value": _index+"-th column")+']';
	}
}
