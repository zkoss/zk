/* MutableInteger.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Feb 16 13:39:47     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

/**
 * Represents an interger that can be modified.
 *
 * <p>It is useful if you want to pass an integer to a method
 * and like to keep the result of how the method modifies the value.
 *
 * @author tomyeh
 */
public class MutableInteger implements Comparable {
	/** The value of ths mutable integer. */
	public int value;

	public MutableInteger(int value) {
		this.value = value;
	}

	/** Returns the value of this {@link MutableInteger} as an int.
	 */
	public int intValue() {
		return this.value;
	}
	/** Sets the value of this {@link MutableInteger}.
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/** Compares two {@link MutableInteger} objects numerically.
	 *
	 * @return the value 0 if the argument is numerically equal to this;
	 * a value less than 0 if the argument is numerically greater than this;
	 * and a value greater than 0 if the argument is numerically less than this.
	 */
	public int compareTo(MutableInteger o) {
		if (this.value > o.value) return 1;
		else if (this.value == o.value) return 0;
		else return -1;
	}
	//Comparable//
	/** Compares two {@link MutableInteger} objects numerically.
	 *
	 * @return the value 0 if the argument is numerically equal to this;
	 * a value less than 0 if the argument is numerically greater than this;
	 * and a value greater than 0 if the argument is numerically less than this.
	 */
	public int compareTo(Object o) {
		return compareTo((MutableInteger)o);
	}
	//Object//
	public String toString() {
		return "" + this.value;
	}
	public int hashCode() {
		return this.value;
	}
	public boolean equals(Object o) {
		return (o instanceof MutableInteger)
			&& ((MutableInteger)o).value == this.value;
	}
}
