/* ContentRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct  1 18:09:04     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.io.IOException;

/**
 * Used with {@link org.zkoss.zk.ui.AbstractComponent#renderProperties}
 * to generate the component content that will be sent to the client.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface ContentRenderer {
	/** Renders a string property. */
	public void render(String name, String value) throws IOException;
	/** Renders an integer property. */
	public void render(String name, int value) throws IOException;
	/** Renders a boolean property. */
	public void render(String name, boolean value) throws IOException;
	/** Renders a double property. */
	public void render(String name, double value) throws IOException;
	/** Renders a char property. */
	public void render(String name, char value) throws IOException;
	/** Renders the value directly.
	 * It depends on the implementation.
	 * For {@link JsContentRenderer}, value must be a valid JavaScript
	 * snippet.
	 */
	public void renderDirectly(String name, Object value);
}
