/* WindowDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 11:58:40     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Window;
import org.zkoss.zul.Caption;

/**
 * {@link Window}'s default mold.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class WindowDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Window self = (Window)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();

		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.wnd.Wnd\" z.autoz=\"true\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");

		final Caption caption = self.getCaption();
		final String title = self.getTitle(), titlesc = self.getTitleSclass();
		String wcExtStyle;
		if (caption == null && title.length() == 0) {
			wcExtStyle = "";
			if (exec.isExplorer() && !exec.isExplorer7()) { /* Bug 1579515: to clickable, a child with 100% width is required for DIV */
				wh.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
					.write("<tr height=\"1px\"><td></td></tr>\n</table>");
			}
		} else {
			wcExtStyle = "border-top:0;";
			wh.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			if (caption == null) {
				wh.write("<tr id=\"").write(uuid).write("!caption\" class=\"title\">")
				  .write("<td class=\"l").write(titlesc).writeln("\"></td>")
				  .write("<td class=\"m").write(titlesc).write("\">");
				new Out(title).render(out);
				wh.writeln("</td>");

				if (self.isClosable()) {
					wh.write("<td width=\"16\" class=\"m").write(titlesc).write("\"><img id=\"")
						.write(uuid).write("!close\" src=\"")
						.write(exec.encodeURL("~./zul/img/close-off.gif")).writeln("\"/></td>");
				}

				wh.write("<td class=\"r").write(titlesc).writeln("\"></td></tr>");
			} else {
				wh.write("<tr id=\"").write(uuid).write("!caption\"><td class=\"l")
					.write(titlesc).write("\"></td>\n<td class=\"m").write(titlesc).write("\">")
					.write(caption)
					.write("</td>\n<td class=\"r").write(titlesc).writeln("\"></td></tr>");
			}
			wh.write("</table>");
		}

		final String cs = self.getContentStyle();
		if(cs != null){
			wcExtStyle += cs;
		}
		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"");
		wh.write(self.getContentSclass()).write("\"").writeAttr("style", wcExtStyle);
		wh.write(">");

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child != caption)
				wh.write(child);
		}
		wh.write("</div></div>"); /* we don't generate shadow here since it looks odd when on top of modal mask */
	}
}
