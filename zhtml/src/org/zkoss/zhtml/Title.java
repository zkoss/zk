/* Title.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:50:43     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zhtml.impl.PageRenderer;

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
		if (!PageRenderer.isDirectContent(null))
			throw new UnsupportedOperationException("The parent of title must be head");

		super.redraw(out);
		out.write('\n');
	}
}
