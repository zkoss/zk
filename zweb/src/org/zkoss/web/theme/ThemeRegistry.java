/* ThemeRegistry.java

	Purpose:
		
	Description:
		
	History:
		Mar 14, 2013 10:57:00 PM Created by neillee

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.web.theme;

/**
 * Used to store a list of themes available to the web application.
 * Themes should be registered before they could be used.
 * 
 * User could define their own theme registry by implementing ThemeRegistry
 * interface, and specifying the following configuration in metainfo/zk/zk.xml
 * or WEB-INF/zk.xml.
 * 
 * <desktop-config>
 *   <theme-registry-class>foo.CustomThemeRegistry</theme-registry>
 * </desktop-config>
 * 
 * @since 6.5.2
 * @author neillee
 */
public interface ThemeRegistry {

	/**
	 * Register a theme, i.e. making the theme known to the web application
	 * 
	 * Note: how to deal with duplicate registration is entirely up to the specific
	 * implementation of ThemeRegistry.
	 * 
	 * @param theme theme-specific information
	 * @return true if the theme is registered; false if it cannot be registered
	 */
	public boolean register(Theme theme);
	
	/**
	 * Remove a theme from web application's use
	 * 
	 * @param theme the theme to be removed from the registry
	 * @return true if the theme is removed; false if the theme cannot be found
	 * 
	 * @since 6.5.2
	 */
	public boolean deregister(Theme theme);

	/**
	 * @return a list of currently registered themes
	 */
	public Theme[] getThemes();
	
	/**
	 * Returns the theme identified by themeName
	 * 
	 * @param themeName the name of the theme
	 * @return the theme identified by themeName; null if the theme is not registered
	 */
	public Theme getTheme(String themeName);
	
	/**
	 * Determine if a theme is registered
	 * 
	 * @param themeName the name of the theme
	 * @return true if a theme with themeName is registered; false if not
	 */
	public boolean hasTheme(String themeName);
}
