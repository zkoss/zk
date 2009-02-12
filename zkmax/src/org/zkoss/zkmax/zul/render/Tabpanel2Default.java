/* Tabpanel2Default.java

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
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tabpanel}'s default mold.
 * It forwards to {@link Tabpanel2Accordion} if the tabbox's mold is accordion.
 * It forwards to {@link Tabpanel2DefaultV} if the tabbox's orient is vertical.
 * 
 * @author RyanWu
 * 
 * @since 3.5.0
 */
public class Tabpanel2Default implements ComponentRenderer {
	private final Tabpanel2DefaultV _vpanel = new Tabpanel2DefaultV();
	private final Tabpanel2Accordion _acdpanel = new Tabpanel2Accordion();
	public void render(Component comp, Writer out) throws IOException {
		final Tabpanel self = (Tabpanel) comp;
		final Tabbox tabbox = self.getTabbox();
		final String mold = tabbox.getMold(); 
		if ("accordion".equals(mold) || "accordion-lite".equals(mold)) {
			_acdpanel.render(comp, out);
			return;
		}
		if ("vertical".equals(tabbox.getOrient())) {
			_vpanel.render(comp, out);
			return;
		}

		final SmartWriter wh = new SmartWriter(out);

		wh.write("<div id=\"").write(self.getUuid()).write('"')
			.write(" z.type=\"zul.tab2.Tabpanel2\"")
			.write(self.getOuterAttrs())
			.write('>')
			.write("<div id=\"").write(self.getUuid()).write("!real\" ")
			.write(self.getInnerAttrs()).write('>')
			.writeChildren(self)
			.writeln("</div></div>");
	}
}
