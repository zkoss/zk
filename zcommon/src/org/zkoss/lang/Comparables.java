/*	Comparables.java


	Purpose:
	Description:
	History:
		2001/11/23, Henri Chen: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.lang;

/**
 * Utilities regarding Comparable type objects.
 *
 * @author henrichen
 */
public final class Comparables {
	/**
	 * Given two comparables, return the minimum of the two.
	 * Note that the two Comparable must be with compatible type, or a
	 * ClassCastException might be thrown.
	 */
	@SuppressWarnings("unchecked")
	public static final Comparable min(Comparable a, Comparable b) {
		return  (a.compareTo(b) < 0) ? a : b;
	}

	/**
	 * Given two comparables, return the maximum of the two.
	 * Note that the two Comparable must be with compatible type, or a
	 * ClassCastException might be thrown.
	 */
	@SuppressWarnings("unchecked")
	public static final Comparable max(Comparable a, Comparable b) {
		return  (a.compareTo(b) < 0) ? b : a;
	}
}
