/* UId.java

	Purpose:
		
	Description:
		
	History:
		2:58 PM 2021/10/20, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.util;

/**
 * Oid generator
 * @author jumperchen
 */
public final class Oid {
	/**
	 * The default Oid prefix.
	 */
	public static String PREFIX = "_";
	public static String generate(Object obj) {
		return generate(obj, PREFIX);
	}
	public static String generate(Object obj, String prefix) {
		int i = System.identityHashCode(obj);
		return prefix + Integer.toUnsignedString(i, 36);
	}
}
