/* $short.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 12 14:41:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.device.marshal;

/**
 * Represents a primitive short.
 * It is usually if you want to distinquish short and Short.
 * @author tomyeh
 * @since 5.0.0
 * @see Marshaller#marshal(Object)
 */
public class $short implements java.io.Serializable, $primitive {
	public final short value;
	public $short(short value) {
		this.value = value;
	}
	public int hashCode() {
		return this.value;
	}
	public String toString() {
		return Short.toString(this.value);
	}
	public boolean equals(Object o) {
		return (o instanceof $short) && (($short)o).value == this.value;
	}
	public Class getType() {
		return Short.TYPE;
	}
}
