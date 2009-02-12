/*	Primitives.java

{{IS_NOTE

	Purpose:
	Description:
	History:
		2002/3/25, Henri Chen: Created.
		2003/4/17, Tom M. Yeh: Moves primitive relevant utilities from Classes

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.util.Map;
import java.util.HashMap;

/**
 * Utilities regarding primitive type and its wrapper class.
 *
 * @author henrichen
 */
public class Primitives {

	/**
	 * Convert Boolean object to primitive  boolean.
	 */
	public static final boolean toPrimitive(Boolean obj) {
		return obj.booleanValue();
	}

	/**
	 * Convert primitive boolean to Boolean.
	 */
	public static final Boolean toWrapper(boolean obj) {
		return Boolean.valueOf(obj);
	}
	/**
	 * Convert Byte object to primitive  byte.
	 */
	public static final byte toPrimitive(Byte obj) {
		return obj.byteValue();
	}

	/**
	 * Convert primitive byte to Byte.
	 */
	public static final Byte toWrapper(byte obj) {
		return new Byte(obj);
	}

	/**
	 * Convert Character object to primitive  char.
	 */
	public static final char toPrimitive(Character obj) {
		return obj.charValue();
	}

	/**
	 * Convert primitive char to Character.
	 */
	public static final Character toWrapper(char obj) {
		return new Character(obj);
	}

	/**
	 * Convert Double object to primitive  double.
	 */
	public static final double toPrimitive(Double obj) {
		return obj.doubleValue();
	}

	/**
	 * Convert primitive double to Double.
	 */
	public static final Double toWrapper(double obj) {
		return new Double(obj);
	}

	/**
	 * Convert Float object to primitive  float.
	 */
	public static final float toPrimitive(Float obj) {
		return obj.floatValue();
	}

	/**
	 * Convert primitive float to Float.
	 */
	public static final Float toWrapper(float obj) {
		return new Float(obj);
	}

	/**
	 * Convert Integer object to primitive  int.
	 */
	public static final int toPrimitive(Integer obj) {
		return obj.intValue();
	}

	/**
	 * Convert primitive int to Integer.
	 */
	public static final Integer toWrapper(int obj) {
		return new Integer(obj);
	}

	/**
	 * Convert Long object to primitive  long.
	 */
	public static final long toPrimitive(Long obj) {
		return obj.longValue();
	}

	/**
	 * Convert primitive long to Long.
	 */
	public static final Long toWrapper(long obj) {
		return new Long(obj);
	}

	/**
	 * Convert Short object to primitive  short.
	 */
	public static final short toPrimitive(Short obj) {
		return obj.shortValue();
	}

	/**
	 * Convert primitive short to Short.
	 */
	public static final Short toWrapper(short obj) {
		return new Short(obj);
	}
	
	/** The infomation about a primitive. */
	private static class PrimInfo {
		private final Class cls;
		private final Object defVal;
		private final char code;
		private PrimInfo(Class cls, Object defVal, char code) {
			this.cls = cls;
			this.defVal = defVal;
			this.code = code;
		}
	}
	private static final Map _prims = new HashMap(23);
	static {
		_prims.put("int",
			new PrimInfo(int.class, new Integer(0), 'I'));
		_prims.put("boolean",
			new PrimInfo(boolean.class, Boolean.FALSE, 'Z'));
		_prims.put("short",
			new PrimInfo(short.class, new Short((short)0), 'S'));
		_prims.put("byte",
			new PrimInfo(byte.class, new Byte((byte)0), 'B'));
		_prims.put("char",
			new PrimInfo(char.class, new Character((char)0), 'C'));
		_prims.put("long",
			new PrimInfo(long.class, new Long(0), 'L'));
		_prims.put("double",
			new PrimInfo(double.class, new Double(0), 'D'));
		_prims.put("float",
			new PrimInfo(float.class, new Float(0), 'F'));
		_prims.put("void",
			new PrimInfo(void.class, null, 'V'));

		//we can use the same map because key is in diff class
		_prims.put(Integer.class, int.class);
		_prims.put(Boolean.class, boolean.class);
		_prims.put(Short.class, short.class);
		_prims.put(Byte.class, byte.class);
		_prims.put(Character.class, char.class);
		_prims.put(Long.class, long.class);
		_prims.put(Double.class, double.class);
		_prims.put(Float.class, float.class);
		_prims.put(Void.class, void.class);
	};
	/** Returns the notation of a primitive class,
	 * or ((char)0) if it is not a primitive class.
	 * Example, I for int, Z for boolean...
	 */
	public static final char getNotation(String className) {
		final PrimInfo pi = (PrimInfo)_prims.get(className);
		return pi != null ? pi.code: (char)0;
	}
	/** Returns the default value of a primitive class,
	 * or null if it is not a primitive class.
	 * Example, getDefaultValue(int.class) returns Integer(0).
	 */
	public static final Object getDefaultValue(Class cls) {
		final PrimInfo pi = (PrimInfo)_prims.get(cls.getName());
		return pi != null ? pi.defVal: null;
	}
	/** Converts a primitive from name to the class,
	 * or null if it is not a primitive class.
	 * <p>Example, toClass("int") returns int.class.
	 */
	public static final Class toClass(String clsName) {
		final PrimInfo pi = (PrimInfo)_prims.get(clsName);
		return pi != null ? pi.cls: null;
	}
	/** Returns the primitive class of the giving wrapper class,
	 * or null if it is not a wrapper class.
	 * <p>Example, toPrimitive(Integer.class) returns int.class.
	 */
	public static final Class toPrimitive(Class wrapper) {
		return (Class)_prims.get(wrapper);
	}
	/** Returns the wrapper class of a primitive class,
	 * or null if it is not a primitive class.
	 * <p>Example, toWrapper(int.class) return Integer.class.
	 */
	public static final Class toWrapper(Class primitive) {
		if (!primitive.isPrimitive())
			return null;
		if (primitive.equals(void.class))
			return Void.class;
		return getDefaultValue(primitive).getClass();
	}
	/** Tests whether a class name is a primitive class, e.g., int and void.
	 */
	public static final boolean isPrimitive(String clsName) {
		return _prims.containsKey(clsName);
	}
}
