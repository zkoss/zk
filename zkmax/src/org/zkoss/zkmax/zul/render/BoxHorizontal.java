/* Hbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 5, 2007 2:15:45 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
import org.zkoss.zul.Box;
/**
 * {@link Box}'s horizontal mold.
 * @author robbiecheng
 * @since 3.0.0
 */

public class BoxHorizontal implements ComponentRenderer{
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Box self = (Box) comp;
		final String uuid = self.getUuid();		
		String spscls = null, spstyle = null;

		wh.write("<table id=\"").write(uuid).write("\" z.type=\"zul.box.Box\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.writeln(" cellpadding=\"0\" cellspacing=\"0\">")	
			.write("<tr id=\"").write(uuid).writeln("!cave\"")
			.write(self.getCaveAttrs()).write('>');
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			wh.write("<td id=\"").write(child.getUuid()).write("!chdextr\"")
				.write(self.getChildOuterAttrs(child))
				.write(self.getChildInnerAttrs(child)).write(">")
				.write(child)
				.writeln("</td>");

			if (child.getNextSibling() != null) {
				if (spscls == null) {
					spscls = self.getSclass();
					spscls = spscls == null || spscls.length() == 0 ? "hbox-sp": spscls + "-sp";
					final String spacing = self.getSpacing();
					if (spacing != null)
						spstyle = "width:" + spacing;
				}

				wh.write("<td id=\"").write(child.getUuid())
					.write("!chdextr2\" class=\"").write(spscls).write("\"");

				if (!child.isVisible()) {
					wh.write(" style=\"display:none;");
					if (spstyle != null) wh.write(spstyle);
					wh.write("\"");
				} else if (spstyle != null) {
					wh.write(" style=\"").write(spstyle).write("\"");
				}

				wh.writeln("></td>");
			}
		}		
		wh.write("</tr></table>");
	}
}
