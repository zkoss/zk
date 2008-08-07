/* Layoutregion2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 7, 2008 3:20:15 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkex.zul.LayoutRegion;

/**
 * {@link LayoutRegion}'s default mold.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Layoutregion2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final LayoutRegion self = (LayoutRegion) comp;
		final String uuid = self.getUuid();
		final String title = self.getTitle();
		final String pos = self.getPosition();
		final boolean hasTitle = title != null && title.length() > 0;
		wh.write("<div id=\"").write(uuid).write('"')
			.write(" z.type=\"zkex.zul.borderlayout.LayoutRegion2\">").write("<div id=\"")
			.write(uuid).write("!real\"").write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write(">");
		if (hasTitle) {
			wh.write("<div id=\"").write(uuid).write("!caption\" class=\"layout-title\">");
			if (!pos.equals("center")) {
				wh.write("<div id=\"").write(uuid).write("!btn\" class=\"layout-tool layout-collapse-")
					.write(pos).write("\"");
				if (!self.isCollapsible()) {
					wh.write(" style=\"display:none;\"");
				}
				wh.write("></div>");
			}
			new Out(title).render(out);
			wh.write("</div>");
		}
		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"layout-region-body\">")
			.writeChildren(self).write("</div></div>");
		
		if (!pos.equals("center")) {
			wh.write("<div id=\"").write(uuid).write("!split\" class=\"layout-split layout-split-")
				.write(pos);
			
			if (pos.equals("north")
					|| pos.equals("south"))
				wh.write(" layout-split-v");
			else if (pos.equals("west")
					|| pos.equals("east"))
				wh.write(" layout-split-h");
			
			wh.write("\"></div>");
			if (hasTitle) {
				wh.write("<div id=\"").write(uuid).write("!collapsed\" class=\"layout-collapsed-")
					.write(pos).write(" layout-collapsed\" style=\"display:none\"><div id=\"")
					.write(uuid).write("!btned\" class=\"layout-tool layout-expand-").write(pos).write('"');
				if (!self.isCollapsible()) {
					wh.write(" style=\"display:none;\"");
				}
				wh.write("></div></div>");
			}
		}
		wh.write("</div>");
	}
}
