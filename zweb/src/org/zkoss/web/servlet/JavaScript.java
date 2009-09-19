/* JavaScript.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 11 15:29:37     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

import org.zkoss.lang.Objects;

/**
 * Represents a javascript.
 *
 * @author tomyeh
 */
public class JavaScript {
	private final String _src, _charset, _content;

	/** Creates by specifying the file to contain the JavaScript codes.
	 *
	 * @param src URI of the file containing the JavaScript codes.
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 */
	public JavaScript(String src, String charset) {
		if (src == null)
			throw new IllegalArgumentException("null src");
		if (charset == null || charset.length() == 0)
			charset = "UTF-8";

		_src = src;
		_charset = charset;
		_content = null;
	}
	/** Creates by assigning the content (JavaScript codes).
	 *
	 * @param content the JavaScript codes.
	 */
	public JavaScript(String content) {
		if (content == null)
			throw new IllegalArgumentException("null content");
		_src = _charset = null;
		_content = content;
	}
	/** Returns the URI of the file containing the JavaScript codes,
	 * or null if {@link #getContent} is not null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Returns the charset, or null if {@link #getContent} is not null. */
	public String getCharset() {
		return _charset;
	}
	/** Returns the JavaScript codes, or null if {@link #getSrc} is not null.
	 */
	public String getContent() {
		return _content;
	}

	//-- Object --//
	public String toString() {
		return "[src: "+_src+" charset="+_charset+']';
	}
	public int hashCode() {
		return _src.hashCode() + _charset.hashCode();
	}
	public boolean equals(Object o) {
		if (!(o instanceof JavaScript))
			return false;
		final JavaScript js = (JavaScript)o;
		return Objects.equals(js._src, _src)
			&& Objects.equals(js._charset, _charset)
			&& Objects.equals(js._content, _content);
	}
}
