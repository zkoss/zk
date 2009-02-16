/* PortalchildrenDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 13, 2008 3:59:05 PM , Created by jumperchen
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
import org.zkoss.zkmax.zul.Portalchildren;

/**
 * {@link Portalchildren}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class PortalchildrenDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Portalchildren self = (Portalchildren) comp;
		final String zcls = self.getZclass();
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zkmax.zul.portallayout.PortalChildren\"").write(
				self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<div class=\"").write(zcls).write("-body\">");
		wh.write("<div id=\"").write(self.getUuid()).write("!cave\" class=\"").write(zcls)
			.write("-cnt\">");
		wh.writeChildren(self);
		wh.write("</div><div style=\"height:1px;position:relative;width:1px;\"><br/></div></div></div>");
	}
}
