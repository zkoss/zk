/* Groupbox23d.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 18, 2008 11:47:15 AM , Created by jumperchen
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Groupbox;

/**
 * {@link Groupbox}'s 3d mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class Groupbox23d implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Groupbox self = (Groupbox) comp;
		final Caption caption = self.getCaption();
		final String uuid = self.getUuid();
		final String mcls = self.getMoldSclass();

		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.widget.Grbox\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write(">");
		String gcExtStyle;
		if (caption != null) {
			wh.write("<div class=\"").write(mcls).write("-tl\"><div class=\"")
				.write(mcls).write("-tr\"><div class=\"").write(mcls).write("-tm\">")
				.write("<div class=\"").write(mcls).write("-header\">").write(caption)
				.write("</div></div></div></div>");
			gcExtStyle = "border-top:0;";
		} else 
			gcExtStyle = "";
		
		String cs = self.getContentStyle();
		if (cs != null) gcExtStyle += cs;
		
		wh.write("<div id=\"").write(uuid).write("!slide\" class=\"").write(mcls).write("-body\"");
		if (!self.isOpen())
			wh.write(" style=\"display:none\" ");
		wh.write(">");

		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"")
			.write(self.getContentSclass()).write(" ").write(mcls).write("-content\"")
			.writeAttr("style", gcExtStyle).write(">");

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			if (caption != child)
				child.redraw(out);
		}

		wh.write("</div></div>");

		// shadow
		wh.write("<div id=\"").write(uuid).write("!sdw\" class=\"").write(mcls)
			.write("-bl\"><div class=\"").write(mcls).write("-br\"><div class=\"")
			.write(mcls).write("-bm\"></div></div></div></div>");
	}
}
