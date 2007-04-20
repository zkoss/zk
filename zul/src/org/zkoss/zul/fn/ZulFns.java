/* ZulFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 12 15:19:42     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.fn;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Row;
import org.zkoss.zul.Box;

/**
 * Utilities for using EL.
 * 
 * @author tomyeh
 */
public class ZulFns {
	protected ZulFns() {}

	/** Returns the column attribute of a child of a row by specifying
	 * the index.
	 */
	public static final String getColAttrs(Row row, int index) {
		return row.getChildAttrs(index);
	}

	/**
	 * Returns the inner attributes used for the cell of the specified child
	 * when it is placed inside of hbox/vbox.
	 */
	public static final String getBoxChildInnerAttrs(Component child) {
		return ((Box)child.getParent()).getChildInnerAttrs(child);
	}
	/**
	 * Returns the outer attributes used for the cell of the specified child
	 * when it is placed inside of hbox/vbox.
	 */
	public static final String getBoxChildOuterAttrs(Component child) {
		return ((Box)child.getParent()).getChildOuterAttrs(child);
	}
}
