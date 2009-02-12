/* Tab2Default.java

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

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tab}'s default mold.
 * It forward to {@link Tab2DefaultV} if the orient is vertical.
 * 

 * @author RyanWu
 * 
 * @since 3.5.0
 * 
 */
public class Tab2Default implements ComponentRenderer {
	private final Tab2DefaultV _vtab = new Tab2DefaultV();

	public void render(Component comp, Writer out) throws IOException {
		final Tab self = (Tab) comp;
		final Tabbox tabbox = self.getTabbox();
		if ("vertical".equals(tabbox.getOrient())) {
			_vtab.render(comp, out);
			return; //done
		}

		final SmartWriter wh = new SmartWriter(out);
		final String zcs = self.getZclass() + '-';
		final String uuid = self.getUuid();
		wh.write("<li id=\"").write(uuid).write("\" z.type=\"Tab2\"").write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write('>');
		if (self.isClosable()) {
			wh.write("<a class=\"").write(zcs).write("close\" id=\"").write(uuid)
				.write("!close\" onclick=\"return false;\"/>");
		}
		wh.write("<a class=\"").write(zcs).write("body\" id=\"").write(uuid)
			.writeln("!a\"  onclick=\"return false;\" href=\"#\">")
			.writeln("<em id=\"").write(uuid).write("!em\">");
		if (self.isClosable()) {
			wh.write("<span id=\"").write(uuid).write("!inner\" class=\"")
				.write(zcs).write("inner ").write(zcs).write("close-inner\">");
		} else {
			wh.write("<span id=\"").write(uuid).write("!inner\" class=\"").write(zcs).write("inner\">");
		}
		wh.writeln("<span class=\"").write(zcs).write("text\">");
		final String imgTag = self.getImgTag(), label = self.getLabel();
		if ("".equals(label) && imgTag == null) {
			wh.write("&#160;");
		} else {
			wh.write(imgTag);
			new Out(label).render(out);
		}
		wh.writeln("</span></span></em></a></li>");
	}
}
