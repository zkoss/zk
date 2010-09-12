/* Cropper.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 22 16:43:40     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

import java.util.Set;

import org.zkoss.zk.ui.Component;

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
	 * must be in the same order of {@link Component#getChildren}.
	 * In order words, you must use LinkedHashSet or similar to ensure the order
	 * in the returned set.
	 *
	 * <p>Note: for better performance, it is better to return null if
	 * all children are available at the client.
	 *
	 * <p>Note: the components in the returned set can have different
	 * parents. It is useful if you want to implement multi-level cropping,
	 * such as tree. Refer to {@link #getCropOwner}.
	 */
	public Set<Component> getAvailableAtClient();
	/** Returns the owner of the cropping scope.
	 * In most cases, {@link #getAvailableAtClient} returns only the
	 * available direct children of this compnent,
	 * and this method returns this component.
	 * It is so-called a single-level cropping, such as listbox and tree.
	 *
	 * <p>If you want to implement multi-level cropping, such as tree,
	 * you can consider the whole tree as a cropping scope.
	 * And, {@link #getAvailableAtClient} returns all available items,
	 * not just the child of this component. In additions, 
	 * this method returns the tree.
	 *
	 * @since 3.6.3
	 */
	public Component getCropOwner();
}
