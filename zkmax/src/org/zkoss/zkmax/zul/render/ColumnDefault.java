/* ColumnDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 10:48:59 AM , Created by jumperchen
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
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;

/*
 * {@link Column}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class ColumnDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Column self = (Column) comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		wh.write("<th id=\"").write(uuid).write("\"").write(" z.type=\"Col\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs())
		.write("><div id=\"").write(self.getUuid()).write("!cave\" class=\"").write(zcls).write("-cnt\">");
		wh.write(self.getImgTag());
		new Out(self.getLabel()).render(out);
		String mpop = ((Columns)self.getParent()).getMenupopup();
		if (mpop != null && mpop.trim().length() > 0 && !mpop.equals("none"))
			wh.write("<a id=\"").write(uuid).write("!btn\" href=\"#\" class=\"").write(zcls)
				.write("-btn\"></a>");
		wh.writeChildren(self);
		wh.writeln("</div></th>");
	}

}
