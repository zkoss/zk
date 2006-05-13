/* DateFormats.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/text/DateFormats.java,v 1.6 2006/02/27 03:42:01 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	91/01/17 15:22:22, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.text;

import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.potix.util.prefs.Apps;

/**
 * DateFormat relevant utilities.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.6 $ $Date: 2006/02/27 03:42:01 $
 */
public class DateFormats {
	/**
	 * Parses a string to a date.
	 * It is smart enough to know whether to use DateFormat.getDateInstance
	 * and DateFormat.getDateTimeInstance.
	 * It also uses {@link Apps#getCurrentLocale}.
	 */
	public static final Date parse(String s)
	throws ParseException {
		final Locale locale = Apps.getCurrentLocale();
		
		if (s.indexOf(':') < 0) { //date only
			final DateFormat df =
				DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
			return df.parse(s);
		} else {
			synchronized (TO_STRING_FORMAT) {
				try {
					return TO_STRING_FORMAT.parse(s);
				} catch (ParseException ex) { //ignore it
				}
			}
			final DateFormat df =
				DateFormat.getDateTimeInstance(
					DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
			return df.parse(s);
		}
	}
	/** The date formatter for generating Date.toString().
	 * To use it, remember to synchronized(TO_STRING_FORMAT)
	 */
	private static final SimpleDateFormat TO_STRING_FORMAT =
		new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

	/** Formats a Date object based on the current Locale.
	 *
	 * @param dateOnly indicates whether to show only date; false to show
	 * both date and time
	 */
	public static final String format(Date d, boolean dateOnly) {
		Locale locale = Apps.getCurrentLocale();

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
