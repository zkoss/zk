/* Padding.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 20, 2009 5:26:16 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

/**
 * Control the padding height of a component(Used mainly for component developing).
 * @author henrichen
 *
 */
public interface Padding {
	/**
	 * Set the padding height in px.
	 * @param height the padding height in px.
	 */
	public void setHeight(int height);
	
	/**
	 * Returns the padding height in px.
	 * @return the padding height in px.
	 */
	public int getHeight();
}
