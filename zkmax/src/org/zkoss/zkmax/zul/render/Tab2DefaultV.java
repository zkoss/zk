/* Tab2DefaultV.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 22, 2008 6:03:53 PM , Created by RyanWu
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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Tab;

/**
 * {@link Tab}'s default mold in vertical only.
 * 
 * @author RyanWu
 * 
 * @since 3.5.0
 * 
 */
public class Tab2DefaultV implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final Tab self = (Tab) comp;
		final SmartWriter wh = new SmartWriter(out);
		final String zcs = self.getZclass() + '-';
		wh.write("<li id=\"" + self.getUuid() + "\"");
		wh.write(" z.type=\"Tab2\"").write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		if (self.isClosable()) {
			wh.writeln("<a id=\"" + self.getUuid() + "!close\" class=\"" + zcs
					+ "close\"  ></a>");
		} else {
			wh.writeln("<a class=\"" + zcs + "noclose\" ></a>");
		}
		wh.write("<a class=\"" + zcs + "body\" id=\"" + self.getUuid()
				+ "!real\"");
		wh.writeln(self.getInnerAttrs() + " >");
		wh.writeln("<em id=\"" + self.getUuid() + "!em\" class=\"" + zcs
				+ "em\">");
		if (self.isClosable()) {
			wh.writeln("<span id=\"" + self.getUuid() + "!inner\" class=\""
					+ zcs + "inner " + zcs + "close-inner\" >");
		} else {
			wh.writeln("<span id=\"" + self.getUuid() + "!inner\" class=\""
					+ zcs + "inner \">");
		}
		wh.writeln("<span class=\"" + zcs + "text\">");
		wh.write(self.getImgTag());
		new Out(self.getLabel()).render(out);
		wh.writeln("</span></span></em></a></li>");
	}
}
