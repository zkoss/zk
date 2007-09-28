/* YuiextzFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
	Jun 22, 2007 6:25:22 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkforge.yuiext.fn;

import org.zkforge.yuiext.grid.Row;

/**
 * Utilities for using EL.
 * 
 * @author jumperchen
 */
public class YuiextzFns {
	protected YuiextzFns() {}

	/** Returns the column attribute of a child of a row by specifying
	 * the index.
	 */
	public static final String getColAttrs(Row row, int index) {
		return row.getChildAttrs(index);
	}

}
