/* CacheableThemeProvider.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 27 10:01:56     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zksandbox;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.zkoss.zul.Messagebox;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ThemeProvider;
import org.zkoss.zk.ui.util.ThemeProvider.Aide;

/**
 * A cacheable theme provider.
 * It provides the theme for zksandbox. This class demostrates how to provide
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
 * @author tomyeh / samchuang
 * @since 5.0.0
 */
public class CacheableThemeProvider implements ThemeProvider{
	private final static String DEFAULT_WCS = "~./zul/css/zk.wcs";
	private final static String DEFAULT_MSGBOX_TEMPLATE_URI = "~./zul/html/messagebox.zul";
	
	public Collection getThemeURIs(Execution exec, List uris) {
		String suffix = getThemeFileSuffix();
		String fsc = Themes.getFontSizeCookie(exec);
		boolean isSilvergray = Themes.isSilvergray() && Themes.hasSilvergrayLib();
		processSilverAndFontURI(isSilvergray, uris, fsc);
		
		if ("lg".equals(fsc)) {
			uris.add("/css/fontlg.css.dsp");
			if (isSilvergray)
				uris.add("/css/silvergraylg.css.dsp");
		} else if ("sm".equals(fsc)) {
			uris.add("/css/fontsm.css.dsp");
			if (isSilvergray)
				uris.add("/css/silvergraysm.css.dsp");
		}
		if (Strings.isEmpty(suffix)) {
			Messagebox.setTemplate(DEFAULT_MSGBOX_TEMPLATE_URI);
			return uris;
		}
		if (isUsingDefaultTemplate(suffix))
			Messagebox.setTemplate(getThemeMsgBoxURI(suffix));
		
		//slivergray
		if (isSilvergray) {
			uris.add("~./silvergray/color.css.dsp");
			uris.add("~./silvergray/img.css.dsp");
		} 
		
		bypassURI(uris, suffix);
		return uris;
	}
	
	/**
	 * Setup inject URI for font and silvergray
	 * @param isSilver
	 * @param uris
	 * @param fsn
	 */
	private static void processSilverAndFontURI (boolean isSilver, List uris, String fsn) {
		for (ListIterator it = uris.listIterator(); it.hasNext();) {
			final String uri = (String)it.next();
			if (isSilver) {
				if (uri.startsWith(Themes.DEFAULT_WCS_URI)) {
					String injectURI = Themes.SILVERGRAY_THEME + ((fsn != null && fsn.length() > 0) ? "-" + fsn : "");
					it.set(Aide.injectURI(uri, injectURI));
				}
			} else {
				/*Remove silvergray URI*/
				if (uri.startsWith(Themes.DEFAULT_SILVERGRAY_URI))
					it.remove();
				else if (fsn != null && fsn.length() > 0 && uri.startsWith(Themes.DEFAULT_WCS_URI))
					it.set(Aide.injectURI(uri, fsn));
			}
		}
	}

	public int getWCSCacheControl(Execution exec, String uri) {
		return 8760; //safe to cache
	}
	public String beforeWCS(Execution exec, String uri) {
		if(!Themes.isClassicBlue() && !Themes.isSilvergray())
			return uri;
		final String[] dec = Aide.decodeURI(uri);
		if (dec != null) {
			if ("lg".equals(dec[1]) || "silvergray-lg".equals(dec[1])) {
				exec.setAttribute("fontSizeM", "15px");
				exec.setAttribute("fontSizeMS", "13px");
				exec.setAttribute("fontSizeS", "13px");
				exec.setAttribute("fontSizeXS", "12px");
			} else if ("sm".equals(dec[1]) || "silvergray-sm".equals(dec[1])) {
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
		if(Themes.isSilvergray() && Themes.hasSilvergrayLib())
			return uri;
		
		String suffix = getThemeFileSuffix();
		if (Strings.isEmpty(suffix)) return uri;

		if(uri.startsWith("~./zul/css/") ||
				uri.startsWith("~./js/zul/") || 
				uri.startsWith("~./js/zkex/") || 
				uri.startsWith("~./js/zkmax/")){
			return uri.replaceFirst(".css.dsp", getWidgetCSSName(suffix));
		}
		return uri;
	}

	private static String getThemeFileSuffix() {
		String suffix = Themes.getCurrentTheme();
		return Themes.CLASSICBLUE_THEME.equals(suffix) ? null : suffix;
	}

	private static boolean isUsingDefaultTemplate(String suffix){
		return getThemeMsgBoxURI(suffix).equals(Messagebox.getTemplate()) ||
			DEFAULT_MSGBOX_TEMPLATE_URI.equals(Messagebox.getTemplate());
	}

	private static String getThemeMsgBoxURI(String suffix) {
		return "~./zul/html/messagebox." + suffix + ".zul";
	}
	
	private void bypassURI(List uris, String suffix) {
		for (ListIterator it = uris.listIterator(); it.hasNext();) {
			final String uri = (String)it.next();
			if (uri.startsWith(DEFAULT_WCS)) {
				it.set(Aide.injectURI(uri, suffix));
				break;
			} 
		}
	}

	private static String getWidgetCSSName(String suffix) {
		return "." + suffix + ".css.dsp";
	}
}
