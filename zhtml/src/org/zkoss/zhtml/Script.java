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
	 * @since 8.0.3
	 */
	public Boolean isAsync() {
		return getDynamicProperty("async") != null;
	}

	/**
	 * Sets the async of this script tag.
	 * @since 8.0.3
	 */
	public void setAsync(Boolean async) throws WrongValueException {
		setDynamicProperty("async", async ? true : null);
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
	public Boolean isDefer() {
		return getDynamicProperty("defer") != null;
	}

	/**
	 * Sets the defer of this script tag.
	 * @since 8.0.3
	 */
	public void setDefer(Boolean defer) throws WrongValueException {
		setDynamicProperty("defer", defer ? true : null);
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
