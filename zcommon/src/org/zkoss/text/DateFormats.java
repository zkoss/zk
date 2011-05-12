/* DateFormats.java


	Purpose: 
	Description: 
	History:
	91/01/17 15:22:22, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.text;

import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.zkoss.util.Locales;

/**
 * DateFormat relevant utilities.
 *
 * @author tomyeh
 */
public class DateFormats {
	private final static
		InheritableThreadLocal<DateFormatInfo> _thdLocale = new InheritableThreadLocal<DateFormatInfo>();

	/** Sets the info of date/time format for the current thread.
	 * It does not have effect on other threads.
	 * <p>When Invoking this method under a thread,
	 * remember to clean up the setting upon completing each request.
	 *
	 * <pre><code>DateFormatInfo old = DateFormats.setLocalFormatFormatInfo(info);
	 *try { 
	 *  ...
	 *} finally {
	 *  DateFormats.setLocalFormatInfo(old);
	 *}</code></pre>
	 * @param info the format info. It could be null (and the JVM's default
	 * is used).
	 * @return the previous format info being set, or null if not set.
	 * @since 5.0.7
	 */
	public static DateFormatInfo setLocalFormatInfo(DateFormatInfo info) {
		DateFormatInfo old = _thdLocale.get();
		_thdLocale.set(info);
		return old;
	}
	/** Returns the info of date/time format  for the current thread,
	 * or null if not set.
	 * @since 5.0.7
	 */
	public static DateFormatInfo getLocalFormatInfo() {
		return _thdLocale.get();
	}
	/** Returns the current date format; never null.
	 * <p>If a format info is set by {@link #setLocalFormatInfo},
	 * {@link DateFormatInfo#getDateFormat} of the info will be called first.
	 * If no info is set or the info returns null,
	 * <code>java.text.DateFormat.getDateInstance(style, locale)</code>
	 * will be called instead.
	 * If no format is found and the value of <code>defaultFormat</code> is not null,
	 * <code>defaultFormat</code> will be returned.
	 * Otherwise, "M/d/yy" is returned.
	 *
	 * @param style the giving formatting style. For example,
	 * {@link DateFormat#SHORT} for "M/d/yy" in the US locale.
	 * @param locale the locale. If null, {@link Locales#getCurrent}
	 * is assumed.
	 * @param defaultFormat the default format. It is used if no default
	 * format. If null, "M/d/yy" is assumed.
	 * @since 5.0.7
	 */
	public static
	String getDateFormat(int style, Locale locale, String defaultFormat) {
		if (locale == null)
			locale = Locales.getCurrent();

		final DateFormatInfo info = getLocalFormatInfo();
		if (info != null) {
			final String fmt = info.getDateFormat(style, locale);
			if (fmt != null)
				return fmt;
		}

		final DateFormat df = DateFormat.getDateInstance(style, locale);
		if (df instanceof SimpleDateFormat) {
			final String fmt = ((SimpleDateFormat)df).toPattern();
			if (fmt != null && !"M/d/yy h:mm a".equals(fmt))
				return fmt; // note: JVM use "M/d/yy h:mm a" if not found!
		}
		return defaultFormat != null ? defaultFormat: "M/d/yy";
	}

	/** Returns the current time format; never null.
	 * <p>If a format info is set by {@link #setLocalFormatInfo},
	 * {@link DateFormatInfo#getTimeFormat} of the info will be called first.
	 * If no info is set or the info returns null,
	 * <code>java.text.DateFormat.getTimeInstance(style, locale)</code>
	 * will be called instead.
	 * If no format is found and the value of <code>defaultFormat</code> is not null,
	 * <code>defaultFormat</code> will be returned.
	 * Otherwise, "h:mm a" is returned.
	 *
	 * @param style the giving formatting style. For example,
	 * {@link DateFormat#SHORT} for "h:mm a" in the US locale.
	 * @param locale the locale. If null, {@link Locales#getCurrent}
	 * is assumed.
	 * @param defaultFormat the default format. It is used if no default
	 * format. If null, "h:mm a" is assumed.
	 * @since 5.0.7
	 */
	public static final
	String getTimeFormat(int style, Locale locale, String defaultFormat) {
		if (locale == null)
			locale = Locales.getCurrent();

		final DateFormatInfo info = getLocalFormatInfo();
		if (info != null) {
			final String fmt = info.getTimeFormat(style, locale);
			if (fmt != null)
				return fmt;
		}

		final DateFormat df = DateFormat.getTimeInstance(style, locale);
		if (df instanceof SimpleDateFormat) {
			final String fmt = ((SimpleDateFormat)df).toPattern();
			if (fmt != null && !"M/d/yy h:mm a".equals(fmt))
				return fmt; // note: JVM use "M/d/yy h:mm a" if not found!
		}
		return "h:mm a";
	}

	/** Returns the current date/time format; never null.
	 * <p>If a format info is set by {@link #setLocalFormatInfo},
	 * {@link DateFormatInfo#getDateTimeFormat} of the info will be called first.
	 * If no info is set or the info returns null,
	 * <code>java.text.DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale)</code>
	 * will be called instead.
	 * If no format is found and the value of <code>defaultFormat</code> is not null,
	 * <code>defaultFormat</code> will be returned.
	 * Otherwise, "M/d/yy h:mm a" is returned.
	 *
	 * @param dateStyle the giving formatting style. For example,
	 * {@link DateFormat#SHORT} for "M/d/yy" in the US locale.
	 * @param timeStyle the giving formatting style. For example,
	 * {@link DateFormat#SHORT} for "h:mm a" in the US locale.
	 * @param locale the locale. If null, {@link Locales#getCurrent}
	 * is assumed.
	 * @since 5.0.7
	 */
	public static final
	String getDateTimeFormat(int dateStyle, int timeStyle, Locale locale,
	String defaultFormat) {
		if (locale == null)
			locale = Locales.getCurrent();

		final DateFormatInfo info = getLocalFormatInfo();
		if (info != null) {
			final String fmt = info.getDateTimeFormat(dateStyle, timeStyle, locale);
			if (fmt != null)
				return fmt;
		}

		final DateFormat df =
			DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
		if (df instanceof SimpleDateFormat) {
			final String fmt = ((SimpleDateFormat)df).toPattern();
			if (fmt != null)
				return fmt;
		}
		return defaultFormat != null ? defaultFormat: "M/d/yy h:mm a";
	}

	/** Formats a Date object based on the current Locale
	 * and the default date format ({@link DateFormat#DEFAULT}.
	 *
	 * @param dateOnly indicates whether to show only date; false to show
	 * both date and time
	 */
	public static final String format(Date d, boolean dateOnly) {
		return getDateFormat(dateOnly).format(d);
	}
	/**
	 * Parses a string, that is formatted by {@link #format}, to a date.
	 * It assumes the current locale is {@link Locales#getCurrent}.
	 *
	 * @param dateOnly indicates whether to show only date; false to show
	 * both date and time
	 * @since 5.0.5
	 */
	public static final Date parse(String s, boolean dateOnly)
	throws ParseException {
		return getDateFormat(dateOnly).parse(s);
	}
	private static final DateFormat getDateFormat(boolean dateOnly) {
		final Locale locale = Locales.getCurrent();
		return dateOnly ?
			DateFormat.getDateInstance(DateFormat.DEFAULT, locale):
			DateFormat.getDateTimeInstance(
					DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
	}
}
