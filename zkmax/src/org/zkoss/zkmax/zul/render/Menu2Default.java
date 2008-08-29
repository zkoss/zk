/* Menu2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2008 11:23:10 AM , Created by jumperchen
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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Menu;

/**
 * {@link Menu}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 *
 */
public class Menu2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menu self = (Menu)comp;
		final String uuid = self.getUuid();
		final String mcls = self.getMoldSclass();
		final Execution exec = Executions.getCurrent();
		if (self.isTopmost()) {
			wh.write("<td id=\"").write(uuid).write("\" align=\"left\" z.type=\"zul.menu2.Menu2\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
			wh.write("<table id=\"").write(uuid).write("!a\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"")
				.write(mcls).write("-btn");
			if (self.isImageAssigned()) {
				wh.write(" ").write(mcls).write("-btn");
				if (self.getLabel().length() > 0)
					wh.write("-text");
				wh.write("-img");
			}
			wh.write("\" style=\"width: auto;\"><tbody><tr><td class=\"").write(mcls).write("-btn-l\"><i>&nbsp;</i></td>");
			String imagesrc;
			if (self.getImageContent() != null)
				imagesrc = "background-image:url(" + self.getContentSrc() + ")";
			else {
				final String src = self.getSrc();
				if (src != null && src.length() > 0)
					imagesrc = "background-image:url(" + exec.encodeURL(src) + ")";
				else imagesrc = "";
			}
			wh.write("<td class=\"").write(mcls).write("-btn-m\"><em unselectable=\"on\"><button id=\"")
				.write(uuid).write("!b\" type=\"button\" class=\"").write(mcls).write("-btn-text\" style=\"")
				.write(imagesrc).write("\">");
			new Out(self.getLabel()).render(out);
			wh.write("</button>").write(self.getMenupopup()).write("</em></td><td class=\"")
				.write(mcls).writeln("-btn-r\"><i>&nbsp;</i></td></tr></tbody></table></td>");
		} else {
			wh.write("<li id=\"").write(uuid).write("\" z.type=\"zul.menu2.Menu2\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs())
				.write(">\n<a href=\"javascript:;\" id=\"").write(uuid).write("!a\" class=\"").write(mcls)
				.write("-cnt ").write(mcls).write("-cnt-img\">")
				.write(self.getImgTag());

			new Out(self.getLabel()).render(out);

			wh.write("</a>")
				.write(self.getMenupopup())
				.writeln("</li>");
		}

	}
}