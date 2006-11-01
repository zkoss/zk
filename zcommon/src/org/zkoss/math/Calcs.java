/* Calcs.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep 25 14:34:48     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.math;

import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;

import org.zkoss.mesg.MCommon;
import org.zkoss.lang.D;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.math.BigDecimals;
import org.zkoss.math.BigIntegers;
import org.zkoss.text.DateFormats;

/**
 * Calculation utilities.
 *
 * @author tomyeh
 */
public class Calcs {
	protected Calcs() {}

	/** Test whether an object is true. A numeric object is true, if it is
	 * not zero (be careful with Double). A vector is true, if it
	 * contains at least one non-zero item.
	 *
	 * <p>If the object is a string, true is returned if the string
	 * is not null and not empty.
	 * Note: Boolean.valueOf() tests whether a string is "true".
	 */
	public static boolean booleanValueOf(Object obj) {
		if (obj == null || obj == Objects.UNKNOWN)
			return false;

		if (obj instanceof String)
			return ((String)obj).length() > 0;
		if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue();
		if (obj instanceof Integer)
			return ((Integer)obj).intValue() != 0;
		if (obj instanceof Long)
			return ((Long)obj).longValue() != 0;
		if (obj instanceof BigDecimal)
			return ((BigDecimal)obj).compareTo(BigDecimals.ZERO) != 0;
		if (obj instanceof Double) {
			final double d = ((Double)obj).doubleValue();
			return d != 0.0 && d != Double.NaN;
		}

		if (obj instanceof Byte)
			return ((Byte)obj).byteValue() != 0;
		if (obj instanceof Short)
			return ((Short)obj).shortValue() != 0;
		if (obj instanceof Character)
			return ((Character)obj).charValue() != (char)0;
		if (obj instanceof Float) {
			final float f = ((Float)obj).floatValue();
			return f != 0.0F && f != Float.NaN;
		}
		if (obj instanceof BigInteger)
			return !((BigInteger)obj).equals(BigInteger.ZERO);

		return true; //non-null object is true
	}

	/**
	 * Converts an object to an integer,
	 *
	 * @exception NumberFormatException if failed to convert to a number or date
	 */
	public static final int intValueOf(Object obj) {
		Object val = valueOf(obj);
		if (val instanceof Integer) {
			return ((Integer)val).intValue();
		} else if (val instanceof BigDecimal) {
			return ((BigDecimal)val).intValue();
		} else if (val instanceof Long) {
			return ((Long)val).intValue();
		} else if (val instanceof Double) {
			return ((Double)val).intValue();
		} else { //Date
			return (int)((Date)val).getTime();
		}
	}
	/**
	 * Converts an object to an long integer,
	 *
	 * @exception NumberFormatException if failed to convert to a number or date
	 */
	public static final long longValueOf(Object obj) {
		Object val = valueOf(obj);
		if (val instanceof Integer) {
			return ((Integer)val).longValue();
		} else if (val instanceof BigDecimal) {
			return ((BigDecimal)val).longValue();
		} else if (val instanceof Long) {
			return ((Long)val).longValue();
		} else if (val instanceof Double) {
			return ((Double)val).longValue();
		} else { //Date
			return (long)((Date)val).getTime();
		}
	}

	/** Tests whether obj is a numeric object.
	 *
	 * @param extend whether to consider Date, Character and Boolean as
	 * a numeric object.
	 * @link org.zkoss.lang.Classes#isNumeric
	 */
/*	public static boolean isNumeric(Object obj, boolean extend) {
		if (obj == null)
			return false;

		if (obj instanceof Number)
			return true;

		return extend && 
			((obj instanceof Date) || (obj instanceof Boolean)
			|| (obj instanceof Character));
	}*/
	/** Tests whether a string is numerable.
	 */
/*	public static final boolean isNumerable(String s) {
		try {
			valueOf(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}*/

	/** Converts an object to a number, either Integer, Long,
	 * Double, BigDecimal, or Date.
	 * <p>Note: Date is considered as a number, so you can do arithmetic on it.
	 *
	 * <p>Accepts 0x, #...
	 *
	 * <p>If the object is null, Integer(0) is returned.
	 * If the object is Boolean, Byte, Character, or Short,
	 * it will be promoted to Integer.
	 * If it is not a number object, it is converted to a string first.
	 *
	 * @return Integer, Long, Double, BigDecimal or Date that best matched
	 */
	public static Object valueOf(Object obj) {
		if (obj == null || obj == Objects.UNKNOWN)
			return Objects.ZERO_INTEGER;

		if ((obj instanceof Integer) || (obj instanceof BigDecimal)
		|| (obj instanceof Long) || (obj instanceof Date)
		|| (obj instanceof Double))
			return obj;

		if (obj instanceof Byte)
			return new Integer(((Byte)obj).intValue());
		if (obj instanceof Short)
			return new Integer(((Short)obj).intValue());
		if (obj instanceof Character)
			return new Integer(((Character)obj).charValue());
		if (obj instanceof Float)
			return new Double(((Float)obj).doubleValue());
		if (obj instanceof BigInteger) {
			BigInteger v = (BigInteger)obj;
			if (v.bitCount() <= 32)
				return new Integer(v.intValue());
			if (v.bitCount() <= 64)
				return new Long(v.longValue());
			throw new UnsupportedOperationException("Not support: BigInteger's bit count > 64, " + v);
		}
		if (obj instanceof Boolean)
			return new Integer(((Boolean)obj).booleanValue() ? 1: 0);

		return valueOf(obj.toString());
	}

	/**
	 * Converts a string to a number, either Integer, Long, Double,
	 * Bigdecimal, or Date.
	 *
	 * <p>Accepts 0x, #...
	 *
	 * <p>Implementation Note:<br>
	 * Integer.decode cannot handle '+', ' ' and consecutive '-' correctly,
	 * so we have to simplify the format first.
	 *
	 * @exception NumberFormatException if failed to convert to a number or date
	 */
	public static final Object valueOf(String s) {
		if (s == null)
			return Objects.ZERO_INTEGER;

		boolean dot = false, exp = false;
		if (!inHexFormat(s)) {
			int k = 0;
			char[] buf = new char[s.length()];
			for (int j = 0; j < buf.length; ++j) {
				char cc = s.charAt(j);
				switch (cc) {
				case '-':
					if (k > 0 && buf[k - 1] == '-') {
						--k; //remove consecutive -
						continue; //skip
					}
					break;
				case ' ':
				case '+':
					continue; //skip
				case '.':
					if (dot || exp)
						return toDate(s); //not a number
					dot = true;
					break;
				case 'e': case 'E':
					if (exp)
						return toDate(s); //not a number
					exp = true;
					break;
				default:
					if (cc < '0' || cc > '9')
						return toDate(s); //not a number
					break;
				}
				buf[k++] = cc; //correct, store it
			}

			if (k == 0)
				return Objects.ZERO_INTEGER;
			if (k < buf.length)
				s = new String(buf, 0, k);
		}

		if (exp) {
			return Double.valueOf(s);
		} else if (dot) {
			return new BigDecimal(s);
		} else {
			try {
				return Integer.decode(s);
			} catch (NumberFormatException ex) { //overflow? or format?
			}
			return Long.decode(s);
		}
	}
	private static final boolean inHexFormat(String s) {
		int b = Strings.skipWhitespaces(s, 0);
		int e = Strings.skipWhitespacesBackward(s, Integer.MAX_VALUE);
		if (b >= e) //less than two characters
			return false;

		char cc = s.charAt(e);
		if (cc < '0' || cc > '9') //the last one must be a digit
			return false;

		cc = s.charAt(b);
		if (cc == '#')
			return true;
		if (cc != '0')
			return false;
		cc = s.charAt(b + 1);
		return cc == 'x' || cc == 'X';
	}
	private static final Date toDate(String s) {
		try {
			return DateFormats.parse(s.trim());
		} catch (ParseException ex) {
			throw new NumberFormatException(ex.getMessage());
		}
	}

	/** Zero init an object.
	 *
	 * <p>It accepts Boolean, Byte, Character, Short, Integer,
	 * Long, Float, Double, BigDecimal, and String.
	 * All other types will be converted to String first.
	 * Then, converted to either Integer, Long, Double or BigDecimal,
	 * depending its format.
	 *
	 * <p>The returned result is either Integer, Long, Double or BigDecimal.
	 */
	public static final Object zero(Object obj) {
		if (obj == null || obj == Objects.UNKNOWN)
			return Objects.ZERO_INTEGER;

		obj = valueOf(obj);
		if (obj instanceof Integer)
			return Objects.ZERO_INTEGER;
		else if (obj instanceof Long)
			return Objects.ZERO_LONG;
		else if (obj instanceof Double)
			return Objects.ZERO_DOUBLE;
		else if (obj instanceof Date)
			return Objects.ZERO_LONG;
		else //BigDecimal
			return Objects.ZERO_BIG_DECIMAL;
	}

	/** Promotes an object, obj, by referencing another one, ref.
	 *
	 * <p>NOTE: you must call valueOf before calling this method, because
	 * it examines only possible classes that might be returned by valueOf.
	 *
	 * <p>Note: If ref is Date but obj is not, obj is promoted to Long
	 * instead of Date.
	 */
/*	private static final Object promote(Object obj, Object ref) {
		if (obj instanceof Integer) {
			//int + bigdec => bigdec
			//int + double => double
			//int + long => long
			//int + date => long
			if (ref instanceof BigDecimal)
				return BigDecimals.toBigDecimal(((Integer)obj).intValue());
			else if (ref instanceof Double)
				return new Double(((Integer)obj).intValue());
			else if ((ref instanceof Long) || (ref instanceof Date))
				return new Long(((Integer)obj).intValue());
			return obj;
		} else if (obj instanceof Long) {
			//long + bigdec => bigdec
			//long + double => double
			if (ref instanceof BigDecimal)
				return BigDecimals.toBigDecimal(((Long)obj).longValue());
			else if (ref instanceof Double)
				return new Double(((Long)obj).longValue());
			return obj;
		} else if (obj instanceof BigDecimal) {
			//bigdec + double => double
			if (ref instanceof Double)
				return new Double(((BigDecimal)obj).doubleValue());
			return obj;
		} else if (obj instanceof Double) {
			//double + xxx => double
			return obj;
		} else { //Date
			assert D.OFF || (obj instanceof Date): "valueOf must be called before promote: "+obj;
			//date + int => long
			//date + long => long
			//date + bigdec => bigdec
			//date + double => double
			if ((ref instanceof Integer) || (ref instanceof Long))
				return new Long(((Date)obj).getTime());
			else if (ref instanceof BigDecimal)
				return BigDecimals.toBigDecimal(((Date)obj).getTime());
			else if (ref instanceof Double)
				return new Double(((Date)obj).getTime());
			return obj;
		}
	}*/

	/** Negates an object.
	 *
	 * <p>It accepts Boolean, Byte, Character, Short, Integer,
	 * Long, Float, Double, BigDecimal, and String.
	 * All other types will be converted to String first.
	 * Then, converted to either Integer, Long, Double or BigDecimal,
	 * depending its format.
	 *
	 * <p>The returned result is either Integer, Long, Double or BigDecimal.
	 */
/*	public static final Object negate(Object obj) {
		if (obj == null || obj == Objects.UNKNOWN)
			return Objects.ZERO_INTEGER;

		obj = valueOf(obj);
		if (obj instanceof Integer)
			return new Integer(- ((Integer)obj).intValue());
		else if (obj instanceof Long)
			return new Long(- ((Long)obj).longValue());
		else if (obj instanceof Double)
			return new Double(- ((Double)obj).doubleValue());
		else if (obj instanceof Date)
			return new Long(- ((Date)obj).getTime());
		else //BigDecimal
			return ((BigDecimal)obj).negate();
	}*/

	/** Plus two objects. If one of them is not an numeric object,
	 * {@link #catenate} is called instead.
	 *
	 * <p>When adding a big decimal with a double, the scale will be
	 * that of big decimal -- not maximal of them.
	 *
	 * <p>Note: Date - Long is Date, Date - Date is Long, Long + Date is Long.
	 *
	 * <p>The returned result is either Integer, Long, Double or BigDecimal.
	 */
/*	public static final Object plus(Object o1, Object o2) {
		return plus0(valueOf(o1), valueOf(o2));
	}*/
	/** Plus two objects. If one of them is not an numeric object,
	 * {@link #catenate} is called instead.
	 *
	 * <p>When adding a big decimal with a double, the scale will be
	 * that of big decimal -- not maximal of them.
	 *
	 * <p>Note: Date - Long is Date, Date - Date is Long, Long + Date is Long.
	 *
	 * <p>The returned result is either Integer, Long, Double or BigDecimal.
	 */
/*	public static final Object plusOrCatenate(Object o1, Object o2) {
		if ((o1 instanceof String) || (o2 instanceof String))
			return catenate(o1, o2);

		try {
			final Object temp = valueOf(o1);
			o2 = valueOf(o2);
			o1 = temp;
		} catch (NumberFormatException ex) {
			return catenate(o1, o2);
		}
		return plus0(o1, o2);
	}*/
	/** A common utility of plus. */
/*	private static final Object plus0(Object o1, Object o2) {
		o1 = promote(o1, o2);
		o2 = promote(o2, o1);

		if (o1 instanceof Integer) {
			return new Integer(
				((Integer)o1).intValue() + ((Integer)o2).intValue());
		} else if (o1 instanceof Long) {
			if (o2 instanceof Date)
				return new Long(
					((Long)o1).longValue() + ((Date)o2).getTime());
			else
				return new Long(
					((Long)o1).longValue() + ((Long)o2).longValue());
		} else if (o1 instanceof BigDecimal) {
			return ((BigDecimal)o1).add((BigDecimal)o2);
		} else if (o1 instanceof Double) {
			return new Double(
				((Double)o1).doubleValue() + ((Double)o2).doubleValue());
		} else {//Date
			if (o2 instanceof Date) //Date + Date
				return new Long(((Date)o1).getTime() + ((Date)o2).getTime());
			else
				return new Date(((Date)o1).getTime() + ((Long)o2).longValue());
		}
	}*/

	/** Subtracts two objects.
	 *
	 * <p>It accepts Boolean, Byte, Character, Short, Integer,
	 * Long, Float, Double, BigDecimal, and String.
	 * All other types will be converted to String first.
	 * Then, converted to either Integer, Long, Double or BigDecimal,
	 * depending its format.
	 *
	 * <p>Note: Date - Long is Date, Date - Date is Long, Long - Date is Long.
	 *
	 * <p>The returned result is either Integer, Long, Double or BigDecimal.
	 */
/*	public static final Object minus(Object o1, Object o2) {
		o1 = valueOf(o1);
		o2 = valueOf(o2);
		o1 = promote(o1, o2);
		o2 = promote(o2, o1);

		if (o1 instanceof Integer) {
			return new Integer(
				((Integer)o1).intValue() - ((Integer)o2).intValue());
		} else if (o1 instanceof Long) {
			if (o2 instanceof Date)
				return new Long(
					((Long)o1).longValue() - ((Date)o2).getTime());
			else
				return new Long(
					((Long)o1).longValue() - ((Long)o2).longValue());
		} else if (o1 instanceof BigDecimal) {
			return ((BigDecimal)o1).subtract((BigDecimal)o2);
		} else if (o1 instanceof Double) {
			return new Double(
				((Double)o1).doubleValue() - ((Double)o2).doubleValue());
		} else {//Date
			if (o2 instanceof Date) //Date - Date
				return new Long(((Date)o1).getTime() - ((Date)o2).getTime());
			else
				return new Date(((Date)o1).getTime() - ((Long)o2).longValue());
		}
	}*/

	/** Multiplies two objects.
	 *
	 * <p>It accepts Boolean, Byte, Character, Short, Integer,
	 * Long, Float, Double, BigDecimal, and String.
	 * All other types will be converted to String first.
	 * Then, converted to either Integer, Long, Double or BigDecimal,
	 * depending its format.
	 *
	 * <p>The returned result is either Integer, Long, Double or BigDecimal.
	 *
	 * <p>Note: Date * Long, Date * Date, Long * Date are all Long.
	 */
/*	public static final Object times(Object o1, Object o2) {
		o1 = valueOf(o1);
		o2 = valueOf(o2);
		o1 = promote(o1, o2);
		o2 = promote(o2, o1);

		if (o1 instanceof Integer) {
			return new Integer(
				((Integer)o1).intValue() * ((Integer)o2).intValue());
		} else if (o1 instanceof Long) {
			if (o2 instanceof Date)
				return new Long(
					((Long)o1).longValue() * ((Date)o2).getTime());
			else
				return new Long(
					((Long)o1).longValue() * ((Long)o2).longValue());
		} else if (o1 instanceof BigDecimal) {
			return ((BigDecimal)o1).multiply((BigDecimal)o2);
		} else if (o1 instanceof Double) {
			return new Double(
				((Double)o1).doubleValue() * ((Double)o2).doubleValue());
		} else {//Date
			if (o2 instanceof Date) //Date * Date
				return new Long(((Date)o1).getTime() * ((Date)o2).getTime());
			else
				return new Long(((Date)o1).getTime() * ((Long)o2).longValue());
		}
	}*/

	/** Divides two objects.
	 *
	 * <p>It accepts Boolean, Byte, Character, Short, Integer,
	 * Long, Float, Double, BigDecimal, and String.
	 * All other types will be converted to String first.
	 * Then, converted to either Integer, Long, Double or BigDecimal,
	 * depending its format.
	 *
	 * <p>Note: Date * Long, Date * Date, Long * Date are all Long.
	 *
	 * @param roundingMode the rounding mode; used only if at least one
	 * operand is BigDecimal
	 */
/*	public static final Object divide(Object o1, Object o2, int roundingMode) {
		o1 = valueOf(o1);
		o2 = valueOf(o2);
		o1 = promote(o1, o2);
		o2 = promote(o2, o1);

		if (o1 instanceof Integer) {
			return new Integer(
				((Integer)o1).intValue() / ((Integer)o2).intValue());
		} else if (o1 instanceof Long) {
			if (o2 instanceof Date)
				return new Long(
					((Long)o1).longValue() / ((Date)o2).getTime());
			else
				return new Long(
					((Long)o1).longValue() / ((Long)o2).longValue());
		} else if (o1 instanceof BigDecimal) {
			return ((BigDecimal)o1).divide((BigDecimal)o2, BigDecimals.FINE_NUMBER_SCALE, roundingMode);
		} else if (o1 instanceof Double) {
			return new Double(
				((Double)o1).doubleValue() / ((Double)o2).doubleValue());
		} else {//Date
			if (o2 instanceof Date) //Date / Date
				return new Long(((Date)o1).getTime() / ((Date)o2).getTime());
			else
				return new Long(((Date)o1).getTime() / ((Long)o2).longValue());
		}
	}*/
	/** Divides two objects with the default rounding mode,
	 * {@link BigDecimals#getRoundingMode}.
	 */
/*	public static final Object divide(Object o1, Object o2) {
		return divide(o1, o2, BigDecimals.getRoundingMode());
	}*/

	/** Remainder (modulus) two objects.
	 *
	 * <p>It accepts Boolean, Byte, Character, Short, Integer,
	 * Long, Float, Double, BigDecimal, and String.
	 * All other types will be converted to String first.
	 * Then, converted to either Integer, Long, Double or BigDecimal,
	 * depending its format.
	 *
	 * <p>Note: Date % Long, Date % Date, Long % Date, Double % Double are
	 * all Integer.
	 */
/*	public static final Object remainder(Object o1, Object o2) {
		o1 = valueOf(o1);
		o2 = valueOf(o2);
		o1 = promote(o1, o2);
		o2 = promote(o2, o1);

		if (o1 instanceof Integer) {
			return new Integer(
				((Integer)o1).intValue() % ((Integer)o2).intValue());
		} else if (o1 instanceof Long) {
			if (o2 instanceof Date)
				return new Long(
					((Long)o1).longValue() % ((Date)o2).getTime());
			else
				return new Long(
					((Long)o1).longValue() % ((Long)o2).longValue());
		} else if (o1 instanceof BigDecimal) {
			return ((BigDecimal)o1).toBigInteger()
				.remainder(((BigDecimal)o2).toBigInteger());
		} else if (o1 instanceof Double) {
			return new Integer(
				(int)(((Double)o1).longValue() % ((Double)o2).longValue()));
		} else {//Date
			if (o2 instanceof Date) //Date % Date
				return new Integer(
					(int)(((Date)o1).getTime() % ((Date)o2).getTime()));
			else
				return new Integer(
					(int)(((Date)o1).getTime() % ((Long)o2).longValue()));
		}
	}*/

/*	private static final int compare(long o1, long o2) {
		return o1 > o2 ? 1: o1 == o2 ? 0: -1;
	}*/
	/** Compares two objects.
	 *
	 * <ol>
	 * <li>If both are the same class implementing Comparable,
	 * Comparable.compareTo() is used. Example, String and Date.</li>
	 * <li>Otherwise, if they are both Number, they are 'promoted' to the same
	 * class and compare them.</li>
	 * <li>If one is null and the other is a number, null is considered as 0.</li>
	 * <li>If one is null and the other is a string, null is considered as "".</li>
	 * <li>If both null, 0 is returned.</li>
	 * <li>If one of them is number, the other is tried to convert to a number.
	 * If failed to convert, an exception is thrown.</li>
	 * <li>If neither a number nor the same class, an exception is thrown.</li>
	 * </ol>
	 *
	 * <p>Note: {@link #equals}: null != 0 and null != "", while
	 * null >= 0.
	 *
	 * <p>Reason to  promotes null to 0 for compare with a number:
	 * it is more like arithmetic operator than others.
	 *
	 * @param ignoreCase true to ignore case when one or two operands
	 * are string
	 * @return 1 if o1>o2; 0 if o1=o2; -1 if o1<o2
	 */
/*	public static final int compare(Object o1, Object o2, boolean ignoreCase) {
		if (o1 == Objects.UNKNOWN) o1 = null;
		if (o2 == Objects.UNKNOWN) o2 = null;
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null) o1 = nullForCompare(o2);
		if (o2 == null) o2 = nullForCompare(o1);

		//first, use Comaparable if same class (including String, Number)
		if ((o1 instanceof Comparable) && o2 != null
		&& o1.getClass().equals(o2.getClass())) {
			if (ignoreCase && (o1 instanceof String))
				return ((String)o1).compareToIgnoreCase((String)o2);
			return ((Comparable)o1).compareTo(o2);
		}

		//now: o1.class != o2.class or o1 is not comparable
		if (!(o1 instanceof Number) && !(o2 instanceof Number))
			throw new ClassCastException("Unable to compare two different objects (unless one of them is number): o1="+o1+" o2="+o2);

		o1 = valueOf(o1);
		o2 = valueOf(o2);
		o1 = promote(o1, o2);
		o2 = promote(o2, o1);
		//assert D.OFF || o1.getClass().equals(o2.getClass());
		return ((Comparable)o1).compareTo(o2);
	}*/
	/** Returns the object representing null based on the specified object. */
/*	private static Object nullForCompare(Object ref) {
		if (ref instanceof Number)
			return new Integer(0);
		if (ref instanceof String)
			return "";
		return null;
	}*/
	/** Compares two objects. A shortcut of compare(o1, o2, false).
	 */
/*	public static final int compare(Object o1, Object o2) {
		return compare(o1, o2, false);
	}*/
	/** Compares two objects, igoring case if both of them are string.
	 * A shortcut of compare(o1, o2, true).
	 */
/*	public static final int compareIgnoreCase(Object o1, Object o2) {
		return compare(o1, o2, true);
	}*/
	/** Tests whether two objects equal.
	 *
	 * <ol>
	 * <li>Object.equals is used first.</li>
	 * <li>If false but they are both Number, they are 'promoted' to the same
	 * class and compare again.</li>
	 * <li>If one of them is null, false is returned.<br>
	 * Note: unlike compare(), 0 != null and "" != null.</li>
	 * <li>If one of them is number, the other is tried to convert to a number.
	 * If failed to convert, false is returned.</li>
	 * </ol>
	 * @see #compare(Object,Object)
	 */
/*	public static final boolean equals(Object o1, Object o2) {
		if (o1 == Objects.UNKNOWN) o1 = null;
		if (o2 == Objects.UNKNOWN) o2 = null;

		if (Objects.equals(o1, o2))
			return true;
		if (o1 == null || o2 == null)
			return false;
		if (!(o1 instanceof Number) && !(o2 instanceof Number))
			return false;

		try {
			o1 = valueOf(o1);
			o2 = valueOf(o2);
			o1 = promote(o1, o2);
			o2 = promote(o2, o1);
			assert D.OFF || o1.getClass().equals(o2.getClass());
			return Objects.equals(o1, o2);
			//don't use o1.equals(o2) because Objects.equals handles BigDecimal
		} catch (NumberFormatException ex) { //ignore it
			return false;
		}
	}*/

	/** Catenate two objects into a single string.
	 *
	 * <p>Since StringBuffer is not used, the performance might not be
	 * good enough.
	 */
/*	public static final String catenate(Object o1, Object o2) {
		if (o1 == Objects.UNKNOWN) o1 = null;
		if (o2 == Objects.UNKNOWN) o2 = null;

		//Unlike Javascript, null is converted to empty string instead of "null"
		//Reason: it is more readable

		String s1 = Objects.toString(o1);
		if (s1 == null) s1 = "";
		String s2 = Objects.toString(o2);
		if (s2 == null) s2 = "";

		if (s1.length() == 0)
			return s2;
		if (s2.length() == 0)
			return s1;
		return s1 + s2;
	}*/
}
