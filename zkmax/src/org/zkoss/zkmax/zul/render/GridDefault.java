/* GridDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 11:42:36 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zul.Grid;

/*
 * {@link Grid}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class GridDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Grid self = (Grid) comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.grid.Grid\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		if(self.getColumns() != null){
			wh.write("<div id=\"").write(uuid).write("!head\" class=\"grid-head\">")
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">")
				.writeComponents(self.getHeads())
				.write("</table></div>");
		}

		wh.write("<div id=\"").write(uuid).write("!body\" class=\"grid-body\">");
		wh.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"grid-btable\">")
			.writeln(self.getRows())
			.write("</table></div>");

		if(self.getFoot() != null){
			wh.write("<div id=\"").write(uuid).write("!foot\" class=\"grid-foot\">")
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  style=\"table-layout:fixed\">")
				.writeln(self.getFoot())
				.write("</table></div>");
		}

		wh.write("</div>");
	}

}
