/* Locales.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/util/Locales.java,v 1.9 2006/02/27 03:42:02 tomyeh Exp $
	Purpose:
	Description:
	History:
	2002/01/25 11:58:34, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.Iterator;
import java.util.Collection;

import com.potix.lang.D;
import com.potix.lang.Objects;

/**
 * The locale relevant utilities.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.9 $ $Date: 2006/02/27 03:42:02 $
 */
public class Locales {
	/** Converts a string that consists of language, country and variant
	 * to a locale.
	 *
	 * <p>The separator between language, country and variant
	 * is customizable, and whitespaces are ignored, e.g.,
	 * "zh_TW" and "zh, TW".
	 *
	 * <p>Thus, locale.equals(Locales.toLocale(locale.toString(), '_')).
	 *
	 * @param localeString the locale in string; null is OK
	 * @param separator the separator; ((char)0) means to decide automatically
	 * (either ',' or '_')
	 * @return the locale or null if locale is null or empty
	 */
	public static final Locale toLocale(String localeString, char separator) {
		if (localeString == null)
			return null;

		assert D.OFF || !localeString.equals("null"):
			"No Locale called 'null'"; //some caller not filter null out

		if (separator == (char)0)
			separator = localeString.indexOf('_') >= 0 ? '_' : ',';
		LinkedList list = new LinkedList();
		CollectionsX.parse(list, localeString, separator);

		String lang = "", cnt = "", var = "";
		switch (list.size()) {
		case 0:
			return null;
		default:
			assert(list.size() <= 3);
			var = (String)list.get(2);
		case 2:
			cnt = (String)list.get(1);
			if (cnt.length() != 2)
				throw new IllegalArgumentException("Not a valid country: "+localeString);
		case 1:
			lang = (String)list.get(0);
			if (lang.length() != 2)
				throw new IllegalArgumentException("Not a valid language: "+localeString);
		}

		return toLocale(new Locale(lang, cnt, var));

	}
	/** Converts a string that consists of language, country and variant
	 * to a locale.
	 *
	 * <p>A shortcut: toLocale(localeString, (char)0).
	 */
	public static final Locale toLocale(String localeString) {
		return toLocale(localeString, (char)0);
	}

	/** Converts a Locale to one of them being used before.
	 * To save memory (since locale is used frequently), it is suggested
	 * to pass thru this method after creating a new instance of Locale.<br>
	 * Example, toLocale(new Locale(...)).
	 *
	 * <p>This method first look for any locale
	 */
	synchronized public static final Locale toLocale(Locale locale) {
		final Locale l = (Locale)_founds.get(locale);
		if (l != null)
			return l;

		_founds.put(locale, locale);
		return locale;
	}
	/** Locales that are found so far. */
	private static final Map _founds = new HashMap(17);
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
		if (match(values, locale))
			return locale;

		final String lang = locale.getLanguage();
		final String cnty = locale.getCountry();
		final String var = locale.getVariant();
		if (var != null && var.length() > 0) {
			locale = new Locale(lang, cnty);
			if (match(values, locale))
				return locale;
		}
		if (cnty != null && cnty.length() > 0) {
			locale = new Locale(lang, "");
			if (match(values, locale))
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
	/** Tests whether a locale exists in the values collection. */
	private static boolean match(Collection values, Locale locale) {
		for (final Iterator it = values.iterator(); it.hasNext();)
			if (locale.equals(it.next()))
				return true;
		return false;
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
