/* Title.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:50:43     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zhtml.impl.AbstractTag;

/**
 * The TITLE tag.
 *
 * @author tomyeh
 */
public class Title extends AbstractTag {
	public Title() {
		super("title");
	}

	//-- super --//
	/** Don't generate the id attribute.
	 */
	protected boolean shallHideId() {
		return true;
	}
	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (!HtmlPageRenders.isDirectContent(null))
			throw new UnsupportedOperationException("Parent of title must be head");

		super.redraw(out);
		out.write('\n');
	}
}
