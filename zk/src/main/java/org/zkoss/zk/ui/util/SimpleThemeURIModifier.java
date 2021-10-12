/* SimpleThemeURIModifier.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 04 17:09:29 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;

import java.util.Collections;
import java.util.List;

import org.zkoss.html.StyleSheet;

/**
 * A simple implementation of {@link ThemeURIModifier}.
 * The adding priority is basically the index of the uri list.
 *
 * @author leon
 * @since 9.6.0
 */
public class SimpleThemeURIModifier implements ThemeURIModifier {
	private List<StyleSheet> _uris;

	public SimpleThemeURIModifier(List<StyleSheet> originalList) {
		_uris = originalList;
	}

	@Override
	public List<StyleSheet> getURIs() {
		return Collections.unmodifiableList(_uris);
	}

	@Override
	public void add(String href) {
		_uris.add(new StyleSheet(href, "text/css"));
	}

	@Override
	public void add(int index, String href) {
		_uris.add(index, new StyleSheet(href, "text/css"));
	}

	@Override
	public void add(StyleSheet uri) {
		_uris.add(uri);
	}

	@Override
	public void add(int index, StyleSheet uri) {
		_uris.add(index, uri);
	}
}
