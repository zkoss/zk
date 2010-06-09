/* XmlContentRenderer.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct  1 19:01:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Map;
import java.util.Iterator;
import java.util.Date;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

/**
 * An implementation of {@link ContentRenderer} that renders
 * the content as a Xml attribute (i.e., name="value").
 * @author tomyeh
 * @since 5.0.0
 */
public class XmlContentRenderer implements ContentRenderer {
	private final StringBuffer _buf = new StringBuffer(128);
	public XmlContentRenderer() {
	}

	/** Returns the content being rendered.
	 */
	public StringBuffer getBuffer() {
		return _buf;
	}
	/** Renders a String property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, String value) {
		HTMLs.appendAttribute(_buf, name, value, false);
	}
	/** Renders a Date property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, Date value) {
		HTMLs.appendAttribute(_buf, name, Objects.toString(value), false);
	}
	/** Renders an Object property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, Object value) {
		HTMLs.appendAttribute(_buf, name, Objects.toString(value), false);
	}
	/** Renders a date property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, int value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a long property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, long value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a short property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, short value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a byte property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, byte value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a boolean property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, boolean value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a double property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, double value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a float property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, float value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a char property.
	 * @param name the property name. Note: it must be a legal XML
	 * attribute name.
	 */
	public void render(String name, char value) {
		HTMLs.appendAttribute(_buf, name, "" + value);
	}
	/** Renders the value by converting it to string.
	 */
	public void renderDirectly(String name, Object value) {
		render(name, Objects.toString(value));
	}

	/** It is the same as <code>render(name, script)</code>.
	 */
	public void renderWidgetListeners(Map listeners) {
		for (Iterator it = listeners.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			render((String)me.getKey(), me.getValue());
		}
	}
	/** It is the same as <code>render(name, script)</code>.
	 */
	public void renderWidgetOverrides(Map methods) {
		renderWidgetOverrides(methods);
	}
}
