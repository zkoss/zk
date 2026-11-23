/* Script.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:04:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zk.ui.WrongValueException;

/**
 * The SCRIPT tag.
 * 
 * @author tomyeh
 */
public class Script extends org.zkoss.zhtml.impl.ContentTag {
	public Script() {
		super("script");
	}

	public Script(String content) {
		super("script", content);
	}
	/**
	 * Returns the async of this script tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public boolean isAsync() {
		final Boolean b = (Boolean) getDynamicProperty("async");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the async of this script tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setAsync(boolean async) throws WrongValueException {
		setDynamicProperty("async", async ? Boolean.valueOf(async) : null);
	}
	/**
	 * Returns the charset of this script tag.
	 * @since 8.0.3
	 */
	public String getCharset() {
		return (String) getDynamicProperty("charset");
	}

	/**
	 * Sets the charset of this script tag.
	 * @since 8.0.3
	 */
	public void setCharset(String charset) throws WrongValueException {
		setDynamicProperty("charset", charset);
	}
	/**
	 * Returns the defer of this script tag.
	 * @since 8.0.3
	 */
	public boolean isDefer() {
		final Boolean b = (Boolean) getDynamicProperty("defer");
		return b != null && b.booleanValue();
	}

	/**
	 * Sets the defer of this script tag.
	 * @since 8.0.3
	 */
	public void setDefer(boolean defer) throws WrongValueException {
		setDynamicProperty("defer", defer ? Boolean.valueOf(defer) : null);
	}
	/**
	 * Returns the src of this script tag.
	 * @since 8.0.3
	 */
	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	/**
	 * Sets the src of this script tag.
	 * @since 8.0.3
	 */
	public void setSrc(String src) throws WrongValueException {
		setDynamicProperty("src", src);
	}
	/**
	 * Returns the type of this script tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this script tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
}
