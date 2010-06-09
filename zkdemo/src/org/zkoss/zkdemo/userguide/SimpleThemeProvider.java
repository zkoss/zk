/* SimpleThemeProvider.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 1, 2007 7:06:57 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import java.util.Collection;
import java.util.List;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * A simple theme provider.
 * It provides the theme for zkdemo. This class demostrates a simple use
 * that manipulates the font size by setting the execution's attribute
 * directly. Thus, it cannot be cached by the client.
 * On the other hand, {@link CacheableThemeProvider} is another example
 * that manipulates the URI for different font size such that
 * the client can cache the result.
 * @author Dennis.Chen / Jumper Chen / Tom Yeh
 */
public class SimpleThemeProvider implements ThemeProvider{
	public Collection getThemeURIs(Execution exec, List uris) {
		if ("silvergray".equals(Themes.getSkinCookie(exec))) {
			uris.add("~./silvergray/color.css.dsp");
			uris.add("~./silvergray/img.css.dsp");
		}
		return uris;
	}

	public int getWCSCacheControl(Execution exec, String uri) {
		return -1;
			//No cache since beforeWCS changes attribute based on cookie,
			//and cookie's value depends on user's preference
	}
	public String beforeWCS(Execution exec, String uri) {
		final String fsc = Themes.getFontSizeCookie(exec);
		if ("lg".equals(fsc)) {
			exec.setAttribute("fontSizeM", "15px");
			exec.setAttribute("fontSizeMS", "13px");
			exec.setAttribute("fontSizeS", "13px");
			exec.setAttribute("fontSizeXS", "12px");
		} else if ("sm".equals(fsc)) {
			exec.setAttribute("fontSizeM", "10px");
			exec.setAttribute("fontSizeMS", "9px");
			exec.setAttribute("fontSizeS", "9px");
			exec.setAttribute("fontSizeXS", "8px");
		}
		return uri;
	}

	public String beforeWidgetCSS(Execution exec, String uri) {
		return uri;
	}
}
