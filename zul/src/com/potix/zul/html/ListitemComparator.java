/* ListitemComparator.java

{{IS_NOTE
	$Id: ListitemComparator.java,v 1.2 2006/05/25 15:02:25 tomyeh Exp $
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
 * @version $Revision: 1.2 $ $Date: 2006/05/25 15:02:25 $
 */
public class ListitemComparator implements Comparator {
	private final int _index;
	/** Ascending. */
	private final boolean _asc;
	/** Ignore case. */
	private final boolean _igcase;

	/** Compares with {@link Listitem#getValue}.
	 *
	 * <p>It assumes the value implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order and case-insensitive.
	 * If not, use {@link #ListitemComparator(int, boolean, boolean)}
	 * instead.
	 */
	public ListitemComparator() {
		this(-1, true, true);
	}
	/** Compares with the cell of the specified index.
	 *
	 * <p>0 for the first cell, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Listitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: It assumes the ascending order and case-insensitive.
	 * If not, use {@link #ListitemComparator(int, boolean, boolean)}
	 * instead.
	 */
	public ListitemComparator(int index) {
		this(index, true, true);
	}
	/** Compares with the cell of the specified index.
	 *
	 * <p>0 for the first cell, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Listitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * @param ascending whether to sort as ascending (or descending).
	 * @param ignoreCase whether to sort case-insensitive
	 */
	public ListitemComparator(int index, boolean ascending,
	boolean ignoreCase) {
		_index = index;
		_asc = ascending;
		_igcase = ignoreCase;
	}

	//Comparator//
	public int compare(Object o1, Object o2) {
		final Listitem li1 = (Listitem)o1, li2 = (Listitem)o2;

		Comparable v1, v2;
		if (_index < 0) {
			v1 = handleCase((Comparable)li1.getValue());
			v2 = handleCase((Comparable)li2.getValue());
		} else {
			final List lc1 = li1.getChildren(), lc2 = li2.getChildren();
			v1 = _index >= lc1.size() ? null:
				handleCase(((Listcell)lc1.get(_index)).getLabel());
			v2 = _index >= lc2.size() ? null:
				handleCase(((Listcell)lc2.get(_index)).getLabel());
		}

		if (v1 == null) return v2 == null ? 0: _asc ? -1: 1;
		if (v2 == null) return _asc ? 1: -1;
		final int v = ((Comparable)v1).compareTo(v2);
		return _asc ? v: -v;
	}
	private Comparable handleCase(Comparable c) {
		if (_igcase) {
			if (c instanceof String)
				return ((String)c).toUpperCase();
			if (c instanceof Character)
				return ((Character)c).toUpperCase();
		}
		return c;
	}

	public boolean equals(Object o) {
		return o instanceof ListitemComparator &&
			((ListitemComparator)o)._index == _index;
	}
}
