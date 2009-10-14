/* URLs.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 14 12:48:18     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.net;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.SystemException;

/**
 * Utilities to handle URL.
 *
 * @author tomyeh
 * @since 3.6.3
 */
public class URLs {
	private static URLEncoder _enc;

	/** Translates a string into x-www-form-urlencoded  format.
	 *
	 * <p>By default, java.net.URLEncoder.encode(s, "UTF-8") is used
	 * to translate the string.
	 * However, you can customize the translation by implementing
	 * the {@link URLEncoder} interface, and and specify the class
	 * in the library property called <code>org.zkoss.net.URLEncoder</code>.
	 */
	public static String encode(String s) {
		final String cls = Library.getProperty("org.zkoss.net.URLEncoder");
		if (cls != null && cls.length() > 0) {
			if (_enc == null || !_enc.getClass().getName().equals(cls)) {
				try {
					_enc = (URLEncoder)Classes.newInstanceByThread(cls);
				} catch (Throwable ex) {
					throw SystemException.Aide.wrap(ex, "Unable to instantiate "+cls);
				}
			}

			return _enc.encode(s);
		}

		_enc = null;
		try {
			return java.net.URLEncoder.encode(s, "UTF-8");
		} catch (java.io.UnsupportedEncodingException ex) {
			throw new SystemException(ex);
		}
	}
}
