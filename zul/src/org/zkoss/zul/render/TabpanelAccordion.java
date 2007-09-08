/* TabpanelAccordion.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 8:07:46 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tabpanel}'s accordion mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */

public class TabpanelAccordion implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {	
		final SmartWriter wh = new SmartWriter(out);
		final Execution exec = Executions.getCurrent();
		final Tabpanel self = (Tabpanel) comp;
		final Tab tab = self.getLinkedTab();
		final String suffix = (self.isSelected()) ? "-sel" : "-uns";
		final int colspan1 = (tab.isClosable()) ? 4:3;
		final int colspan2 = (tab.isClosable()) ? 6:5;
		final String height = Strings.isBlank(self.getHeight()) ? "22" : self.getHeight(); 		
		
		wh.write("<tr id=\"").write(self.getUuid()).write("\">"); //no exteriorAttribute here because tab.js controls it diff
		wh.write("<td>");
		wh.write("<table id=\"").write(tab.getUuid()).write("\"").write(tab.getOuterAttrs()).write(tab.getInnerAttrs() 
				).write(" z.sel=\"").write(tab.isSelected()).write("\" z.type=\"zul.tab.Tab\" z.box=\"" 
				).write(tab.getTabbox().getUuid()).write("\" z.panel=\"").write(self.getUuid() 
				).write("\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");		
		if(!Strings.isBlank(self.getTabbox().getPanelSpacing()) && self.getIndex() != 0)
			wh.write("<tr height=\"").write(self.getTabbox().getPanelSpacing()).write("\"><td></td></tr>");
		
		wh.write("<tr>");
			wh.write("<td class=\"").write("tabaccd-3d-tl").write(suffix).write("\"></td>");			
			wh.write("<td colspan=\"").write(colspan1).write("\" class=\"").write("tabaccd-3d-tm").write(suffix).write("\"></td>");
			wh.write("<td class=\"").write("tabaccd-3d-tr").write(suffix).write("\"></td>");			
		wh.write("</tr>");	
	    
		wh.write("<tr height=\"").write(height).write("\">");		
			wh.write("<td class=\"").write("tabaccd-3d-ml").write(suffix).write("\"></td>");
			wh.write("<td width=\"3\" class=\"").write("tabaccd-3d-mm").write(suffix).write("\"></td>");
			wh.write("<td align=\"left\" class=\"").write("tabaccd-3d-mm").write(suffix).write("\"><a href=\"javascript:;\"")
			.write(" id=\"").write(tab.getUuid()).write("!a\">");			
			wh.write(tab.getImgTag());
			new Out(tab.getLabel()).render(out);
			wh.write( "</a></td>");			

		if(tab.isClosable()){
			wh.write("<td width=\"11\" align=\"right\" class=\"").write("tabaccd-3d-mm").write(suffix) 
			.write("\"><img id=\"").write(self.getUuid()).write("!close\" src=\"")
			.write(exec.encodeURL("~./zul/img/close-off.gif")).write("\"/></td>");
		}
			wh.write("<td width=\"3\" class=\"").write("tabaccd-3d-mm").write(suffix).write("\"></td>");	
			wh.write("<td class=\"").write("tabaccd-3d-mr").write(suffix).write("\"></td>");
		wh.write("</tr>");	
		wh.write("<tr>");
			wh.write("<td colspan=\"").write(colspan2)
				.write("\" class=\"tabaccd-3d-b\"></td></tr></table>");

			wh.write("<div id=\"").write(self.getUuid()).write("!real\"")
				.write(self.getOuterAttrs()).write(self.getInnerAttrs())
				.write("><div id=\"").write(self.getUuid()).write("!cave\">")
				.writeChildren(self)
				.writeln("</div></div></td></tr>");

	}

}
