/* ThemeURIHandler.java

		Purpose:

		Description:

		History:
				Thu Mar 04 17:09:29 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Execution;

/**
 * Used to add theme uri dynamically without extending any ThemeProvider.
 * Different from ThemeProvider, ThemeURIHandler is composable with other ThemeURIHandler.
 *
 * <p>When a desktop is about to be rendered, {@link #modifyThemeURIs} will
 * be called before {@link ThemeProvider} to allow developer to add CSS/WCS files.
 * The earlier the ThemeURIHandler being added to Configuration is processed earlier.
 * (first-in-first-out).
 *
 * Note: {@link ThemeProvider} still has the final decision.
 * ThemeURIHandler will always be executed.
 *
 * @author leon
 * @since 9.6.0
 */
public interface ThemeURIHandler {
	/**
	 * Allow developer to modify the theme uri list through {@link ThemeURIModifier} when a desktop is about to be rendered.
	 *
	 * @param exec the current execution (never null), where you can retrieve
	 * the desktop, request and response.
	 * Note: if your Web application supports multiple devices, you have
	 * to check {@link org.zkoss.zk.ui.Desktop#getDevice}.
	 * @param modifier {@link ThemeURIModifier} provide methods (e.g. add(StyleSheet uri))
	 * to modify the theme uri list.
	 */
	public void modifyThemeURIs(Execution exec, ThemeURIModifier modifier);
}
