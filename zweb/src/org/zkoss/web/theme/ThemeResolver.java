/* ThemeResolver.java

	Purpose:
		
	Description:
		
	History:
		Mar 15, 2013 01:42 AM, Created by neillee

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.web.theme;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interface for web-based theme resolution strategies that allows for both 
 * theme resolution via the request and theme selection via request and response.
 *
 * This interface allows for implementations based on session, cookies, etc. The default 
 * implementation is CookieThemeResolver, which uses cookie for theme resolution and
 * selection.
 *
 * Note that this resolver is only responsible for determining the current theme name. 
 * The Theme instance for the resolved theme name must be looked up via ThemeRegistry
 * interface. For example, the user could call ThemeFns.getThemeRegistry() to obtain
 * the current theme registry, and then call themeRegistry.getTheme(themeName) to
 * retrieve the corresponding Theme instance, if a theme with such name is previously
 * registered.
 *
 * @author neillee
 * @since 6.5.2
 */
public interface ThemeResolver {
	/**
	 * Resolve the current intended theme name via the given request.
	 * Whether this theme name will actually be used is subject to the theme validation
	 * and fall-back strategies.
	 * 
	 * @param request request to be used for resolution
	 * @return the current intended theme name
	 */
	public String getTheme(HttpServletRequest request);

	/**
	 * Set the current intended theme name to the given one.
	 * 
	 * @param request request to be used for theme name selection
	 * @param response response to be used for theme name selection   
	 * @param themeName the new intended theme name
	 */
	public void setTheme(HttpServletRequest request, HttpServletResponse response, String themeName);
}
