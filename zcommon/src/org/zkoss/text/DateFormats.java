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
import org.zkoss.util.TimeZones;

/**
 * DateFormat relevant utilities.
 *
 * @author tomyeh
 */
public class DateFormats {
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

	/**
	 * @deprecated As of release 5.0.5, replaced with {@link #parse(String, boolean)}.
	 * Unlike {@link #parse(String, boolean)}, it detects the format by
	 * searching the existence of colon. However, it is not safe so
	 * {@link #parse(String, boolean)} shall be used.
	 */
	public static final Date parse(String s)
	throws ParseException {
		final Locale locale = Locales.getCurrent();
		
		if (s.indexOf(':') < 0) { //date only
			return getDateFormat(true).parse(s);
		} else {
			try { //backward compatible (wrong spec)
				return getHttpDateFormat().parse(s);
			} catch (ParseException ex) { //ignore it
			}
			return getDateFormat(false).parse(s);
		}
	}
	private static final SimpleDateFormat getHttpDateFormat() {
		SimpleDateFormat df = (SimpleDateFormat)_df.get();
		if (df == null)
			_df.set(df = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US));
		df.setTimeZone(TimeZones.getCurrent());
		return df;
	}
	private static final ThreadLocal _df = new ThreadLocal();

}
