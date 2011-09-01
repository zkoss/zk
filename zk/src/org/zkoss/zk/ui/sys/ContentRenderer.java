/* ContentRenderer.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct  1 18:09:04     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
	 * null, String, Date, the wrapper of primitives,
	 * an array of the above types, and a map of the above types.
	 *
	 * <p>If the object is an instance of {@link org.zkoss.json.JSONAware},
	 * {@link org.zkoss.json.JSONAware#toJSONString} will be called,
	 * and the return will be generated directly.
	 * In other word, it is the same as
	 * <code>renderDirectly(name, value.toJSONString())</code>.
	 *
	 * <p>If the value is not recognized, it will be converted to a string
	 * by use of Object.toString().
	 * It the client's job to convert the string back to the correct object.
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
	/** Renders the client code snippet for event listeners
	 * registered for the peer widget.
	 * @param listeners the map of listeners to register at the peer widget.
	 * The key is the event name (such as onClick), and the value the code
	 * snippet (such as <code>this.getFellow('inf').setValue('new')</code>).
	 * They are both String instances.
	 */
	public void renderWidgetListeners(Map listeners);
	/** Renders the client code snippet to override the methods
	 * and properties of the peer widget.
	 *
	 * @param overrides the map of methods or properties to override the peer widget.
	 * The key is the method name (such as setValue), and the value
	 * must be a valid JavaScript snippet that can be evaluated to
	 * a value. They are both instances of String.
	 * For example, the map will be generated as follows by {@link JsContentRenderer}:<br/>
	 * <code>{name1: value1, name2: value2}</code>.
	 */
	public void renderWidgetOverrides(Map overrides);
	/** Renders the client's DOM attributes for the peer widgets.
	 * @param attrs the map of attributes. The key is the attribute's name,
	 * while the value is the attribute's value. They are both instances of String.
	 * @since 5.0.3
	 */
	public void renderWidgetAttributes(Map attrs);
}
