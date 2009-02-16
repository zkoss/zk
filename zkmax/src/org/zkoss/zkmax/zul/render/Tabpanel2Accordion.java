/* Tabpanel2Accordion.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 22, 2008 6:03:53 PM , Created by RyanWu
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tabpanel}'s accordion mold.
 * 
 * @author RyanWu
 * 
 * @since 3.5.0
 */

public class Tabpanel2Accordion implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {	
		final SmartWriter wh = new SmartWriter(out);		
		final Tabpanel self = (Tabpanel) comp;
		final Tab tab = self.getLinkedTab();
		if (tab == null) return; //generate nothing (Bug 1848377)
		final Tabbox tabbox = self.getTabbox();
		final String tabzcs = tab.getZclass() + '-';
		final String zcs = self.getZclass();
		final String uuid = self.getUuid();
		final String tabuuid = tab.getUuid();
		final String mold = tabbox.getMold();
		if (mold.equals("accordion")) {
			wh.write("<div class=\"").write(zcs).write("-outer\" id=\"").write(uuid).write("\">");
			
			if(!Strings.isBlank(tabbox.getPanelSpacing()) && self.getIndex() != 0)
				wh.writeln("<div style=\"margin:0;display:list-item;width:100%;height:${self.tabbox.panelSpacing};\"></div>");
			
			wh.write("<div id=\"").write(tabuuid).write("\"").write(tab.getOuterAttrs())
				.write(tab.getInnerAttrs()).write(" z.type=\"zul.tab2.Tab2\">");
			wh.write("<div align=\"left\" class=\"").write(tabzcs).write("header\">");
			if (tab.isClosable()) {
				wh.write("<a id=\"").write(tabuuid).write("!close\" class=\"").write(tabzcs).write("close\"></a>");
			}
			wh.writeln("<a href=\"javascript:;\" id=\"").write(tabuuid).write("!a\" class=\"").write(tabzcs).write("tl\">");
			wh.writeln("<em class=\"").write(tabzcs).write("tr\">");
			wh.writeln("<span class=\"").write(tabzcs).write("tm\">");
			wh.writeln("<span class=\"").write(tabzcs).write("text\">");
			wh.write(tab.getImgTag());
			new Out(tab.getLabel()).render(out);
			wh.writeln("</span>").writeln("</span>").writeln("</em>").writeln("</a>")
				.writeln("</div>").writeln("</div>");
			wh.write("<div id=\""+uuid+"!real\"")
				.writeln(self.getOuterAttrs()).write(self.getInnerAttrs()+">");
			wh.writeln("<div id=\""+uuid+"!cave\">");
			wh.writeChildren(self);
			wh.writeln("</div>");
			wh.writeln("</div>");
			wh.writeln("</div>");
		
		}else if("accordion-lite".equals(mold)){
			wh.write("<div class=\"").write(zcs).write("-outer\" id=\"").write(uuid).write("\">");
			wh.write("<div id=\"").write(tabuuid).write("\"").write(tab.getOuterAttrs())
				.write(tab.getInnerAttrs()).write(" z.type=\"zul.tab2.Tab2\">");
			wh.write("<div align=\"left\" class=\"").write(tabzcs).write("header\">");
			if (tab.isClosable()) {
				wh.write("<a id=\"").write(tabuuid).write("!close\" class=\"").write(tabzcs).write("close\"></a>");
			}
			wh.writeln("<a href=\"javascript:;\" id=\"").write(tabuuid).write("!a\" class=\"").write(tabzcs).write("tl\">");
			wh.writeln("<em class=\"").write(tabzcs).write("tr\">");
			wh.writeln("<span class=\"").write(tabzcs).write("tm\">");
			wh.writeln("<span class=\"").write(tabzcs).write("text\">");
			wh.write(tab.getImgTag());
			new Out(tab.getLabel()).render(out);
			wh.writeln("</span>").writeln("</span>").writeln("</em>").writeln("</a>")
			.writeln("</div>").writeln("</div>");
			wh.write("<div id=\""+uuid+"!real\"")
				.writeln(self.getOuterAttrs()).write(self.getInnerAttrs()+">");
			wh.writeln("<div id=\""+uuid+"!cave\">");
			wh.writeChildren(self);
			wh.writeln("</div>");
			wh.writeln("</div>");
			wh.writeln("</div>");
		}		
	}
}
