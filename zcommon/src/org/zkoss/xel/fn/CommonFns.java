/* CommonFns.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 20 18:35:21     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.fn;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.math.BigDecimal;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.mesg.Messages;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.util.resource.Labels;
import org.zkoss.util.logging.Log;
import org.zkoss.text.DateFormats;
import org.zkoss.text.MessageFormats;

/**
 * Common functions used with EL.
 *
 * @author tomyeh
 */
public class CommonFns {
	private static final Log log = Log.lookup(CommonFns.class);

	protected CommonFns() {}

	/** Converts the specified object to a boolean.
	 */
	public static boolean toBoolean(Object val) {
		return ((Boolean)Classes.coerce(boolean.class, val)).booleanValue();
	}
	/** Converts the specified object to a string.
	 */
	public static String toString(Object val) {
		return (String)Classes.coerce(String.class, val);
	}
	/** Converts the specified object to a number.
	 */
	public static Number toNumber(Object val) {
		return (Number)Classes.coerce(Number.class, val);
	}
	/** Converts the specified object to an integer.
	 */
	public static int toInt(Object val) {
		return ((Integer)Classes.coerce(int.class, val)).intValue();
	}
	/** Converts the specified object to a (big) decimal.
	 */
	public static BigDecimal toDecimal(Object val) {
		return (BigDecimal)Classes.coerce(BigDecimal.class, val);
	}
	/** Converts the specified object to an character.
	 */
	public static char toChar(Object val) {
		return ((Character)Classes.coerce(char.class, val)).charValue();
	}
	/** Tests whehter an object, o, is an instance of a class, c.
	 */
	public static boolean isInstance(Object c, Object o) {
		if (c instanceof Class) {
			return ((Class)c).isInstance(o);
		} else if (c instanceof String) {
			try {
				return Classes.forNameByThread((String)c).isInstance(o);
			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException("Class not found: "+c);
			}
		} else {
			throw new IllegalArgumentException("Unknown class: "+c);
		}
	}

	/** Returns the label or message of the specified key.
	 * <ul>
	 * <li>If key is "mesg:class:MMM", Messages.get(class.MMM) is called</li>
	 * <li>Otherwise, {@link Labels#getLabel(String)} is called.
	 * </ul>
	 * @see #getLabel(String, Object[])
	 */
	public static final String getLabel(String key) {
		if (key == null)
			return "";

		if (key.startsWith("mesg:")) {
			final int j = key.lastIndexOf(':');
			if (j > 5) {
				final String clsnm = key.substring(5, j);
				final String fldnm = key.substring(j + 1);
				try {
					final Class cls = Classes.forNameByThread(clsnm);
					final Field fld = cls.getField(fldnm);
					return Messages.get(((Integer)fld.get(null)).intValue());
				} catch (ClassNotFoundException ex) {
					log.warning("Class not found: "+clsnm, ex);
				} catch (NoSuchFieldException ex) {
					log.warning("Field not found: "+fldnm, ex);
				} catch (IllegalAccessException ex) {
					log.warning("Field not accessible: "+fldnm, ex);
				}
			} else if (log.debugable()) {
				log.debug("Not a valid format: "+key);
			}
		}
		return Labels.getLabel(key);
	}
	/** Returns the label of the specified key and formats it
	 * with the specified argument, or null if not found.
	 *
	 * <p>It first uses {@link #getLabel(String)} to load the label.
	 * Then, it, if not null, invokes {@link MessageFormats#format} to format it.
	 *
	 * <p>The current locale is given by {@link org.zkoss.util.Locales#getCurrent}.
	 * @since 3.0.6
	 */
	public static final String getLabel(String key, Object[] args) {
		final String s = getLabel(key);
		return s != null ? MessageFormats.format(s, args, null): null;
	}
	/** Returns the length of an array, string, collection or map.
	 */
	public static final int length(Object o) {
		if (o instanceof String) {
			return ((String)o).length();
		} else if (o == null) {
			return 0;
		} else if (o instanceof Collection) {
			return ((Collection)o).size();
		} else if (o instanceof Map) {
			return ((Map)o).size();
		} else if (o.getClass().isArray()) {
			return Array.getLength(o);
		} else {
			throw new IllegalArgumentException("Unknown object for length: "+o.getClass());
		}
	}

	/** Returns the index of the given element.
	 * @param o the array/collection of objects to examine, or a string.
	 * If o is a map, then {@link Map#keySet} is assumed.
	 * @since 5.0.7
	 */
	public static final int indexOf(Object o, Object element) {
		if (o instanceof String) {
			return element instanceof String ?
				((String)o).indexOf((String)element): -1;
		} else if (o instanceof Collection) {
			int j = 0;
			for (Iterator it = ((Collection)o).iterator(); it.hasNext(); ++j)
				if (Objects.equals(it.next(), element))
					return j;
		} else if (o instanceof Map) {
			return indexOf(((Map)o).keySet(), element);
		} else if (o instanceof Object[]) {
			final Object[] ary = (Object[])o;
			for (int j = 0; j < ary.length; j++)
				if (Objects.equals(ary[j], element))
					return j;
		} else if (o instanceof int[]) {
			if (element instanceof Number) {
				int v = ((Number)element).intValue();
				final int[] ary = (int[])o;
				for (int j = 0; j < ary.length; j++)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof long[]) {
			if (element instanceof Number) {
				long v = ((Number)element).longValue();
				final long[] ary = (long[])o;
				for (int j = 0; j < ary.length; j++)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof short[]) {
			if (element instanceof Number) {
				short v = ((Number)element).shortValue();
				final short[] ary = (short[])o;
				for (int j = 0; j < ary.length; j++)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof byte[]) {
			if (element instanceof Number) {
				byte v = ((Number)element).byteValue();
				final byte[] ary = (byte[])o;
				for (int j = 0; j < ary.length; j++)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof double[]) {
			if (element instanceof Number) {
				double v = ((Number)element).doubleValue();
				final double[] ary = (double[])o;
				for (int j = 0; j < ary.length; j++)
					if (Double.compare(ary[j], v) == 0)
						return j;
			}
		} else if (o instanceof float[]) {
			if (element instanceof Number) {
				float v = ((Number)element).floatValue();
				final float[] ary = (float[])o;
				for (int j = 0; j < ary.length; j++)
					if (Float.compare(ary[j], v) == 0)
						return j;
			}
		} else if (o instanceof char[]) {
			char v;
			if (element instanceof Character)
				v = ((Character)element).charValue();
			else if (element instanceof String && ((String)element).length() > 0)
				v = ((String)element).charAt(0);
			else
				return -1;

			final char[] ary = (char[])o;
			for (int j = 0; j < ary.length; j++)
				if (ary[j] == v)
					return j;
		} else if (o != null) {
			throw new IllegalArgumentException("Unknown object for indexOf: "+o.getClass());
		}
		return -1;
	}
	/** Returns the last index of the given element.
	 * @param o the array/list of objects to examine, or a string.
	 * @since 5.0.7
	 */
	public static final int lastIndexOf(Object o, Object element) {
		if (o instanceof String) {
			return element instanceof String ?
				((String)o).lastIndexOf((String)element): -1;
		} else if (o instanceof List) {
			int j = ((List)o).size();
			for (ListIterator it = ((List)o).listIterator(j); it.hasPrevious(); j--)
				if (Objects.equals(it.previous(), element))
					return j - 1;
		} else if (o instanceof Object[]) {
			final Object[] ary = (Object[])o;
			for (int j = ary.length; --j >= 0;)
				if (Objects.equals(ary[j], element))
					return j;
		} else if (o instanceof int[]) {
			if (element instanceof Number) {
				int v = ((Number)element).intValue();
				final int[] ary = (int[])o;
				for (int j = ary.length; --j >= 0;)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof long[]) {
			if (element instanceof Number) {
				long v = ((Number)element).longValue();
				final long[] ary = (long[])o;
				for (int j = ary.length; --j >= 0;)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof short[]) {
			if (element instanceof Number) {
				short v = ((Number)element).shortValue();
				final short[] ary = (short[])o;
				for (int j = ary.length; --j >= 0;)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof byte[]) {
			if (element instanceof Number) {
				byte v = ((Number)element).byteValue();
				final byte[] ary = (byte[])o;
				for (int j = ary.length; --j >= 0;)
					if (ary[j] == v)
						return j;
			}
		} else if (o instanceof double[]) {
			if (element instanceof Number) {
				double v = ((Number)element).doubleValue();
				final double[] ary = (double[])o;
				for (int j = ary.length; --j >= 0;)
					if (Double.compare(ary[j], v) == 0)
						return j;
			}
		} else if (o instanceof float[]) {
			if (element instanceof Number) {
				float v = ((Number)element).floatValue();
				final float[] ary = (float[])o;
				for (int j = ary.length; --j >= 0;)
					if (Float.compare(ary[j], v) == 0)
						return j;
			}
		} else if (o instanceof char[]) {
			char v;
			if (element instanceof Character)
				v = ((Character)element).charValue();
			else if (element instanceof String && ((String)element).length() > 0)
				v = ((String)element).charAt(0);
			else
				return -1;

			final char[] ary = (char[])o;
			for (int j = ary.length; --j >= 0;)
				if (ary[j] == v)
					return j;
		} else if (o != null) {
			throw new IllegalArgumentException("Unknown object for indexOf: "+o.getClass());
		}
		return -1;
	}

	/** Instantiates the specified class.
	 */
	public static final Object new_(Object o) throws Exception {
		if (o instanceof String) {
			return Classes.newInstanceByThread((String)o);
		} else if (o instanceof Class) {
			return ((Class)o).newInstance();
		} else {
			throw new IllegalArgumentException("Unknow object for new: "+o);
		}
	}
	/** Instantiates the specified class, and argument.
	 * @param o the class name or class
	 * @param arg the argument
	 * @since 5.0.5
	 */
	public static final Object new_(Object o, Object arg) throws Exception {
		if (o instanceof String) {
			return Classes.newInstance(Classes.forNameByThread((String)o), new Object[] {arg});
		} else if (o instanceof Class) {
			return Classes.newInstance((Class)o, new Object[] {arg});
		} else {
			throw new IllegalArgumentException("Unknow object for new: "+o);
		}
	}
	/** Instantiates the specified class, and two arguments.
	 * @param o the class name or class
	 * @param arg1 the first argument
	 * @param arg2 the second argument
	 * @since 5.0.5
	 */
	public static final Object new_(Object o, Object arg1, Object arg2) throws Exception {
		if (o instanceof String) {
			return Classes.newInstance(Classes.forNameByThread((String)o), new Object[] {arg1, arg2});
		} else if (o instanceof Class) {
			return Classes.newInstance((Class)o, new Object[] {arg1, arg2});
		} else {
			throw new IllegalArgumentException("Unknow object for new: "+o);
		}
	}
	/** Instantiates the specified class, and two arguments.
	 * @param o the class name or class
	 * @param arg1 the first argument
	 * @param arg2 the second argument
	 * @since 5.0.5
	 */
	public static final Object new_(Object o, Object arg1, Object arg2, Object arg3) throws Exception {
		if (o instanceof String) {
			return Classes.newInstance(Classes.forNameByThread((String)o), new Object[] {arg1, arg2, arg3});
		} else if (o instanceof Class) {
			return Classes.newInstance((Class)o, new Object[] {arg1, arg2, arg3});
		} else {
			throw new IllegalArgumentException("Unknow object for new: "+o);
		}
	}

	/**
	 * Formats a Date into a date/time string.
	 * @param date the time value to be formatted into a time string.
	 * @param pattern the pattern describing the date and time format
	 * @return the formatted time string.
	 * @since 6.0.0
	 */
	public static final String formatDate(Date date, String pattern) {
		return formatDate(date, pattern, null, null, null, null);
	}
	/**
	 * Parses text from the beginning of the given string to produce a date.
	 * The method may not use the entire text of the given string.
	 * @param source A <code>String</code> whose beginning should be parsed.
	 * @param pattern the pattern describing the date and time format
	 * @return A <code>Date</code> parsed from the string.
	 * @throws Exception
	 * @since 6.0.0
	 */
	public static final Date parseDate(String source, String pattern) throws Exception {
		return parseDate(source, pattern, null, null, null, null);
	}

	/**
	 * Formats a number (Integer, BigDecimal...) into a string.
	 * If null, an empty string is returned.
	 * <p>A utility to assist the handling of numeric data.
	 * @param value The number to format.
	 * @param format The pattern to apply, if it is null,
	 * the system's default format is used.
	 * @return the formatted number string.
	 * @since 6.0.1
	 */
	public static final String formatNumber(Object value, String format) {
		return formatNumber(value, format, null);
	}
	/**
	 * Parses text from the beginning of the given string to produce a number.
	 * The method may not use the entire text of the given string.
	 * @param source A <code>String</code> whose beginning should be parsed.
	 * @param pattern the pattern describing the date and time format
	 * @return A <code>Number</code> parsed from the string.
	 * @throws Exception
	 * @since 6.0.1
	 */
	public static final Number parseNumber (String source, String pattern) throws Exception {
		return parseNumber(source, pattern, null);
	}

	/**
	 * Formats a Date into a date/time string.
	 * @param date the time value to be formatted into a time string.
	 * @param pattern the pattern describing the date and time format
	 * @param locale The Locale to apply, if it is null,
	 * The current locale given by {@link org.zkoss.util.Locales#getCurrent} is used.
	 * @param timezone the time zone to apply, if it is null,
	 * The current timezone given by {@link org.zkoss.util.TimeZones#getCurrent} is used.
	 * @param dateStyle styling index of date.
	 * @param timeStyle styling index of time.
	 * @return the formatted time string.
	 * @since 6.0.0
	 */
	public static final String formatDate(Date date, String pattern, Locale locale, TimeZone timezone, String dateStyle, String timeStyle) {
		return getDateFormat(pattern, locale, timezone, dateStyle, timeStyle).format(date);
	}
	/**
	 * Parses text from the beginning of the given string to produce a date.
	 * The method may not use the entire text of the given string.
	 * @param source A <code>String</code> whose beginning should be parsed.
	 * @param pattern the pattern describing the date and time format
	 * @param locale The Locale to apply, if it is null,
	 * The current locale given by {@link org.zkoss.util.Locales#getCurrent} is used.
	 * @param timezone the time zone to apply, if it is null,
	 * The current timezone given by {@link org.zkoss.util.TimeZones#getCurrent} is used.
	 * @param dateStyle styling index of date.
	 * @param timeStyle styling index of time.
	 * @return A <code>Date</code> parsed from the string.
	 * @throws Exception
	 * @since 6.0.0
	 */
	public static final Date parseDate(String source, String pattern, Locale locale, TimeZone timezone, String dateStyle, String timeStyle) throws Exception {
		return getDateFormat(pattern, locale, timezone, dateStyle, timeStyle).parse(source);
	}
	/**
	 * Formats a number (Integer, BigDecimal...) into a string.
	 * If null, an empty string is returned.
	 * <p>A utility to assist the handling of numeric data.
	 * @param number The number to format.
	 * @param pattern The pattern to apply, if it is null,
	 * the system's default format is used.
	 * @param locale The Locale to apply, if it is null,
	 * The current locale given by {@link org.zkoss.util.Locales#getCurrent} is used.
	 * @return String The formatted number string.
	 * @since 6.0.1
	 */
	public static final String formatNumber (Object number, String pattern, Locale locale) {
		return getDecimalFormat(pattern, locale).format(number);
	}
	/**
	 * Parses text from the beginning of the given string to produce a number.
	 * The method may not use the entire text of the given string.
	 * @param source A <code>String</code> whose beginning should be parsed.
	 * @param pattern the pattern describing the date and time format
	 * @param locale The Locale to apply, if it is null,
	 * The current locale given by {@link org.zkoss.util.Locales#getCurrent} is used.
	 * @return A <code>Number</code> parsed from the string.
	 * @throws Exception
	 * @since 6.0.1
	 */
	public static final Number parseNumber (String source, String pattern, Locale locale) throws Exception {
		return getDecimalFormat(pattern, locale).parse(source);
	}
	private static final DecimalFormat getDecimalFormat (String pattern, Locale locale) {
		final DecimalFormat df = (DecimalFormat)
			NumberFormat.getInstance(locale != null ? locale : Locales.getCurrent());
		if (pattern != null)
			df.applyPattern(pattern);
		return df;
	}
	private static final DateFormat getDateFormat (String pattern, Locale locale, TimeZone timezone, String dateStyle, String timeStyle) {
		if (locale == null)
			locale = Locales.getCurrent();
		if (timezone == null)
			timezone = TimeZones.getCurrent();
		pattern = getRealFormat(pattern, locale, dateStyle, timeStyle);
		final DateFormat df = new SimpleDateFormat(pattern,
				locale);
		df.setTimeZone(timezone);
		return df;
	}
	private static final String getRealFormat (String pattern, Locale locale, String dateStyle, String timeStyle) {
		int ts, ds;
		if (pattern.isEmpty())
			pattern = null;
		ds = toStyle(dateStyle);
		if (ds != -111) {
			ts = toStyle(timeStyle);
			if (ts != -111) {
				return DateFormats.getDateTimeFormat(ds, ts, locale, pattern);
			}
			return DateFormats.getDateFormat(ds, locale, pattern);
		}
		return pattern != null ? pattern : "M/d/yy";
	}
	private static final int toStyle (String style) {
		if (style != null) {
			style = style.trim().toLowerCase();
			return "short".equals(style) ? DateFormat.SHORT
					: "medium".equals(style) ? DateFormat.MEDIUM
					: "long".equals(style) ? DateFormat.LONG
					: "full".equals(style) ? DateFormat.FULL
					: -111; //not found
		}
		return -111;
	}
}