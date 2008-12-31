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

import java.util.Date;
import java.util.Map;
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
	/** Renders a Date property. */
	public void render(String name, Date value) throws IOException;
	/** Renders an arbitrary object property.
	 * What type of suppoted objects depends on the implementation.
	 * However, the following types are always supported:
	 * null, String, Date, the wrapper of primitives, and
	 * an array of the above types.
	 */
	public void render(String name, Object value) throws IOException;
	/** Renders an integer property. */
	public void render(String name, int value) throws IOException;
	/** Renders a short property. */
	public void render(String name, short value) throws IOException;
	/** Renders a long property. */
	public void render(String name, long value) throws IOException;
	/** Renders a byte property. */
	public void render(String name, byte value) throws IOException;
	/** Renders a boolean property. */
	public void render(String name, boolean value) throws IOException;
	/** Renders a double property. */
	public void render(String name, double value) throws IOException;
	/** Renders a float property. */
	public void render(String name, float value) throws IOException;
	/** Renders a char property. */
	public void render(String name, char value) throws IOException;
	/** Renders the value directly without encapsulation or conversion.
	 * It depends on the implementation.
	 * For {@link JsContentRenderer}, value must be a valid JavaScript
	 * snippet.
	 */
	public void renderDirectly(String name, Object value);
	/** Renders the JavaScript code snippet for event listeners
	 * registered for the peer widget.
	 * @param listeners the map of listeners to register at the peer widget.
	 * The key is the event name, and the value the code snippet.
	 * They are both String instances.
	 */
	public void renderWidgetListeners(Map listeners);
}
