/* ListModelExt.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 30 16:40:50     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;

/**
 * An extra interface that can be implemented with {@link ListModel}
 * to control the sorting of the data model.
 *
 * @author tomyeh
 */
public interface ListModelExt<T> {
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
	 */
	public void sort(Comparator<T> cmpr, boolean ascending);
}
