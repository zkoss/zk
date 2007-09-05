/* DefaultWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 11:58:40     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.xml.XMLs;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Window;
import org.zkoss.zul.Caption;

/**
 * {@link Window}'s default mold.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class DefaultWindow implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final Window self = (Window)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();

		out.write("<div id=\"");
		out.write(uuid);
		out.write("\" z.type=\"zul.wnd.Wnd\" z.autoz=\"true\"");
		out.write(self.getOuterAttrs());
		out.write(self.getInnerAttrs());
		out.write(">\n");

		final Caption caption = self.getCaption();
		final String title = self.getTitle(), titlesc = self.getTitleSclass();
		String wcExtStyle = "";
		if (caption == null && title.length() == 0) {
			if (exec.isExplorer() && !exec.isExplorer7()) {
				out.write(
					"<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
					+"<tr height=\"1px\"><td></td></tr></table>\n");
			}
		} else {
			out.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n");
			if (caption == null) {
				out.write("<tr id=\"");
				out.write(uuid);
				out.write("!caption\" class=\"title\">\n");
				out.write("<td class=\"l");
				out.write(titlesc);
				out.write("\"></td>\n");
				out.write("<td class=\"m");
				out.write(titlesc);
				out.write("\">");
				out.write(XMLs.escapeXML(title));
				out.write("</td>");
				if (self.isClosable()) {
					out.write("<td width=\"16\" class=\"m");
					out.write(titlesc);
					out.write("\"><img id=\"");
					out.write(uuid);
					out.write("!close\" src=\"");
					out.write(exec.encodeURL("~./zul/img/close-off.gif"));
					out.write("\"/></td>");
				}
				out.write("<td class=\"r");
				out.write(titlesc);
				out.write("\"></td></tr>\n");
			} else {
				out.write("<tr id=\"");
				out.write(uuid);
				out.write("!caption\"><td class=\"l");
				out.write(titlesc);
				out.write("\"></td><td class=\"m");
				out.write(titlesc);
				out.write("\">");
				caption.redraw(out);
				out.write("</td><td class=\"r");
				out.write(titlesc);
				out.write("\"></td></tr>\n");
			}
			out.write("</table>\n");
			wcExtStyle = "border-top:0;";
		}

		wcExtStyle += self.getContentStyle();
		out.write("<div id=\"");
		out.write(uuid);
		out.write("!cave\" class=\"");
		out.write(self.getContentSclass());
		out.write("\"");
		if (wcExtStyle.length() > 0) {
			out.write(" style=\"");
			out.write(wcExtStyle);
			out.write('"');
		}

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child != caption)
				child.redraw(out);
		}
		out.write("</div></div>\n");
	}
}
