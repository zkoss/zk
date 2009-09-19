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
 * @author tomyeh
 * @since 3.0.0
 */
public interface ThemeProvider {
	/** Returns a list of the theme's URIs (Collection&lt;String&gt;) of the specified execution,
	 * or null if no theme shall be generated.
	 *
	 * @param exec the current execution (never null), where you can retrieve
	 * the current desktop, request and response.
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
}
