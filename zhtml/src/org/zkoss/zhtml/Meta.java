/* Meta.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:22:59     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The META tag.
 * 
 * @author tomyeh
 */
public class Meta extends AbstractTag {
	public Meta() {
		super("meta");
	}

	/**
	 * Returns the charset of this meta tag.
	 * @since 8.0.3
	 */
	public String getCharset() {
		return (String) getDynamicProperty("charset");
	}

	/**
	 * Sets the charset of this meta tag.
	 * @since 8.0.3
	 */
	public void setCharset(String charset) throws WrongValueException {
		setDynamicProperty("charset", charset);
	};
	/**
	 * Returns the content of this meta tag.
	 * @since 8.0.3
	 */
	public String getContent() {
		return (String) getDynamicProperty("content");
	}

	/**
	 * Sets the content of this meta tag.
	 * @since 8.0.3
	 */
	public void setContent(String content) throws WrongValueException {
		setDynamicProperty("content", content);
	};
	/**
	 * Returns the http-equiv of this meta tag.
	 * @since 8.0.3
	 */
	public String getHttpequiv() {
		return (String) getDynamicProperty("http-equiv");
	}

	/**
	 * Sets the http-equiv of this meta tag.
	 * @since 8.0.3
	 */
	public void setHttpequiv(String httpequiv) throws WrongValueException {
		setDynamicProperty("http-equiv", httpequiv);
	};
	/**
	 * Returns the name of this meta tag.
	 * @since 8.0.3
	 */
	public String getName() {
		return (String) getDynamicProperty("name");
	}

	/**
	 * Sets the name of this meta tag.
	 * @since 8.0.3
	 */
	public void setName(String name) throws WrongValueException {
		setDynamicProperty("name", name);
	};
}
