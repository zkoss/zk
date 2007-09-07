/* Groupbox3d.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed Sep  5 13:18:53     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul.render;

import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

import org.zkoss.web.fn.XMLFns;
import org.zkoss.xel.fn.StringFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
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
		final WriterHelper wh = new WriterHelper(out);
		final Groupbox self = (Groupbox) comp;
		final Caption caption = self.getCaption();
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();

		wh.write("<table id=\"");
		wh.write(uuid);
		wh.write("\" z.type=\"zul.widget.Grbox\"");
		wh.write(self.getOuterAttrs());
		wh.write(self.getInnerAttrs());
		wh.write("><tr valign=\"top\"><td>");

		if (caption != null) {
			wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr><td class=\"groupbox-3d-tl\"></td>");
			wh.write("<td colspan=\"3\" class=\"groupbox-3d-tm\"></td>");
			wh.write("<td class=\"groupbox-3d-tr\"></td></tr>");

			wh.write("<tr height=\"22\"><td class=\"groupbox-3d-ml\"></td>");
			wh.write("<td width=\"3\" class=\"groupbox-3d-mm\"></td>");
			wh.write("<td class=\"groupbox-3d-mm\">");
			if (caption != null) {
				caption.redraw(out);
			}
			wh.write("</td>");
			wh.write("<td width=\"3\" class=\"groupbox-3d-mm\"></td>");
			wh.write("<td class=\"groupbox-3d-mr\"></td></tr>");
			wh.write("<tr><td colspan=\"5\" class=\"groupbox-3d-b\"></td></tr>");
			wh.write("</table>");
		}

		String gcExtStyle = StringFns.cat(caption == null ? ""
				: "border-top:0;", self.getContentStyle());
		wh.write("<div id=\"");
		wh.write(uuid);
		wh.write("!slide\"");
		if (!self.isOpen()) {
			wh.write(" style=\"display:none\" ");
		}

		wh.write(">");

		wh.write("<div id=\"");
		wh.write(uuid);
		wh.write("!cave\" ");
		wh.write("class=\"");
		wh.write(self.getContentSclass());
		wh.write("\"");
		wh.write(XMLFns.attr("style", gcExtStyle)).write(">");

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			if (caption != child)
				child.redraw(out);
		}

		wh.write("</div></div>");

		// shadow
		wh.write("<table id=\"");
		wh.write(self.getUuid());
		wh.write("!sdw\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
		wh.write("<tr><td class=\"groupbox-3d-shdl\"></td>");
		wh.write("<td class=\"groupbox-3d-shdm\">");
		wh.write("<img width=\"1\" height=\"1\" src=\"");
		wh.write(exec.encodeURL("~./img/spacer.gif"));
		wh.write("\"/></td> ");
		wh.write("<td class=\"groupbox-3d-shdr\"></td>");
		wh.write("</tr></table>");

		wh.write("</td></tr></table>");
		wh.writeln();
	}
}
