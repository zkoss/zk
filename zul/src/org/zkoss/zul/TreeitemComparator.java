/* TreeitemComparator.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 31 18:28:55     2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.Comparator;

/**
 * A comparator used to compare {@link Treeitem}, if not live data,
 * or the data themselves, if live data.
 * @since 5.0.6
 * @author jimmy
 */
public class TreeitemComparator implements Comparator {
	/** The Treecol (optinal). */
	private final Treecol _treecol;
	/** Column index. */
	private int _index;
	/** Ascending. */
	private final boolean _asc;
	/** Ignore case. */
	private boolean _igcase;
	/** Compares by value (instead of label) */
	private boolean _byval;
	/** Whether to treat null as the maximum value. */
	private boolean _maxnull;

	/** Compares with {@link Treeitem#getValue}.
	 *
	 * <p>It assumes the value returned by {@link Treeitem#getValue}
	 * implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order and case-insensitive.
	 * If not, use {@link #TreeitemComparator(int, boolean, boolean)}
	 * instead.
	 */
	public TreeitemComparator() {
		this(-1, true, true, false, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Treeitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order, case-insensitive and
	 * comparing the returned values of {@link Treecell#getLabel}.
	 * If not, use {@link #TreeitemComparator(int, boolean, boolean, boolean)}
	 * instead.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param index which column to compare. If -1, {@link Treeitem#getValue}
	 * is used.
	 */
	public TreeitemComparator(int index) {
		this(index, true, true, false, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Treeitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: it compares the returned value of {@link Treecell#getLabel}.
	 * If you want to compare {@link Treeitem#getValue}.,
	 * use {@link #TreeitemComparator(int, boolean, boolean, boolean)}
	 * instead.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param index which column to compare. If -1, {@link Treeitem#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public TreeitemComparator(int index, boolean ascending,
	boolean ignoreCase) {
		this(index, ascending, ignoreCase, false, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Treeitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param index which column to compare. If -1, {@link Treeitem#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byValue whether to compare {@link Treeitem#getValue}.
	 * If false, it compares {@link Treecell#getLabel}.
	 * If true, it assumes the value returned by {@link Treeitem#getValue}
	 * implements Comparable.
	 * It is ignored if the index is -1.
	 */
	public TreeitemComparator(int index, boolean ascending,
	boolean ignoreCase, boolean byValue) {
		this(index, ascending, ignoreCase, byValue, false);
	}
	/** Compares with the column of the specified index.
	 *
	 * <p>0 for the first column, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Treeitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * @param index which column to compare. If -1, {@link Treeitem#getValue}
	 * is used.
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byValue whether to compare {@link Treeitem#getValue}.
	 * If false, it compares {@link Treecell#getLabel}.
	 * If true, it assumes the value returned by {@link Treeitem#getValue}
	 * implements Comparable.
	 * It is ignored if the index is -1.
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public TreeitemComparator(int index, boolean ascending,
	boolean ignoreCase, boolean byValue, boolean nullAsMax) {
		_treecol = null;
		_index = index;
		_asc = ascending;
		_igcase = ignoreCase;
		_byval = byValue;
		_maxnull = nullAsMax;
	}
	/** Compares with the column which the tree header is at.
	 *
	 * <p>Note: it compares the returned value of {@link Treecell#getLabel}.
	 * If you want to compare {@link Treeitem#getValue}.,
	 * use {@link #TreeitemComparator(Treecol, boolean, boolean, boolean)}
	 * instead.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public TreeitemComparator(Treecol treecol, boolean ascending,
	boolean ignoreCase) {
		this(treecol, ascending, ignoreCase, false, false);
	}
	/** Compares with the column which the tree header is at.
	 *
	 * <p>A null value is considered as the minimum value.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byValue whether to compare {@link Treeitem#getValue}.
	 * If false, it compares {@link Treecell#getLabel}.
	 */
	public TreeitemComparator(Treecol treecol, boolean ascending,
	boolean ignoreCase, boolean byValue) {
		this(treecol, ascending, ignoreCase, byValue, false);
	}
	/** Compares with the column which the tree header is at.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 * @param byValue whether to compare {@link Treeitem#getValue}.
	 * If false, it compares {@link Treecell#getLabel}.
	 * @param nullAsMax whether to consider null as the maximum value.
	 * If false, null is considered as the minimum value.
	 */
	public TreeitemComparator(Treecol treecol, boolean ascending,
	boolean ignoreCase, boolean byValue, boolean nullAsMax) {
		_treecol = treecol;
		_index = -1; //not decided yet
		_asc = ascending;
		_igcase = ignoreCase;
		_byval = byValue;
		_maxnull = nullAsMax;
	}

	/** Returns the Treecol that this comparator is associated with, or null
	 * if not available.
	 */
	public Treecol getTreecol() {
		return _treecol;
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
	/** Returns whether to compare the returned value of {@link Treeitem#getValue}
	 */
	public boolean byValue() {
		return _byval;
	}

	//Comparator//
	public int compare(Object o1, Object o2) {
		final int index =
			_index < 0 && _treecol != null ? _treecol.getColumnIndex(): _index;

		Object v1, v2;
		if (o1 instanceof Treeitem) { //not live data
			final Treeitem ti1 = (Treeitem)o1, ti2 = (Treeitem)o2;
			if (index < 0) {
				v1 = handleCase((Comparable)ti1.getValue());
				v2 = handleCase((Comparable)ti2.getValue());
			} else {
				List tcs1 = ti1.getTreerow().getChildren();
				if (index >= tcs1.size()) v1 = null;
				else {
					final Treecell tc = (Treecell)tcs1.get(index);
					v1 = handleCase(_byval ? ti1.getValue(): tc.getLabel());
				}
				List lcs2 = ti2.getTreerow().getChildren();
				if (index >= lcs2.size()) v2 = null;
				else {
					final Treecell lc = (Treecell)lcs2.get(index);
					v2 = handleCase(_byval ? ti1.getValue(): lc.getLabel());
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
		if (!(o instanceof TreeitemComparator))
			return false;
		final TreeitemComparator c = (TreeitemComparator)o;
		return c._index == _index && c._asc == _asc && c._igcase == _igcase;
	}
	public int hashCode() {
		return _index ^ (_asc ? 1: 5) ^ (_igcase ? 9: 3);
	}
	public String toString() {
		return "[Comparator "+_index+"-th col, asc:"+_asc+']';
	}
}
