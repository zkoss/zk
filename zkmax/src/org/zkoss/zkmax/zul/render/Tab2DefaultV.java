/* Tab2DefaultV.java

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

import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tab}'s default mold in vertical only.
 * 
 * @author RyanWu
 * 
 * @since 3.5.0
 * 
 */
public class Tab2DefaultV implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final Tab self = (Tab) comp;
		final Tabbox tabbox = self.getTabbox();
		final SmartWriter wh = new SmartWriter(out);
		final Execution exec = Executions.getCurrent();
		final String look = tabbox.getTabLook() + '-';		
		final Tabpanel panel = self.getLinkedPanel();
		wh.write("<li id=\""+self.getUuid()+"\"");
			if (!Strings.isBlank(self.getWidth())){
				wh.write(" style=\""+self.getWidth()+"\"");
			}
			wh.write(" z.type=\"Tab2\"")
					.write(self.getOuterAttrs())
					.write(" z.sel=\"").write(self.isSelected())
					.write("\" z.box=\"").write(tabbox.getUuid())
					.write("\" z.panel=\"").write(panel==null?"":panel.getUuid()).write("\" ")
					.write("z.disabled=\"").write(self.isDisabled())
					.writeln("\">");	
			if (self.isClosable()){
				wh.writeln("<a id=\""+self.getUuid()+"!close\" class=\""+look+"close\"  ></a>");
			}else{
				wh.writeln("<a class=\""+look+"noclose\" ></a>");
			}
			wh.write("<a class=\""+look+"a\" id=\""+self.getUuid()+"!real\"");
			wh.writeln(self.getInnerAttrs()+" >");		
				wh.writeln("<em id=\""+self.getUuid()+"\" class=\""+look+"em\">");
					if (self.isClosable()){
						wh.writeln("<span id=\""+self.getUuid()+"!inner\" class=\""+look+"inner "+look+"innerclose\" \">");
					}else{
						wh.writeln("<span id=\""+self.getUuid()+"!inner\" class=\""+look+"inner \">");
					}
						wh.writeln("<span class=\""+look+"text\">");
							wh.write(self.getImgTag());
							new Out(self.getLabel()).render(out);
						wh.writeln("</span>");
					wh.writeln("</span>");
				wh.writeln("</em>");
			wh.writeln("</a>");
		wh.writeln("</li>");	
	}
}
