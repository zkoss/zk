/* GridPaging.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 11:54:13 AM , Created by jumperchen
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Column;
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
		final SmartWriter wh = new SmartWriter(out);
		final Grid self = (Grid) comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.grid.Grid\" z.pg=\"t\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");

		if (self.getPagingChild() != null && self.getPagingPosition().equals("top") || self.getPagingPosition().equals("both")) {
			wh.write("<div id=\"").write(uuid).write("!pgit\" class=\"").write(zcls).write("-pgi-t\">")
				.write(self.getPagingChild())
				.write("</div>");
		}
		if(self.getColumns() != null){
			wh.write("<div id=\"").write(uuid).write("!head\" class=\"").write(zcls).write("-header\">")
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">");
			if(self.getColumns() != null) {
				wh.write("<tbody style=\"visibility:hidden;height:0px\">")
					.write("<tr id=\"").write(self.getColumns().getUuid()).write("!hdfaker\" class=\"").write(zcls).write("-faker\">");
					
				for (Iterator it = self.getColumns().getChildren().iterator(); it.hasNext();) {
					final Column child = (Column) it.next();
					wh.write("<th id=\"").write(child.getUuid()).write("!hdfaker\"").write(child.getOuterAttrs())
					.write("><div style=\"overflow:hidden\"></div></th>");
				}
				wh.write("</tr></tbody>");
			}
			wh.writeComponents(self.getHeads())
				.write("</table></div>");
		}

		wh.write("<div id=\"").write(uuid).write("!body\" class=\"").write(zcls).write("-body\"");
		if (self.getHeight() != null) wh.write("style=\"height:").write(self.getHeight()).write("\"");
		wh.write("><table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"");
		if (self.isFixedLayout())
			wh.write(" style=\"table-layout:fixed\"");
		wh.write(">");
			if(self.getColumns() != null) {
				wh.write("<tbody style=\"visibility:hidden;height:0px\">")
					.write("<tr id=\"").write(self.getColumns().getUuid()).write("!bdfaker\" class=\"").write(zcls).write("-faker\">");
					
				for (Iterator it = self.getColumns().getChildren().iterator(); it.hasNext();) {
					final Column child = (Column) it.next();
					wh.write("<th id=\"").write(child.getUuid()).write("!bdfaker\"").write(child.getOuterAttrs())
					.write("><div style=\"overflow:hidden\"></div></th>");
				}
				wh.write("</tr></tbody>");
			}
		wh.writeln(self.getRows())
			.write("</table></div>");

		if(self.getFoot() != null){
			wh.write("<div id=\"").write(uuid).write("!foot\" class=\"").write(zcls).write("-footer\">")
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  style=\"table-layout:fixed\">");
			if(self.getColumns() != null) {
				wh.write("<tbody style=\"visibility:hidden;height:0px\">")
					.write("<tr id=\"").write(self.getColumns().getUuid()).write("!ftfaker\" class=\"").write(zcls).write("-faker\">");
					
				for (Iterator it = self.getColumns().getChildren().iterator(); it.hasNext();) {
					final Column child = (Column) it.next();
					wh.write("<th id=\"").write(child.getUuid()).write("!ftfaker\"").write(child.getOuterAttrs())
					.write("><div style=\"overflow:hidden\"></div></th>");
				}
				wh.write("</tr></tbody>");
			}
			wh.writeln(self.getFoot())
				.write("</table></div>");
		}
		
		if (self.getPagingChild() != null && self.getPagingPosition().equals("bottom") || self.getPagingPosition().equals("both")) {
			wh.write("<div id=\"").write(uuid).write("!pgib\" class=\"").write(zcls).write("-pgi-b\">")
				.write(self.getPagingChild())
				.write("</div>");
		}
		wh.write("</div>");
	}
}
