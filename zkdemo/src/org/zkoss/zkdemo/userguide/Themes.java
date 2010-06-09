/* Themes.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 09:48:02     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.userguide;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Execution;

/**
 * Utilities to manipulate the cooke for theme preferences.
 *
 * @author Dennis Chen / Jumper Chen / Tom Yeh
 */
public class Themes {
	private static String COOKIE_FONT_SIZE = "zkdemotfs";
	private static String COOKIE_SKIN = "zkdemoskin";

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
	 * Returns the skin value specified in cookie
	 */
	public static String getSkinCookie(Execution exe) {
		Cookie[] cookies = ((HttpServletRequest)exe.getNativeRequest()).getCookies();
		if(cookies!=null){
			for(int i=0;i<cookies.length;i++){
				if(COOKIE_SKIN.equals(cookies[i].getName())){
					String fs = cookies[i].getValue();
					if (fs != null)
						return fs;
				}
			}
		}
		return "";
	}

	/**
	 * Sets the skin value to cookie
	 */
	public static void setSkinCookie(Execution exe,String skin){
		Cookie cookie = new Cookie(COOKIE_SKIN, skin);
		cookie.setMaxAge(60*60*24*30);//store 30 days
		String cp = exe.getContextPath();
		cookie.setPath(cp);
		((HttpServletResponse)exe.getNativeResponse()).addCookie(cookie);
	}
}
