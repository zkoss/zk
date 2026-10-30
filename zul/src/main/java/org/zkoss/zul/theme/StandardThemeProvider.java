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

import org.zkoss.lang.Library;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.web.theme.StandardTheme;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;

/**
 * A standard implementation of ThemeProvider for ZK CE, which works with the
 * Marble theme (ZK 11.0 and later; formerly the Breeze series themes)
 *
 * @author simonpai
 * @author jumperchen
 * @author neillee
 */
public class StandardThemeProvider implements ThemeProvider {

	/**
	 * Default theme css file
	 */
	public static final String DEFAULT_WCS = "~./zul/css/zk.wcs";

	private static final String BROWSER_DEFAULT = "org.zkoss.zul.theme.browserDefault";
	private static final String RESET_GLOBAL = "~./zul/css/reset.css";
	private static final String RESET_EMBED = "~./zul/css/reset-embed.css";

	protected static String getThemeFileSuffix() {
		String suffix = Themes.getCurrentTheme();
		return StandardTheme.DEFAULT_NAME.equals(suffix) ? null : suffix;
	}

	/**
	 * Returns whether the current theme is the default theme (Marble), which ships
	 * its own reset stylesheet and no Font Awesome.
	 * @since 11.0.0
	 */
	protected static boolean isDefaultTheme() {
		return getThemeFileSuffix() == null;
	}

	private void bypassURI(List<Object> uris, String suffix) {
		for (ListIterator<Object> it = uris.listIterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof String) {
				final String uri = (String) o;
				if (uri.startsWith(DEFAULT_WCS)) {
					it.set(Aide.injectURI(uri, suffix));
					break;
				}
			}
		}
	}

	private void insertResetURI(List<Object> uris) {
		final boolean browserDefault = Boolean.parseBoolean(Library.getProperty(BROWSER_DEFAULT, "false"));
		final String resetUri = ServletFns.resolveThemeURL(browserDefault ? RESET_EMBED : RESET_GLOBAL);

		int index = 0; // no zk.wcs entry found: load the reset first
		for (int i = 0; i < uris.size(); i++) {
			Object o = uris.get(i);
			if (o instanceof String && ((String) o).startsWith(DEFAULT_WCS)) {
				index = i;
				break;
			}
		}
		uris.add(index, resetUri);
	}

	/**
	 * Marble ships a reset stylesheet that must be loaded immediately before ZK's
	 * widget-CSS bundle ({@code zk.wcs}) so it keeps its "first" cascade position in
	 * the page. For the default theme this method inserts the reset URI right before the
	 * first entry that starts with {@link #DEFAULT_WCS}; for any other theme it only applies
	 * the theme suffix to that entry, since a legacy theme resets in its own norm.css.dsp.
	 *
	 * <p>Which reset stylesheet is served is controlled by the library property
	 * {@code org.zkoss.zul.theme.browserDefault} (boolean, default {@code false}):
	 * <ul>
	 * <li>{@code false} (default) &mdash; serve the standard global reset
	 * ({@code ~./zul/css/reset.css}), which overrides browser defaults page-wide.</li>
	 * <li>{@code true} &mdash; serve the host-safe reset ({@code ~./zul/css/reset-embed.css}),
	 * scoped so it does not override a host page's own styles; use this when a ZK page
	 * is embedded inside another (non-ZK) page.</li>
	 * </ul>
	 *
	 * <p>Since ZK 11.0.0 this method inserts the reset stylesheet for the default theme;
	 * earlier versions only applied the non-default-theme suffix to the widget-CSS bundle.
	 */
	public Collection<Object> getThemeURIs(Execution exec, List<Object> uris) {
		// for compatibility with the legacy themes (iceblue and the like), which reset in their own norm.css.dsp
		if (isDefaultTheme())
			insertResetURI(uris);
		else
			bypassURI(uris, getThemeFileSuffix());

		return uris;
	}

	public int getWCSCacheControl(Execution exec, String uri) {
		return 8760; // a year. (JVM will utilize it, don't have to count the answer)
	}

	public String beforeWCS(Execution exec, String uri) {
		return uri;
	}

	public String beforeWidgetCSS(Execution exec, String uri) {
		if (uri.startsWith("~./zul/css/") || uri.startsWith("~./js/zul/") || uri.startsWith("~./zul/font/")) {
			uri = ServletFns.resolveThemeURL(uri);
		}

		return uri;
	}

}
