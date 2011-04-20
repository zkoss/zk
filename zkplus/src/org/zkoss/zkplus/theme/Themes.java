/* Themes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 22, 2011 7:33:44 PM , Created by sam
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.theme;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * Utilities for managing standard theme. 
 * @author sam
 */
public class Themes {
	
	private final static String THEME_COOKIE_KEY = "zktheme";
	private final static String THEME_PREFERRED_KEY = "org.zkoss.theme.preferred";
	private final static String THEME_NAMES_KEY = "org.zkoss.theme.names";
	private final static String THEME_DEFAULT_KEY = "org.zkoss.theme.default.name";
	private final static String THEME_PRIORITY_PREFIX = "org.zkoss.theme.priority.";
	private final static String THEME_DISPLAY_NAME_PREFIX = "org.zkoss.theme.display.";
	
	public final static String CLASSICBLUE_NAME = "classicblue";
	public final static String CLASSICBLUE_DISPLAY = "Classic Blue";
	public final static int CLASSICBLUE_PRIORITY = 1000;
	
	/**
	 * Sets the theme name in cookie
	 */
	public static void setTheme (Execution exe, String theme) {
		Cookie cookie = new Cookie(THEME_COOKIE_KEY, theme);
		cookie.setMaxAge(60*60*24*30); //store 30 days
		String cp = exe.getContextPath();
		// if path is empty, cookie path will be request path, which causes problems
		if(cp.length() == 0)
			cp = "/";
		cookie.setPath(cp);
		((HttpServletResponse)exe.getNativeResponse()).addCookie(cookie);
	}
	
	/**
	 * Returns the theme specified in cookies
	 * @param exe Execution
	 * @return the name of the theme or "" for default theme.
	 */
	public static String getTheme (Execution exe) {
		Cookie[] cookies = 
			((HttpServletRequest)exe.getNativeRequest()).getCookies();
		if(cookies == null) 
			return "";
		for(int i=0; i < cookies.length; i++){
			Cookie c = cookies[i];
			if(THEME_COOKIE_KEY.equals(c.getName())) {
				String theme = c.getValue();
				if(theme != null) 
					return theme;
			}
		}
		return "";
	}
	
	/**
	 * Returns the current theme name
	 */
	public static String getCurrentTheme() {
		String themes = getThemeString();
		
		// 1. cookie's key
		String t = getTheme(Executions.getCurrent());
		if (contains(themes, t))
			return t;
		
		// 2. library property
		t = Library.getProperty(THEME_PREFERRED_KEY);
		if (contains(themes, t))
			return t;
		
		// 3. theme of highest priority
		return Library.getProperty(THEME_DEFAULT_KEY);
	}
	
	/**
	 * Returns true if the theme is registered
	 */
	public static boolean hasTheme(String theme) {
		return contains(getThemeString(), theme);
	}
	
	/**
	 * Return an array of all registered theme names
	 */
	public static String[] getThemes() {
		return getThemeString().split(";");
	}
	
	/**
	 * Register the theme, so it becomes available in the theme list
	 */
	public static void register(String theme) {
		String themes = getThemeString();
		if (Strings.isEmpty(themes))
			Library.setProperty(THEME_NAMES_KEY, theme);
		else if (!contains(themes, theme))
			Library.setProperty(THEME_NAMES_KEY, themes + ";" + theme);
	}
	
	/**
	 * Register the theme with details
	 * @param displayName The human name of the theme
	 * @param priority Priority is higher if the value the smaller
	 */
	public static void register(String theme, String displayName, int priority) {
		register(theme);
		setDisplayName(theme, displayName);
		setPriority(theme, priority);
	}
	
	/**
	 * Set the display name (human name) of the theme
	 */
	public static void setDisplayName(String theme, String name) {
		Library.setProperty(THEME_DISPLAY_NAME_PREFIX + theme, name);
	}
	
	/**
	 * Return the display name (human name) of the theme
	 */
	public static String getDisplayName(String theme) {
		String name = Library.getProperty(THEME_DISPLAY_NAME_PREFIX + theme);
		return Strings.isEmpty(name) ? capitalize(theme) : name;
	}
	
	/**
	 * Set the priority of the theme.
	 * @param priority Priority is higher if the value the smaller
	 */
	public static void setPriority(String theme, int priority) {
		Library.setProperty(THEME_PRIORITY_PREFIX + theme, "" + priority);
		// update default theme
		if (getPriority(Library.getProperty(THEME_DEFAULT_KEY)) >= priority)
			Library.setProperty(THEME_DEFAULT_KEY, theme);
	}
	
	/**
	 * Return the priority of the given theme
	 */
	public static int getPriority(String theme) {
		return Library.getIntProperty(THEME_PRIORITY_PREFIX + theme, Integer.MAX_VALUE);
	}
	
	
	
	// helper //
	private static String getThemeString() {
		return Library.getProperty(THEME_NAMES_KEY);
	}
	
	private static boolean contains(String themes, String target) {
		return !Strings.isEmpty(target) && (";" + themes + ";").contains(";" + target + ";");
	}
	
	private static String capitalize(String str) {
		if(Strings.isEmpty(str))
			return str;
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}
	
}
