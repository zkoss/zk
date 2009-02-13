/* ToolbarDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 3:06:14 PM , Created by robbiecheng
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
import org.zkoss.zul.Toolbar;

/**
 * {@link Toolbar}'s default mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class ToolbarDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Toolbar self = (Toolbar) comp;
		boolean shallBreak = self.getOrient().equals("vertical"), follow = false;
		
		wh.write("<div id=\"").write(self.getUuid()).write('"')
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>');

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			if (shallBreak)
				if (follow) wh.writeln("<br/>");
				else follow = true;
			((Component)it.next()).redraw(out);
		}

		wh.write("</div>");
	}
}
