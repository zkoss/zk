/* Pair.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	 2001/8/8, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import org.zkoss.lang.Objects;

/**
 * A pair of keys. It is used with DualHashSet and DualHashMap to
 * represent a pair of keys as an object.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 */
public class Pair {
	/** The first key. */
	public final Object x;
	/** The second key. */
	public final Object y;

	public Pair(Object x, Object y) {
		this.x = x;
		this.y = y;
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
		return Objects.hashCode(x) + Objects.hashCode(y);
	}

	public String toString() {
		return '(' + Objects.toString(x) + ", "  + Objects.toString(y) + ')';
	}
}
