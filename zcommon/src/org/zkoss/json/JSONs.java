/* JSONs.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 27 09:26:49 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.json;

import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.zkoss.util.TimeZones;

/**
 * Utilities to json-ize objects that JSON is not aware, such as Date.
 * <p>Notice that implementing {@link JSONAware} is another way to make
 * an object able to json-ized.
 * @author tomyeh
 * @since 5.0.5
 */
public class JSONs {
	/** Marshalls a Date object to a string, such that it can be add
	 * to {@link JSONArray} or {@link JSONObject} (aka., json-ize).
	 * <p>It is used with {@link #j2d}. {@link #d2j} is used to json-ize a Date
	 * object, while {@link #j2d} is to unmarshall it back to a Date object.
	 * <p>Notice it assumes {@link TimeZones#getCurrent} (and Locale-independent).
	 * However, the result string has no time zone information. Thus,
	 * if the client is in different time zone, the date object will be different.
	 * However, since the object will be marshalled back in the same way,
	 * the value sent back from the client will be the same (regardless
	 * the time zone is different).
	 */
	public static final String d2j(Date d) {
		return getDateFormat().format(d);
	}
	/**
	 * Unmarshalls a string to a date.
	 * It is used with {@link #d2j}. {@link #d2j} is used to json-ize a Date
	 * object, while {@link #j2d} is to unmarshall it back to a Date object.
	 * <p>Notice it assumes {@link TimeZones#getCurrent} (and Locale-independent).
	 */
	public static final Date j2d(String s)
	throws ParseException {
		return getDateFormat().parse(s);
	}
	private static final SimpleDateFormat getDateFormat() {
		SimpleDateFormat df = (SimpleDateFormat)_df.get();
		if (df == null)
			_df.set(df = new SimpleDateFormat(
				"yyyy.M.d.H.m.s.S", Locale.US));
		df.setTimeZone(TimeZones.getCurrent());
		return df;
	}
	private static final ThreadLocal _df = new ThreadLocal();
}
