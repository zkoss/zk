/* Binable.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/idom/Binable.java,v 1.3 2006/02/27 03:41:54 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/10/21 21:27:23, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom;

/**
 * Represent a class that allows any type of objects, not just String.
 * It is usually implemented by a class that also implements Item.
 * Currently, only Binary implements it.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:41:54 $
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
