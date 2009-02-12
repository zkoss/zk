/* TreeitemDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:20:04 AM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.fn.ZulFns;

/**
 * {@link Treeitem}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreeitemDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {		
		final Treeitem self = (Treeitem) comp;
		final SmartWriter wh = new SmartWriter(out);
		final Tree tree = self.getTree();
		if ("paging".equals(tree.getMold())) {
			if (self.isVisible() && ZulFns.shallVisitTree(tree, self)) {
				if (ZulFns.shallRenderTree(tree))
					wh.write(self.getTreerow());
				if (self.isOpen())
					wh.write(self.getTreechildren());
			}
		} else {
			wh.write(self.getTreerow()).write(self.getTreechildren());
		}
	}
}
