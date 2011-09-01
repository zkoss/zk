/* Themes.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 09:48:02     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zksandbox;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * Utilities to manipulate the cooke for theme preferences.
 *
 * @author Dennis Chen / Jumper Chen / Tom Yeh / Sam Chuang
 */
public class Themes {
	private final static String COOKIE_FONT_SIZE = "zksandboxfs";
	private final static String THEME_COOKIE_KEY = "zktheme";
	public final static String BREEZE_THEME = "breeze";
	public final static String CLASSICBLUE_THEME = "classicblue";
	public final static String SILVERGRAY_THEME = "silvergray";
	
	public final static String DEFAULT_WCS_URI = "~./zul/css/zk.wcs";
	public final static String DEFAULT_SILVERGRAY_URI = "~./silvergray";
	
	private final static String SAPPHIRE_THEME = "sapphire";
	public final static String PREFERRED_THEME = "org.zkoss.theme.preferred";
	public final static String THEME_NAMES = "org.zkoss.theme.names";
	public final static String THEME_DEFAULT = "org.zkoss.theme.default";

	/**
	 * Returns the font size specified in cookies
	 * @param exe Execution
	 * @return "lg" for larger font, "sm" for smaller font or "" for normal font.
	 */
	public static String getFontSizeCookie(Execution exe) {
		Cookie[] cookies = ((HttpServletRequest)exe.getNativeRequest()).getCookies();
		if(cookies!=null){
			for(int i=0;i<cookies.length;i++){
				if(COOKIE_FONT_SIZE.equals(cookies[i].getName())){
					String fs = cookies[i].getValue();
					if("lg".equals(fs)){
						return "lg";
					}else if("sm".equals(fs)){
						return "sm";
					}
				}
			}
		}
		return "";
	}
	
	/**
	 * Sets the font size to cookie
	 * @param exe Execution
	 * @param fontSize "lg" for larger font, "sm" for smaller font or other string for normal font.
	 */
	public static void setFondSizeCookie(Execution exe,String fontSize){
		String fs = "";
		if("lg".equals(fontSize)){
			fs = "lg";
		}else if("sm".equals(fontSize)){
			fs = "sm";
		}
		Cookie cookie = new Cookie(COOKIE_FONT_SIZE,fs);
		cookie.setMaxAge(60*60*24*30);//store 30 days
		String cp = exe.getContextPath();
		cookie.setPath(cp);
		((HttpServletResponse)exe.getNativeResponse()).addCookie(cookie);
	}

	/**
	 * Sets the theme style in cookie
	 */
	public static void setThemeStyle (Execution exe, String theme) {
		Cookie cookie = new Cookie(THEME_COOKIE_KEY, theme);
		cookie.setMaxAge(60*60*24*30);//store 30 days
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
	public static String getThemeStyle (Execution exe) {
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
	 * Returns whether has breeze library or not
	 * @return boolean
	 */
	public static boolean hasBreezeLib() {
		return "true".equals(Library.getProperty("org.zkoss.zul.themejar.breeze"));
	}

	/**
	 * Returns whether has silver library or not
	 * @return boolean
	 */
	public static boolean hasSilvergrayLib() {
		return "true".equals(Library.getProperty(DemoWebAppInit.KEY_SILVERGREY_PROPERTY));
	}

	/**
	 * Return true if current theme is Breeze
	 */
	public static boolean isBreeze(Execution exe){
		String themekey = getThemeStyle(exe);
		return BREEZE_THEME.equals(themekey) || themekey.length() == 0;
	}
	
	/**
	 * Return true if current theme is Silvergray
	 */
	public static boolean isSilvergray(){
		return SILVERGRAY_THEME.equals(getCurrentTheme());
	}
	
	/**
	 * Returns the current theme
	 * @return
	 */
	public static String getCurrentTheme() {
		// Priority
		//	1. cookie's key
		//	2. library property
		//	3. theme's first priority
		String names = Library.getProperty(THEME_NAMES);
		
		String name = getThemeStyle(Executions.getCurrent());
		if (!Strings.isEmpty(name) && containTheme(names, name)) {
			return name;
		}
		
		name = Library.getProperty(PREFERRED_THEME);
		if (!Strings.isEmpty(name) && containTheme(names, name)) {
			return name;
		}
		return Library.getProperty(THEME_DEFAULT);
	}

	/**
	 * Returns whether themes contains target theme or not
	 * @param themes
	 * @param targetTheme
	 * @return
	 */
	public static boolean containTheme(String themes, String target) {
		return !Strings.isEmpty(target) && (";" + themes + ";").contains(";" + target + ";");
	}

	public static boolean isBlueTheme () {
		return isClassicBlue() || SAPPHIRE_THEME.equals(getCurrentTheme());
	}

	public static boolean isClassicBlue(){
		return CLASSICBLUE_THEME.equals(getCurrentTheme());
	}
}
