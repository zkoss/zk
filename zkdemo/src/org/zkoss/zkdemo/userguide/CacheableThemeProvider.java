/* CacheableThemeProvider.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 10:01:56     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.userguide;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * A cacheable theme provider.
 * It provides the theme for zkdemo. This class demostrates how to provide
 * a theme provider that can be cached by the client.
 * <p>The basic algorithm is as follows.
 * <ol>
 * <li>Rename the URI in {@link #getThemeURIs} such that a different URI
 * is associated with a different font size.</li>
 * <li>Rename the URI back in {@link #beforeWCS}</li>
 * <li>Return 8760 (or any positive number to specify URI won't be changed)
 * since it is safe to cache</li>
 * </ol>
 * <p>See also {@link SimpleThemeProvider} - a simple theme provider.
 * @author tomyeh
 * @since 5.0.0
 */
public class CacheableThemeProvider implements ThemeProvider{
	private static String DEFAULT_WCS = "~./zul/css/zk.wcs";

	public Collection getThemeURIs(Execution exec, List uris) {
		//font-size
		final String fsc = Themes.getFontSizeCookie(exec);
		if (fsc != null && fsc.length() > 0) {
			for (ListIterator it = uris.listIterator(); it.hasNext();) {
				final String uri = (String)it.next();
				if (uri.startsWith(DEFAULT_WCS)) {
					it.set(Aide.injectURI(uri, fsc));
					break;
				}
			}
		}

		//slivergray
		if ("silvergray".equals(Themes.getSkinCookie(exec))) {
			uris.add("~./silvergray/color.css.dsp");
			uris.add("~./silvergray/img.css.dsp");
		}
		
		return uris;
	}

	public int getWCSCacheControl(Execution exec, String uri) {
		return 8760; //safe to cache
	}
	public String beforeWCS(Execution exec, String uri) {
		final String[] dec = Aide.decodeURI(uri);
		if (dec != null) {
			if ("lg".equals(dec[1])) {
				exec.setAttribute("fontSizeM", "15px");
				exec.setAttribute("fontSizeMS", "13px");
				exec.setAttribute("fontSizeS", "13px");
				exec.setAttribute("fontSizeXS", "12px");
			} else if ("sm".equals(dec[1])) {
				exec.setAttribute("fontSizeM", "10px");
				exec.setAttribute("fontSizeMS", "9px");
				exec.setAttribute("fontSizeS", "9px");
				exec.setAttribute("fontSizeXS", "8px");
			}
			return dec[0];
		}
		return uri;
	}

	public String beforeWidgetCSS(Execution exec, String uri) {
		return uri;
	}
}
