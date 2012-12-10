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
package org.zkoss.zk.ui.theme;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * A standard implementation of ThemeProvider, which works with the Breeze series
 * themes.
 * @author simonpai
 */
public class StandardThemeProvider implements ThemeProvider {
	
	public final static String DEFAULT_WCS = "~./zul/css/zk.wcs";

	public final static String TABLET_THEME_DISABLED_KEY = "org.zkoss.zkmax.tablet.theme.disabled";
	public final static String DEFAULT_TABLET_CSS = "~./zkmax/css/tablet.css.dsp";
	
	private boolean isMobile(Execution exec) {
		Double number = exec.getBrowser("mobile");
		return (number != null && number.doubleValue() > 0); 
	}
	
	private boolean isTabletThemeSupported(Execution exec) {
		return isMobile(exec) && "EE".equals(WebApps.getEdition()) &&
				!"true".equals(Library.getProperty(TABLET_THEME_DISABLED_KEY, "false"));
	}
	
	private static String getThemeFileSuffix() {
		String suffix = Themes.getCurrentTheme();
		return Themes.BREEZE_NAME.equals(suffix) ? null : suffix;
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
		
		if (isTabletThemeSupported(exec))
			uris.add(ServletFns.resolveThemeURL(DEFAULT_TABLET_CSS));
		
		return uris;
	}
	
	@Override
	public int getWCSCacheControl(Execution exec, String uri) {
		return 8760; // a year. (JVM will utilize it, don't have to count the answer)
	}
	
	@Override
	public String beforeWCS(Execution exec, String uri) {
		if (isTabletThemeSupported(exec)) {
			exec.setAttribute("fontSizeM", "18px");
			exec.setAttribute("fontSizeMS", "16px");
			exec.setAttribute("fontSizeS", "14px");
			exec.setAttribute("fontSizeXS", "12px");
			exec.setAttribute("fontFamilyT", "\"Helvetica Neue\", Helvetica, Arial, \"Lucida Grande\", sans-serif");
			exec.setAttribute("fontFamilyC", "\"Helvetica Neue\", Helvetica, Arial, \"Lucida Grande\", sans-serif");
		}
		return uri;
	}
	
	private static Map<String, Boolean> _ignoreTheme = null;
	static {
		if ("EE".equals(WebApps.getEdition())) {
			_ignoreTheme = new HashMap<String, Boolean>();
    		_ignoreTheme.put("~./js/zul/box/css/box.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/inp/css/combo.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/inp/css/input.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/inp/css/slider.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/db/css/calendar.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/grid/css/grid.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/mesh/css/paging.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/menu/css/menu.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/sel/css/listbox.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/sel/css/tree.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/tab/css/tabbox.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/button.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/combobutton.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/caption.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/groupbox.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/popup.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/progressmeter.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/separator.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wgt/css/toolbar.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wnd/css/panel.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/wnd/css/window.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zul/layout/css/borderlayout.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zkex/grid/css/grid.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zkex/inp/css/colorbox.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zkmax/inp/css/chosenbox.css.dsp", Boolean.TRUE);
    		_ignoreTheme.put("~./js/zkmax/big/css/biglistbox.css.dsp", Boolean.TRUE);
		}
	}
	
	@Override
	public String beforeWidgetCSS(Execution exec, String uri) {
		if (isTabletThemeSupported(exec) &&
			_ignoreTheme != null && _ignoreTheme.containsKey(uri))
			return null;
		
		String suffix = getThemeFileSuffix();
		if (Strings.isEmpty(suffix)) return uri;

		if (uri.startsWith("~./zul/css/") ||
			uri.startsWith("~./js/zul/")  || 
			uri.startsWith("~./js/zkex/") || 
			uri.startsWith("~./js/zkmax/") ||
			uri.startsWith("~./zkmax")) {
			
			uri = ServletFns.resolveThemeURL(uri);
		}
		
		return uri;
	}
	
}
