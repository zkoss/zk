/* TabDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 3:59:51 PM , Created by robbiecheng
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

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

import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tab}'s default mold.
 * It forward to {@link TabDefaultV} if the orient is vertical.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 * 
 */
public class Tab2Default implements ComponentRenderer {
	private final TabDefaultV _vtab = new TabDefaultV();

	public void render(Component comp, Writer out) throws IOException {
		final Tab self = (Tab) comp;
		final Tabbox tabbox = self.getTabbox();
		if ("vertical".equals(tabbox.getOrient())) {
			_vtab.render(comp, out);
			return; //done
		}

		final SmartWriter wh = new SmartWriter(out);		
		final String look = tabbox.getTabLook() + '-';		
		final Tabpanel panel = self.getLinkedPanel();		
			String uuid = self.getUuid();
			wh.write("<li id=\""+uuid+"\""); 
				if (!Strings.isBlank(self.getHeight())){
					wh.write("style=\"height:"+self.getHeight()+";\"");
				}
				wh.write("z.type=\"Tab2\"").write(self.getOuterAttrs()).write(self.getInnerAttrs())
					.write(" z.sel=\"").write(self.isSelected()).write("\" z.box=\"")
					.write(tabbox.getUuid()).write("\" z.panel=\"")
					.write(panel==null?"":panel.getUuid()).write("\" ")
					.write("z.disabled=\"").write(self.isDisabled())
					.writeln("\">");	
				if(self.isClosable()){				
					wh.writeln("<a class=\""+look+"close\" id=\""+uuid+"!close\" onclick=\"return false;\"/>");
				}
				wh.writeln("<a class=\""+look+"a\" id=\""+uuid+"a\"  onclick=\"return false;\" href=\"#\">");
					wh.writeln("<em id=\""+uuid+"\" class=\""+look+"em\">");						
						if(self.isClosable()){
							wh.writeln("<span id=\""+uuid+"\" class=\""+look+"inner"+" "+look+"innerclose\">");
						}else{
							wh.writeln("<span id=\""+uuid+"\" class=\""+look+"inner\"");	
						}
							wh.write("<span class=\""+look+"text\">").write(self.getImgTag());
							new Out(self.getLabel()).render(out);
							wh.writeln("</span>");
						wh.writeln("</span>");
					wh.writeln("</em>");
				wh.writeln("</a>");
			wh.writeln("</li>");
	}
}
