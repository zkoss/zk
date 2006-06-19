/* StyleSheet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  2 12:32:59     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet;

import com.potix.lang.Objects;

/**
 * Represents a style sheet.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class StyleSheet implements java.io.Serializable {
	private final String _href, _type, _content;
	/** Creates by specifying the file to contain the style sheets.
	 *
	 * @param href URI of the file containing the style sheets.
	 * @param type the type. If null, "text/css" is assumed.
	 */
	public StyleSheet(String href, String type) {
		if (href == null)
			throw new NullPointerException();
		if (type == null || type.length() == 0)
			type = "text/css";
		_href = href;
		_type = type;
		_content = null;
	}
	/** Creates by assigning the content (style sheets).
	 *
	 * @param content style sheets.
	 */
	public StyleSheet(String content) {
		if (content == null)
			throw new IllegalArgumentException("null content");
		_href = null;
		_type = "text/css";
		_content = content;
	}

	/** Returns the href that contains the style sheets, or null if
	 * {@link #getContent} is not null.
	 */
	public String getHref() {
		return _href;
	}
	/** Returns the type. */
	public String getType() {
		return _type;
	}
	/** Returns the style sheets, or null if {@link #getHref} is not null.
	 */
	public String getContent() {
		return _content;
	}

	//-- Object --//
	public String toString() {
		return "[href: "+_href+" type="+_type+']';
	}
	public int hashCode() {
		return _href.hashCode() + _type.hashCode();
	}
	public boolean equals(Object o) {
		if (!(o instanceof StyleSheet))
			return false;
		final StyleSheet ss = (StyleSheet)o;
		return Objects.equals(ss._href, _href)
			&& Objects.equals(ss._type, _type)
			&& Objects.equals(ss._content, _content);
	}
}
