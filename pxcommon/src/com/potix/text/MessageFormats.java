/* MessageFormats.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/text/MessageFormats.java,v 1.8 2006/02/27 03:42:01 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2002/02/08 11:03:32, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.text;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Locale;
import java.net.InetAddress;
import java.text.MessageFormat;

import com.potix.lang.D;
import com.potix.lang.Strings;
import com.potix.util.prefs.Apps;

/**
 * The message formatting relevant utilities.
 *
 * <p>See also java.text.MessageFormat.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.8 $ $Date: 2006/02/27 03:42:01 $
 */
public class MessageFormats {
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments.
	 *
	 * <p>An extension to java.text.MessageFormat.format by allowing
	 * to specify a Locale.
	 *
	 * @param locale the locale; null for {@link Apps#getCurrentLocale}
	 * @see #trimEmpty
	 */
	public static final String
	format(String pattern, Object[] args, Locale locale) {
		return getFormat(pattern, locale)
			.format(args, new StringBuffer(), null).toString();
	}
	private static MessageFormat getFormat(String pattern, Locale locale) {
		if (locale == null)
			locale = getLocale();
		return locale.equals(Locale.getDefault()) ?
			new MessageFormat(pattern): new MessageFormat(pattern, locale);
	}
	private static final Locale getLocale() {
		return Apps.getCurrentLocale();
	}
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments, by use of
	 * {@link Apps#getCurrentLocale}.
	 *
	 * <p>Note: java.text.MessageFormat.format uses Locale.getDefault,
	 * which might not be corrent in a multi-user environment.
	 *
	 * @see #trimEmpty
	 */
	public static final String format(String pattern, Object[] args) {
		return format(pattern, args, null);
	}
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments, by use of StringBuffer.
	 *
	 * <p>If you want to catenate a sequence of formated string, use
	 * this method.
	 *
	 * @see #trimEmpty
	 */
	public static final StringBuffer format(
	StringBuffer result, String pattern, Object[] args, Locale locale) {
		return getFormat(pattern, locale).format(args, result, null);
	}
	/** Creates a MessageFormat with the given pattern and uses it to
	 * format the given arguments, by use of StringBuffer and
	 * {@link Apps#getCurrentLocale}.
	 *
	 * @see #trimEmpty
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
	 * {@link Apps#getCurrentLocale}
	 * @exception IllegalArgumentException if the pattern is invalid
	 * @see #parseByName
	 * @see #trimEmpty
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
	 * {@link Apps#getCurrentLocale}.
	 *
	 * @see #parseByName
	 * @see #trimEmpty
	 */
	public static final String formatByName(String pattern, Map mapping) {
		return formatByName(pattern, mapping, null);
	}

	/** Specifies whether to include {user} and {domain}
	 * in {@link #formatByStatus}.
	 */
	public static final int INCLUDE_USER_INFO = 0x0001;
	/** Formats a pattern to a real name by substiting {app},
	 * {host}, {user} and {domain}.
	 *
	 * @param mode a combination of INCLUDE_xxx or 0. For better performance,
	 * don't specify unnecessary flags.
	 *
	 * @see #trimEmpty
	 */
	public static final String
	formatByStatus(String pattern, int mode) {
		if (_statusMap.size() == 0) {
			_statusMap.put("app", Apps.getApp().getCodeName());
			try {
				_statusMap.put("host", InetAddress.getLocalHost().getHostName());
			}catch(Exception ex) {
				_statusMap.put("host", null);
			}
		}
		if ((mode & INCLUDE_USER_INFO) != 0) {
			_statusMap.put("user", Apps.getCurrentUsername());
			_statusMap.put("domain", Apps.getCurrentDomain());
		}
		return formatByName(pattern, _statusMap);
	}
	private static final Map _statusMap = new HashMap(7);

	/** Trims the format to remove whitespaces and other non-letter, non-digits
	 * surrounding a null or empty argument.
	 *
	 * <p>Example: {0}, {1}; {2}<br>
	 * generates<br>
	 * abc<br>
	 * if args[0] and args[1] are null or empty and args[2] is "abc".
	 */
	public static final String trimEmpty(String fmt, Object[] args) {
		for (int j = 0; j < args.length; ++j) {
			final Object arg = args[j];
			if (arg == null
			|| ((arg instanceof String) && ((String)arg).length() == 0)) {
				final String p = "{" + j;
				final int b = fmt.indexOf(p);
				if (b < 0) {
					if (D.ON) throw new IllegalArgumentException(p+"} not found in "+fmt);
					continue;
				}
				int e = fmt.indexOf('}', b + 2);
				if (e < 0) {
					if (D.ON) throw new IllegalArgumentException("Enclosing '}' not found: "+fmt);
					continue;
				}

				final int len = fmt.length();
				while (++e < len) {
					final char c = fmt.charAt(e);
					if (c == '{' || Character.isUnicodeIdentifierPart(c))
						break;
				}
				fmt = fmt.substring(0, b) + fmt.substring(e);
			}
		}
		return fmt;
	}
}
