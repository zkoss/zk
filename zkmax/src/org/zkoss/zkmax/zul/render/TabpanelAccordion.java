/* TabpanelAccordion.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 8:07:46 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Tabbox;
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
		if (tab == null) return; //generate nothing (Bug 1848377)
		final Tabbox tabbox = self.getTabbox();
		final String look = tabbox.getTabLook() + '-';
		final String suffix = self.isSelected() ? "-sel" : "-uns";
		
		wh.write("<div id=\"").write(self.getUuid()).write("\">");
		wh.write("<table id=\"").write(tab.getUuid()).write("\"")
			.write(tab.getOuterAttrs()).write(tab.getInnerAttrs())
			.write(" z.sel=\"").write(tab.isSelected()).write("\" z.type=\"zul.tab.Tab\" z.box=\"")
			.write(tabbox.getUuid()).write("\" z.panel=\"").write(self.getUuid())
			.write("\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" ")
			.write("z.disabled=\"").write(tab.isDisabled()).write("\"")
			.writeln(">");		

		if(!Strings.isBlank(tabbox.getPanelSpacing()) && self.getIndex() != 0)
			wh.write("<tr height=\"").write(tabbox.getPanelSpacing()).writeln("\"><td></td></tr>");
		
		wh.write("<tr><td class=\"").write(look).write("tl").write(suffix).writeln("\"></td>")
			.write("<td colspan=\"").write(tab.isClosable() ? 4 : 3)
			.write("\" class=\"").write(look).write("tm").write(suffix).writeln("\"></td>")
			.write("<td class=\"").write(look).write("tr").write(suffix).writeln("\"></td></tr>");	

		wh.write("<tr class=\"").write(look).write('m');
		if (!Strings.isBlank(tab.getHeight()))
			wh.write("\" style=\"height:").write(tab.getHeight());
		wh.write("\"><td class=\"")
			.write(look).write("ml").write(suffix).writeln("\"></td>")
			.write("<td width=\"3\" class=\"").write(look).write("mm").write(suffix).writeln("\"></td>")
			.write("<td align=\"left\" class=\"").write(look).write("mm").write(suffix)
			.write("\"><a href=\"javascript:;\" id=\"").write(tab.getUuid()).write("!a\">")
			.write(tab.getImgTag());
			new Out(tab.getLabel()).render(out);
			wh.writeln("</a></td>");			

		if(tab.isClosable()){
			wh.write("<td width=\"11\" align=\"right\" class=\"")
			.write(look).write("mm").write(suffix).write("\"><img id=\"")
			.write(tab.getUuid()).write("!close\" src=\"")
			.write(exec.encodeURL("~./zul/img/close-off.gif"))
			.writeln("\"/></td>");
		}

		wh.write("<td width=\"3\" class=\"").write(look).write("mm").write(suffix).writeln("\"></td>")
			.write("<td class=\"").write(look).write("mr").write(suffix).writeln("\"></td></tr>");

		wh.write("<tr><td colspan=\"").write(tab.isClosable() ? 6 : 5)
			.write("\" class=\"").write(look).write("b\"></td></tr>\n</table>");

		wh.write("<div id=\"").write(self.getUuid()).write("!real\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write("><div id=\"").write(self.getUuid()).write("!cave\">")
			.writeChildren(self)
			.writeln("</div></div></div>");
	}
}
