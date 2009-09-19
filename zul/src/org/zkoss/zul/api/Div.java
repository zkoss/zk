/* Div.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * The same as HTML DIV tag.
 * 
 * <p>
 * An extension. It has the same effect as
 * <code><h:div xmlns:h="http://www.w3.org/1999/xhtml"></code>. Note: a
 * {@link Window} without title and caption has the same visual effect as
 * {@link Div}, but {@link Div} doesn't implement IdSpace. In other words,
 * {@link Div} won't affect the uniqueness of identifiers.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Div extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Returns the alignment.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getAlign();

	/**
	 * Sets the alignment: one of left, center, right, ustify,
	 */
	public void setAlign(String align);

}
