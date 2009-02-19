/* PageRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 14 16:32:46     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Collection;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Page;

/**
 * The page render used to render a page.
 * It is called by {@link PageCtrl#redraw}.
 *
 * @author tomyeh
 * @since 3.5.1
 */
public interface PageRenderer {
	/** Renders the page.
	 *
	 * @param out the output writer to put the content to (never null).
	 */
	public void render(Page page, Writer out) throws IOException;
}
