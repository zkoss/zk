/* TabboxDefaultV.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 7:08:15 PM , Created by robbiecheng
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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;

/**
 * {@link Tabbox}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TabboxDefaultV implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Tabbox self = (Tabbox) comp;
		final Tabs tabs = self.getTabs();

		wh.write("<div id=\"").write(self.getUuid()).write('"')
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.writeln(" z.type=\"zul.tab.Tabbox\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");

		wh.write("<tr valign=\"top\">")
			.write(tabs)
			.write(self.getTabpanels())
			.writeln("</tr>")
			.write("</table></div>");
	}
}
