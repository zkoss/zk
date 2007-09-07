/* GridPaging.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 11:54:13 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Grid;

/*
 * {@link Grid}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class GridPaging implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Grid self = (Grid) comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.grid.Grid\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<div id=\"").write(uuid).write("!paging\" class=\"grid-paging\">");
		wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"grid-btable\">");
		wh.write("<tbody>");
		ZkFns.redraw(self.getColumns(), out);
		wh.write("</tbody>");
		ZkFns.redraw(self.getRows(), out);
		wh.write("<tbody class=\"grid-foot\">");
		ZkFns.redraw(self.getFoot(), out);
		wh.write("</tbody></table>");
		wh.write("<div id=\"").write(uuid).write("!pgi\" class=\"grid-pgi\">");
		ZkFns.redraw(self.getPaging(), out);
		wh.writeln("</div></div></div>");
	}

}
