/* MenuitemDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Menuitem;
import org.zkoss.lang.Strings;

/**
 * {@link Menuitem}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class MenuitemDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menuitem self = (Menuitem)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		if(self.isTopmost()){
			wh.write("<td id=\"").write(uuid).write("\" align=\"left\" z.type=\"Menuit\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
			wh.write("<a href=\"");
			if(Strings.isBlank(self.getHref()))
				wh.write("javascript:;");
			else
				wh.write(exec.encodeURL(self.getHref()));
			wh.write("\"").writeAttr("target",self.getTarget());
			wh.write(" id=\"").write(uuid).write("!a\">").write(self.getImgTag());
			new Out(self.getLabel()).render(out);
			wh.writeln("</a></td>");
		}else{
			wh.write("<tr id=\"").write(uuid).write("\" z.type=\"Menuit\"")
			  .write(self.getOuterAttrs()).write(self.getInnerAttrs())
			  .writeln(">")
			  .write("<td class=\"menu1");
			if(self.isChecked())
				wh.write("ck");
			wh.write("\"></td>\n<td align=\"left\"><a href=\"");
			if(Strings.isBlank(self.getHref()))
				wh.write("javascript:;");
			else
				wh.write(exec.encodeURL(self.getHref()));
			wh.write("\"").writeAttr("target",self.getTarget());
			wh.write(" id=\"").write(uuid).write("!a\">").write(self.getImgTag());
			new Out(self.getLabel()).render(out);
			wh.writeln("</a></td>\n<td width=\"9px\"></td></tr>");
		}
	}
}
