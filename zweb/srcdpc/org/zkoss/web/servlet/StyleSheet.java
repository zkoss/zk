/* StyleSheet.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  2 12:32:59     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet;

/** @deprecated As of release 5.1.0, replaced with {@link org.zkoss.html.StyleSheet}.
 * Represents a style sheet.
 *
 * @author tomyeh
 */
public class StyleSheet extends org.zkoss.html.StyleSheet {
	/** Creates by specifying the file to contain the style sheets.
	 *
	 * @param href URI of the file containing the style sheets.
	 * @param type the type. If null, "text/css" is assumed.
	 */
	public StyleSheet(String href, String type) {
		super(href, type);
	}
	/** Creates by assigning the content (style sheets).
	 *
	 * @param content the style content or an URI to an external file.
	 * @param type the type. If null, "text/css" is assumed.
	 * @param byContent the content argument is the style content, or
	 * an URI to an external content
	 */
	public StyleSheet(String content, String type, boolean byContent) {
		super(content, type, byContent);
	}
	/** Creates by assigning the content (style sheets).
	 *
	 * @param content the style content or an URI to an external file.
	 * @param type the type. If null, "text/css" is assumed.
	 * @param media the media. If null, it is omitted.
	 * @param byContent the content argument is the style content, or
	 * an URI to an external content
	 * @since 5.0.3
	 */
	public StyleSheet(String content, String type, String media, boolean byContent) {
		super(content, type, media, byContent);
	}
}
