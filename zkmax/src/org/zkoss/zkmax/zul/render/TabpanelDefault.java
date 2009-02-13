/* TabpanelDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 6:49:21 PM , Created by robbiecheng
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
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tabpanel}'s default mold.
 * It forwards to {@link TabpanelAccordion} if the tabbox's mold is accordion.
 * It forwards to {@link TabpanelDefaultV} if the tabbox's orient is vertical.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TabpanelDefault implements ComponentRenderer {
	private final TabpanelDefaultV _vpanel = new TabpanelDefaultV();
	private final TabpanelAccordion _acdpanel = new TabpanelAccordion();
	public void render(Component comp, Writer out) throws IOException {
		final Tabpanel self = (Tabpanel) comp;
		final Tabbox tabbox = self.getTabbox();
		if ("accordion".equals(tabbox.getMold())) {
			_acdpanel.render(comp, out);
			return;
		}
		if ("vertical".equals(tabbox.getOrient())) {
			_vpanel.render(comp, out);
			return;
		}

		final SmartWriter wh = new SmartWriter(out);

		wh.write("<div id=\"").write(self.getUuid()).write('"')
			.write(" z.type=\"zul.tab.Tabpanel\"")
			.write(self.getOuterAttrs()).write('>')
			.write("<div id=\"").write(self.getUuid()).write("!real\"")
			.write(self.getInnerAttrs()).write('>')
			.writeChildren(self)
			.writeln("</div></div>");
	}
}
