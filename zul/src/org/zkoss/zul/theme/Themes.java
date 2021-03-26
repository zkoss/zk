/* Themes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 22, 2011 7:33:44 PM , Created by sam
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.theme;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.zkoss.lang.Strings;
import org.zkoss.web.fn.ThemeFns;
import org.zkoss.web.theme.StandardTheme;
import org.zkoss.web.theme.StandardTheme.ThemeOrigin;
import org.zkoss.web.theme.Theme;
import org.zkoss.web.theme.ThemeRegistry;
import org.zkoss.web.theme.ThemeResolver;
import org.zkoss.zk.ui.Execution;

/**
 * Facade for accessing internal theming subsystem
 * In most cases, users need not use the underlying theme registry and
 * theme resolver directly.
 * 
 * @author sam
 * @author neillee
 */
public class Themes {

	/**
	 * Should use StandardTheme.DEFAULT_NAME
	 * @deprecated since 6.5.2
	 */
	public static final String BREEZE_NAME = "breeze";

	/**
	 * Should use StandardTheme.DEFAULT_DISPLAY
	 * @deprecated since 6.5.2
	 */
	public static final String BREEZE_DISPLAY = "Breeze";

	/**
	 * Should use StandardTheme.DEFAULT_PRIORITY
	 * @deprecated since 6.5.2
	 */
	public static final int BREEZE_PRIORITY = 500;

	/**
	 * Sets the theme name using the current theme resolution strategy
	 * Default strategy is to use cookies
	 * 
	 * @param exe Execution
	 * @param themeName the new intended theme name
	 */
	public static void setTheme(Execution exe, String themeName) {
		ThemeResolver themeResolver = ThemeFns.getThemeResolver();

		themeResolver.setTheme((HttpServletRequest) exe.getNativeRequest(),
				(HttpServletResponse) exe.getNativeResponse(), themeName);
	}

	/**
	 * Returns the theme specified using the current theme resolution strategy
	 * Default strategy is to use cookies
	 * 
	 * @param exe Execution
	 * @return the name of the theme or a fall back theme name determined by the
	 * theme resolution strategy used.
	 */
	public static String getTheme(Execution exe) {
		ThemeResolver themeResolver = ThemeFns.getThemeResolver();

		return themeResolver.getTheme((HttpServletRequest) exe.getNativeRequest());
	}

	/**
	 * Returns the current theme name
	 * @return the current theme name
	 */
	public static String getCurrentTheme() {
		return ThemeFns.getCurrentTheme();
	}

	/**
	 * Returns true if the theme is registered
	 * @param themeName the name of the theme
	 * @return true if the theme with the given name is registered
	 */
	public static boolean hasTheme(String themeName) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		return themeRegistry.hasTheme(themeName);
	}

	/**
	 * Returns an array of registered theme names
	 * @return an array of registered theme names
	 */
	public static String[] getThemes() {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		Theme[] themes = themeRegistry.getThemes();
		String[] themeNames = new String[themes.length];
		for (int i = 0; i < themes.length; i++) {
			themeNames[i] = themes[i].getName();
		}
		return themeNames;
	}

	/**
	 * Register the theme, so it becomes available in the theme list
	 * 
	 * @param themeName the name of the theme to be registered
	 * Since 6.5.2, to register additional tablet themes, just prepend 
	 * theme names with the "tablet:" prefix (ZK EE only). For example, 
	 * <code>Themes.register("tablet:dark")</code> would register a tablet 
	 * theme with the name "dark".
	 */
	public static void register(String themeName) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		themeRegistry.register(new StandardTheme(themeName));
	}

	/**
	 * Register the theme, and specifies its origin (e.g. from JAR or from FOLDER)
	 * Please use <code>Themes.register("custom", Themes.ThemeOrigin.FOLDER)</code> 
	 * to make your custom theme available if the theme resource is inside a folder
	 * 
	 * @param themeName theme name
	 * Since 6.5.2, to register additional tablet themes, just prepend 
	 * theme names with the "tablet:" prefix (ZK EE only). For example, 
	 * <code>Themes.register("tablet:dark")</code> would register a tablet 
	 * theme with the name "dark".
	 * @param origin origin of the theme resource
	 * @since 6.5.2
	 */
	public static void register(String themeName, ThemeOrigin origin) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		themeRegistry.register(new StandardTheme(themeName, origin));
	}

	/**
	 * Register the theme with details
	 * 
	 * @param themeName theme name
	 * Since 6.5.2, to register additional tablet themes, just prepend 
	 * theme names with the "tablet:" prefix (ZK EE only). For example, 
	 * <code>Themes.register("tablet:dark")</code> would register a tablet 
	 * theme with the name "dark".
	 * @param displayName The human name of the theme
	 * @param priority Priority is higher if the value the smaller
	 */
	public static void register(String themeName, String displayName, int priority) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		themeRegistry.register(new StandardTheme(themeName, displayName, priority));
	}

	/**
	 * Register the theme, its display name, its priority; and also specifies 
	 * its origin (e.g. from JAR or from FOLDER). Please use 
	 * <code>Themes.register("custom", "Custom Theme", 100, Themes.ThemeOrigin.FOLDER)</code> 
	 * to make your custom theme available if the theme resource is inside a folder.
	 * 
	 * @param themeName theme name
	 * Since 6.5.2, to register additional tablet themes, just prepend 
	 * theme names with the "tablet:" prefix (ZK EE only). For example, 
	 * <code>Themes.register("tablet:dark")</code> would register a tablet 
	 * theme with the name "dark".
	 * @param displayName a more descriptive name for the theme, for display purpose
	 * @param priority priority of the theme
	 * @param origin origin of the theme resource
	 * @since 6.5.2
	 */
	public static void register(String themeName, String displayName, int priority, ThemeOrigin origin) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		themeRegistry.register(new StandardTheme(themeName, displayName, priority, origin));
	}

	/**
	 * Set the display name (human name) of the theme
	 * 
	 * @param themeName theme name
	 * Since 6.5.2, to identify tablet themes for changing display names, 
	 * just prepend theme names with the "tablet:" prefix (ZK EE only). 
	 * For example, <code>Themes.setDisplayName("tablet:dark", "foo")</code> 
	 * would change the display name for the tablet theme with the name "dark".
	 * @param displayName the new name to be displayed
	 */
	public static void setDisplayName(String themeName, String displayName) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		Theme theme = themeRegistry.getTheme(themeName);
		if (theme instanceof StandardTheme) {
			((StandardTheme) theme).setDisplayName(displayName);
		}
	}

	/**
	 * Return the display name (human name) of the theme
	 *
	 * @param themeName theme name
	 * Since 6.5.2, to identify tablet themes for getting display names, 
	 * just prepend theme names with the "tablet:" prefix (ZK EE only). 
	 * For example, <code>Themes.getDisplayName("tablet:dark")</code> would 
	 * return the display name for the tablet theme with the name "dark".
	 * @return the display name
	 */
	public static String getDisplayName(String themeName) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		Theme theme = themeRegistry.getTheme(themeName);
		String displayName = "";
		if (theme instanceof StandardTheme) {
			displayName = ((StandardTheme) theme).getDisplayName();
		}
		return Strings.isEmpty(displayName) ? capitalize(themeName) : displayName;
	}

	/**
	 * Set the priority of the theme.
	 * @param themeName theme name
	 * Since 6.5.2, to identify tablet themes for changing priorities, 
	 * just prepend theme names with the "tablet:" prefix (ZK EE only). 
	 * For example, <code>Themes.setPriority("tablet:dark", 100)</code> would 
	 * change the display name for the tablet theme with the name "dark".
	 * @param priority Priority is higher if the value the smaller
	 */
	public static void setPriority(String themeName, int priority) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		Theme theme = themeRegistry.getTheme(themeName);

		if (theme instanceof StandardTheme) {
			((StandardTheme) theme).setPriority(priority);
		}
	}

	/**
	 * Return the priority of the given theme
	 * 
	 * @param themeName theme name
	 * Since 6.5.2, to identify tablet themes for getting priorities, 
	 * just prepend theme names with the "tablet:" prefix (ZK EE only). 
	 * For example, <code>Themes.getDisplayName("tablet:dark")</code> would 
	 * return the priority for the tablet theme with the name "dark".
	 * @return the priority of the given theme
	 */
	public static int getPriority(String themeName) {
		ThemeRegistry themeRegistry = ThemeFns.getThemeRegistry();
		Theme theme = themeRegistry.getTheme(themeName);

		if (theme instanceof StandardTheme) {
			return ((StandardTheme) theme).getPriority();
		} else {
			return Integer.MAX_VALUE;
		}
	}

	// helper //

	private static String capitalize(String str) {
		if (Strings.isEmpty(str))
			return str;
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

}
