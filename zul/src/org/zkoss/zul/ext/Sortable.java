/* Sortable.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 30 14:05:07 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul.ext;

import java.util.Comparator;

/**
 * Indicate a data model that supports sorting.
 * It is typically used with {@link org.zkoss.zul.ListModel}
 * <p> If the implementation also implements {@link Selectable} interface,
 * it is up to the particular implementation to decide what the selections 
 * remains in order after sorted.
 * 
 * @author tomyeh
 * @since 6.0.0
 */
public interface Sortable<T> {
	/** It called when the associated component
	 * (such as {@link org.zkoss.zul.Listbox}) has to sort the content.
	 *
	 * <p>After sorting, this model shall notify the component abort the result.
	 * For example, if this interface is used with {@link org.zkoss.zul.ListModel},
	 * then it might do the following to notify all registered listeners
	 * {@link org.zkoss.zul.event.ListDataListener} (registered by {@link org.zkoss.zul.ListModel#addListDataListener})
	 * to update the content:
	 * <pre><code>new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1)</code></pre>
	 *
	 * <p>The comparator assigned to, say, {@link org.zkoss.zul.Listheader#setSortAscending}
	 * is passed to method as the cmpr argument.
	 * Thus, developers could use it as a tag to know which column
	 * or what kind of order to sort.
	 * Notice that the comparator is capable to sort under the order specified
	 * in the ascending parameter. In other words, you could ignore the
	 * ascending parameter (which is used only for providing additional information)
	 *
	 * @param cmpr the comparator assigned to {@link org.zkoss.zul.Listheader#setSortAscending}
	 * and other relative methods. If developers didn't assign any one,
	 * the default comparator is used.
	 * Notice that it is capable to sort the data in the correct order,
	 * you could ignore the ascending parameter.
	 * @param ascending whether to sort in the ascending order (or in
	 * the descending order, if false). Notice that it is used only to
	 * provide additional information. To sort the data correctly, you could
	 * count on the cmpr parameter only.
	 */
	public void sort(Comparator<T> cmpr, boolean ascending);

	/** Returns the sort direction of this model for the given comparator.
	 * It must be one of "ascending", "descending" and "natural".
	 * <p>Default: "natural".
	 * @since 6.0.0
	 */
	public String getSortDirection(Comparator<T> cmpr);
}
