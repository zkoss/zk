/* ColumnchildrenDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 20 11:20:23 TST 2008, Created by gracelin
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
import org.zkoss.zkex.zul.Columnchildren;

/**
 * {@link Columnchildren}'s default mold.
 * 
 * @author garcelin
 * @since 3.5.0
 */
public class ColumnchildrenDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Columnchildren self = (Columnchildren) comp;
		final String sclass = self.getZclass();
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zkex.zul.columnlayout.ColumnChildren\"").write(
				self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<div class=\"").write(sclass).write("-body\">");
		wh.write("<div id=\"").write(self.getUuid()).write("!cave\" class=\"").write(sclass)
			.write("-cnt\">");
		wh.writeChildren(self);
		wh.write("</div><div style=\"height:1px;position:relative;width:1px;\"><br/></div></div></div>");
	}
}
