/* Locales.java


	Purpose:
	Description:
	History:
	2002/01/25 11:58:34, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.Iterator;
import java.util.Collection;

import org.zkoss.lang.D;
import org.zkoss.lang.Objects;

/**
 * The locale relevant utilities.
 *
 * @author tomyeh
 */
public class Locales {
	private final static
		InheritableThreadLocal<Locale> _thdLocale = new InheritableThreadLocal<Locale>();

	/** Returns the current locale; never null.
	 * This is the locale that every other objects shall use,
	 * unless they have special consideration.
	 *
	 * <p>Default: If {@link #setThreadLocal} was called with non-null,
	 * the value is returned. Otherwise, Locale.getDefault() is returned,
	 */
	public static final Locale getCurrent() {
		final Locale l = _thdLocale.get();
		return l != null ? l: Locale.getDefault();
	}
	/** Returns whether the current locale ({@link #getCurrent}) belongs
	 * to the specified language and/or country.
	 *
	 * @param lang the language code, e.g., en and zh. Ignored if null.
	 * @param country the country code, e.g., US. Ignored if null.
	 * If empty, it means no country code at all.
	 */
	public static final boolean testCurrent(String lang, String country) {
		final Locale l = getCurrent();
		return (lang == null || lang.equals(l.getLanguage()))
			&& (country == null || country.equals(l.getCountry()));
	}
	/**
	 * Sets the locale for the current thread only.
	 *
	 * <p>Each thread could have an independent locale, called
	 * the thread locale.
	 *
	 * <p>When Invoking this method under a thread that serves requests,
	 * remember to clean up the setting upon completing each request.
	 *
	 * <pre><code>Locale old = Locales.setThreadLocal(newValue);
	 *try { 
	 *  ...
	 *} finally {
	 *  Locales.setThreadLocal(old);
	 *}</code></pre>
	 *
	 * @param locale the thread locale; null to denote no thread locale
	 * @return the previous thread locale
	 */
	public static final Locale setThreadLocal(Locale locale) {
		final Locale old = _thdLocale.get();
		_thdLocale.set(locale);
		return old;
	}
	/**
	 * Returns the locale defined by {@link #setThreadLocal}.
	 *
	 * @since 3.0.0
	 * @see #getCurrent
	 */
	public static final Locale getThreadLocal() {
		return _thdLocale.get();
	}

	/** Converts a string that consists of language, country and variant
	 * to a locale.
	 *
	 * <p>The separator between language, country and variant
	 * is customizable, and whitespaces are ignored, e.g.,
	 * "zh_TW" and "zh, TW".
	 *
	 * <p>Thus, locale.equals(Locales.getLocale(locale.toString(), '_')).
	 *
	 * @param localeString the locale in string; null is OK
	 * @param separator the separator; ((char)0) means to decide automatically
	 * (either ',' or '_')
	 * @return the locale or null if locale is null or empty
	 */
	public static final Locale getLocale(String localeString, char separator) {
		if (localeString == null)
			return null;

		assert D.OFF || !localeString.equals("null"):
			"No Locale called 'null'"; //some caller not filter null out

		if (separator == (char)0)
			separator = localeString.indexOf('_') >= 0 ? '_' : ',';
		LinkedList<String> list = new LinkedList<String>();
		CollectionsX.parse(list, localeString, separator);

		String lang = "", cnt = "", var = "";
		switch (list.size()) {
		case 0:
			return null;
		default:
			assert(list.size() <= 3);
			var = list.get(2);
		case 2:
			cnt = list.get(1);
			if (cnt.length() != 2)
				throw new IllegalArgumentException("Not a valid country: "+localeString);
		case 1:
			lang = list.get(0);
			if (lang.length() != 2)
				throw new IllegalArgumentException("Not a valid language: "+localeString);
		}

		return getLocale(new Locale(lang, cnt, var));

	}
	/** Converts a string that consists of language, country and variant
	 * to a locale.
	 *
	 * <p>A shortcut: getLocale(localeString, (char)0).
	 */
	public static final Locale getLocale(String localeString) {
		return getLocale(localeString, (char)0);
	}

	/** Converts a Locale to one of them being used before.
	 * To save memory (since locale is used frequently), it is suggested
	 * to pass thru this method after creating a new instance of Locale.<br>
	 * Example, getLocale(new Locale(...)).
	 *
	 * <p>This method first look for any locale
	 */
	public static final Locale getLocale(Locale locale) {
		final Locale l = _founds.get(locale);
		if (l != null)
			return l;

		synchronized  (_founds) {
			final Map<Locale, Locale> fs = new HashMap<Locale, Locale>(_founds);
			fs.put(locale, locale);
			_founds = fs;
		}
		return locale;
	}
	/** Locales that are found so far. */
	private static Map<Locale, Locale> _founds = new HashMap<Locale, Locale>(16);
	static {
		final Locale[] ls = new Locale[] {
			Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE,
			Locale.ENGLISH, Locale.US,
			Locale.JAPAN, Locale.JAPANESE,
			Locale.KOREA, Locale.KOREAN,
			Locale.FRANCE, Locale.FRENCH,
			Locale.GERMANY, Locale.GERMAN,
			Locale.CHINESE
		};
		for (int j = 0; j < ls.length; ++j)
			_founds.put(ls[j], ls[j]);
	}

	/** Returns any occurence of the specified Locale or any its fallback
	 * in the value collection, or null if not found.
	 * By fallback, we mean will try without variant and country.
	 * Example, if locale is zh_TW, it will try zh_TW and then zh.
	 */
	public static Locale getByFallback(Collection values, Locale locale) {
		if (values.contains(locale))
			return locale;

		final String lang = locale.getLanguage();
		final String cnty = locale.getCountry();
		final String var = locale.getVariant();
		if (var != null && var.length() > 0) {
			locale = new Locale(lang, cnty);
			if (values.contains(locale))
				return locale;
		}
		if (cnty != null && cnty.length() > 0) {
			locale = new Locale(lang, "");
			if (values.contains(locale))
				return locale;
		}

		//search the first one that matches partially
		Locale rtn = null;
		for (final Iterator it = values.iterator(); it.hasNext();) {
			final Locale l = (Locale)it.next();
			if (l.getLanguage().equals(lang)) {
				//case 1: it matches all but the last element -> done
				if (var == null || var.length() == 0
				|| Objects.equals(l.getCountry(), cnty))//country might null
					return l;

				//case 2: it matches only language, we seeek for any case 1
				if (rtn == null)
					rtn = l;
			}
		}
		return rtn;
	}

	/** Returns the index of '_' preceding the country part, starting from j.
	 * It is similar to s.indexOf('_', j), except it detects country part
	 * (which must be only two letter in lower cases.
	 */
	public static int indexOfUnderline(String s, int j) {
		int k, last = s.length() - 2;
		for (;(k = s.indexOf('_', j)) >= 0 && k < last; j = k + 1) {
			char cc = s.charAt(k + 1);
			if (cc < 'a' || cc > 'z') continue; //not found
			cc = s.charAt(k + 2);
			if (cc < 'a' || cc > 'z') continue; //not found
			cc = s.charAt(k + 3);
			if (cc < 'a' || cc > 'z') return k; //found
		}
		return -1;
	}
	/** Returns the index of '_' preceding the country part.
	 * It is similar to s.indexOf('_'), except it detects country part
	 * (which must be only two letter in lower cases.
	 */
	public static int indexOfUnderline(String s) {
		return indexOfUnderline(s, 0);
	}
}
