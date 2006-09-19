/* Authens.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Apr 13 15:30:56     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.auth;

import javax.servlet.ServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

/**
 * Utilities for authentications.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Authens {
	/** The cookie's name used to store user info. */
	public static final String COOKIE_USER_INFO = "pxuic";
	/** The cookie's name used to store remembe-me. */
	public static final String COOKIE_REMEMBER_ME = "pxrmc";
	/** Used to disable the remember-me on certain situation. */
	private static final String ATTR_REMEMBER_ME_DISABLED =
		"org.zkoss.web.servlet.auth.rememberMe.disabled";

	/** Returns whether this request has been authenticated.
	 * Note: calling request.getRemoteUser() will auto-login if remember-me
	 * is on.
	 */
	public static final boolean isAuthenticated(HttpServletRequest request) {
		request.setAttribute(ATTR_REMEMBER_ME_DISABLED, Boolean.TRUE);
		try {
			return request.getRemoteUser() != null;
		} finally {
			request.removeAttribute(ATTR_REMEMBER_ME_DISABLED);
		}
	}
	/** Returns whether this request has been authenticated.
	 * Equivalent to <br>
	 * request instanceof HttpServletRequest
	 and {@link #isAuthenticated(HttpServletRequest)}.
	 */
	public static final boolean isAuthenticated(ServletRequest request) {
		return (request instanceof HttpServletRequest)
			&& isAuthenticated((HttpServletRequest)request);
	}
	/** Returns whether remember-me is disabled.
	 * If true, the underlying authentication mechansim shall not apply
	 * remember-me.
	 */
	public static boolean isRememberMeDisabled(HttpServletRequest request) {
		return request.getAttribute(ATTR_REMEMBER_ME_DISABLED) != null;
	}
	
	/** Logouts.
	 * After calling this method, the session is invalidated but
	 * request.getRemoteUser() is still valid, so it is better to
	 * send-redirect to other page showing some info upon this logout.
	 */
	public static final void logout(HttpServletRequest request,
	HttpServletResponse response) {
		final HttpSession session = request.getSession(false);
		if (session != null)
			session.invalidate();

		//invalidate cookie
		String page = request.getContextPath();
		if (page.length() == 0) page = "/";
		final Cookie cookie = new Cookie(COOKIE_REMEMBER_ME, "");
		cookie.setPath(page);
		cookie.setMaxAge(2); //simulate "removal"
		response.addCookie(cookie);
	}
}
