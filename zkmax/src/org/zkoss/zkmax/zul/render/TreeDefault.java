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
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tree;

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
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">")
				.writeComponents(self.getHeads())
				.write("</table></div>");
		}

		wh.write("<div id=\"").write(self.getUuid()).write("!body\" class=\"tree-body\">")
			 .write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
			.writeln(self.getTreechildren())
			.write("</table></div>");

		if(self.getTreefoot() != null){
			wh.write("<div id=\"").write(self.getUuid()).write("!foot\" class=\"tree-foot\">")
				.write("<table width=\"").write(self.getInnerWidth()).write("\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">")
				.writeln(self.getTreefoot())
				.write("</table></div>");
		}
		wh.write("</div>");
	}
}
