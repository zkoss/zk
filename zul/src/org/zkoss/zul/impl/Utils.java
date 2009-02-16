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

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.WrongValueException;

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
}
