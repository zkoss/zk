/* IdentityComparator.java

	Purpose:
		The comparator uses == and System.identifyHashCode to do
		the comparison.
	Description:
		
	History:
		Fri Sep 13 10:14:11  2002, Created by tomyeh

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.Comparator;

/**
 * The comparator uses == and System.identifyHashCode to compare two objects.
 * It assumes if o1 != o2, then c1 != c2 where c1 = System.identityHashCode(o1).
 *
 * <p>This is useful if dynamic proxy is used with TreeSet or TreeMap
 * (so equals is expensive).
 * Reason: the speed of identifyHashCode is much faster than dynamic proxy
 * (150:1).
 *
 * <p>However, if possible, java.util.IdentityHashMap and {@link IdentityHashSet}
 * are preferred.
 *
 * @author tomyeh
 * @see IdentityHashSet
 */
public class IdentityComparator implements Comparator {
	public IdentityComparator() {
	}

	//-- Comparator --//
	public int compare(Object o1, Object o2) {
		if (o1 == o2)
			return 0;

		int c1 = System.identityHashCode(o1);
		int c2 = System.identityHashCode(o2);
		assert c1 != c2; //if o1 != o2, c1 != c2
		return c1 > c2 ? 1: -1;
	}
}
