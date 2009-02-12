/* HeadersElement.java

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

/**
 * A skeletal implementation for headers, the parent of a group of
 * {@link HeaderElement}.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface HeadersElement extends XulElement {

	/**
	 * Returns whether the width of the child column is sizable.
	 */
	public boolean isSizable();

	/**
	 * Sets whether the width of the child column is sizable. If true, an user
	 * can drag the border between two columns (e.g.,
	 * {@link org.zkoss.zul.Column}) to change the widths of adjacent columns.
	 * <p>
	 * Default: false.
	 */
	public void setSizable(boolean sizable);

}
