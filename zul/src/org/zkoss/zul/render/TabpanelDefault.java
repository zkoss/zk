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
import org.zkoss.zk.ui.util.ComponentRenderer;
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
		final WriterHelper wh = new WriterHelper(out);
		final Tabpanel self = (Tabpanel) comp;
		
		if(self.getTabbox().getOrient().equals("vertical")){
			wh.write("<div id=\"" + self.getUuid() + "\"" + self.getOuterAttrs() + self.getInnerAttrs() + ">");		
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component) it.next();
				child.redraw(out);
			}	
			wh.write("</div>");		
		}
		else{
			wh.write("<tr id=\"" + self.getUuid() + "\"" + self.getOuterAttrs() + ">");
			wh.write("<td id=\"" + self.getUuid() + "!real\" class=\"tabpanel-hr\""
				+ self.getInnerAttrs() + ">");
			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component) it.next();
				child.redraw(out);
			}		
			wh.write("</td>");
			wh.write("</tr>");
		}
	}
}
