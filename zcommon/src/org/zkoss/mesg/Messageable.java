/* Messageable.java


	Purpose: Extended exception interface
	Description: 
	History:
	 2001/7/2, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.mesg;

/**
 * Denotes an object that contains a message by use of an integer,
 * called code.
 *
 * @author tomyeh
 */
public interface Messageable extends MessageConst {
	/**
	 * Gets the message code.
	 *
	 * @return the message code
	 */
	public int getCode();
}
