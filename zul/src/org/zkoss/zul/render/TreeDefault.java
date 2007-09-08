/* TreeDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:17:26 AM , Created by robbiecheng
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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treefoot;

/**
 * {@link Tree}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreeDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Tree self = (Tree) comp;

		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.tree.Tree\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");

		if(self.getTreecols() != null){
			wh.write("<div id=\"").write(self.getUuid()).write("!head\" class=\"tree-head\">")
				.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">")
				.writeln(self.getTreecols())
				.write("</table></div>");
		}

		wh.write("<div id=\"").write(self.getUuid()).write("!body\" class=\"tree-body\">")
			.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
			.writeln(self.getTreechildren())
			.write("</table></div>");

		if(self.getTreefoot() != null){
			wh.write("<div id=\"").write(self.getUuid()).write("!foot\" class=\"tree-foot\">")
				.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">")
				.writeln(self.getTreefoot())
				.write("</table></div>");
		}
		wh.write("</div>");
	}
}
