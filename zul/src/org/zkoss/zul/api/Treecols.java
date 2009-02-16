/* Treecols.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A treecols.
 * <p>
 * Default {@link #getZclass}: z-tree-cols.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Treecols extends org.zkoss.zul.impl.api.HeadersElement {
	/**
	 * Returns the tree that it belongs to.
	 * <p>
	 * It is the same as {@link #getParent}.
	 */
	public org.zkoss.zul.api.Tree getTreeApi();

}
