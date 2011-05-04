package org.zkoss.zksandbox;

/* Themes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 30, 2010 3:58:02 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * Utilities to manipulate the cookie for theme preferences. Modified from 
 * org.zkoss.zkdemo.userguide.Themes.java of the previous live demo.
 * 
 * @author Dennis Chen / Jumper Chen / Tom Yeh / Sam Chuang / Simon Pai
 */
public class Themes2 {

	
	private static Properties themesProperty = null;
	/**
	 * Sets the theme style in cookie
	 */
	public static void setTheme (Execution exe, String theme) {
		Cookie cookie = new Cookie(	getProperty("THEME_COOKIE_KEY"),
				theme);
		cookie.setMaxAge(60*60*24*30); //store 30 days
		String cp = exe.getContextPath();
		// if path is empty, cookie path will be request path, which causes problems
		if(cp.isEmpty())
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
			if(!getProperty("THEME_COOKIE_KEY").equals(c.getName())) 
				continue;
			String theme = c.getValue();
			if(theme != null) 
				return theme;
		}
		return "";
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
		String names = Library.getProperty(getProperty("THEME_NAMES"));
		
		String name = getTheme(Executions.getCurrent());
		if (!Strings.isEmpty(name) && containTheme(names, name)) {
			return name;
		}
		
		name = Library.getProperty(getProperty("PREFERRED_THEME"));
		if (!Strings.isEmpty(name) && containTheme(names, name)) {
			return name;
		}
		return Library.getProperty(getProperty("THEME_DEFAULT"));
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

	public static boolean isClassicBlue(){
		return getCurrentTheme().contains(getProperty("CLASSICBLUE_THEME"));
	}
	/**
	 * @deprecated
	 * Returns whether has breeze library or not
	 * @return boolean
	 */
	public static boolean hasBreezeLib() {
		return "true".equals(Library.getProperty("org.zkoss.zul.themejar.breeze"));
	}
	
	/**
	 * @deprecated
	 * Returns whether has breeze library or not
	 * @return boolean
	 */
	public static boolean hasSilvertailLib() {
		return "true".equals(Library.getProperty("org.zkoss.zul.themejar.silvertail"));
	}
	
	/**
	 * @deprecated
	 * Returns whether has breeze library or not
	 * @return boolean
	 */
	public static boolean hasSapphireLib() {
		return "true".equals(Library.getProperty("org.zkoss.zul.themejar.sapphire"));
	}

	/**
	 * Returns whether has silver library or not
	 * @return boolean
	 */
	public static boolean hasSilvergrayLib() {
		return "true".equals(Library.getProperty("org.zkoss.zul.themejar.silvergray"));
	}

	/**
	 * @deprecated
	 * Return true if current theme is Breeze or theme is not specified 
	 */
	public static boolean isBreeze(Execution exe){
		String themekey = getTheme(exe);
		return getProperty("BREEZE_THEME")
			.equals(themekey) || themekey.length() == 0;
	}
	
	/**
	 * @deprecated
	 * Return true if current theme is Breeze or theme is not specified 
	 */
	public static boolean isSilvertail(Execution exe){
		String themekey = getTheme(exe);
		return getProperty("SILVERTAIL_THEME")
			.equals(themekey) || themekey.length() == 0;
	}
	
	/**
	 * @deprecated
	 * Return true if current theme is Breeze or theme is not specified 
	 */
	public static boolean isSapphire(Execution exe){
		String themekey = getTheme(exe);
		return getProperty("SAPPHIRE_THEME")
			.equals(themekey) || themekey.length() == 0;
	}
	
	/**
	 * @deprecated
	 * Return true if current theme is Silvergray
	 */
	public static boolean isSilvergray(Execution exe){
		return getProperty("SILVERGRAY_THEME")
			.equals(getTheme(exe));
	}
	
	/**
	 * Returns the font size specified in cookies
	 * @param exe Execution
	 * @return "lg" for larger font, "sm" for smaller font or "" for normal font.
	 */
	public static String getFontSizeCookie(Execution exe) {
		Cookie[] cookies = ((HttpServletRequest)exe.getNativeRequest()).getCookies();
		if(cookies!=null){
			for(int i=0;i<cookies.length;i++){
				if(getProperty("COOKIE_FONT_SIZE")
						.equals(cookies[i].getName())){
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
		Cookie cookie = new Cookie(getProperty("COOKIE_FONT_SIZE"),fs);
		cookie.setMaxAge(60*60*24*30);//store 30 days
		String cp = exe.getContextPath();
		cookie.setPath(cp);
		((HttpServletResponse)exe.getNativeResponse()).addCookie(cookie);
	}
	
	public static Properties getThemesProperty(){
		if(themesProperty == null){
			themesProperty = new Properties();
			try {
				FileInputStream fis = new FileInputStream("src/archive/themes.properties");
				themesProperty.load(fis);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return themesProperty;
	}
	public static String getProperty(String key){
		return getThemesProperty().getProperty(key);
	}
}
