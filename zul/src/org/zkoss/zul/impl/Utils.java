/* Utils.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 14 15:30:49     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.mesg.MZul;

/**
 * A collection of utilities.
 *
 * @author tomyeh
 */
public class Utils {
	/** Parse a list of numbers.
	 *
	 * @param defaultValue the value if a number is omitted. For example, ",2"
	 * means "1,2" if defafultValue is 1
	 * @return an array of int, or null if no integer at all
	 */
	public static final
	int[] stringToInts(String numbers, int defaultValue)
	throws WrongValueException {
		if (numbers == null)
			return null;

		List list = new LinkedList();
		for (int j = 0;;) {
			int k = numbers.indexOf(',', j);
			final String s =
				(k >= 0 ? numbers.substring(j, k): numbers.substring(j)).trim();
			if (s.length() == 0) {
				if (k < 0) break;
				list.add(null);
			} else {
				try {
					list.add(Integer.valueOf(s));
				} catch (Throwable ex) {
					throw new WrongValueException("Not a valid number list: "+numbers);
				}
			}	

			if (k < 0) break;
			j = k + 1;
		}

		int[] ary;
		final int sz = list.size();
		if (sz > 0) {
			ary = new int[sz];
			int j = 0;
			for (Iterator it = list.iterator(); it.hasNext(); ++j) {
				final Integer i = (Integer)it.next();
				ary[j] = i != null ? i.intValue(): defaultValue;
			}
		} else {
			ary = null;
		}
		return ary;
	}
	/** Converts an array of numbers to a string.
	 */
	public static final String intsToString(int[] ary) {
		if (ary == null || ary.length == 0)
			return "";

		final StringBuffer sb = new StringBuffer(50);
		for (int j = 0; j < ary.length; ++j) {
			if (j > 0)
				sb.append(',');
			sb.append(ary[j]);
		}
		return sb.toString();
	}

	/** Parse a list of numbers.
	 *
	 * @param defaultValue the value used if an empty string is fund.
	 * For example, ",2" means "1,2" if defafultValue is "1"
	 * @return an array of string, or null if no data at all
	 */
	public static final
	String[] stringToArray(String src, String defaultValue) {
		if (src == null)
			return null;

		List list = new LinkedList();
		for (int j = 0;;) {
			int k = src.indexOf(',', j);
			final String s =
				(k >= 0 ? src.substring(j, k): src.substring(j)).trim();
			if (s.length() == 0) {
				if (k < 0) break;
				list.add(defaultValue);
			} else {
				list.add(s);
			}	

			if (k < 0) break;
			j = k + 1;
		}

		return (String[])list.toArray(new String[list.size()]);
	}
	/** Converts an array of objects to a string, by catenating them
	 * together and separated with comma.
	 */
	public static final String arrayToString(Object[] ary) {
		if (ary == null || ary.length == 0)
			return "";

		final StringBuffer sb = new StringBuffer(50);
		for (int j = 0; j < ary.length; ++j) {
			if (j > 0)
				sb.append(',');
			if (ary[j] != null)
				sb.append(ary[j]);
		}
		return sb.toString();
	}

	/** Returns the encoded URL for the dynamic generated content, or empty
	 * the component doesn't belong to any desktop.
	 *
	 * @since 3.0.2
	 */
	public static String getDynamicMediaURI(AbstractComponent comp,
	int version, String name, String format) {
		final Desktop desktop = comp.getDesktop();
		if (desktop == null) return ""; //no avail at client

		final StringBuffer sb = new StringBuffer(64).append('/');
		Strings.encode(sb, version & 0xffff);
		if (name != null || format != null) {
			sb.append('/');
			boolean bExtRequired = true;
			if (name != null && name.length() != 0) {
				name = name.replace('\\', '/');
				if (name.charAt(0) == '/') {
					name = name.substring(1);
					if (name.length() == 0) name = "ua";
				}
				sb.append(name);
				bExtRequired = name.lastIndexOf('.') < 0;
			} else {
				sb.append(comp.getId());
			}
			if (bExtRequired && format != null)
				sb.append('.').append(format);
		}
		return desktop.getDynamicMediaURI(comp, sb.toString()); //already encoded
	}

	/** Generates the locale-dependent JavaScript codes, such as messages
	 * (msgzul).
	 * <p>It is called by zul/lang/zk.wpd.
	 */
	public static final String outLocaleJavaScript() {
		final StringBuffer sb = new StringBuffer(1024)
			.append("zk.$default(msgzul, {");

		addLocaleJS(sb, "VALUE_NOT_MATCHED", MZul.VALUE_NOT_MATCHED);
		addLocaleJS(sb, "EMPTY_NOT_ALLOWED", MZul.EMPTY_NOT_ALLOWED);
		addLocaleJS(sb, "INTEGER_REQUIRED", MZul.INTEGER_REQUIRED);
		addLocaleJS(sb, "NUMBER_REQUIRED", MZul.NUMBER_REQUIRED);
		addLocaleJS(sb, "DATE_REQUIRED", MZul.DATE_REQUIRED);
		addLocaleJS(sb, "CANCEL", MZul.CANCEL);

		addLocaleJS(sb, "NO_POSITIVE_NEGATIVE_ZERO", MZul.NO_POSITIVE_NEGATIVE_ZERO);
		addLocaleJS(sb, "NO_POSITIVE_NEGATIVE", MZul.NO_POSITIVE_NEGATIVE);
		addLocaleJS(sb, "NO_POSITIVE_ZERO", MZul.NO_POSITIVE_ZERO);
		addLocaleJS(sb, "NO_POSITIVE", MZul.NO_POSITIVE);
		addLocaleJS(sb, "NO_NEGATIVE_ZERO", MZul.NO_NEGATIVE_ZERO);
		addLocaleJS(sb, "NO_NEGATIVE", MZul.NO_NEGATIVE);
		addLocaleJS(sb, "NO_ZERO", MZul.NO_ZERO);

		addLocaleJS(sb, "NO_FUTURE_PAST_TODAY", MZul.NO_FUTURE_PAST_TODAY);
		addLocaleJS(sb, "NO_FUTURE_PAST", MZul.NO_FUTURE_PAST);
		addLocaleJS(sb, "NO_FUTURE_TODAY", MZul.NO_FUTURE_TODAY);
		addLocaleJS(sb, "NO_FUTURE", MZul.NO_FUTURE);
		addLocaleJS(sb, "NO_PAST_TODAY", MZul.NO_PAST_TODAY);
		addLocaleJS(sb, "NO_PAST", MZul.NO_PAST);
		addLocaleJS(sb, "NO_TODAY", MZul.NO_TODAY);	

		addLocaleJS(sb, "FIRST", MZul.FIRST);
		addLocaleJS(sb, "LAST", MZul.LAST);
		addLocaleJS(sb, "PREV", MZul.PREV);
		addLocaleJS(sb, "NEXT", MZul.NEXT);
		
		addLocaleJS(sb, "GRID_GROUP", MZul.GRID_GROUP);
		addLocaleJS(sb, "GRID_OTHER", MZul.GRID_OTHER);
		addLocaleJS(sb, "GRID_ASC", MZul.GRID_ASC);
		addLocaleJS(sb, "GRID_DESC", MZul.GRID_DESC);
		addLocaleJS(sb, "GRID_COLUMNS", MZul.GRID_COLUMNS);

		addLocaleJS(sb, "OK", MZul.OK);
		addLocaleJS(sb, "CANCEL", MZul.CANCEL);
		addLocaleJS(sb, "YES", MZul.YES);
		addLocaleJS(sb, "NO", MZul.NO);
		addLocaleJS(sb, "RETRY", MZul.RETRY);
		addLocaleJS(sb, "ABORT", MZul.ABORT);
		addLocaleJS(sb, "IGNORE", MZul.IGNORE);

		addLocaleJS(sb, "UPLOAD_CANCEL", MZul.UPLOAD_CANCEL);

		int j = sb.length() - 1;
		if (sb.charAt(j) == ',') sb.setLength(j);

		return sb.append("});").toString();
	}
	private static
	void addLocaleJS(StringBuffer sb, String name, int mesgCode) {
		sb.append('\n').append(name).append(":'");
		Strings.escape(sb, Messages.get(mesgCode), Strings.ESCAPE_JAVASCRIPT);
		sb.append("',");
	}
}
