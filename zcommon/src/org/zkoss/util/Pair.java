/* Pair.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	 2001/8/8, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import org.zkoss.lang.Objects;

/**
 * A pair of keys. It is used with DualHashSet and DualHashMap to
 * represent a pair of keys as an object.
 *
 * @author tomyeh
 */
public class Pair implements java.io.Serializable {
	/** The first key. */
	public final Object x;
	/** The second key. */
	public final Object y;

	public Pair(Object x, Object y) {
		this.x = x;
		this.y = y;
	}
	protected Pair() {
		this(null, null);
	}

	/** Returns the first value of the pair.
	 */
	public Object getX() {
		return this.x;
	}
	/** Returns the second value of the pair.
	 */
	public Object getY() {
		return this.y;
	}

	//-- Object --//
	public final boolean equals(Object o) {
		if (!(o instanceof Pair))
			return false;
		final Pair pair = (Pair)o;
		return Objects.equals(x, pair.x) &&
				Objects.equals(y, pair.y);
	}
	public final int hashCode() {
		return Objects.hashCode(x) ^ Objects.hashCode(y);
	}

	public String toString() {
		return '(' + Objects.toString(x) + ", "  + Objects.toString(y) + ')';
	}
}
