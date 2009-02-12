/* Tablelayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 23 09:22:13     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkmax.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * Tablelayout lay outs a container as an HTML table whose columns can be
 * specified, and rowspan and colspan of its child can also be specified to
 * create complex layouts within the table.
 * 
 * <p>
 * Default {@link org.zkoss.zkmax.zul.Tablelayout#getZclass}: z-table-layout.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public interface Tablelayout extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Sets the number of columns.
	 */
	public void setColumns(int columns) throws WrongValueException;

	/**
	 * Returns number of rows to span this header. Default: 1.
	 */
	public int getColumns();

}
