/* Button2Default.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 6, 2008 2:51:53 PM , Created by robbiecheng
 }}IS_NOTE

 Copyright (C) 2008 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Button;

/**
 * 
 * {@link Button}'s default mold.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public class Button2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Button self = (Button) comp;
		final String uuid = self.getUuid();
		final String mcls = self.getMoldSclass();
		
		wh.write("<span id=\"").write(uuid).write("\" class=\"").write(mcls)
			.write("\" z.type=\"zul.widget.Button\">").write("<table id=\"").write(uuid)
			.write("!real\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"")						
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write("><tbody><tr><td class=\"").write(mcls).write("-tl\"></td>");
		wh.write("<td colspan=\"3\" class=\"").write(mcls).write("-tm\"></td><td class=\"").write(mcls).write("-tr\"></td></tr>");
		wh.write("<tr><td class=\"").write(mcls).write("-cl\"><i>&#160;</i></td><td colspan=\"3\" class=\"").write(mcls).write("-cm\"><em unselectable=\"on\">");
		wh.write("<a id=\"").write(uuid).write("!btn\" href=\"javascript:;\" class=\"").write(mcls).write("-btn\">");				
		if (self.getDir().equals("reverse")) {
			new Out(self.getLabel()).render(out);
			if (self.isImageAssigned()
				&& self.getOrient().equals("vertical"))
			wh.writeln("<br/>");
			wh.write(self.getImgTag());
		} else {
			wh.write(self.getImgTag());
			if (self.isImageAssigned()
				&& self.getOrient().equals("vertical"))
			wh.writeln("<br/>");
			new Out(self.getLabel()).render(out);
		}		
		wh.write("</a></em></td><td class=\"").write(mcls).write("-cr\"><i>&#160;</i></td></tr><tr><td class=\"").write(mcls).write("-bl\"></td>" +
				"<td colspan=\"3\" class=\"").write(mcls).write("-bm\"></td><td class=\"").write(mcls).write("-br\"></td></tr></tbody></table></span>");						
	}
}