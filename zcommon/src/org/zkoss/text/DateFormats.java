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
import java.text.ParseException;

import org.zkoss.util.Locales;

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
}
