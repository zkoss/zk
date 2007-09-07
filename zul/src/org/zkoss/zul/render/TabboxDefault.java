/* TabboxDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 7:08:15 PM , Created by robbiecheng
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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

/**
 * {@link Tabbox}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */

public class TabboxDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Tabbox self = (Tabbox) comp;
		final Tabs tabs = self.getTabs();
		
		wh.write("<table id=\"").write(self.getUuid()).write("\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write(" z.tabs=\"").write(tabs==null ?null:tabs.getUuid())
			.writeln("\" z.type=\"zul.tab.Tabbox\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");		

		if("vertical".equals(self.getOrient())){
			wh.write("<tr valign=\"top\">");
			wh.write(tabs);
			wh.writeln(self.getTabpanels());
			wh.write("</tr>");
		} else {
			wh.write(tabs);
			wh.writeln(self.getTabpanels());
		}

		wh.write("</table>");
	}

}
