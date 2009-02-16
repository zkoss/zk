/* TabboxDefault.java

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;

/**
 * {@link Tabbox}'s default mold.
 * It forwards the vertical orient to {@link Tabbox2DefaultV}.
 * 
 * @author RyanWu
 * 
 * @since 3.5.0
 */
public class Tabbox2Default implements ComponentRenderer {
	private final Tabbox2DefaultV _vtabbox = new Tabbox2DefaultV();

	public void render(Component comp, Writer out) throws IOException {
		final Tabbox self = (Tabbox) comp;
		if("vertical".equals(self.getOrient())){
			_vtabbox.render(comp, out);
			return;
		}

		final SmartWriter wh = new SmartWriter(out);
		final Tabs tabs = self.getTabs();		
		
			wh.write("<div id=\"").write(self.getUuid()).write("\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.writeln(" z.type=\"zul.tab2.Tabbox2\">")
			.writeln(tabs)
			.writeln(self.getTabpanels())
			.write("</div>");
	}
}
