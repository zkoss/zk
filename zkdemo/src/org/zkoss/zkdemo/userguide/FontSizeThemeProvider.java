/* FontSizeThemeProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 1, 2007 7:06:57 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * @author Dennis.Chen
 *
 */
public class FontSizeThemeProvider implements ThemeProvider{

	private static String _cssPrefix = "~./zul/css/norm";
	private static String _cookieName = "zkdemotfs";
	
	public Collection getThemeURIs(Execution exe, List uris) {
		int size = uris.size();
		for(int i=0;i<size;i++){
			String uri = (String)uris.get(i);
			if(uri.startsWith(_cssPrefix)){
				uri = _cssPrefix+getFontSizeCookie(exe)+uri.substring(_cssPrefix.length());
				uris.set(i,uri);
			}
		}
		return uris;
	}

	/**
	 * get font size value from cookie
	 * @param exe Execution
	 * @return "lg" for larger font, "sm" for smaller font or "" for normal font.
	 */
	public static  String getFontSizeCookie(Execution exe) {
		Cookie[] cookies = ((HttpServletRequest)exe.getNativeRequest()).getCookies();
		if(cookies!=null){
			for(int i=0;i<cookies.length;i++){
				if(_cookieName.equals(cookies[i].getName())){
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
	 * set font size value to cookie
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
		Cookie cookie = new Cookie(_cookieName,fs);
		cookie.setMaxAge(60*60*24*30);//store 30 days
		String cp = exe.getContextPath();
		cookie.setPath(cp);
		((HttpServletResponse)exe.getNativeResponse()).addCookie(cookie);
	}

}
