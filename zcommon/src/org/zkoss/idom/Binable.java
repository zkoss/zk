/* Binable.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2001/10/21 21:27:23, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

/**
 * Represent a class that allows any type of objects, not just String.
 * It is usually implemented by a class that also implements Item.
 * Currently, only Binary implements it.
 *
 * @author tomyeh
 * @see Item
 * @see Group
 * @see Attributable
 * @see Namespaceable
 */
public interface Binable {
	/**
	 * Gets the value of a item that accepts any type as its value.
	 */
	public Object getValue();
	/**
	 * Sets the value of a item that accepts any type as its value.
	 */
	public void setValue(Object value);
}
