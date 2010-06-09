/* MessageFormats.java


	Purpose: 
	Description: 
	History:
	2002/02/08 11:03:32, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.text;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Locale;
import java.net.InetAddress;
import java.text.MessageFormat;

import org.zkoss.lang.D;
import org.zkoss.lang.Strings;
import org.zkoss.util.Locales;

/**
 * The message formatting relevant utilities.
 *
 * <p>See also java.text.MessageFormat.
 *
 * @author tomyeh
 */
public class MessageFormats {
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments.
	 *
	 * <p>An extension to java.text.MessageFormat.format by allowing
	 * to specify a Locale.
	 *
	 * @param locale the locale; null for {@link Locales#getCurrent}
	 */
	public static final String
	format(String pattern, Object[] args, Locale locale) {
		return getFormat(pattern, locale)
			.format(args, new StringBuffer(), null).toString();
	}
	private static MessageFormat getFormat(String pattern, Locale locale) {
		if (locale == null)
			locale = getLocale();
		return new MessageFormat(pattern, locale);
	}
	private static final Locale getLocale() {
		return Locales.getCurrent();
	}
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments, by use of
	 * {@link Locales#getCurrent}.
	 *
	 * <p>Note: java.text.MessageFormat.format uses Locale.getDefault,
	 * which might not be corrent in a multi-user environment.
	 */
	public static final String format(String pattern, Object[] args) {
		return format(pattern, args, null);
	}
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments, by use of StringBuffer.
	 *
	 * <p>If you want to catenate a sequence of formated string, use
	 * this method.
	 */
	public static final StringBuffer format(
	StringBuffer result, String pattern, Object[] args, Locale locale) {
		return getFormat(pattern, locale).format(args, result, null);
	}
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments, by use of StringBuffer and
	 * {@link Locales#getCurrent}.
	 */
	public static final StringBuffer format(
	StringBuffer result, String pattern, Object[] args) {
		return format(result, pattern, args, null);
	}

	/** Parses a pattern and converts it to the format of
	 * java.text.MessageFormat.
	 * Names are the first element of a substring enclosing with {}
	 * in the pattern.
	 *
	 * <p>Example: "{a} is {b,number} of {a}" will return
	 * <code>new NameInfo("{0} is {1,number} of {0}", {"a", "b"})</code>.
	 *
	 * @see #formatByName(String, Map, Locale)
	 */
	public static final NameInfo parseByName(String pattern) {
		final Map names = new LinkedHashMap();
		final int len = pattern.length();
		final StringBuffer sb = new StringBuffer(len + 32);
		int j = 0;
		for (;;) {
			final int k = Strings.anyOf(pattern, "'{", j);
			if (k >= len) //not found
				break; //done

			final char cc = pattern.charAt(k);
			if (cc == '\'') {
				final int l = pattern.indexOf('\'', k + 1);
				if (l < 0) //not found
					break; //done

				sb.append(pattern.substring(j, l + 1));
				j = l + 1;
			} else {
				final int l = Strings.anyOf(pattern, ",}", k + 1);
				if (l >= len) //not found
					break; //done
				final int m = pattern.indexOf('}', l);
				if (m < 0) //not found
					break; //done

				final String nm = pattern.substring(k + 1, l);
				Integer pos = (Integer)names.get(nm);
				if (pos == null) {
					pos = new Integer(names.size());
					names.put(nm, pos);
				}
				sb.append(pattern.substring(j, k + 1))
					.append(pos).append(pattern.substring(l, m + 1));
				j = m + 1;
			}
		}
		if (j < len)
			sb.append(pattern.substring(j));
		return new NameInfo(sb.toString(), names.keySet());
	}
	/** The name info returned by {@link #parseByName}).
	 */
	public static class NameInfo {
		/** The pattern in the format of java.text.MessageFormat. */
		public final String pattern;
		/** The name of relative position. */
		public final String[] names;
		NameInfo(String pattern, Collection names) {
			this.pattern = pattern;
			this.names = (String[])names.toArray(new String[names.size()]);
		}
		NameInfo(String pattern, String[] names) {
			this.pattern = pattern;
			this.names = names;
		}
	}
	/** Formats a pattern by substituting names, enclosing with {}, with
	 * values found in the giving map.
	 *
	 * <p>This is an extension to java.text.MessageFormat.
	 * The only difference is that this method use arbitrary name
	 * instead of a number. For example, it use {name} instead of {0}.
	 * The quotation rule and formating pattern are the same as
	 * java.text.MessageFormat. Example, {var,number,$'#',###}.
	 *
	 * <p>It actually uses {@link #parseByName} to convert names to numbers,
	 * and then passes to java.text.MessageFormat.
	 *
	 * @param locale the locale; null for
	 * {@link Locales#getCurrent}
	 * @exception IllegalArgumentException if the pattern is invalid
	 * @see #parseByName
	 */
	public static final String
	formatByName(String pattern, Map mapping, Locale locale) {
		final NameInfo ni = parseByName(pattern);
		final Object[] args = new Object[ni.names.length];
		for (int j = args.length; --j >= 0;)
			args[j] = mapping.get(ni.names[j]);
		return format(ni.pattern, args, locale);
	}
	/** Formats a pattern by substituting names with values found in
	 * the giving map, by use of
	 * {@link Locales#getCurrent}.
	 *
	 * @see #parseByName
	 */
	public static final String formatByName(String pattern, Map mapping) {
		return formatByName(pattern, mapping, null);
	}
}
