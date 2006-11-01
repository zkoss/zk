/* Transparent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 22 16:22:57     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * to denote the component that might be transparent.
 * By transparent we mean this component doesn't have any counterpart
 * at the client.
 *
 * @author tomyeh
 */
public interface Transparent {
	/** Returns whether the component that owns this object is transparent.
	 * By transparent we mean the component doesn't have any counterpart
	 * in the client.
	 * In other words, it doesn't generate any element in the client.
	 * In this case, invalidate the component implies invalidate all
	 * its children, and {@link org.zkoss.zk.ui.Component#smartUpdate} is meaningless
	 * (and causes exception).
	 *
	 * <p>All its children are considered as children of its parent when
	 * inserting.
	 */
	public boolean isTransparent();
}
