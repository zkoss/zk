/* $float.java

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
 * Represents a primitive float.
 * It is usually if you want to distinquish float and Float.
 * @author tomyeh
 * @since 5.0.0
 * @see Marshaller#marshal(Object)
 */
public class $float implements java.io.Serializable, $primitive {
	public final float value;
	public $float(float value) {
		this.value = value;
	}
	public int hashCode() {
		return (int)this.value;
	}
	public String toString() {
		return Float.toString(this.value);
	}
	public boolean equals(Object o) {
		return (o instanceof $float) && (($float)o).value == this.value;
	}
	public Class getType() {
		return Float.TYPE;
	}
}
