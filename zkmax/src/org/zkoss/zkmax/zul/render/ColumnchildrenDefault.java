/* ColumnchildrenDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 20 11:20:23 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkmax.zul.Columnchildren;

/**
 * {@link Columnchildren}'s default mold.
 * 
 * @author garcelin
 * @since 3.1.0
 */
public class ColumnchildrenDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Columnchildren self = (Columnchildren) comp;

		wh.write("<div id=\"").write(self.getUuid()).write('"').write(
				" z.type=\"zkmax.zul.columnlayout.ColumnChildren\">");
		wh.write("<div id=\"").write(self.getUuid()).write("!real\"").write(
				self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<div class=\"z-plain-bwrap\">");
		wh.write("<div id=\"").write(self.getUuid()).write(
				"!cave\" class=\"z-plain-body\">");
		wh.writeChildren(self);
		wh.write("</div></div></div></div>");
	}
}
