/* Charsets.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan  5 13:55:30     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.lang.Library;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
import org.zkoss.util.Locales;
import org.zkoss.util.logging.Log;

import org.zkoss.web.Attributes;

/**
 * Utilities to handle characters
 *
 * @author tomyeh
 */
public class Charsets {
	private static final Log log = Log.lookup(Charsets.class);
	private static final String ATTR_SETUP = "org.zkoss.web.charset.setup";

	private static final String _uriCharset;
	static {
		String cs = Library.getProperty("org.zkoss.web.uri.charset");
		if (cs == null || cs.length() == 0)
			cs = "UTF-8"; //Default: UTF-8
		_uriCharset = cs;
	}

	/** Returns the charset used to encode URI and query string.
	 */
	public static final String getURICharset() {
		return _uriCharset;
	}

	/** Sets up the charset for the request and response based on
	 * {@link #getPreferredLocale(HttpSession,ServletRequest)}. After setting up, you shall invoke
	 * {@link #cleanup} before exiting.
	 *
	 * <pre><code> final Object old = setup(request, response, null);
	 * try {
	 *	  ....
	 * } finally {
	 *    cleanup(request, old);
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
	 * @param sess the session to look for the preferred locale. Ignored if null.
	 * @param charset the response's charset. If null or empty,
	 * response.setCharacterEncoding won't be called, i.e., the container's
	 * default is used.
	 * @return an object that must be passed to {@link #cleanup}
	 */
	public static final
	Object setup(HttpSession sess, ServletRequest request, ServletResponse response, String charset) {
		if (hasSetup(request)) //processed before?
			return Objects.UNKNOWN;

		final Locale locale = getPreferredLocale(sess, request);
		response.setLocale(locale);
		if (charset != null && charset.length() > 0) {
			try {
				if (Servlets.isServlet24()) {
					response.setCharacterEncoding(charset);
				} else {
				//don't access 2.4 API: setCharacterEncoding, getContentType
					response.setContentType(";charset=" + charset);
				}
			} catch (Throwable ex) {
				try {
					final String v = response.getCharacterEncoding();
					if (!Objects.equals(v, charset))
						log.warningBriefly("Unable to set response's charset: "+charset+" (current="+v+')', ex);
				} catch (Throwable t) { //just in case
				}
			}
		}

		if (request.getCharacterEncoding() == null) {
			charset = response.getCharacterEncoding();
			try {
				request.setCharacterEncoding(charset);
			} catch (Throwable ex) {
				final String v = request.getCharacterEncoding();
				if (!Objects.equals(v, charset))
					log.warning("Unable to set request's charset: "+charset+" (current="+v+"): "+Exceptions.getMessage(ex));
			}
		}

		markSetup(request, true);
		return Locales.setThreadLocal(locale);
	}
	/** Sets up the charset for the request and response based on
	 * {@link #getPreferredLocale(HttpSession,ServletRequest)}.
	 * It is the same as setup(request.getSession(false), request, response, charset);
	 */
	public static final Object
	setup(ServletRequest request, ServletResponse response, String charset) {
		return setup(getSession(request), request, response, charset);
	}
	private static final HttpSession getSession(ServletRequest request) {
		return request instanceof HttpServletRequest ?
			((HttpServletRequest)request).getSession(false): null;
	}
	/** Cleans up what has been set in {@link #setup}.
	 * Some invocation are not undo-able, so this method only does the basic
	 * cleanups.
	 *
	 * @param old the value must be the one returned by the last call to
	 * {@link #setup}.
	 */
	public static final void cleanup(ServletRequest request, Object old) {
		if (old != Objects.UNKNOWN) {
			Locales.setThreadLocal((Locale)old);
			markSetup(request, false);
		}
	}
	/** Returns whether the specified request has been set up, i.e.,
	 * {@link #setup} is called
	 *
	 * <p>It is rarely needed to call this method, because it is called
	 * automatically by {@link #setup}.
	 */
	public static final boolean hasSetup(ServletRequest request) {
		return request.getAttribute(ATTR_SETUP) != null; //processed before?
	}
	/** Marks the specified request whether it has been set up, i.e.,
	 * {@link #setup} is called.
	 *
	 * <p>It is rarely needed to call this method, because it is called
	 * automatically by {@link #setup}.
	 */
	public static final void markSetup(ServletRequest request, boolean setup) {
		if (setup) request.setAttribute(ATTR_SETUP, Boolean.TRUE);
		else request.removeAttribute(ATTR_SETUP);
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
	 *
	 * @param sess the session to look for the preferred locale. Ignored if null.
	 */
	public static final
	Locale getPreferredLocale(HttpSession sess, ServletRequest request) {
		if (sess != null) {
			final Object v = sess.getAttribute(Attributes.PREFERRED_LOCALE);
			if (v != null) {
				if (v instanceof Locale)
					return (Locale)v;
				log.warning(Attributes.PREFERRED_LOCALE+" ignored. Locale is required, not "+v.getClass());
			}
		}

		final Locale l = request.getLocale();
		return l != null ? l: Locale.getDefault();
	}
	/** Returns the preferred locale of the specified request.
	 * It is the same as getPreferredLocale(request.getSession(false), request).
	 */
	public static final Locale getPreferredLocale(ServletRequest request) {
		return getPreferredLocale(getSession(request), request);
	}
	/** Sets the preferred locale for the current session of the specified
	 * request.
	 * @param locale the preferred Locale. If null, it means no preferred
	 * locale (and then {@link #getPreferredLocale(HttpSession,ServletRequest)} use request.getLocale
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
