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
	/**
	 * Parses a string to a date.
	 * It is smart enough to know whether to use DateFormat.getDateInstance
	 * and DateFormat.getDateTimeInstance.
	 * It also uses {@link Locales#getCurrent}.
	 */
	public static final Date parse(String s)
	throws ParseException {
		final Locale locale = Locales.getCurrent();
		
		if (s.indexOf(':') < 0) { //date only
			final DateFormat df =
				DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
			return df.parse(s);
		} else {
			try {
				return getDateFormat().parse(s);
			} catch (ParseException ex) { //ignore it
			}

			final DateFormat df =
				DateFormat.getDateTimeInstance(
					DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
			return df.parse(s);
		}
	}
	private static final SimpleDateFormat getDateFormat() {
		SimpleDateFormat df = (SimpleDateFormat)_df.get();
		if (df == null)
			_df.set(df = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US));
		df.setTimeZone(TimeZones.getCurrent());
		return df;
	}
	private static final ThreadLocal _df = new ThreadLocal();

	/** Formats a Date object based on the current Locale.
	 *
	 * @param dateOnly indicates whether to show only date; false to show
	 * both date and time
	 */
	public static final String format(Date d, boolean dateOnly) {
		Locale locale = Locales.getCurrent();

		if (dateOnly) {
			DateFormat df =
				DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
			return df.format(d);
		} else {
			DateFormat df =
				DateFormat.getDateTimeInstance(
					DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
			return df.format(d);
		}
	}
}
