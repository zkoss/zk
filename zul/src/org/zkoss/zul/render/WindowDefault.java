/* WindowDefault.java

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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.WriterHelper;
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
		final WriterHelper wh = new WriterHelper(out);
		final Window self = (Window)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();

		wh.write("<div id=\"");
		wh.write(uuid);
		wh.write("\" z.type=\"zul.wnd.Wnd\" z.autoz=\"true\"");
		wh.write(self.getOuterAttrs());
		wh.write(self.getInnerAttrs());
		wh.writeln(">");

		final Caption caption = self.getCaption();
		final String title = self.getTitle(), titlesc = self.getTitleSclass();
		String wcExtStyle = "";
		if (caption == null && title.length() == 0) {
			if (exec.isExplorer() && !exec.isExplorer7()) {
				wh.writeln(
					"<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
					+"<tr height=\"1px\"><td></td></tr></table>");
			}
		} else {
			wh.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			if (caption == null) {
				wh.write("<tr id=\"").write(uuid).write("!caption\" class=\"title\">")
				  .write("<td class=\"l").write(titlesc).writeln("\"></td>")
				  .write("<td class=\"m").write(titlesc).write("\">")
				  .write(XMLs.escapeXML(title))
				  .writeln("</td>");

				if (self.isClosable()) {
					wh.write("<td width=\"16\" class=\"m");
					wh.write(titlesc);
					wh.write("\"><img id=\"");
					wh.write(uuid);
					wh.write("!close\" src=\"");
					wh.write(exec.encodeURL("~./zul/img/close-off.gif"));
					wh.write("\"/></td>");
				}
				wh.write("<td class=\"r");
				wh.write(titlesc);
				wh.write("\"></td></tr>\n");
			} else {
				wh.write("<tr id=\"");
				wh.write(uuid);
				wh.write("!caption\"><td class=\"l");
				wh.write(titlesc);
				wh.write("\"></td><td class=\"m");
				wh.write(titlesc);
				wh.write("\">");
				caption.redraw(out);
				wh.write("</td><td class=\"r");
				wh.write(titlesc);
				wh.write("\"></td></tr>\n");
			}
			wh.write("</table>\n");
			wcExtStyle = "border-top:0;";
		}

		final String cs = self.getContentStyle();
		if(cs!=null){
			wcExtStyle += cs;
		}
		wh.write("<div id=\"");
		wh.write(uuid);
		wh.write("!cave\" class=\"");
		wh.write(self.getContentSclass());
		wh.write("\"");
		if (wcExtStyle.length() > 0) {
			wh.write(" style=\"");
			wh.write(wcExtStyle);
			wh.write("\"");
		}
		wh.write(">");

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child != caption)
				child.redraw(out);
		}
		wh.write("</div></div>\n");
	}
}
