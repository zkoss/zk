/* SimpleMarshaller.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 12 11:10:23     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.device.marshal;

import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;

/**
 * A simple implementation of {@link Marshaller}.
 * It supports the following types of objects:
 * null, a String instance, a Date instance, the wrapper
 * class of all primitive objects, an array of these kind of
 * objects, and an array of primitives (boolean, int...).

 * @author tomyeh
 * @since 5.0.0
 * @see org.zkoss.zk.device.Device
 */
public class SimpleMarshaller implements Marshaller {
	/** Marshals an object to a String that the client can recognize.
	 */
	public String marshal(Object o) {
		if (o == null) return "n";
		if (o instanceof String)
			return 's' + (String)o;
		if (o instanceof Integer)
			return "I" + o;
		if (o instanceof Boolean)
			return ((Boolean)o).booleanValue() ? "3": "2";
		if (o instanceof $primitive) {
			if (o instanceof $int)
				return marshal((($int)o).value);
			if (o instanceof $boolean)
				return marshal((($boolean)o).value);
			if (o instanceof $short)
				return marshal((($short)o).value);
			if (o instanceof $long)
				return marshal((($long)o).value);
			if (o instanceof $double)
				return marshal((($double)o).value);
			if (o instanceof $float)
				return marshal((($float)o).value);
			if (o instanceof $byte)
				return marshal((($byte)o).value);
			if (o instanceof $char)
				return marshal((($char)o).value);
			throw new InternalError("Unknow primitive "+o.getClass());
		}
		if (o instanceof Date)
			return "t" + ((Date)o).getTime();
		if (o instanceof BigDecimal)
			return "K" + o;
		if (o instanceof BigInteger)
			return "J" + o;
		if (o instanceof Character)
			return "C" + o;
		if (o instanceof Long)
			return "L" + o;
		if (o instanceof Short)
			return "H" + o;
		if (o instanceof Byte)
			return "B" + o;
		if (o instanceof Float)
			return "F" + o;
		if (o instanceof Double)
			return "D" + o;
		if (o instanceof Object[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final Object[] ary = (Object[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				Strings.escape(sb, marshal(ary[j]), "\\,");
			}
			return sb.toString();
		}
		if (o instanceof int[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final int[] ary = (int[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				sb.append(marshal(ary[j]));
			}
			return sb.toString();
		}
		if (o instanceof boolean[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final boolean[] ary = (boolean[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				sb.append(marshal(ary[j]));
			}
			return sb.toString();
		}
		if (o instanceof long[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final long[] ary = (long[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				sb.append(marshal(ary[j]));
			}
			return sb.toString();
		}
		if (o instanceof short[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final short[] ary = (short[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				sb.append(marshal(ary[j]));
			}
			return sb.toString();
		}
		if (o instanceof byte[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final byte[] ary = (byte[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				sb.append(marshal(ary[j]));
			}
			return sb.toString();
		}
		if (o instanceof char[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final char[] ary = (char[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				Strings.escape(sb, marshal(ary[j]), "\\,");
			}
			return sb.toString();
		}
		if (o instanceof float[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final float[] ary = (float[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				sb.append(marshal(ary[j]));
			}
			return sb.toString();
		}
		if (o instanceof double[]) {
			final StringBuffer sb = new StringBuffer().append('[');
			final double[] ary = (double[])o;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) sb.append(',');
				sb.append(marshal(ary[j]));
			}
			return sb.toString();
		}
		throw new UnsupportedOperationException("Unknown "+o.getClass());
	}
	/** Marshals a boolean to a String that the client can recognize.
	 */
	public String marshal(boolean v) {
		return v ? "1": "0";
	}
	/** Marshals an integer to a String that the client can recognize.
	 */
	public String marshal(int v) {
		return "i" + v;
	}
	/** Marshals a long to a String that the client can recognize.
	 */
	public String marshal(long v) {
		return "l" + v;
	}
	/** Marshals a short to a String that the client can recognize.
	 */
	public String marshal(short v) {
		return "h" + v;
	}
	/** Marshals a byte to a String that the client can recognize.
	 */
	public String marshal(byte v) {
		return "b" + v;
	}
	/** Marshals a character to a String that the client can recognize.
	 */
	public String marshal(char v) {
		return "c" + v;
	}
	/** Marshals a double to a String that the client can recognize.
	 */
	public String marshal(double v) {
		return "d" + v;
	}
	/** Marshals a float to a String that the client can recognize.
	 */
	public String marshal(float v) {
		return "f" + v;
	}
}
