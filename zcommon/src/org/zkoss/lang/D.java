/* D.java

{{IS_NOTE

	Purpose: Debugging utilities
	Description: 
	History:
	 2001/5/17, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

/**
 * Debugging utilities.
 *
 * <p>Because Java has no preprocessor, we have to make the debugging
 * codes being able to removed by testing D.ON or D.OFF first.<br>
 * For example,
 *
 * <p>if (D.ON) your_test_codes;
 *
 * @author tomyeh
 */
public class D {
	/**
	 * Denotes whether the debugging is ON.
	 * If ON is true, the debugging is turned on. Otherwise, it is turned off.
	 *
	 * <p>NOTE: don't modify the definition manually. It is done
	 * automatically by bin/setdbg
	 */
	public static final boolean /*-*/ON=true/*-*/; //don't modify this line
	/**
	 * Denotes whether the debugging is OFF.
	 */
	public static final boolean OFF = !ON;
}
