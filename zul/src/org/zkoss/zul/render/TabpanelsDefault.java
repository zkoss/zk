/* TabpanelsDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 6:59:53 PM , Created by robbiecheng
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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanels;

/**
 * {@link Tabpanels}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */

public class TabpanelsDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Tabpanels self = (Tabpanels) comp;

		if(((Tabbox)self.getParent()).getOrient().equals("vertical")){
			wh.write("<td id=\"").write(self.getUuid()).write("\"")
				.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");

			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				((Component) it.next()).redraw(out);
			}	

			wh.writeln("</td>");
		} else {
			wh.write("<tbody id=\"").write(self.getUuid()).write("\"")
				.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");		

			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				((Component) it.next()).redraw(out);
			}	

			wh.writeln("</tbody>");
		}
	}

}
