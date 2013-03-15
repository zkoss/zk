/* StandardThemeProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 17, 2011 8:49:08 AM
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.theme;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.zkoss.lang.Strings;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.web.theme.StandardTheme;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * A standard implementation of ThemeProvider for ZK CE, which works with the 
 * Breeze series themes
 * 
 * @author simonpai
 * @author jumperchen
 * @author neillee
 */
public class StandardThemeProvider implements ThemeProvider {
	
	/**
	 * Default theme css file
	 */
	public final static String DEFAULT_WCS = "~./zul/css/zk.wcs";
	
	protected static String getThemeFileSuffix() {
		String suffix = Themes.getCurrentTheme();
		return StandardTheme.DEFAULT_NAME.equals(suffix) ? null : suffix;
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
	
	@Override
	public Collection<Object> getThemeURIs(Execution exec, List<Object> uris) {
		String suffix = getThemeFileSuffix();
		
		if (!Strings.isEmpty(suffix))
			bypassURI(uris, suffix);
				
		return uris;
	}
	
	@Override
	public int getWCSCacheControl(Execution exec, String uri) {
		return 8760; // a year. (JVM will utilize it, don't have to count the answer)
	}
	
	@Override
	public String beforeWCS(Execution exec, String uri) {
		return uri;
	}
	
	@Override
	public String beforeWidgetCSS(Execution exec, String uri) {
		if (uri.startsWith("~./zul/css/") ||
			uri.startsWith("~./js/zul/")) {
			
			uri = ServletFns.resolveThemeURL(uri);
		}
		
		return uri;
	}
	
}
