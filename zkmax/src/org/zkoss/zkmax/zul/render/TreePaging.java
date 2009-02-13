/* TreePaging.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 22, 2008 11:37:54 AM , Created by jumperchen
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.fn.ZulFns;

/**
 * {@link Tree}'s Paging mold.
 * @author jumperchen
 *
 */
public class TreePaging implements ComponentRenderer {
	
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Tree self = (Tree) comp;
		final String uuid = self.getUuid();

		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.tree.Tree\" z.pg=\"t\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		
		if (self.getPagingChild() != null && self.getPagingPosition().equals("top") || self.getPagingPosition().equals("both")) {
			wh.write("<div id=\"").write(uuid)
				.write("!pgit\" class=\"tree-pgi-t\">")
				.write(self.getPagingChild())
				.write("</div>");
		}
		
		if(self.getTreecols() != null){
			wh.write("<div id=\"").write(self.getUuid()).write("!head\" class=\"tree-head\">")
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">");
			if(self.getTreecols() != null) {
				wh.write("<tbody style=\"visibility:hidden;height:0px\">")
					.write("<tr id=\"").write(self.getTreecols().getUuid()).write("!hdfaker\" class=\"tree-fake\">");
					
				for (Iterator it = self.getTreecols().getChildren().iterator(); it.hasNext();) {
					final Treecol child = (Treecol) it.next();
					wh.write("<th id=\"").write(child.getUuid()).write("!hdfaker\"").write(child.getOuterAttrs())
					.write("><div style=\"overflow:hidden\"></div></th>");
				}
				wh.write("</tr></tbody>");
			}
			wh.writeComponents(self.getHeads())
				.write("</table></div>");
		}

		wh.write("<div id=\"").write(self.getUuid()).write("!body\" class=\"tree-body\">")
			 .write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"");
		if (self.isFixedLayout())
			wh.write(" style=\"table-layout:fixed\"");
		wh.write(">");
		
		if(self.getTreecols() != null) {
			wh.write("<tbody style=\"visibility:hidden;height:0px\">")
				.write("<tr id=\"").write(self.getTreecols().getUuid()).write("!bdfaker\" class=\"tree-fake\">");
				
			for (Iterator it = self.getTreecols().getChildren().iterator(); it.hasNext();) {
				final Treecol child = (Treecol) it.next();
				wh.write("<th id=\"").write(child.getUuid()).write("!bdfaker\"").write(child.getOuterAttrs())
				.write("><div style=\"overflow:hidden\"></div></th>");
			}
			wh.write("</tr></tbody>");
		}
		wh.writeln(self.getTreechildren())
			.write("</table></div>");

		if(self.getTreefoot() != null){
			wh.write("<div id=\"").write(self.getUuid()).write("!foot\" class=\"tree-foot\">")
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">");
			if(self.getTreecols() != null) {
				wh.write("<tbody style=\"visibility:hidden;height:0px\">")
					.write("<tr id=\"").write(self.getTreecols().getUuid()).write("!ftfaker\" class=\"tree-fake\">");
					
				for (Iterator it = self.getTreecols().getChildren().iterator(); it.hasNext();) {
					final Treecol child = (Treecol) it.next();
					wh.write("<th id=\"").write(child.getUuid()).write("!ftfaker\"").write(child.getOuterAttrs())
					.write("><div style=\"overflow:hidden\"></div></th>");
				}
				wh.write("</tr></tbody>");
			}
			wh.writeln(self.getTreefoot())
				.write("</table></div>");
		}

		//Paging
		if (self.getPagingChild() != null && self.getPagingPosition().equals("bottom") || self.getPagingPosition().equals("both")) {
			wh.write("<div id=\"").write(uuid)
				.write("!pgib\" class=\"tree-pgi\">")
				.write(self.getPagingChild())
				.write("</div>");
		}
		ZulFns.clearTreeRenderInfo(self);
		wh.write("</div>");
	}

}
