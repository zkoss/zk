/* DefaultGroupbox3D.java

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
public class DefaultGroupbox3D implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final Groupbox self = (Groupbox)comp;
		final Caption caption = self.getCaption();
		
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		out.write("<table id=\"");
		out.write(uuid);
		out.write("\" z.type=\"zul.widget.Grbox\"");
		out.write(self.getOuterAttrs());
		out.write(self.getInnerAttrs());
		out.write("><tr valign=\"top\"><td>");
		
		if(caption!=null){
			out.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			out.write("<tr><td class=\"groupbox-3d-tl\"></td>");
			out.write("<td colspan=\"3\" class=\"groupbox-3d-tm\"></td>");
			out.write("<td class=\"groupbox-3d-tr\"></td></tr>");
			
			out.write("<tr height=\"22\"><td class=\"groupbox-3d-ml\"></td>");
			out.write("<td width=\"3\" class=\"groupbox-3d-mm\"></td>");
			out.write("<td class=\"groupbox-3d-mm\">");
			caption.redraw(out);
			out.write("</td>");
			out.write("<td width=\"3\" class=\"groupbox-3d-mm\"></td>");
			out.write("<td class=\"groupbox-3d-mr\"></td></tr>");
			out.write("<tr><td colspan=\"5\" class=\"groupbox-3d-b\"></td></tr>");
			out.write("</table>");
		}
		
		String gcExtStyle = "";
		if(caption!=null){
			gcExtStyle += "border-top:0;";
		}
		gcExtStyle+=self.getContentStyle();
		
		out.write("<div id=\"");
		out.write(uuid);
		out.write("!slide\" ");
		if(self.isOpen()){
		}else{
			out.write("style=\"display:none\" ");
		}
		out.write(">");
		
		out.write("<div id=\"");
		out.write(uuid);
		out.write("!cave\" ");
		out.write("class=\"");
		out.write(self.getContentSclass());
		out.write("\"");
		if (gcExtStyle.length() > 0) {
			out.write(" style=\"");
			out.write(gcExtStyle);
			out.write('"');
		}
		out.write(">");
		
		
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child != caption)
				child.redraw(out);
		}
		
		out.write("</div></div>");
		
		//shadow
		out.write("<table id=\"");
		out.write(self.getUuid());
		out.write("!sdw\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
		out.write("<tr><td class=\"groupbox-3d-shdl\"></td>");
		out.write("<td class=\"groupbox-3d-shdm\">");
		out.write("<img width=\"1\" height=\"1\" src=\"");
		out.write(exec.encodeURL("~./img/spacer.gif"));
		out.write("\"/></td> ");
		out.write("<td class=\"groupbox-3d-shdr\"></td>");
		out.write("</tr></table>");
		
		
		out.write("</td></tr></table>\n");

	}
}
