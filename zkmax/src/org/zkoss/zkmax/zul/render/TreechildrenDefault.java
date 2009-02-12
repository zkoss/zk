/* TreechildrenDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:23:50 AM , Created by robbiecheng
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;

/**
 * {@link Treechildren}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreechildrenDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Treechildren self = (Treechildren) comp;
		if (self.getParent() instanceof Tree) { //top level
			wh.write("<tbody id=\"").write(self.getUuid()).write('"')
				.write(self.getOuterAttrs()).write( self.getInnerAttrs() ).writeln(">");
			for (Iterator it = self.getVisibleChildrenIterator(); it.hasNext();) {
				final Component child = (Component) it.next();
				child.redraw(out);
			}
			wh.writeln("</tbody>");
		} else {
			for (Iterator it = self.getVisibleChildrenIterator(); it.hasNext();) {
				final Component child = (Component) it.next();
				child.redraw(out);
			}
		}
	}
}
