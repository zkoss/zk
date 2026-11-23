/* ThemeURIModifier.java

		Purpose:

		Description:

		History:
				Thu Mar 04 17:09:29 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zk.ui.util;

import java.util.List;

import org.zkoss.html.StyleSheet;

/**
 * ThemeURIModifier is used to restrict developer's operation on the theme uri list.
 *
 * @author leon
 * @since 9.6.0
 */
public interface ThemeURIModifier {
	/**
	 * Returns a read-only list of theme uri which present the current status of the CSS/WCS files list.
	 * @return a read-only list of StyleSheet
	 */
	public List<StyleSheet> getURIs();

	/**
	 * Appends the specified CSS/WCS file uri to the end of the theme uri list.
	 *
	 * @param uri the specified CSS/WCS file uri
	 */
	public void add(String uri);

	/**
	 * Inserts the specified CSS/WCS file uri at the specified position in the theme uri list according to the priority.
	 *
	 * @param priority the basis used to determine the insertion position
	 * @param uri the specified CSS/WCS file uri
	 */
	public void add(int priority, String uri);

	/**
	 * Appends the specified CSS/WCS file uri to the end of the theme uri list.
	 *
	 * @param uri the specified CSS/WCS file uri
	 */
	public void add(StyleSheet uri);

	/**
	 * Inserts the specified CSS/WCS file uri at the specified position in the theme uri list according to the priority.
	 *
	 * @param priority the basis used to determine the insertion position
	 * @param uri the specified CSS/WCS file uri
	 */
	public void add(int priority, StyleSheet uri);
}
