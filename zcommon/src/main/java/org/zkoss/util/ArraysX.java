/*	ArraysX.java


	Purpose:
	Description:
	History:
		2001/11/13, Henri Chen: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for handling arrays.
 *
 * @author henrichen
 */
public class ArraysX {
	/** Converts an array to a readable string (for debugging purpose).
	 */
	public final static String toString(Object[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			if (array[j] == array)
				sb.append("(this array)");
			else
				sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of int to a readable string (for debugging purpose).
	 */
	public final static String toString(int[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of long to a readable string (for debugging purpose).
	 */
	public final static String toString(long[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of short to a readable string (for debugging purpose).
	 */
	public final static String toString(short[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of byte to a readable string (for debugging purpose).
	 */
	public final static String toString(byte[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of char to a readable string (for debugging purpose).
	 */
	public final static String toString(char[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of boolean to a readable string (for debugging purpose).
	 */
	public final static String toString(boolean[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of float to a readable string (for debugging purpose).
	 */
	public final static String toString(float[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/** Converts an array of char to a readable string (for debugging purpose).
	 */
	public final static String toString(double[] array) {
		if (array == null)
			return "null";

		StringBuffer sb = new StringBuffer(128).append('[');
		for (int j = 0; j < array.length; ++j) {
			sb.append(array[j]);
			if (j != array.length - 1)
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	/**
	 * Returns the hex String representation of a byte array without prefix 0x.
	 * The String is formed by making value[0] the leftmost two digits and
	 * value[value.length-1] the rightmost two digits.
	 *
	 * @param array the byte array
	 */
	public final static String toHexString(byte[] array) {
		StringBuffer sb = new StringBuffer(array.length*2 + 8);
		char ch;
		for (int i=0; i< array.length; i++) {
			// byte will be promote to integer first, mask with 0x0f is a must.
			ch = Character.forDigit(array[i] >>> 4 & 0x0f, 16);
			sb.append(ch);
			ch = Character.forDigit(array[i] & 0x0f, 16);
			sb.append(ch);
		}

		return sb.toString();
	}
	/**
	 * Returns the octal String representation of a byte array with optional
	 * prefix. The String is formed by making value[0] the leftmost three digits
	 * and value[value.length-1] the rightmost three digits.
	 *
	 * @param array the byte array
	 */
	public final static String toOctalString(byte[] array, String prefix) {
		StringBuffer sb = new StringBuffer(array.length*
				(3 + (prefix == null ? 0 : prefix.length())) + 8);
		if (prefix == null) {
			for (int i=0; i< array.length; i++) {
				appendOctalDigits(sb, array[i]);
			}
		} else {
			for (int i=0; i< array.length; i++) {
				sb.append(prefix);
				appendOctalDigits(sb, array[i]);
			}
		}
		return sb.toString();
	}
	/**
	 * Returns the octal digit String buffer representation of a byte.
	 * @param byte the byte
	 */
	private final static StringBuffer appendOctalDigits(StringBuffer sb, byte b) {
		// b will be promote to integer first, mask with 0x07 is a must.
		return sb.append(Character.forDigit(b >>> 6 & 0x07, 8))
			.append(Character.forDigit(b >>> 3 & 0x07, 8))
			.append(Character.forDigit(b & 0x07, 8));
	}

	/**
	 * Duplicates the specified array.
	 *
	 * <p>The array could be an array of objects or primitives.
	 *
	 * @param ary the array
	 * @param jb the beginning index (included)
	 * @param je the ending index (excluded)
	 * @return an array duplicated from ary
	 * @exception IllegalArgumentException if ary is not any array
	 * @exception IndexOutOfBoundsException if out of bounds
	 */
	public static final Object duplicate(Object ary, int jb, int je) {
		int len = Array.getLength(ary);
		if (jb<0 || je>len || jb>je)
			throw new IndexOutOfBoundsException(jb + " or " + je + " exceeds " + len);

		len = je - jb;
		Object dst = Array.newInstance(ary.getClass().getComponentType(), len);
		System.arraycopy(ary, jb, dst, 0, len);
		return dst;
	}
	/**
	 * Duplicates the specified generic array.
	 *
	 * <p>The array could be an array of objects or primitives.
	 *
	 * @param ary the array
	 * @param jb the beginning index (included)
	 * @param je the ending index (excluded)
	 * @return an array duplicated from ary
	 * @exception IllegalArgumentException if ary is not any array
	 * @exception IndexOutOfBoundsException if out of bounds
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T[] duplicate(T[] ary, int jb, int je) {
		return (T[])duplicate((Object)ary, jb, je);
	}
	/**
	 * Duplicates the specified array.
	 * @param ary the array
	 * @return an array duplicated from ary
	 * @exception IllegalArgumentException if ary is not any array
	 * @exception IndexOutOfBoundsException if out of bounds
	 */
	public static final Object duplicate(Object ary) {
		return duplicate(ary, 0, Array.getLength(ary));
	}
	/**
	 * Duplicates the specified generic array.
	 * @param ary the array
	 * @return an array duplicated from ary
	 * @exception IllegalArgumentException if ary is not any array
	 * @exception IndexOutOfBoundsException if out of bounds
	 * @since 6.0.0
	 */
	public static final <T> T[] duplicate(T[] ary) {
		return duplicate(ary, 0, ary.length);
	}

	/**
	 * Concatenates the two specified array.
	 *
	 * <p>The array could be an array of objects or primitives.
	 *
	 * @param ary the array
	 * @param ary1 the array
	 * @return an array concatenating the ary and ary1
	 * @exception IllegalArgumentException if ary and ary1 component types are not compatible
	 */
	public static final Object concat(Object ary, Object ary1) {
		int len = Array.getLength(ary) + Array.getLength(ary1);
		if (!ary.getClass().getComponentType().isAssignableFrom(ary1.getClass().getComponentType()))
			throw new IllegalArgumentException("These concated array component types are not compatible.");
		Object dst = Array.newInstance(ary.getClass().getComponentType(), len);
		
		System.arraycopy(ary, 0, dst, 0, Array.getLength(ary));
		System.arraycopy(ary1, 0, dst, Array.getLength(ary), Array.getLength(ary1));
		
		return dst;
	}
	/**
	 * Concatenates the two specified generic array.
	 *
	 * <p>The array could be an array of objects or primitives.
	 *
	 * @param ary the array
	 * @param ary1 the array
	 * @return an array concatenating the ary and ary1
	 * @exception IllegalArgumentException if ary and ary1 component types are not compatible
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T[] concat(T[] ary, T[] ary1) {
		return (T[])concat((Object)ary, (Object)ary1);
	}

	/**
	 * Shrink the specified array. It is similar to duplicate, except
	 * it returns the previous instance if je==length && jb==0.
	 *
	 * @param ary the array
	 * @param jb the beginning index (included)
	 * @param je the ending index (excluded)
	 * @return ary or an array duplicated from ary
	 * @exception IllegalArgumentException if ary is not any array
	 * @exception IndexOutOfBoundsException if out of bounds
	 */
	public static final Object shrink(Object ary, int jb, int je) {
		if (jb == 0 && je == Array.getLength(ary))
			return ary; //nothing changed
		return duplicate(ary, jb, je);
	}
	/**
	 * Shrink the specified array. It is similar to duplicate, except
	 * it returns the previous instance if je==length && jb==0.
	 *
	 * @param ary the array
	 * @param jb the beginning index (included)
	 * @param je the ending index (excluded)
	 * @return ary or an array duplicated from ary
	 * @exception IllegalArgumentException if ary is not any array
	 * @exception IndexOutOfBoundsException if out of bounds
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T[] shrink(T[] ary, int jb, int je) {
		return (T[])shrink((Object)ary, jb, je);
	}

	/**
	 * Returns a mutable list.
	 * <p> Never be null.
	 * @param ary the data array
	 * @return a list view of the specified array
	 * @since 6.0.1
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<T> asList(T[] ary) {
		if (ary == null)
			return Collections.EMPTY_LIST;
		List<T> list = new ArrayList<T>(ary.length);
		for (T t : ary)
			list.add(t);
		return list;
	}
	
	/**
	 * Returns a two dimensional mutable list.
	 * <p> Never be null.
	 * @param ary the two dimensional data array
	 * @return a two dimensional list view of the specified array
	 * @since 6.0.1
	 * @see #asList(Object[])
	 */
	@SuppressWarnings("unchecked")
	public static final <T> List<List<T>> asList(T[][] ary) {
		if (ary == null)
			return Collections.EMPTY_LIST;
		
		List<List<T>> list = new ArrayList<List<T>>(ary.length);
		for (T[] t : ary)
			list.add(asList(t));
		return list;
	}
	/**
	 * Resizes the specified array. Similar to {@link #shrink}, but
	 * it can enlarge and it keeps elements from the first.
	 */
	public static final Object resize(Object ary, int size) {
		final int oldsz = Array.getLength(ary);
		if (oldsz == size)
			return ary;

		final Object dst = Array.newInstance(ary.getClass().getComponentType(), size);
		System.arraycopy(ary, 0, dst, 0, oldsz > size ? size: oldsz);
		return dst;
	}
	/**
	 * Resizes the specified generic array.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T[] resize(T[] ary, int size) {
		return (T[])resize((Object)ary, size);
	}

	/**
	 * Returns a fixed-size boxed list backed by the specified primitive array.
	 *
	 * @param primitiveArray A primitive array object
	 * @return A List object
	 * @since 9.6.0
	 */
	public static List<Object> asBoxedList(final Object primitiveArray) {
		final int length = Array.getLength(primitiveArray);
		final Object[] boxed = new Object[length];
		Arrays.parallelSetAll(boxed, i -> Array.get(primitiveArray, i));
		return Arrays.asList(boxed);
	}

//meaningless to provide this method since the caller's casting to Class<T> causes
//an warning. But, if we use Class<?>, a compiler error is generated (T cannot be
//determined...)
//	@SuppressWarnings("unchecked")
//	public static final <T> T[] newInstance(Class<T> cls, int size) {
//		return (T[])Array.newInstance(cls, size);
//	}
}
