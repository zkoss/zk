/* $boolean.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 12 14:41:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.lang;

/**
 * Represents a primitive boolean.
 * It is usually if you want to distinquish boolean and Boolean.
 * @author tomyeh
 * @since 5.0.0
 */
public class $boolean implements java.io.Serializable, $primitive {
	public final boolean value;
	public $boolean(boolean value) {
		this.value = value;
	}
	public int hashCode() {
		return this.value ? 1: 0;
	}
	public String toString() {
		return Boolean.toString(this.value);
	}
	public boolean equals(Object o) {
		return (o instanceof $boolean) && (($boolean)o).value == this.value;
	}
	public Class getType() {
		return Boolean.TYPE;
	}
}
