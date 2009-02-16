/* ToolbarpanelDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 19, 2008 10:14:47 AM , Created by jumperchen
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Toolbar;


/**
 * {@link Toolbar}'s panel mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class ToolbarpanelDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Toolbar self = (Toolbar) comp;
		final String zcls = self.getZclass(), uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write('"')
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>');
		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"")
			.write(zcls).write("-body ").write(zcls).write('-').write(self.getAlign())
			.write("\">");
		wh.writeln("<table class=\"").write(zcls).write("-cnt\" cellspacing=\"0\"><tbody>");
		if (!self.getOrient().equals("vertical")) {
			wh.write("<tr>");
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				wh.write("<td class=\"").write(zcls).write("-hor\">");
				((Component)it.next()).redraw(out);
				wh.write("</td>");
			}
			wh.writeln("</tr>");
		} else {
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				wh.write("<tr><td class=\"").write(zcls).write("-ver\">");
				((Component)it.next()).redraw(out);
				wh.writeln("</td></tr>");
			}
		}

		wh.write("</tbody></table><div class=\"z-clear\"></div></div></div>");
	}
}