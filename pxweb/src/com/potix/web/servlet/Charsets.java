/* Charsets.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan  5 13:55:30     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet;

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.potix.lang.Objects;
import com.potix.lang.SystemException;
import com.potix.util.Locales;
import com.potix.util.logging.Log;

import com.potix.web.Attributes;

/**
 * Utilities to handle characters
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Charsets {
	private static final Log log = Log.lookup(Charsets.class);

	private static final String _uriCharset, _respCharset;
	static {
		_uriCharset = //Default: UTF-8
			System.getProperty("com.potix.web.uri.charset", "UTF-8");
		if (_uriCharset.length() == 0)
			throw new SystemException("com.potix.web.uri.charset cannot be empty");
		final String cs  //Default: UTF-8
			= System.getProperty("com.potix.web.response.charset", "UTF-8");
		_respCharset = cs.length() > 0 ? cs: null;
	}

	/** Returns the charset used to encode URI and query string.
	 */
	public static final String getURICharset() {
		return _uriCharset;
	}
	/** Returns the default charset used for the HTTP response, or null
	 * if not specified.
	 * In this case, the caller shall assume the charset defined in
	 * web.xml is used.
	 */
	public static final String getResponseCharset() {
		return _respCharset;
	}

	/** Sets up the charset for the request and response based on
	 * {@link #getPreferredLocale}. After setting up, you shall invoke
	 * {@link #cleanup} before exiting.
	 *
	 * <pre><code> final Object old = setup(request, response);
	 * try {
	 *	  ....
	 * } finally {
	 *    cleanup(old);
	 * }
	 *
	 * <p>It is OK to call this method multiple time, since it is smart
	 * enough to ignore redudant calls.
	 *
	 * <p>{@link CharsetFilter} actually use this method to setup
	 * the proper charset and locale. By mapping {@link CharsetFilter} to
	 * all servlets, the encoding charset could be prepared correctly.
	 * However, if you are writing libraries to be as independent of
	 * web.xml as possible, you might choose to invoke this method directly.
	 *
	 * @return an object that must be passed to {@link #cleanup}
	 */
	public static final
	Object setup(ServletRequest request, ServletResponse response) {
		//20050420: Tom Yeh:
		//Since we store the preferred locale in HttpSession rather than
		//com.potix.ext.security.Session,
		//we don't need to invoke Authens.isAuthenticated() to re-authen.
		//
		//Side effect: if remember-me is ON and a page invokes getRemoteUser
		//which causes auto-login, then this page is rendered with the
		//preferred locale before login (not what is specified in i3 session)
		//Note: if a protected page is visited, there is no such side effect
		//because Realm.authenticate is called before any filter
		//Authens.isAuthenticated(request);

		final String ATTR = "com.potix.web.charset.setup";
		if (request.getAttribute(ATTR) != null) //processed before?
			return Objects.UNKNOWN;

		final Locale locale = getPreferredLocale(request);
		response.setLocale(locale);
		final String charset = getResponseCharset();
		if (charset != null)
			response.setCharacterEncoding(charset);
			//if null, the mapping defined in web.xml is used

		if (request.getCharacterEncoding() == null) {
			try {
				request.setCharacterEncoding(response.getCharacterEncoding());
			} catch (java.io.UnsupportedEncodingException ex) {
				log.warning("Unable to set request's charset: "+response.getCharacterEncoding());
			}
		}

		request.setAttribute(ATTR, Boolean.TRUE); //mark as processed
		return Locales.setThreadLocal(locale);
	}
	/** Cleans up what has been set in {@link #setup}.
	 * Some invocation are not undo-able, so this method only does the basic
	 * cleanups.
	 *
	 * @param old the value must be the one returned by the last call to
	 * {@link #setup}.
	 */
	public static final void cleanup(Object old) {
		if (old != Objects.UNKNOWN)
			Locales.setThreadLocal((Locale)old);
	}

	/** Returns the preferred locale of the specified request.
	 * You rarely need to invoke this method directly, because it is done
	 * automatically by {@link #setup}.
	 *
	 * <ol>
	 * <li>It checks whether any attribute stored in HttpSession called
	 * {@link Attributes#PREFERRED_LOCALE}. If so, return it.</li>
	 * <li>Otherwise, use ServletRequest.getLocale().</li>
	 * </ol>
	 */
	public static final Locale getPreferredLocale(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			final HttpSession sess =
				((HttpServletRequest)request).getSession(false);
			if (sess != null) {
				final Locale l =
					(Locale)sess.getAttribute(Attributes.PREFERRED_LOCALE);
				if (l != null)
					return l;
			}
		}

		final Locale l = request.getLocale();
		return l != null ? l: Locale.getDefault();
	}
	/** Sets the preferred locale for the current session of the specified
	 * request.
	 * @param locale the preferred Locale. If null, it means no preferred
	 * locale (and then {@link #getPreferredLocale} use request.getLocale
	 * instead).
	 */
	public static final
	void setPreferredLocale(ServletRequest request, Locale locale) {
		if (request instanceof HttpServletRequest) {
			final HttpSession sess =
				((HttpServletRequest)request).getSession(); //auto-create
			if (locale != null)
				sess.setAttribute(Attributes.PREFERRED_LOCALE, locale);
			else
				sess.removeAttribute(Attributes.PREFERRED_LOCALE);
		}
	}
}
