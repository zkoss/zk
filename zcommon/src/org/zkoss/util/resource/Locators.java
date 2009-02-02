/* Locators.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	90/12/07 10:34:55, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.zkoss.util.Locales;
import org.zkoss.util.logging.Log;
import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;

/**
 * Utilities to load and locate resources.
 *
 * @author tomyeh
 * @see Locator
 */
public class Locators {
	private static final Log log = Log.lookup(Locators.class);

	/** The default locator. */
	private static final Locator _locator = new ClassLocator();
	/**
	 * Returns the default resource locator which is an instance of
	 * {@link ClassLocator}.
	 */
	public static final Locator getDefault() {
		return _locator;
	}

	/**
	 * Locates the filename based on the locale.
	 *
	 * <p>Example, assume file="/a/b.ext" and locale="zh_TW", then
	 * the following files are tried in sequence:<br>
	 * /a/b_zh_TW.ext<br>
	 * /a/b_zh.ext<br>
	 * /a/b.ext
	 *
	 * <p>Unlike ClassLoader.getResource, it returns the found file,
	 * not the URL. In the previous example, one of /a/b_zh_TW.ext,
	 * /a/b_zh.ext and /a/b.ext will be returned.
	 *
	 * <p>To get the real URL, you still require locator.getResource:<br>
	 * <code>locator.getResource(Locators.locate(locator, "/a/b.ext", locale));</code>
	 *
	 * <p>Note: '_' is considered as a special character in the parsing,
	 * so there might be problem if a filename contains '_' used other
	 * than locale.
	 *
	 * <p>Unlike {@link org.zkoss.io.Files#locate}, where the filename
	 * must contain '*', this method always tries to locate the file by
	 * inserting the locale before '.'. In other words,
	 * Files.locate("/a/b*.c") is similar to
	 * Locators.locate(("/a/b.c", null, a_file_locator);
	 *
	 * @param locale the locale; null means the current locale
	 * @return the URL containing proper locale characters; null if not found.
	 * Note: it could compare it with the file argument with ==, because
	 * this method will return the same string if it is the result.
	 */
	public static final
	URLLocation locate(String file, Locale locale, Locator locator) {
		return (URLLocation)myLocate(file, locale, locator, false);
	}
	/**
	 * Locates the input stream based on the locale, and returns the info
	 * of StreamLocation.
	 *
	 * @see #locate
	 */
	public static final StreamLocation
	locateAsStream(String file, Locale locale, Locator locator) {
		return (StreamLocation)myLocate(file, locale, locator, true);
	}
	/** the location information. */
	public static class URLLocation {
		public final URL url;
		public final String file;
		public final Locale locale;
		public URLLocation(URL url, String file, Locale locale) {
			this.url = url;
			this.file = file;
			this.locale = locale;
		}
		public String toString() {
			return "[url: "+url+" file="+file+" l="+locale+']';
		}
	}
	/** the location information. */
	public static class StreamLocation {
		public final InputStream stream;
		public final Locale locale;
		public StreamLocation(InputStream stream, Locale locale) {
			this.stream = stream;
			this.locale = locale;
		}
		public String toString() {
			return "[l="+locale+']';
		}
	}
	/** Locates the file. */
	private static final Object
	myLocate(String file, Locale locale, Locator locator, boolean asStream) {
		if (locale == null)
			locale = Locales.getCurrent();

		final int jslash = file.lastIndexOf('/'); //>= -1
		final int jdot = 
			jslash >= 0 ? file.indexOf('.', jslash + 1): file.indexOf('.');
		final String ext = jdot >= 0 ? file.substring(jdot): "";
		final int jul = Locales.indexOfUnderline(file, jslash >= 0 ? jslash + 1: 0);
		final String base = file.substring(0,
			jul >= 0 && (jdot < 0 || jul < jdot) ? jul:
			jdot >= 0 ? jdot: file.length());

		if (D.ON && log.debugable())
			log.debug("svl=" + file + " base=" + base + " ext=" + ext);

		//search the existence based on locale
		final int baseLen = base.length();
		final StringBuffer sb = new StringBuffer(baseLen + 16).append(base);
		final String[] args = new String[1];
		final String[] secs = new String[] {
			locale.getLanguage(), locale.getCountry(), locale.getVariant()
		};

		for (int j = secs.length; --j >= -1;) {
			if (j >= 0 && secs[j].length() == 0)
				continue;

			sb.setLength(baseLen);
			for (int k = 0; k <= j; ++k)
				sb.append('_').append(secs[k]);
			sb.append(ext);

			args[0] = sb.toString();
			final Object found =
				asStream ?
					(Object)((Locator)locator).getResourceAsStream(args[0]):
					(Object)((Locator)locator).getResource(args[0]);
			if (found != null) {
				//decide the locale
				final Locale l;
				if (j >= 0) {
					sb.setLength(0);
					for (int k = 0; k <= j; ++k) {
						if (k > 0) sb.append('_');
						sb.append(secs[k]);
					}
				 	l = Locales.getLocale(sb.toString(), '_');
				} else {
					l = null;
				}

				//return the info
				if (asStream) {
					return new StreamLocation((InputStream)found, l);
				} else {
					return new URLLocation((URL)found,
						args[0].equals(file) ? file: (String)args[0], l);
				}
			}
		}
		return null;
	}
}
