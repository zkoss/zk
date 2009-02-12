/* PortallayoutDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 13, 2008 3:56:38 PM , Created by jumperchen
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
import org.zkoss.zkmax.zul.Portallayout;

/**
 * {@link Portallayout}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class PortallayoutDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Portallayout self = (Portallayout) comp;
		wh.write("<div id=\"").write(self.getUuid()).write('"').write(
				self.getOuterAttrs()).write(self.getInnerAttrs()).write(
				" z.type=\"zkmax.zul.portallayout.PortalLayout\">");
		wh.write("<div id=\"").write(self.getUuid() + "!real\" class=\"")
			.write(self.getZclass()).write("-inner\">");
		wh.writeChildren(self);
		wh.write("<div class=\"z-clear\"></div>");
		wh.write("</div></div>");
	}
}
