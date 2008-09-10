/* Tabpanel2Accordion.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 22, 2008 6:03:53 PM , Created by RyanWu
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
		final String look = tabbox.getTabLook() + '-';
		final String lookaccd = look + "accd-";
		final String lookaccdL = look + "accdlite-";
		String uuid = self.getUuid();
		String tabuuid = tab.getUuid();
		String mold = tabbox.getMold();
		
		if (mold.equals("accordion")) {
		
		wh.writeln("<div class=\""+look+"accd\" id=\""+uuid+"\">");//-- self.outerAttrs/innerAttrs gen below
			if(!Strings.isBlank(tabbox.getPanelSpacing()) && self.getIndex() != 0)
				wh.writeln("<div style=\"margin:0;display:list-item;width:100%;height:${self.tabbox.panelSpacing};\"></div>");
			
			wh.write("<div id=\""+tabuuid+"\"")	
				.write(tab.getOuterAttrs()).write(tab.getInnerAttrs())
				.write(" z.max=\"true\"")
				.write(" z.sel=\""+tab.isSelected()+"\"")
				.write(" z.type=\"zul.tab2.Tab2\"")
				.write(" z.box=\""+tabbox.getUuid()+"\"")
				.write(" z.panel=\""+uuid+"\"")
				.write(" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"")
				.write(" z.disabled=\""+tab.isDisabled()+"\"").writeln(">");
				wh.writeln("<div align=\"left\" class=\""+lookaccd+"header\" >");
					if (tab.isClosable()){
						wh.writeln("<a id=\""+tabuuid+"!close\"  class=\""+lookaccd+"closebtn\"></a>");
					}
					wh.writeln("<a href=\"javascript:;\" id=\""+tabuuid+"!a\" class=\""+lookaccd+"left-tr\">");
						wh.writeln("<em class=\""+lookaccd+"right-tr\">");
							wh.writeln("<span class=\""+lookaccd+"inner\">");
								wh.writeln("<span class=\""+lookaccd+"text\">");
									wh.write(tab.getImgTag());
									new Out(tab.getLabel()).render(out);
								wh.writeln("</span>");
							wh.writeln("</span>");
						wh.writeln("</em>");
					wh.writeln("</a>");
				wh.writeln("</div>");
			wh.writeln("</div>");
			wh.write("<div id=\""+uuid+"!real\"")
				.writeln(self.getOuterAttrs()).write(self.getInnerAttrs()+">");
				wh.writeln(" <div id=\""+uuid+"!cave\" style=\"height:100%\">");
					wh.writeChildren(self);
				wh.writeln("</div>");
			wh.writeln("</div>");
		wh.writeln("</div>");	
		}else if("accordion-lite".equals(mold)){
		wh.writeln("<div class=\""+look+"accdlite\" id=\""+uuid+"\">");//-- self.outerAttrs/innerAttrs gen below			
			wh.write("<div id=\""+tabuuid+"\"")	
				.write(tab.getOuterAttrs()).write(tab.getInnerAttrs())
				.write(" z.sel=\""+tab.isSelected()+"\"")
				.write(" z.type=\"zul.tab2.Tab2\"")
				.write(" z.box=\""+tabbox.getUuid()+"\"")
				.write(" z.panel=\""+uuid+"\"")
				.write(" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"")
				.write(" z.disabled=\""+tab.isDisabled()+"\"").writeln(">");
				wh.writeln("<div align=\"left\" class=\""+lookaccdL+"header\" >");
					if (tab.isClosable()){
						wh.writeln("<a id=\""+tabuuid+"!close\"  class=\""+lookaccdL+"closebtn\"></a>");
					}
					wh.writeln("<a href=\"javascript:;\" id=\""+tabuuid+"!a\" class=\""+lookaccdL+"left-tr\">");
						wh.writeln("<em class=\""+lookaccdL+"right-tr\">");
							wh.writeln("<span class=\""+lookaccdL+"inner\">");
								wh.writeln("<span class=\""+lookaccdL+"text\">");
									wh.write(tab.getImgTag());
									new Out(tab.getLabel()).render(out);
								wh.writeln("</span>");
							wh.writeln("</span>");
						wh.writeln("</em>");
					wh.writeln("</a>");
				wh.writeln("</div>");
			wh.writeln("</div>");
			wh.write("<div id=\""+uuid+"!real\"")
				.writeln(self.getOuterAttrs()).write(self.getInnerAttrs()+">");
				wh.writeln(" <div id=\""+uuid+"!cave\" style=\"height:100%\">");
					wh.writeChildren(self);
				wh.writeln("</div>");
			wh.writeln("</div>");
		wh.writeln("</div>");		
		}		
	}
}
