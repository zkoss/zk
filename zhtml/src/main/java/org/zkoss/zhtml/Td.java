/* Td.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:54:30     2005, Created by tomyeh

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
 * The TD tag.
 *
 * @author tomyeh
 */
public class Td extends AbstractTag {
	public Td() {
		super("td");
	}
	/**
	 * Returns the colspan of this td tag.
	 * @since 8.0.3
	 */
	public Integer getColspan() {
		return (Integer) getDynamicProperty("colspan");
	}

	/**
	 * Sets the colspan of this td tag.
	 * @since 8.0.3
	 */
	public void setColspan(Integer colspan) throws WrongValueException {
		setDynamicProperty("colspan", colspan);
	}
	/**
	 * Returns the headers of this td tag.
	 * @since 8.0.3
	 */
	public String getHeaders() {
		return (String) getDynamicProperty("headers");
	}

	/**
	 * Sets the headers of this td tag.
	 * @since 8.0.3
	 */
	public void setHeaders(String headers) throws WrongValueException {
		setDynamicProperty("headers", headers);
	}
	/**
	 * Returns the rowspan of this td tag.
	 * @since 8.0.3
	 */
	public Integer getRowspan() {
		return (Integer) getDynamicProperty("rowspan");
	}

	/**
	 * Sets the rowspan of this td tag.
	 * @since 8.0.3
	 */
	public void setRowspan(Integer rowspan) throws WrongValueException {
		setDynamicProperty("rowspan", rowspan);
	}
}
