/* Menuitem2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2008 11:55:42 AM , Created by jumperchen
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

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;


/**
 * {@link Menuitem}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class Menuitem2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menuitem self = (Menuitem)comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		final Execution exec = Executions.getCurrent();
		if (self.isTopmost()){
			wh.write("<td id=\"").write(uuid).write("\" align=\"left\" z.type=\"Menuit2\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write("><a href=\"");
			if(Strings.isBlank(self.getHref()))
				wh.write("javascript:;");
			else
				wh.write(exec.encodeURL(self.getHref()));
			wh.write("\"").writeAttr("target",self.getTarget());
			wh.write(" class=\"").write(zcls).write("-cnt\">");
			wh.write("<table id=\"").write(uuid).write("!a\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"").write(zcls).write("-btn ");
			if (self.isImageAssigned()) {
				wh.write("").write(zcls).write("-btn");
				if (self.getLabel().length() > 0)
					wh.write("-text");
				wh.write("-img");
			}
			wh.write("\" style=\"width: auto;\"><tbody><tr><td class=\"").write(zcls).write("-btn-l\"><i>&nbsp;</i></td>");
			wh.write("<td class=\"").write(zcls).write("-btn-m\"><em unselectable=\"on\"><button id=\"")
				.write(uuid).write("!b\" type=\"button\" class=\"").write(zcls).write("-btn-text\"");

			final String imagesrc = self.getEncodedImageURL();
			if (imagesrc != null)
				wh.write(" style=\"background-image:url(").write(imagesrc).write(")\"");
			wh.write('>');

			new Out(self.getLabel()).render(out);
			wh.write("</button>").writeln("</em></td><td class=\"").write(zcls).write("-btn-r\"><i>&nbsp;</i></td></tr></tbody></table></a></td>");
		} else {
			wh.write("<li id=\"").write(uuid).write("\" z.type=\"Menuit2\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs())
				.write(">\n<a id=\"").write(uuid).write("!a\" href=\"");
			if(Strings.isBlank(self.getHref()))
				wh.write("javascript:;");
			else
				wh.write(exec.encodeURL(self.getHref()));
			wh.write("\"").writeAttr("target",self.getTarget());
			wh.write(" class=\"");
			if (!self.isImageAssigned() && self.isCheckmark()) {
				if (self.isChecked()) 
					wh.write(zcls).write("-cnt ").write(zcls).write("-cnt-ck");
				else 
					wh.write(zcls).write("-cnt ").write(zcls).write("-cnt-unck");
			} else
				wh.write(zcls).write("-cnt");
			
			wh.write("\">").write(self.getImgTag());
			
			new Out(self.getLabel()).render(out);

			wh.write("</a>").writeln("</li>");
		}
	}
}
