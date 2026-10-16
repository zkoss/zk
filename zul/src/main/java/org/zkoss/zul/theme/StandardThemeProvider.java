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
import org.zkoss.lang.Strings;
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
	 * the page. This method inserts the reset URI right before the first entry that
	 * starts with {@link #DEFAULT_WCS}, before the non-default-theme suffix bypass runs.
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
	 * <p>Since ZK 11.0.0 this method inserts the reset stylesheet; earlier versions only
	 * applied the non-default-theme suffix to the widget-CSS bundle.
	 */
	public Collection<Object> getThemeURIs(Execution exec, List<Object> uris) {
		insertResetURI(uris);

		String suffix = getThemeFileSuffix();

		if (!Strings.isEmpty(suffix))
			bypassURI(uris, suffix);

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
