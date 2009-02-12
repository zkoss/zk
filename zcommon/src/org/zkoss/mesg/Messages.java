/* Messages.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	 2001/9/, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.mesg;

import java.util.Date;
import java.util.Locale;

import org.zkoss.lang.Objects;
import org.zkoss.util.Locales;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.PropertyBundle;
import org.zkoss.text.MessageFormats;

/**
 * The message manager.
 * This class manages how an message is retrieved based on the message code
 * and the locale.
 *
 * <p>Note: unlike MessageFormat's default behavior, all null objects
 * are treated as an empty string rather than "null".
 *
 * @author tomyeh
 */
public class Messages implements MessageConst {
	private static final Log log = Log.lookup(Messages.class);

	private static Formatter _formatter;

	protected Messages() {//prohibit from inited
	}

	/**
	 * Gets a message based on the specified code without formating arugments.
	 * <p>Equivalent to get(code, null).
	 */
	public static final String get(int code) {
		return get(code, null, getLocale());
	}
	/**
	 * Gets a message based on the locale of current user with
	 * ONE format-argument.
	 */
	public static final String get(int code, Object fmtArg) {
		return get(code, new Object[] {fmtArg}, getLocale());
	}
	/**
	 * Gets a message based on the locale of current user.
	 * <p>Equivalent to get(code, fmtArgs, current_locale).
	 * The current_locale argument depends on the implementation.
	 */
	public static final String get(int code, Object[] fmtArgs) {
		return get(code, fmtArgs, getLocale());
	}

	private static final Locale getLocale() {
		return Locales.getCurrent();
	}

	/**
	 * Gets a message from the resource bundle.
	 *
	 * @return null if no found
	 */
	private static final String getFromBundle(int code, Locale locale) {
		final BundleInfo bi = Aide.getBundleInfo(code);
		final PropertyBundle rb = //case insensitive
			PropertyBundle.getBundle(bi.filename, locale, true);
		if (rb != null)
			return rb.getProperty(Integer.toHexString(code - getType(code)));

		throw new IllegalStateException("Missing resource: " + bi + " locale=" + locale);
	}
	private static final String getNotFound(int code, Locale locale) {
		if (code == NULL_CODE)
			return ""; //special code

		try {
			log.error("Message code not found: " +
				Integer.toHexString(code) + " not in " + Aide.getBundleInfo(code));
	
			final String hexcode = Integer.toHexString(code);
			final String s = getFromBundle(
				MCommon.MESSAGE_CODE_NOT_FOUND, locale);
			return s != null ?
				MessageFormats.format(s, new Object[] {hexcode}, locale):
				"Unknown message code: " + hexcode;
		}catch(Exception ex) {
			log.error(ex);
			return "Unknown message code: " + Integer.toHexString(code);
		}
	}

	/**
	 * Gets a message based on the specified code. If not found, returns
	 * an error message to denote it.
	 *
	 * <p>If fmtArgs is not null,
	 * {@link org.zkoss.text.MessageFormats#format} is called to format
	 * the message. However, unlike MessageFormat's default behavior,
	 * all null objects are treated as an empty string rather than "null".
	 *
	 * <p>It also recognizes {@link org.zkoss.lang.Objects#UNKNOWN}.
	 *
	 * @param code the code
	 * @param fmtArgs the argument lists to format the message
	 * @param locale the locale of the message to load
	 * @return the message; never be null
	 */
	public static String get(int code, Object[] fmtArgs, Locale locale) {
		try {
			String s = getFromBundle(code, locale);
			if (s == null)
				return getNotFound(code, locale);

			if (fmtArgs != null && fmtArgs.length > 0) {
				final Object[] args = new Object[fmtArgs.length];
				final Formatter formatter = _formatter;
				for (int j = 0; j < fmtArgs.length; ++j) {
					final Object arg = fmtArgs[j];
					if (formatter != null)
						args[j] = formatter.format(arg);
					else if (arg == null || arg == Objects.UNKNOWN)
						args[j] = "";
					else if (arg instanceof Object[])
						args[j] = Objects.toString(arg);
					else
						args[j] = arg;
				}
				s = MessageFormats.format(s, args, locale);
			}
			return s;
		}catch(Exception ex) {
			log.error(ex);
			return getNotFound(code, locale);
		}
	}
	/** Returns the formatter used by {@link #get(int, Object[], Locale)},
	 * or null if not set.
	 */
	public static Formatter getFormatter() {
		return _formatter;
	}
	/** Sets the formatter used by {@link #get(int, Object[], Locale)}.
	 * <p>Default: null.
	 */
	public static void setFormatter(Formatter fmt) {
		_formatter = fmt;
	}

	/**
	 * Gets the message type of the specified code.
	 */
	public static final int getType(int code) {
		return code & 0xffff0000;
	}
	/** The formatter used by {@link #get(int, Object[], Locale)} to
	 * format the specified object.
	 */
	public static interface Formatter {
		/** Formats the specified object into a string.
		 */
		public Object format(Object o);
	}
}
