/* $double.java

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
 * Represents a primitive double.
 * It is usually if you want to distinquish double and Double.
 * @author tomyeh
 * @since 5.0.0
 * @see Marshaller#marshal(Object)
 */
public class $double implements java.io.Serializable, $primitive {
	public final double value;
	public $double(double value) {
		this.value = value;
	}
	public int hashCode() {
		return (int)this.value;
	}
	public String toString() {
		return Double.toString(this.value);
	}
	public boolean equals(Object o) {
		return (o instanceof $double) && (($double)o).value == this.value;
	}
	public Class getType() {
		return Double.TYPE;
	}
}
