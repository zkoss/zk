/* TreeitemDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:20:04 AM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

/**
 * {@link Treeitem}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreeitemDefault implements ComponentRenderer {
/**
<c:set var="self" value="${requestScope.arg.self}"/>
${z:redraw(self.treerow, null)}
${z:redraw(self.treechildren, null)}
 */
	public void render(Component comp, Writer out) throws IOException {		
		final Treeitem self = (Treeitem) comp;
		final Treerow tr = self.getTreerow();
		final Treechildren tc = self.getTreechildren();
		
		if (tr != null)			
			tr.redraw(out);
		if (tc != null)
			tc.redraw(out);
	}

}
