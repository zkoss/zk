/* XmlContentRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct  1 19:01:56     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

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
	public void render(String name, String value) {
		HTMLs.appendAttribute(_buf, name, value, false);
	}
	public void render(String name, int value) {
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
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, double value) {
		HTMLs.appendAttribute(_buf, name, value);
	}
	/** Renders a char property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, char value) {
		HTMLs.appendAttribute(_buf, name, "" + value);
	}
}
