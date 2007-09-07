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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.WriterHelper;
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
		new WriterHelper(out)
			.write(self.getTreerow())
			.write(self.getTreechildren());
	}

}
