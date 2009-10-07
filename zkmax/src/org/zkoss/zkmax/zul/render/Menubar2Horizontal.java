/* Menubar2Horizontal.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2008 11:49:20 AM , Created by jumperchen
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
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Menubar;

/**
 * {@link Menubar}'s horizontal oriented.
 * @author jumperchen
 * @since 3.5.0
 *
 */
public class Menubar2Horizontal implements ComponentRenderer {
	private final Menubar2Vertical _ver = new Menubar2Vertical();
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menubar self = (Menubar)comp;
		if ("vertical".equals(self.getOrient())) {
			_ver.render(comp, out);
			return; 
		}
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.menu2.Menubar2\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>');
		if (self.isScrollable()) {
			wh.write("<div id=\"").write(uuid).write("!left\" class=\"").write(zcls).write("-left\"></div>");
			wh.write("<div id=\"").write(uuid).write("!right\" class=\"").write(zcls).write("-right\"></div>");
			wh.write("<div id=\"").write(uuid).write("!body\" class=\"").write(zcls).write("-body\">");
			wh.write("<div id=\"").write(uuid).write("!cnt\" class=\"").write(zcls).write("-cnt\">");
		}
		wh.writeln("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		wh.write("<tr valign=\"bottom\" id=\"").write(uuid).writeln("!cave\">");
		wh.writeChildren(self);
		wh.write("</tr>\n</table>");
		if (self.isScrollable()) {
			wh.write("</div></div>");
		}
		wh.write("</div>");
	}
}