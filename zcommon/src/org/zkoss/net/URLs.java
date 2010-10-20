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
	/** @deprecated As of release 5.0.5, for simple ZK core. */
	private static URLEncoder _enc;

	/**
	 * @deprecated As of release 5.0.5, for simple ZK core.
	 * Translates a string into x-www-form-urlencoded  format.
	 *
	 * <p>By default, java.net.URLEncoder.encode(s, "UTF-8") is used
	 * to translate the string.
	 * However, you can customize the translation by implementing
	 * the {@link URLEncoder} interface, and and specify the class
	 * in the library property called <code>org.zkoss.net.URLEncoder</code>.
	 */
	public static String encode(String s) {
		if (_enc == null) { //no need to sync
			final String cls = Library.getProperty("org.zkoss.net.URLEncoder");
			if (cls != null && cls.length() > 0) {
				try {
					_enc = (URLEncoder)Classes.newInstanceByThread(cls);
				} catch (Throwable ex) {
					throw SystemException.Aide.wrap(ex, "Unable to instantiate "+cls);
				}
			} else {
				_enc = new URLEncoder() {
					public String encode(String s) {
						try {
							return java.net.URLEncoder.encode(s, "UTF-8");
						} catch (java.io.UnsupportedEncodingException ex) {
							throw new SystemException(ex);
						}
					}
				};
			}
		}
		return _enc.encode(s);
	}
}
