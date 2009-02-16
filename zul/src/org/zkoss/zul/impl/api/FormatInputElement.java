/* FormatInputElement.java

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
package org.zkoss.zul.impl.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * A skeletal implementation for an input box with format.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface FormatInputElement extends InputElement {
	/**
	 * Returns the format.
	 * <p>
	 * Default: null (used what is defined in the format sheet).
	 */
	public String getFormat();

	/**
	 * Sets the format.
	 */
	public void setFormat(String format) throws WrongValueException;

}
