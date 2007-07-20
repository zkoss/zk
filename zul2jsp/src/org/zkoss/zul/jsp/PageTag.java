/* PageTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 11:32:56     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.zkoss.zul.jsp.impl.RootTag;

/**
 * Defines a ZK page.
 * It is reponsible for doing the lifecycle for ZK components, such
 * as event processing and rendering,
 *
 * <p>All other ZK tags must be placed inside a {@link PageTag} tag.
 * Nested page tags are not allowed.
 *
 * @author tomyeh
 */
public class PageTag extends RootTag {
	private String _style = null;

	/** Returns the style.
	 * Default: null (no style at all).
	 */
	public String getStyle() {
		return _style;
	}
	/** Sets the style.
	 */
	public void setStyle(String style) {
		_style = style != null && style.length() > 0 ? style: null;
	}

	/** To process this root tag.
	 * The deriving class rarely need to override this method.
	 */
	public void doTag() throws JspException, IOException {
		if (!isEffective())
			return; //nothing to do
	}
}
