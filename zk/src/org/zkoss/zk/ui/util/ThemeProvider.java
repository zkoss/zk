/* ThemeProvider.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov  1 14:22:12     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.Collection;
import java.util.List;

import org.zkoss.web.servlet.StyleSheet;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.sys.Attributes;

/**
 * Used to replace the theme defined in the language definitions
 * (lang.xml and lang-addon.xml) and the configuration
 * (the <code>theme-uri</code> elements in web.xml).
 *
 * <p>When a desktop is about to be rendered, {@link #getThemeURIs} will
 * be called to allow developer to rename, add or remove CSS/WCS files.
 *
 * <p>When loading each WCS (Widget CSS descriptor) file (excluding CSS files), {@link #beforeWCS}
 * will be called to allow developer to rename or remove the WCS file.
 *
 * <p>When a WCS file is about to load the CSS file of a widget,
 * {@link #beforeWidgetCSS} will be called to allow developer to rename
 * or remove the CSS file associated with a widget.
 *
 * <p>To allow the client to cache the WCS file, you can inject a special
 * fragment into the URI of the WCS file such that a different URI represents
 * a different theme. To inject, you can use @{link Aide#injectURI} when
 * preprocessing URIs in {@link #getThemeURIs}.
 * Therefore, we can rertrieve the injected fragment in {@link #beforeWCS}
 * by use of {@link Aide#decodeURI}.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public interface ThemeProvider {
	/** Returns a list of the theme's URIs of the specified execution,
	 * or null if no theme shall be generated.
	 * Each item could be an instance of either {@link String} or {@link StyleSheet}.
	 * If you want to specify the <code>media</code> attribute, use {@link StyleSheet}.
	 *
	 * <p>It is called when a desktop is about to be rendered.
	 * It is called only once for each desktop.
	 *
	 * <p>Notice that {@link StyleSheet} is allowed since 5.0.3.
	 *
	 * @param exec the current execution (never null), where you can retrieve
	 * the desktop, request and response.
	 * Note: if your Web application supports multiple devices, you have
	 * to check {@link org.zkoss.zk.ui.Desktop#getDevice}.
	 * @param uris the default set of theme's URIs,
	 * i.e., the themes defined in language definitions (lang.xml and lang-addon.xml)
	 * and the configuration (the <code>theme-uri</code> elements in web.xml).
	 * Each URI is an instance of of either {@link String} or {@link StyleSheet}.
	 * Notice that, unless it is customized by application specific lang-addon,
	 * all URIs are, by default, String instances.
	 * @return the collection of the theme's URIs
	 * that the current desktop shall use.
	 * Each URI is an instance of of either {@link String} or {@link StyleSheet}.
	 */
	public Collection getThemeURIs(Execution exec, List uris);

	/** Returns the number of hours that the specified WCS
	 * (Widget CSS descriptor) file won't be changed.
	 * In other words, the client is allowed to cache the file until
	 * the returned hours expires.
	 *
	 * @param uri the URI of the WCS file, e.g., ~./zul/css/zk.wcs
	 * @return number of hours that the WCS file is allowed to cache.
	 * If it is never changed until next ZK upgrade, you could return 8760
	 * (the default if ThemeProvider is not specified).
	 * If you don't want the client to cache, return a nonpostive number.
	 */
	public int getWCSCacheControl(Execution exec, String uri);
	/** Called when a WCS (Widget CSS descriptor) file is about to be loaded.
	 * This method then returns the real URI of the WCS file to load.
	 * If no need to change, just return the <code>uri</code> parameter.
	 *
	 * <p>If you want to change the font size, you can set the attributes
	 * of the execution accordingly as follows.
	 *
	 * <dl>
	 * <dt>fontSizeM</dt>
	 * <dd>The default font size. Default: 12px</dd>
	 * <dt>fontSizeMS</dt>
	 * <dd>The font size for menus. Default: 11px</dd>
	 * <dt>fontSizeS</dt>
	 * <dd>The font size for smaller fonts, such as toolbar. Default: 11px</dd>
	 * <dt>fontSizeXS</dt>
	 * <dd>The font size for extreme small fonts. Default 10px</dd>
	 * <dt>fontFamilyT</dt>
	 * <dd>The font family for titles. Default: Verdana, Tahoma, Arial, Helvetica, sans-serif</dd>
	 * <dt>fontFamilyC</dt>
	 * <dd>The font family for content. Default: Verdana, Tahoma, Arial, serif</dd>
	 * </dl>
	 *
	 * <p>For example,
	 * <pre><code>String beforeWCS(Execution exec, String uri) {
	 *  exec.setAttribute("fontSizeM", "15px");
	 *  return uri;
	 *}</code></pre>
	 *
	 * @param exec the current executioin (never null), where you can retrieve
	 * the request ad responsne. However, unlike
	 * {@link #getThemeURIs}, the desktop might not be available when this
	 * method is called.
	 * @param uri the URI of the WCS file, e.g., ~./zul/css/zk.wcs
	 * @return the real URI of the WCS file to load.
	 * If null is returned, the WCS file is ignored.
	 * @since 5.0.0
	 */
	public String beforeWCS(Execution exec, String uri);

	/** Called when a WCS (Widget CSS descriptor) file is about to load the CSS file associated
	 * with a widget.
	 * This method then returns the real URI of the WCS file to load.
	 * If no need to change, just return the <code>uri</code> parameter.
	 *
	 * <p>This method is usually overriden to load the CSS files from
	 * a different directory. For example,
	 * <pre><code>String beforeWidgetCSS(Execution exec, String uri) {
	 *  return uri.startsWith("~./") ? "~./foo/" + uri.substring(3): uri;
	 *}</code></pre>
	 *
	 * @param exec the current executioin (never null), where you can retrieve
	 * the request ad responsne. However, unlike
	 * {@link #getThemeURIs}, the desktop might not be available when this
	 * method is called.
	 * @param uri the URI of the CSS file associated with a widget, e.g.,
	 * ~./js/zul/wgt/css/a.css.dsp
	 * @return the real URI of the CSS file to load
	 * If null is returned, the CSS file is ignored.
	 * @since 5.0.0
	 */
	public String beforeWidgetCSS(Execution exec, String uri);

	/** Utilties to help the implementation of {@link ThemeProvider}
	 * to manipulate the URI such that it is able to use a different URI
	 * for a different theme.
	 */
	public static class Aide {
		/** Injects a fragment into the specified URI, and returns
		 * the injected URI.
		 * @param uri the URI to be modified
		 * @param fragment the fragment that will be injected <code>uri</code>.
		 */
		public static String injectURI(String uri, String fragment) {
			if (uri.startsWith("~./")) {
				//rename / to -
				for (int j = 0, k; (k = fragment.indexOf("/", j)) >= 0;) {
					fragment = fragment.substring(0, k) + '-' + fragment.substring(k + 1);
					j = k + 1;
				}
				return "~./" + Attributes.INJECT_URI_PREFIX
					+ fragment + uri.substring(2);
			}
			return uri;
		}
		/** Decodes the injected URI and returns a two-element array.
		 * The first element is the original URI, while the second
		 * element is the fragment.
		 * <p>Notice that it returns null if no injection is found.
		 */
		public static String[] decodeURI(String uri) {
			if (uri.startsWith("~./")
			&& uri.substring(3).startsWith(Attributes.INJECT_URI_PREFIX)) {
				final int j = 3 + Attributes.INJECT_URI_PREFIX.length(),
					k = uri.indexOf('/', j);
				if (k > 0)
					return new String[] {
						"~./" + uri.substring(k + 1), uri.substring(j, k)
					};
			}
			return null;
		}
	}
}
