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

/** @deprecated As of release 5.5.0, replaced with {@link org.zkoss.html.JavaScript}.
 * Represents a javascript.
 *
 * @author tomyeh
 */
public class JavaScript extends org.zkoss.html.JavaScript {
	/** Creates by specifying the file to contain the JavaScript codes.
	 *
	 * @param src URI of the file containing the JavaScript codes.
	 * @param charset the charset. If null, "UTF-8" is assumed.
	 */
	public JavaScript(String src, String charset) {
		super(src, charset);
	}
	/** Creates by assigning the content (JavaScript codes).
	 *
	 * @param content the JavaScript codes.
	 */
	public JavaScript(String content) {
		super(content);
	}
}
