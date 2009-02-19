/* Objects.java

{{IS_NOTE

	Purpose: Utilities related to Object.
	Description:
	History:
	 2001/5/12, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Collection;
import java.lang.reflect.Method;
import java.rmi.MarshalledObject;

import org.zkoss.util.ArraysX;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.text.DateFormats;

/**
 * Utilities related to the Object class.
 *
 * @author tomyeh
 */
public class Objects {
	private static final Log log = Log.lookup(Objects.class);

	/** Denotes unknown. It is useful if both null and unknown is required.
	 */
	public static final Object UNKNOWN = new Object() { //anonymous class
			public final String toString() {
				return "(null)"; //a little different from null
			}
		};

	/** The zero long.
	 */
	public static final Long ZERO_LONG = new Long(0L);
	/** The zero integer.
	 */
	public static final Integer ZERO_INTEGER = new Integer(0);
	/** The zero short.
	 */
	public static final Short ZERO_SHORT = new Short((short)0);
	/** The zero integer.
	 */
	public static final Byte ZERO_BYTE = new Byte((byte)0);
	/** The zero float.
	 */
	public static final Float ZERO_FLOAT = new Float(0F);
	/** The zero double.
	 */
	public static final Double ZERO_DOUBLE = new Double(0D);
	/** Represents 0 in big decimal.
	 * The same as {@link org.zkoss.math.BigDecimals#ZERO}.
	 * @see org.zkoss.math.BigDecimals#ONE
	 */
	public static final BigDecimal ZERO_BIG_DECIMAL = new BigDecimal(0D);
	/** Represents 0 in big integer.
	 * Same as {@link org.zkoss.math.BigIntegers#ZERO}.
	 */
	public static final BigInteger ZERO_BIG_INTEGER = BigInteger.ZERO;
	/** The null character.
	 */
	public static final Character NULL_CHARACTER = new Character('\u0000');

	/** A separator char that is from the Unicode reserved area.
	 */
	public static final char SEPARATOR_CHAR = 0xf77a; //DON'T CHANGE IT (Or, db broke)
	/** The second separator char that is from the Unicode reserved area.
	 */
	public static final char SEPARATOR_CHAR2 = (char)(SEPARATOR_CHAR - 1); //DON'T CHANGE IT (Or, db broke)
	/** A separator char that is from the Unicode reserved area.
	 */
	public static final String SEPARATOR_STRING = "" + SEPARATOR_CHAR;

	/** The path seperator character that is used to construct a path.
	 * See org.zkoss.i3.ds.Path.
	 */
	public static final char PATH_SEPARATOR_CHAR = '/';
	/** The path seperator string that is used to construct a path.
	 * See org.zkoss.i3.ds.Path.
	 */
	public static final String PATH_SEPARATOR_STRING = "" + PATH_SEPARATOR_CHAR;

	/** The horizontal bar 0 (i.e., .........). */
	public static final String BAR0_STRING = "..\t\t..............................";
	/** The horizontal bar 1 (i.e., ---------). */
	public static final String BAR1_STRING = "--\t\t------------------------------";
	/** The horizontal bar 2 (i.e., =========). */
	public static final String BAR2_STRING = "==\t\t==============================";

	/**
	 * Returns the next hash value by giving the previous one and a new one.
	 * The caller usually uses a loop to accumulate all related fields.
	 *
	 * @param prevHashVal the previous hash value returned by this method; 0
	 * if it is the first call.
	 * @param newVal the new value to put in
	 * @return the new hash value
	 */
	public static final int nextHashCode(int prevHashVal, int newVal) {
		return prevHashVal*31 + newVal;
	}

	/**
	 * Generates hash codes for an array of boolean.
	 * It is suggested to cache the hash code.
	 *
	 * @param v the array
	 * @return the hash code
	 */
	public static final int hashCode(boolean[] v) {
		int h = 1; //not to return 0 if possible, so caller cache it easily
		for (int j=v.length; --j>=0;)
			h = nextHashCode(h, v[j] ? 1: 0);
		return h;
	}
	/**
	 * Generates hash codes for an array of bytes.
	 * It is suggested to cache the hash code.
	 *
	 * @param v the array
	 * @return the hash code
	 */
	public static final int hashCode(byte[] v) {
		int h = 1; //not to return 0 if possible, so caller cache it easily
		for (int j=v.length; --j>=0;)
			h = nextHashCode(h, v[j]);
		return h;
	}
	/** 
	 * Generates hash codes for an array of bytes up to the specified length.
	 * It is suggested to cache the hash code.
	 *
	 * @param v the array
	 * @param len the maximal length to generate hashCode
	 * @return the hash code
	 */
	public static final int hashCode(byte[] v, int len) {
		int h = 1; //not to return 0 if possible, so caller cache it easily
		if (len > v.length) len = v.length;
		for (int j=len; --j>=0;)
			h = nextHashCode(h, v[j]);
		return h;
	}
	/**
	 * Generates hash codes for an array.
	 * It is suggested to cache the hash code.
	 *
	 * @param v the array
	 * @return the hash code
	 */
	public static final int hashCode(char[] v) {
		int h = 1; //not to return 0 if possible, so caller cache it easily
		for (int j=v.length; --j>=0;)
			h = nextHashCode(h, v[j]);
		return h;
	}
	/**
	 * Generates hash codes for an array.
	 * It is suggested to cache the hash code.
	 *
	 * @param v the array
	 * @return the hash code
	 */
	public static final int hashCode(short[] v) {
		int h = 1; //not to return 0 if possible, so caller cache it easily
		for (int j=v.length; --j>=0;)
			h = nextHashCode(h, v[j]);
		return h;
	}
	/**
	 * Generates hash codes for an array.
	 * It is suggested to cache the hash code.
	 *
	 * @param v the byte array
	 * @return the hash code
	 */
	public static final int hashCode(int[] v) {
		int h = 1; //not to return 0 if possible, so caller cache it easily
		for (int j=v.length; --j>=0;)
			h = nextHashCode(h, v[j]);
		return h;
	}
	/**
	 * Generates hash codes for an array.
	 * It is suggested to cache the hash code.
	 *
	 * @param v the array
	 * @return the hash code
	 */
	public static final int hashCode(long[] v) {
		int h = 1; //not to return 0 if possible, so caller cache it easily
		for (int j=v.length; --j>=0;) {
			h = nextHashCode(h, (int)v[j]);
			h = nextHashCode(h, (int)(v[j]>>32));
		}
		return h;
	}
	/** Returns the object's hash code, or zero if null. */
	public static final int hashCode(Object o) {
		return o==null ? 0: o.hashCode();
	}
	/**
	 * Tests whether two objects are equals.
	 *
	 * <p>It takes care of the null case. Thus, it is helpful to implement
	 * Object.equals.
	 *
	 * <p>Notice: it uses compareTo if BigDecimal is found. So, in this case,
	 * a.equals(b) might not be the same as Objects.equals(a, b).
	 *
	 * <p>If both a and b are Object[], they are compared item-by-item.
	 */
	public static final boolean equals(Object a, Object b) {
		if (a == b || (a != null && b != null && a.equals(b)))
			return true;
		if ((a instanceof BigDecimal) && (b instanceof BigDecimal))
			return ((BigDecimal)a).compareTo((BigDecimal) b) == 0;

		if (a == null || !a.getClass().isArray())
			return false;

		if ((a instanceof Object[]) && (b instanceof Object[])) {
			final Object[] as = (Object[])a;
			final Object[] bs = (Object[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (!equals(as[j], bs[j])) //recursive
					return false;
			return true;
		}
		if ((a instanceof int[]) && (b instanceof int[])) {
			final int[] as = (int[])a;
			final int[] bs = (int[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof byte[]) && (b instanceof byte[])) {
			final byte[] as = (byte[])a;
			final byte[] bs = (byte[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof char[]) && (b instanceof char[])) {
			final char[] as = (char[])a;
			final char[] bs = (char[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof long[]) && (b instanceof long[])) {
			final long[] as = (long[])a;
			final long[] bs = (long[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof short[]) && (b instanceof short[])) {
			final short[] as = (short[])a;
			final short[] bs = (short[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof double[]) && (b instanceof double[])) {
			final double[] as = (double[])a;
			final double[] bs = (double[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof float[]) && (b instanceof float[])) {
			final float[] as = (float[])a;
			final float[] bs = (float[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof boolean[]) && (b instanceof boolean[])) {
			final boolean[] as = (boolean[])a;
			final boolean[] bs = (boolean[])b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >=0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		return false;
	}

	/**
	 * Converts any object to a character array.
	 *
	 * @param o the object to convert
	 * @return the char array or null if o is null
	 */
    public static final char[] toCharArray(Object o) {
    	if (o == null)
    		return null;
		if (o instanceof char[])
			return (char[])o;

		if (o instanceof String)
			return ((String)o).toCharArray();

		try {
			Method m = o.getClass().getMethod("toCharArray", null);
			return (char[])m.invoke(o, null);
		} catch (Exception ex) {
			return (o.toString()).toCharArray();
		}
	}
	/**
	 * Converts any object to a string.
	 * If o is an object array, it invokes {@link ArraysX#toString}
	 * to make the string more readable.
	 */
	public static final String toString(Object o) {
    	if (o == null) return null;
		if (o instanceof Date) return DateFormats.format((Date)o, false);
		if (o instanceof Class) {
			final Class cls = (Class)o;
			final String clsnm = cls.getName();
			if (!clsnm.startsWith("$Proxy"))
				return "class "+clsnm;

			final Class[] ifs = cls.getInterfaces();
			switch (ifs.length) {
			case 0:
				return "class "+clsnm;
			case 1:
				return "proxy "+Objects.toString(ifs[0]);
			default:
				return "proxy "+Objects.toString(ifs);
			}
		}
		if (o.getClass().isArray()) {
			if (o instanceof Object[])
				return ArraysX.toString((Object[])o);
			if (o instanceof int[])
				return ArraysX.toString((int[])o);
			if (o instanceof short[])
				return ArraysX.toString((short[])o);
			if (o instanceof long[])
				return ArraysX.toString((long[])o);
			if (o instanceof double[])
				return ArraysX.toString((double[])o);
			if (o instanceof byte[])
				return ArraysX.toString((byte[])o);
			if (o instanceof boolean[])
				return ArraysX.toString((boolean[])o);
			if (o instanceof char[])
				return ArraysX.toString((char[])o);
			if (o instanceof float[])
				return ArraysX.toString((float[])o);
		}
		return o.toString();
 	}

	/** Converts an integer to a big-endian byte array.
	 */
	public static final byte[] toByteArray(int v) {
		return new byte[] {
			(byte)(v>>>24), (byte)(v>>>16), (byte)(v>>>8), (byte)v};
	}
	/** Converts a long to a big-endian byte array.
	 */
	public static final byte[] toByteArray(long v) {
		return new byte[] {
			(byte)(v>>>56), (byte)(v>>>48), (byte)(v>>>40), (byte)(v>>>32),
			(byte)(v>>>24), (byte)(v>>>16), (byte)(v>>>8),  (byte)v};
	}
	/** Converts a short to a big-endian byte array.
	 */
	public static final byte[] toByteArray(short v) {
		return new byte[] {(byte)(v>>>8), (byte)v};
	}
	/** Converts a byte to a big-endian byte array.
	 */
	public static final byte[] toByteArray(byte v) {
		return new byte[] {v};
	}

	/**
	 * Clones the specified object. Use clone() if Cloeable.
	 * Otherwise, try to serialize/deserialize it by use of MarshalledObject.
	 *
	 * <p>If o is null, null is returned.
	 *
	 * @exception SystemException if failed to clone
	 */
	public static final Object clone(Object o) {
		if (o == null)
			return o;

		try {
			final Class kls = o.getClass();
			if (kls.isArray())
				return ArraysX.clone(o);

			if (o instanceof Cloneable) {
				try {
					return kls.getMethod("clone", null).invoke(o, null);
				} catch (NoSuchMethodException ex) {
					if (D.ON) log.warning("No clone() for "+kls);
				}
			}

			//:TODO: MarshalledObject is said with very bad performance, change it
			//if exists other good deep clone method.
			return new MarshalledObject(o).get();
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
}
