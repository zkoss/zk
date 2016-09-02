/* Table.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:54:05     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The TABLE tag.
 * 
 * @author tomyeh
 */
public class Table extends AbstractTag {
	public Table() {
		super("table");
	}
	/**
	 * Returns the border of this table tag.
	 * @since 8.0.3
	 */
	public String getBorder() {
		return (String) getDynamicProperty("border");
	}

	/**
	 * Sets the border of this table tag.
	 * @since 8.0.3
	 */
	public void setBorder(String border) throws WrongValueException {
		setDynamicProperty("border", border);
	};
	/**
	 * Returns the sortable of this table tag.
	 * @since 8.0.3
	 */
	public String getSortable() {
		return (String) getDynamicProperty("sortable");
	}

	/**
	 * Sets the sortable of this table tag.
	 * @since 8.0.3
	 */
	public void setSortable(String sortable) throws WrongValueException {
		setDynamicProperty("sortable", sortable);
	};
}
