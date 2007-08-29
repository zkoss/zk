/* Inline.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 19 18:05:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsp.zul.impl;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.AbstractComponent;

/**
 * A comonent used to embed the browser native content (i.e., HTML tags)
 * into the output sent to the browser without additional HTML SPAN.
 * The browser native content is specified by {@link #setContent}.
 *
 * <p>Since the content is sent to client directly, {@link Inline}
 * has some limitations:
 *
 * <ol>
 * <li>The content cannot be changed dynamically. In other words,
 * once the output of {@link Inline} is sent to the client,
 * calling {@link #setContent} won't cause the client to change
 * the content accordingly. 
 * Rather, you have to invalidate its parent, such that the new
 * content will be sent to the client with its parent's content.</li>
 * <li>No style, no tooltip or others to control the look of {@link Inline}.</li>
 * </ol>
 *
 * @author tomyeh
 * @see org.zkoss.zul.Html
 */
public class Inline extends AbstractComponent {
	private String _content = "";

	/** Contructs a {@link Inline} component to embed HTML tags.
	 */
	public Inline() {
	}
	/** Contructs a {@link Inline} component to embed HTML tags
	 * with the specified content.
	 */
	public Inline(String content) {
		_content = content != null ? content: "";
	}

	/** Returns the embedded content (i.e., HTML tags).
	 * <p>Default: empty ("").
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the embedded content (i.e., HTML tags).
	 *
	 * <p>Note: Unlike {@link org.zkoss.zul.Html}, the content of {@link Inline}
	 * cannot be changed dynamically. 
	 * In other words, once the output of
	 * {@link Inline} is sent the client, calling this method
	 * won't change the content at the client accordingly.
	 * Rather, you have to invalidate its parent, such that the new
	 * content will be sent to the client with its parent's content.
	 */
	public void setContent(String content) {
		_content = content != null ? content: "";
	}

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
	public void redraw(Writer out) throws IOException {
		out.write(_content); //no encodding
	}
}
