/* GroupModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May  2 11:26:28     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;

/**
 * Used with {@link ListModel} to represent a list model with grouping.
 *
 * <p>Notice that elements of {@link ListModel} shall include group elements.
 * In other words, {@link ListModel#getSize} shall include the number
 * of groups, and {@link ListModel#getElementAt} might return a group
 * object.
 *
 * <p>For example, a list model with two groups, and each have 3 items.
 * Then, there are totally 8 elements ({@link ListModel#getSize}),
 * Elements at index 0 and 4 will be a group object.
 * Elements at index 1, 2 and 3 are items belonging to the first group,
 * and at index 5, 6 and 7 the second group.
 *
 * @author tomyeh
 * @since 3.5.0
 * @see ListModel
 */
public interface GroupModel {
	/** Returns the number of groups in the list model.
	 */
	public int getGroupCount();
	/** Returns the number of items belong the specified group.
	 */
	public int getChildCount(int index);
	/**
	 * Returns whether the Group has a Groupfoot or not.
	 */
	public boolean hasGroupfoot(int index);
	/**
	 * Groups the rows by the specified column.
	 * It only called when {@link Listbox} or {@link Grid} has the sort function.
	 * @param cmpr the comparator assigned to {@link Column#setSortAscending}
	 * 	and other relative methods. If developers didn't assign any one,
	 * 	the method is returned directly.
	 * @param ascending whether to sort in the ascending order (or in
	 * 	the descending order)
	 * @param index the index of the column
	 */
	public void groupByField(Comparator cmpr, boolean ascending, int index);
}
