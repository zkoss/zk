/* DateFormatInfo.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 20 10:47:04 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.text;

import java.util.Locale;

/** The format infomation for date/time.
 * It is used with {@link DateFormats#setLocalFormatInfo} to provide
 * the format information for different styling and locales.
 * @author tomyeh
 * @since 5.0.7
 */
public interface DateFormatInfo {
	/** Return the date format of the given style and locale, or null
	 * if the default one shall be used.
	 * @param style the giving formatting style (never null). For example,
	 * {@link DateFormat#SHORT} for "M/d/yy" in the US locale.
	 * @param locale the locale (never null).
	 */
	public String getDateFormat(int style, Locale locale);
	/** Return the time format of the given style and locale, or null
	 * if the default one shall be used.
	 * @param style the giving formatting style (never null). For example,
	 * {@link DateFormat#SHORT} for "h:mm a" in the US locale.
	 * @param locale the locale (never null).
	 */
	public String getTimeFormat(int style, Locale locale);
	/** Return the date/time format of the given style and locale, or null
	 * if the default one shall be used.
	 * @param dateStyle the giving formatting style (never null). For example,
	 * {@link DateFormat#SHORT} for "M/d/yy" in the US locale.
	 * @param timeStyle the giving formatting style (never null). For example,
	 * {@link DateFormat#SHORT} for "h:mm a" in the US locale.
	 * @param locale the locale (never null).
	 */
	public String getDateTimeFormat(int dateStyle, int timeStyle, Locale locale);
}
