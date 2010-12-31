/* ArrayComparator.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 31 10:44:45 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import java.util.Comparator;
import java.lang.reflect.Array;

/**
 * Compares the specified element of the array.
 * It assumes the data passed to {@link #compare} is an array, and
 * it compares the n-th element, where n is passed in the constructor,
 * {@link #ArrayComparator} (the index parameter).
 * It also assumes the element must implement {@link Comparable}.
 *
 * @author tomyeh
 * @since 5.0.6
 */
public class ArrayComparator implements Comparator, java.io.Serializable {
	private final int _index;
	private final boolean _ascend;
	/** The constructor.
	 * @param index which index of an array to compare
	 * @param ascending whether to sort as ascending (or descending).
	 */
	public ArrayComparator(int index, boolean ascending) {
		_index = index;
		_ascend = ascending;
	}
	//@Override
	public int compare(Object o1, Object o2) {
        int v = ((Comparable)Array.get(o1, _index)).compareTo(Array.get(o2, _index));
		return _ascend ? v: -v;
	}
}
