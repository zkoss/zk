/* TreeModelExt.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 31 09:35:50     2011, Created by jimmy

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;

/**
 * An extra interface that can be implemented with {@link TreeModel}
 * to control the sorting of the data model.
 * @since 5.0.6
 * @author jimmy
 */
public interface TreeModelExt<Node> {
	/** It called when {@link TreeModel} or {@link Tree} has to sort
	 * the content.
	 *
	 * <p>After sorting, this model shall notify the instances of
	 * {@link org.zkoss.zul.event.TreeDataListener} (registered thru {@link AbstractTreeModel#addTreeDataListener})
	 * to update the content.
	 * Typically you have to notify with
	 * <pre><code>new TreeDataEvent(this, TreeDataEvent.CONTENTS_CHANGED, parent, -1, -1)</code></pre>
	 * to denote all data are changed (and reloading is required).
	 *
	 * <p>The comparator assigned to, say, {@link Treecol#setSortAscending}
	 * is passed to method as the cmpr argument.
	 * Thus, developers could use it as a tag to know which column
	 * or what kind of order to sort.
	 * Notice that the comparator is capable to sort under the order specified
	 * in the ascending parameter. In other words, you could ignore the
	 * ascending parameter (which is used only for providing additional information)
	 *
	 * @param cmpr the comparator assigned to {@link Treecol#setSortAscending}
	 * and other relative methods. If developers didn't assign any one,
	 * the default comparator is used.
	 * Notice that it is capable to sort the data in the correct order,
	 * you could ignore the ascending parameter.
	 * @param ascending whether to sort in the ascending order (or in
	 * the descending order, if false). Notice that it is used only to
	 * provide additional information. To sort the data correctly, you could
	 * count on the cmpr parameter only.
	 */
	public void sort(Comparator<Node> cmpr, boolean ascending);
}
