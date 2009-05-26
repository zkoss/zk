/* Tablechildren.java

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
 * The cell of Tablelayout. <br>
 * The child component of Tablechildren only can be Panel.
 * 
 * <p>
 * Default {@link org.zkoss.zkmax.zul.Tablechildren#getZclass}: z-tablechildren.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public interface Tablechildren extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns number of columns to span this header. Default: 1.
	 */
	public int getColspan();

	/**
	 * Sets the number of columns to span this header.
	 * <p>
	 * It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setColspan(int colspan) throws WrongValueException;

	/**
	 * Returns number of rows to span this header. Default: 1.
	 */
	public int getRowspan();

	/**
	 * Sets the number of rows to span this header.
	 * <p>
	 * It is the same as the rowspan attribute of HTML TD tag.
	 */
	public void setRowspan(int rowspan) throws WrongValueException;

}
