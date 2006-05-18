/* Pair.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/util/Pair.java,v 1.2 2006/02/27 03:42:03 tomyeh Exp $
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
package com.potix.util;

import com.potix.lang.Objects;

/**
 * A pair of keys. It is used with DualHashSet and DualHashMap to
 * represent a pair of keys as an object.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:03 $
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
