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

import org.zkoss.zk.ui.Execution;

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
 * @author tomyeh
 * @since 3.0.0
 */
public interface ThemeProvider {
	/** Returns a list of the theme's URIs (Collection&lt;String&gt;) of the specified execution,
	 * or null if no theme shall be generated.
	 *
	 * <p>It is called when a desktop is about to be rendered.
	 * It is called only once for each desktop.
	 *
	 * @param exec the current execution (never null), where you can retrieve
	 * the desktop, request and response.
	 * Note: if your Web application supports multiple devices, you have
	 * to check {@link org.zkoss.zk.ui.Desktop#getDevice}.
	 * @param uris the default set of theme's URIs (List&lt;String&gt;),
	 * i.e., the themes defined in language definitions (lang.xml and lang-addon.xml)
	 * and the configuration (the <code>theme-uri</code> elements in web.xml).
	 * Each URI is a String instance.
	 * @return the collection of the theme's URIs (Collection&lt;String&gt;)
	 * that the current desktop shall use.
	 * Each URI is a String instance.
	 */
	public Collection getThemeURIs(Execution exec, List uris);

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
}
