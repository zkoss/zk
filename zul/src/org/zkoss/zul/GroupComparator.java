/* GroupComparator.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep  3 10:31:01     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;

/**
 * A comparison function used to compare the group order of two objects,
 * and the order of them in the group if they belongs to the same group.
 *
 * <p>To know whether two objects is in the same group, {@link #compareGroup}
 * is invoked. If zero is returned, it implies they are in the same group,
 * and then {@link #compare} is used to compare the order in the group.
 *
 * <p>{@link #compare} won't be called if they don't belong to the same group.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public interface GroupComparator<D> extends Comparator<D> {
	/** Compares if the two arguments is in the same group.
	 *
	 * <ul>
	 * <li>compareGroup(x, y) == 0 implies x and y is in the same group.</li>
	 * <li>compareGroup(x, y) > 0 implies x and y is in a different group
	 * and the order of x's group is larger than that of y's group.<li>
	 * <li>compareGroup(x, y) &lt; 0 implies x and y is in a different group
	 * and the order of x's group is less than that of y's group.<li>
	 * </ul>
	 */
	public int compareGroup(D o1, D o2);
}
