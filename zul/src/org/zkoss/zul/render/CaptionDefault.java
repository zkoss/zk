/* CaptionDefault.java

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

import org.zkoss.xml.XMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Caption;

/**
 * {@link Caption}'s default mold.
 *
 * @author dennis.chen
 * @since 3.0.0
 */
public class CaptionDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Caption self = (Caption)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		final String imgTag = self.getImgTag();
		
		if(self.isLegend()){
			final String label = self.getLabel();
			
			wh.write("<legend>").write(imgTag).write(XMLs.escapeXML(label));
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				child.redraw(out);
			}
			wh.write("</legend>");
		}else{
			final String clabel = self.getCompoundLabel();
			
			wh.write("<table id=\"").write(uuid).write("\" ");
			wh.write("z.type=\"zul.widget.Capt\"").write(self.getOuterAttrs()).write(self.getInnerAttrs());
			wh.write(" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr valign=\"middle\">");
			wh.write("<td align=\"left\" class=\"caption\">").write(imgTag);
			
			if(clabel==null || clabel.trim().length()==0){
				wh.write("&nbsp;");//<%-- bug 1688261: nbsp is required --%>
			}else{
				wh.write(XMLs.escapeXML(clabel));
			}
			wh.write("</td>");
			
			wh.write("<td align=\"right\" class=\"caption\" id=\"").write(uuid).write("!cave\">");
			
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				child.redraw(out);
			}
			wh.write("</td>");
			if(self.isClosableVisible()){
				wh.write("<td width=\"16\"><img id=\"").write(self.getParent().getUuid()).write("!close\" src=\"");
				wh.write(exec.encodeURL("~./zul/img/close-off.gif")).write("\"/></td>");
			}
			
			wh.writeln("</tr></table>");
		}
	}
}
