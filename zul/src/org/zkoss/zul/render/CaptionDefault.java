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
		final Caption self = (Caption)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		final String imgTag = self.getImgTag();
		
		if(self.isLegend()){
			final String label = self.getLabel();
			
			out.write("<legend>");
			if(imgTag!=null){
				out.write(imgTag);
			}
			out.write(XMLs.escapeXML(label));
			
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				child.redraw(out);
			}
			out.write("</legend>");
		}else{
			final String clabel = self.getCompoundLabel();
			
			out.write("<table id=\"");
			out.write(uuid);
			out.write("\" ");
			out.write("z.type=\"zul.widget.Capt\"");
			out.write(self.getOuterAttrs());
			out.write(self.getInnerAttrs());
			out.write(" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			out.write("<tr valign=\"middle\">");
			out.write("<td align=\"left\" class=\"caption\">");
			if(imgTag!=null){
				out.write(imgTag);
			}
			
			if(clabel==null || clabel.trim().length()==0){
				out.write("&nbsp;");//<%-- bug 1688261: nbsp is required --%>
			}else{
				out.write(XMLs.escapeXML(clabel));
			}
			out.write("</td>");
			
			
			out.write("<td align=\"right\" class=\"caption\" id=\"");
			out.write(uuid);
			out.write("!cave\">");
			
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				child.redraw(out);
			}
			
			if(self.isClosableVisible()){
				out.write("<td width=\"16\"><img id=\"");
				out.write(self.getParent().getUuid());
				out.write("!close\" src=\"");
				out.write(exec.encodeURL("~./zul/img/close-off.gif"));
				out.write("\"/></td>");
			}
			
			out.write("</tr></table>");
		}
	}
}
