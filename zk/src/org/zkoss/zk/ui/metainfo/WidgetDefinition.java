/* WidgetDefinition.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 16 10:48:43     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collection;

/**
 * A widget definition.
 * A widget is an UI object at the client.
 *
 * @author tomyeh
 * @since 5.0.0
 * @see LanguageDefinition#getWidgetDefinition
 */
public interface WidgetDefinition {
	/** Returns the widget class (a JavaScript class).
	 */
	public String getWidgetClass();
	/** Returns a collection of the mold names that have the molde URL.
	 */
	public Collection<String> getMoldNames();
	/** Returns whether the mold URI exists for the given mold.
	 */
	public boolean hasMold(String name);
	/** Adds the mold URI for the specified mold.
	 *
	 * @param name the mold name.
	 * @param moldURI an URI of the mold. Ignored if null.
	 * @since 5.0.4
	 */
	public void addMold(String name, String moldURI);
	/** Returns the URI (String) of the template to generate the mold,
	 * or null if not available.
	 * For Ajax clients, the template is a JavaScript method.
	 *
	 * @param name the mold name
	 * @return an URI in String
	 * @see org.zkoss.zk.ui.AbstractComponent#redraw
	 */
	public String getMoldURI(String name);

	/** Returns whether to preserve the blank text.
	 * If false, the blank text (a non-empty string consisting of whitespaces)
	 * are ignored.
	 * If true, they are converted to a label child.
	 *
	 * <p>It is used only with <a href="http://docs.zkoss.org/wiki/IZUML">iZUML</a>.
	 */
	public boolean isBlankPreserved();
}
