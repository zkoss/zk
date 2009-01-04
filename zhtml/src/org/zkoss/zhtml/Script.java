/* Script.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:04:35     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zhtml.impl.PageRenderer;

/**
 * The SCRIPT tag.
 * 
 * @author tomyeh
 */
public class Script extends AbstractTag {
	public Script() {
		super("script");
	}

	//super//
	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (!PageRenderer.isDirectContent(null))
			throw new IllegalStateException();

		super.redraw(out);
		out.write('\n');
	}
}
