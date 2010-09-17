/* GroupsModelExt.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep  1 10:40:49     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;

/**
 * An extra interface that can be implemented with {@link GroupsModel}
 * to control the sorting and grouping of the data model.
 *
 * <p>D: the type of the data.
 * @author tomyeh
 * @since 3.5.0
 */
public interface GroupsModelExt<D> {
	/** It called when {@link Listbox} or {@link Grid} has to sort
	 * the content.
	 *
	 * <p>After sorting, this model shall notify the instances of
	 * {@link org.zkoss.zul.event.ListDataListener} (registered thru {@link ListModel#addListDataListener})
	 * to update the content.
	 * Typically you have to notify with
	 * <pre><code>new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1)</code></pre>
	 * to denote all data are changed (and reloading is required).
	 *
	 * <p>The comparator assigned to, say, {@link Listheader#setSortAscending}
	 * is passed to method as the cmpr argument.
	 * Thus, developers could use it as a tag to know which column
	 * or what kind of order to sort.
	 *
	 * @param cmpr the comparator assigned to {@link Listheader#setSortAscending}
	 * and other relative methods. If developers didn't assign any one,
	 * the default comparator is used.
	 * @param ascending whether to sort in the ascending order (or in
	 * the descending order)
	 * @param colIndex the index of the column
	 */
	public void sort(Comparator<D> cmpr, boolean ascending, int colIndex);

	/**
	 * Groups and sorts the data by the specified column.
	 * It only called when {@link Listbox} or {@link Grid} has the sort function.
	 *
	 * @param cmpr the comparator assigned to {@link Column#setSortAscending}
	 * 	and other relative methods. If developers didn't assign any one,
	 * 	the method is returned directly.
	 * @param ascending whether to sort in the ascending order (or in
	 * 	the descending order)
	 * @param colIndex the index of the column
	 */
	public void group(Comparator<D> cmpr, boolean ascending, int colIndex);
}
