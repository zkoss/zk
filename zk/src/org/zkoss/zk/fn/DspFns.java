/* DspFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 17 09:31:58     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.fn;

import java.util.Iterator;
import java.util.Locale;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DecimalFormatSymbols;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;
import org.zkoss.util.CacheMap;
import org.zkoss.util.Locales;
import org.zkoss.web.fn.ServletFns;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.HtmlPageRenders;

/**
 * Utilities to generate ZK related information in DSP pages.
 *
 * <p>For JSP pages, use {@link JspFns} instead.<br/>
 * For ZUML pages, use {@link ZkFns} instead.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public class DspFns {
	/** Generates and returns the ZK specific HTML tags such as stylesheet
	 * and JavaScript.
	 * If you want to generate HTML HEAD and BODY tags by yourself in
	 * a non-ZUML page (DSP), you can invoke this method at
	 * the location you want (such as inside the HTML HEAD tag).
	 *
	 * @return the string holding the HTML tags, or null if already generated.
	 * @param deviceType the device type. If null, ajax is assumed.
	 */
 	public static String outZkHtmlTags(String deviceType) {
 		Execution exec = Executions.getCurrent();
 		if (exec != null)
	 		return HtmlPageRenders.outZkTags(exec, null, null);

	 	return JspFns.outZkHtmlTags(
			ServletFns.getCurrentServletContext(),
			(HttpServletRequest)ServletFns.getCurrentRequest(),
			(HttpServletResponse)ServletFns.getCurrentResponse(),
			deviceType);
 	}

	/** Returns HTML tags to include style sheets of the specified device
	 * of the current application (never null).
	 *
	 * <p>It is the same as {@link HtmlPageRenders#outDeviceStyleSheets}
	 * except this method is used for DSP pages.
	 * @param deviceType the device type. If null, ajax is assumed.
	 */
	public static final String outDeviceStyleSheets(String deviceType) {
		final Execution exec = Executions.getCurrent();
 		if (exec != null)
			return HtmlPageRenders.outDeviceStyleSheets(exec, null, null);

	 	return JspFns.outDeviceStyleSheets(
			ServletFns.getCurrentServletContext(),
			(HttpServletRequest)ServletFns.getCurrentRequest(),
			(HttpServletResponse)ServletFns.getCurrentResponse(),
			deviceType);
	}

	/** Generates Locale-dependent strings in JavaScript syntax.
	 */
	public final static String outLocaleJavaScript() {
		final Locale locale = Locales.getCurrent();
		return outNumberJavaScript(locale) + outDateJavaScript(locale);
	}
	/** Output number relevant texts.
	 */
	private final static String outNumberJavaScript(Locale locale) {
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		final StringBuffer sb = new StringBuffer(128);
		appendAssignJavaScript(
			sb, "zk.GROUPING", symbols.getGroupingSeparator());
		appendAssignJavaScript(
			sb, "zk.DECIMAL", symbols.getDecimalSeparator());
		appendAssignJavaScript(
			sb, "zk.PERCENT", symbols.getPercent());
		appendAssignJavaScript(
			sb, "zk.MINUS", symbols.getMinusSign());
		appendAssignJavaScript(
			sb, "zk.PER_MILL", symbols.getPerMill());
		return sb.toString();
	}
	private final static
	void appendAssignJavaScript(StringBuffer sb, String nm, char val) {
		final char quot = val == '"' ? '\'': '"';
		sb.append(nm).append('=').append(quot).append(val).append(quot).append(";\n");
	}
	/** Output date/calendar relevant labels.
	 */
	private final static String outDateJavaScript(Locale locale) {
		synchronized (_datejs) {
			final String djs = (String)_datejs.get(locale);
			if (djs != null) return djs;
		}

		String djs = getDateJavaScript(locale);
		synchronized (_datejs) { //OK to race
			//To minimize memory use, reuse the string if they are the same
			//which is common
			for (Iterator it = _datejs.values().iterator(); it.hasNext();) {
				final String val = (String)it.next();
				if (val.equals(djs))
					djs = val; 
			}
			_datejs.put(locale, djs);
		}
		return djs;
	}
	private final static String getDateJavaScript(Locale locale) {
		final StringBuffer sb = new StringBuffer(512);

		final Calendar cal = Calendar.getInstance(locale);
		cal.clear();

		final int firstDayOfWeek = cal.getFirstDayOfWeek();
		sb.append("zk.DOW_1ST=")
			.append(firstDayOfWeek - Calendar.SUNDAY)
			.append(";\n");

		final boolean zhlang = locale.getLanguage().equals("zh");
		SimpleDateFormat df = new SimpleDateFormat("E", locale);
		final String[] sdow = new String[7], s2dow = new String[7];
		for (int j = firstDayOfWeek, k = 0; k < 7; ++k) {
			cal.set(Calendar.DAY_OF_WEEK, j);
			sdow[k] = df.format(cal.getTime());
			if (++j > Calendar.SATURDAY) j = Calendar.SUNDAY;

			if (zhlang) {
				s2dow[k] = sdow[k].length() >= 3 ?
					sdow[k].substring(2): sdow[k];
			} else {
				final int len = sdow[k].length();
				final char cc  = sdow[k].charAt(len - 1);
				s2dow[k] = cc == '.' || cc == ',' ?
					sdow[k].substring(0, len - 1): sdow[k];
			}
		}

		df = new SimpleDateFormat("EEEE", locale);
		final String[] fdow = new String[7];
		for (int j = firstDayOfWeek, k = 0; k < 7; ++k) {
			cal.set(Calendar.DAY_OF_WEEK, j);
			fdow[k] = df.format(cal.getTime());
			if (++j > Calendar.SATURDAY) j = Calendar.SUNDAY;
		}

		df = new SimpleDateFormat("MMM", locale);
		final String[] smon = new String[12], s2mon = new String[12];
		for (int j = 0; j < 12; ++j) {
			cal.set(Calendar.MONTH, j);
			smon[j] = df.format(cal.getTime());

			if (zhlang) {
				s2mon[j] = smon[0].length() >= 2 ? //remove the last char
					smon[j].substring(0, smon[j].length() -1): smon[j];
			} else {
				final int len = smon[j].length();
				final char cc  = smon[j].charAt(len - 1);
				s2mon[j] = cc == '.' || cc == ',' ?
					smon[j].substring(0, len - 1): smon[j];
			}
		}

		df = new SimpleDateFormat("MMMM", locale);
		final String[] fmon = new String[12];
		for (int j = 0; j < 12; ++j) {
			cal.set(Calendar.MONTH, j);
			fmon[j] = df.format(cal.getTime());
		}

		appendJavaScriptArray(sb, "SDOW", sdow);
		if (Objects.equals(s2dow, sdow))
			sb.append("zk.S2DOW=zk.SDOW;\n");
		else
			appendJavaScriptArray(sb, "S2DOW", s2dow);
		if (Objects.equals(fdow, sdow))
			sb.append("zk.FDOW=zk.SDOW;\n");
		else
			appendJavaScriptArray(sb, "FDOW", fdow);

		appendJavaScriptArray(sb, "SMON", smon);
		if (Objects.equals(s2mon, smon))
			sb.append("zk.S2MON=zk.SMON;\n");
		else
			appendJavaScriptArray(sb, "S2MON", s2mon);
		if (Objects.equals(fmon, smon))
			sb.append("zk.FMON=zk.SMON;\n");
		else
			appendJavaScriptArray(sb, "FMON", fmon);

		//AM/PM available since ZK 3.0
		df = new SimpleDateFormat("a", locale);
		cal.set(Calendar.HOUR_OF_DAY, 3);
		final String[] ampm = new String[2];
		ampm[0] = df.format(cal.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 15);
		ampm[1] = df.format(cal.getTime());
		appendJavaScriptArray(sb, "APM", ampm);

		return sb.toString();
	}
	private static final void appendJavaScriptArray(StringBuffer sb,
	String varnm, String[] vals) {
		sb.append("zk.").append(varnm).append("=[");
		for (int j = 0;;) {
			sb.append('\'')
				.append(Strings.escape(vals[j], Strings.ESCAPE_JAVASCRIPT))
				.append('\'');
			if (++j >= vals.length) break;
			else sb.append(',');
		}
		sb.append("];\n");
	}
	private static final CacheMap _datejs;
	static {
		_datejs = new CacheMap(8);
		_datejs.setLifetime(24*60*60*1000);
	}
}
