/* Html.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 25 11:39:49     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zul.impl.XulElement;

/**
 * A HTML embedder that embeds any HTML tags, even including JavaScript.
 * Besides using this component, you could use XUL's standard approach:
 * xmlns:h="http://www.w3.org/1999/xhtml".
 * The XUL's standard approach allows app developers to mix HTML tags
 * with XUL components.
 *
 * <p>A non-XUL extension.
 *
 * @author tomyeh
 */
public class Html extends XulElement {
	private String _content = "";

	public Html() {
	}
	public Html(String content) {
		setContent(content);
	}

	/** Returns the HTML tags being embedded.
	 * <p>Default: empty ("").
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the HTML tags being embedded.
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!Objects.equals(_content, content)) {
			_content = content;
			invalidate();
		}
	}

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
}
