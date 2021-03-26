/* CookieThemeResolver.java

	Purpose:
		
	Description:
		
	History:
		Mar 14, 2013 04:46:08 PM, Created by neillee

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul.theme;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.zkoss.web.theme.ThemeResolver;

/**
 * A standard implementation of ThemeResolver
 * Retrieves and stores theme names via cookie
 * 
 * ZK CE/PE/EE 
 * @author neillee
 * @since 6.5.2
 */
public class CookieThemeResolver implements ThemeResolver {

	private static final String THEME_COOKIE_KEY = "zktheme";

	/**
	 * Retrieves theme name from Cookie
	 * @param request
	 * @return theme name stored in Cookie, or "" if not found
	 * @since 6.5.2
	 */
	public String getTheme(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return "";
		for (Cookie c : cookies) {
			if (THEME_COOKIE_KEY.equals(c.getName())) {
				String themeName = c.getValue();
				if (themeName != null)
					return themeName;
			}
		}
		return "";
	}

	/**
	 * Stores theme name in Cookie
	 * @param request
	 * @param response
	 * @param themeName theme name to be stored in Cookie
	 * @since 6.5.2
	 */
	public void setTheme(HttpServletRequest request, HttpServletResponse response, String themeName) {
		Cookie cookie = new Cookie(THEME_COOKIE_KEY, themeName);
		if (request.isSecure()) {
			cookie.setSecure(true);
		}
		cookie.setMaxAge(60 * 60 * 24 * 30); //store 30 days
		String cp = request.getContextPath();
		if (cp == null || "/".equals(cp))
			cp = "";
		// if path is empty, cookie path will be request path, which causes problems
		if (cp.length() == 0)
			cp = "/";
		cookie.setPath(cp);
		response.addCookie(cookie);
	}

}
