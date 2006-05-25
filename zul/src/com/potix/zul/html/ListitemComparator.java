/* ListitemComparator.java

{{IS_NOTE
	$Id: ListitemComparator.java,v 1.1 2006/05/25 14:11:13 tomyeh Exp $
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
 * @version $Revision: 1.1 $ $Date: 2006/05/25 14:11:13 $
 */
public class ListitemComparator implements Comparator {
	private final int _index;
	/** null-as-largest. */
	private final boolean _nal;

	/** Compares with {@link Listitem#getValue}.
	 *
	 * <p>It assumes the value implements Comparable.
	 *
	 * <p>Note: null is assumed to be smallest. If you want null to be
	 * largest, use {@link #ListitemComparator(int, boolean)}.
	 */
	public ListitemComparator() {
		_index = -1; //compare value
		_nal = false;
	}
	/** Compares with the cell of the specified index.
	 *
	 * <p>0 for the first cell, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Listitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * <p>Note: null is assumed to be smallest. If you want null to be
	 * largest, use {@link #ListitemComparator(int, boolean)}.
	 */
	public ListitemComparator(int index) {
		_index = index;
		_nal = false;
	}
	/** Compares with the cell of the specified index.
	 *
	 * <p>0 for the first cell, 1 for the second and so on
	 *
	 * <p>Note: -1 for {@link Listitem#getValue} and it assumes
	 * the value implements Comparable.
	 *
	 * @param nullAsLargest whether null is assumed to be largest.
	 */
	public ListitemComparator(int index, boolean nullAsLargest) {
		_index = index;
		_nal = nullAsLargest;
	}

	//Comparator//
	public int compare(Object o1, Object o2) {
		final Listitem li1 = (Listitem)o1, li2 = (Listitem)o2;

		if (_index < 0) {
			final Object v1 = li1.getValue(), v2 = li2.getValue();
			if (v1 == null)
				return v2 == null ? 0: _nal ? 1: -1;
			return v2 == null ? _nal ? -1: 1: ((Comparable)v1).compareTo(v2);
		}

		final List lc1 = li1.getChildren(), lc2 = li2.getChildren();
		final String
			v1 = _index >= lc1.size() ? null:
				((Listcell)lc1.get(_index)).getLabel(),
			v2 = _index >= lc2.size() ? null:
				((Listcell)lc2.get(_index)).getLabel();
		if (v1 == null)
			return v2 == null ? 0: _nal ? 1: -1;
		return v2 == null ? _nal ? -1: 1: v1.compareTo(v2);
	}
	public boolean equals(Object o) {
		return o instanceof ListitemComparator &&
			((ListitemComparator)o)._index == _index;
	}
}
