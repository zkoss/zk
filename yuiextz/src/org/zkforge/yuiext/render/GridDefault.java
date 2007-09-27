/* GridDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 14, 2007 11:02:17 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkforge.yuiext.render;

import java.io.IOException;
import java.io.Writer;

import org.zkforge.yuiext.grid.Grid;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

/**
 * {@link Grid}'s default mold.
 * @author jumperchen
 * @since ZK 3.0.0
 *
 */
public class GridDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Grid self = (Grid) comp;
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"yuiextz.grid.ExtGrid\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(" z.rows=\"")
			.write(self.getRows().getUuid()).write("\">");
		wh.writeln("<table width=\"100%\" border=\"0\">");
		if(self.getColumns() != null) {
			wh.write("<thead>").write(self.getColumns()).writeln("</thead>");
		}
		wh.write(self.getRows()).writeln("</table></div>");
	}

}
