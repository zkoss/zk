/* StandardThemeProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 17, 2011 8:49:08 AM
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.theme;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zul.Messagebox;

/**
 * A standard implementation of ThemeProvider, which works with the Breeze series
 * themes.
 * @author simonpai
 */
public class StandardThemeProvider implements org.zkoss.zk.ui.util.ThemeProvider {
	
	public final static String DEFAULT_WCS = "~./zul/css/zk.wcs";
	public final static String DEFAULT_MSGBOX_TEMPLATE_URI = "~./zul/html/messagebox.zul";
	
	public Collection<Object> getThemeURIs(Execution exec, List<Object> uris) {
		String suffix = getThemeFileSuffix();
		if (Strings.isEmpty(suffix)) {
			Messagebox.setTemplate(DEFAULT_MSGBOX_TEMPLATE_URI);
			return uris;
		}
		
		if (isUsingDefaultTemplate(suffix))
			Messagebox.setTemplate(getThemeMsgBoxURI(suffix));
		
		bypassURI(uris, suffix);
		return uris;
	}
	
	private static String getThemeFileSuffix() {
		String suffix = Themes.getCurrentTheme();
		return Themes.CLASSICBLUE_NAME.equals(suffix) ? null : suffix;
	}
	
	private static String getThemeMsgBoxURI(String suffix) {
		return "~./zul/html/messagebox." + suffix + ".zul";
	}
	
	private static boolean isUsingDefaultTemplate(String suffix){
		return getThemeMsgBoxURI(suffix).equals(Messagebox.getTemplate()) ||
			DEFAULT_MSGBOX_TEMPLATE_URI.equals(Messagebox.getTemplate());
	}
	
	private void bypassURI(List<Object> uris, String suffix) {
		for (ListIterator<Object> it = uris.listIterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof String) {
				final String uri = (String)o;
				if (uri.startsWith(DEFAULT_WCS)) {
					it.set(Aide.injectURI(uri, suffix));
					break;
				}
			}
		}
	}
	
	public int getWCSCacheControl(Execution exec, String uri) {
		return 8760; // a year. (JVM will utilize it, don't have to count the answer)
	}
	
	public String beforeWCS(Execution exec, String uri) {
		return uri;
	}
	
	public String beforeWidgetCSS(Execution exec, String uri) {
		String suffix = getThemeFileSuffix();
		if (Strings.isEmpty(suffix)) return uri;
		
		if(uri.startsWith("~./js/zul/") || 
				uri.startsWith("~./js/zkex/") || 
				uri.startsWith("~./js/zkmax/")){
			return uri.replaceFirst(".css.dsp", getWidgetCSSName(suffix));
		}
		return uri;
	}
	
	private static String getWidgetCSSName(String suffix) {
		return "." + suffix + ".css.dsp";
	}
	
}
