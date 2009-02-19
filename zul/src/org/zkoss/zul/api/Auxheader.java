/* Auxheader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * An auxiliary header.
 * <p>
 * Default {@link #getZclass}: z-auxheader.(since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Auxheader extends org.zkoss.zul.impl.api.HeaderElement {

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
