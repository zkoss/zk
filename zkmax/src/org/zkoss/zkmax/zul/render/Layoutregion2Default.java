/* Layoutregion2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 7, 2008 3:20:15 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkex.zul.Borderlayout;
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
		final String zcls = self.getZclass();
		final String pzcls = ((Borderlayout)self.getParent()).getZclass();
		final boolean hasTitle = title != null && title.length() > 0;
		wh.write("<div id=\"").write(uuid).write('"')
			.write(" z.type=\"zkex.zul.borderlayout.LayoutRegion2\">").write("<div id=\"")
			.write(uuid).write("!real\"").write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write(">");
		if (hasTitle) {
			wh.write("<div id=\"").write(uuid).write("!caption\" class=\"").write(zcls).write("-header\">");
			if (!pos.equals("center")) {
				wh.write("<div id=\"").write(uuid).write("!btn\" class=\"").write(pzcls)
					.write("-icon ").write(zcls).write("-collapse\"");
				if (!self.isCollapsible()) {
					wh.write(" style=\"display:none;\"");
				}
				wh.write("></div>");
			}
			new Out(title).render(out);
			wh.write("</div>");
		}
		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"").write(zcls).write("-body\">")
			.writeChildren(self).write("</div></div>");
		
		if (!pos.equals("center")) {
			wh.write("<div id=\"").write(uuid).write("!split\" class=\"").write(zcls).write("-split\"\"></div>");
			if (hasTitle) {
				wh.write("<div id=\"").write(uuid).write("!collapsed\" class=\"").write(zcls).write("-collapsed\" style=\"display:none\"><div id=\"")
					.write(uuid).write("!btned\" class=\"").write(pzcls)
					.write("-icon ").write(zcls).write("-expand\"");
				if (!self.isCollapsible()) {
					wh.write(" style=\"display:none;\"");
				}
				wh.write("></div></div>");
			}
		}
		wh.write("</div>");
	}
}
