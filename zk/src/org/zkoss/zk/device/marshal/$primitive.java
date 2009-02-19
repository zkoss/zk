/* $primitive.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 12 15:08:35     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.device.marshal;

/**
 * Represents a primitive type, such as {@link $boolean} and so on.
 *
 * @author tomyeh
 * @since 5.0.0
 * @see Marshaller#marshal(Object)
 */
public interface $primitive {
	/** Returns the primitive type it represents, such as Boolean.TYPE.
	 */
	public Class getType();
}
