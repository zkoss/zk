/* Groupbox3d.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed Sep  5 13:18:53     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkmax.zul.render;

import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Groupbox;

/**
 * {@link Groupbox}'s 3d mold.
 * 
 * @author dennis.chen
 * @since 3.0.0
 */
public class Groupbox3d implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Groupbox self = (Groupbox) comp;
		final String look = self.getCaptionLook() + '-';
		final Caption caption = self.getCaption();
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();

		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.widget.Grbox\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write(">");
		String gcExtStyle;
		if (caption != null) {
			wh.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr><td class=\"").write(look).writeln("tl\"></td>");
			wh.write("<td colspan=\"3\" class=\"").write(look).writeln("tm\"></td>");
			wh.write("<td class=\"").write(look).writeln("tr\"></td></tr>");

			wh.write("<tr class=\"").write(look).write('m')
				.write("\"><td class=\"").write(look).writeln("ml\"></td>");
			wh.write("<td width=\"3\" class=\"").write(look).writeln("mm\"></td>");
			wh.write("<td class=\"").write(look).write("mm\">");
			caption.redraw(out);
			wh.writeln("</td>");

			wh.write("<td width=\"3\" class=\"").write(look).writeln("mm\"></td>");
			wh.write("<td class=\"").write(look).writeln("mr\"></td></tr>");

			wh.write("<tr><td colspan=\"5\" class=\"").write(look).writeln("b\"></td></tr>");
			wh.write("</table>");
			gcExtStyle = "border-top:0;";
		} else {
			gcExtStyle = "";
		}
		
		String cs = self.getContentStyle();
		if (cs != null) gcExtStyle += cs;
		
		wh.write("<div id=\"").write(uuid).write("!slide\"");
		if (!self.isOpen())
			wh.write(" style=\"display:none\" ");
		wh.write(">");

		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"")
			.write(self.getContentSclass()).write("\"")
			.writeAttr("style", gcExtStyle).write(">");

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			if (caption != child)
				child.redraw(out);
		}

		wh.write("</div></div>");

		// shadow
		wh.write("<table id=\"").write(self.getUuid())
			.writeln("!sdw\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
		wh.write("<tr><td class=\"").write(look).writeln("shdl\"></td>");
		wh.write("<td class=\"").write(look).write("shdm\"><img src=\"")
			.write(exec.encodeURL("~./img/spacer.gif"))
			.writeln("\"/></td>");
		wh.write("<td class=\"").write(look).writeln("shdr\"></td>");
		wh.write("</tr></table></div>");
	}
}
