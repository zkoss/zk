/* Cropper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 22 16:43:40     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

import java.util.Set;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * if not all of the children of a component are available at the client.
 * In other word, the component support 'cropping'.
 * Example: only children of the active page of ZUL's grid are available
 * at the client, if it is in the paging mold.
 *
 * <p>ZK Update Engine detects this interface and then ignores any update to
 * components that don't have counterpart at the client.
 *
 * @author tomyeh
 */
public interface Cropper {
	/** Returns whether it is a cropper, i.e., cropping is applied.
	 *
	 * <p>Note: this method is called when an child is detached, so
	 * you have to count one more for the total number of children.
	 * Example &lt;= must be used, not &lt;:<br/>
	 * <code>getChildCount() &lt;= getPageSize()</code>
	 *
	 * <p>The result of returning false is the same as not declaring
	 * with {@link Cropper}.
	 */
	public boolean isCropper();
	/** Returns a set of child components that are available at the client,
	 * or null if ALL available. The child components in the returned set
	 * must be in the same order of {@link org.zkoss.zk.ui.Component#getChildren}.
	 * In order words, you must use LinkedHashSet or similar to ensure the order
	 * in the returned set.
	 *
	 * <p>Note: for better performance, it is better to return null if
	 * all children are available at the client.
	 */
	public Set getAvailableAtClient();
}
