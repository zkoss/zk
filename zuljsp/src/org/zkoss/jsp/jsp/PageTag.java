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
package org.zkoss.jsp.jsp;

import org.zkoss.jsp.jsp.impl.RootTag;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;

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
	private String _style;

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

	/** Creates and returns the page.
	 */
	protected void init(Execution exec, Page page) {
		super.init(exec, page);

		page.setStyle(_style);
	}

}
