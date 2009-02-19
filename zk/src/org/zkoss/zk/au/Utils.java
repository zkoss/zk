/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 08:30:36     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au;

import java.util.Date;
import org.zkoss.lang.Objects;

/**
 * Utilities to handle the AU requests.
 * @author tomyeh
 * @since 5.0.0
 */
public class Utils {
	/** Encodes an object to a String that the client can recognize.
	 */
	public static String encode(Object o) {
		if (o == null) return "n";
		if (o instanceof String)
			return 's' + (String)o;
		if (o instanceof Integer)
			return encode(((Integer)o).intValue());
		if (o instanceof Boolean)
			return encode(((Boolean)o).booleanValue());
		if (o instanceof Date)
			return "t" + ((Date)o).getTime();
		if (o instanceof Character)
			return encode(((Character)o).charValue());
		if (o instanceof Long)
			return encode(((Long)o).longValue());
		if (o instanceof Byte)
			return encode(((Byte)o).byteValue());
		if (o instanceof Float)
			return encode(((Float)o).floatValue());
		if (o instanceof Double)
			return encode(((Double)o).floatValue());
		return 's' + Objects.toString(o);
	}
	/** Encodes a boolean to a String that the client can recognize.
	 */
	public static String encode(boolean v) {
		return v ? "1": "0";
	}
	/** Encodes an integer to a String that the client can recognize.
	 */
	public static String encode(int v) {
		return "i" + v;
	}
	/** Encodes a long to a String that the client can recognize.
	 */
	public static String encode(long v) {
		return "l" + v;
	}
	/** Encodes a byte to a String that the client can recognize.
	 */
	public static String encode(byte v) {
		return "b" + v;
	}
	/** Encodes a character to a String that the client can recognize.
	 */
	public static String encode(char v) {
		return "c" + v;
	}
	/** Encodes a double to a String that the client can recognize.
	 */
	public static String encode(double v) {
		return "d" + v;
	}
	/** Encodes a float to a String that the client can recognize.
	 */
	public static String encode(float v) {
		return "f" + v;
	}
}
