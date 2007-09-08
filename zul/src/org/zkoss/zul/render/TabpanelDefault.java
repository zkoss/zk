/* TabpanelDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 6:49:21 PM , Created by robbiecheng
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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tabpanel}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TabpanelDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Tabpanel self = (Tabpanel) comp;

		if(self.getTabbox().getOrient().equals("vertical")){
			wh.write("<div id=\"").write(self.getUuid()).write('"')
				.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>')
				.writeChildren(self)
				.write("</div>");		
		}
		else{
			wh.write("<tr id=\"").write(self.getUuid()).write('"')
				.write(self.getOuterAttrs()).writeln('>')
				.write("<td id=\"").write(self.getUuid()).write("!real\" class=\"tabpanel-hr\"")
				.write(self.getInnerAttrs()).write('>')
				.writeChildren(self)
				.writeln("</td></tr>");
		}
	}
}
