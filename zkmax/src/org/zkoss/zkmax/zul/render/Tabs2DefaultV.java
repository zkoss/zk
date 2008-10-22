/* Tabs2DefaultV.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 22, 2008 6:03:53 PM , Created by RyanWu
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
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tabs;

/**
 * {@link Tabs}'s default mold for vertial orient only.
 * 
 * @author RyanWu
 * 
 * @since 3.5.0
 */
public class Tabs2DefaultV implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Tabs self = (Tabs)comp;
		final String look = self.getTabbox().getTabLook() + '-';
		wh.write("<div id=\""+self.getUuid()+"\" class=\""+look+"tabs\"")
			.write("z.type=\"zul.tab2.Tabs2\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.writeln(">");
		
		wh.write("<div id=\""+self.getUuid()+"!header\"")
			.write(" class=\""+look+"header\"")
			.writeln(">");
			wh.write("<ul id=\""+self.getUuid()+"!ul\"")
				.write(" class=\""+look+"si\"")
				.write(">");
				wh.writeChildren(self);
				wh.write("<li id=\""+self.getUuid()+"!edge\"")
					.write(" class=\""+look+"edge\"")
					.writeln("</li>");
			wh.write("</ul>");
		wh.write("</div>");
		wh.write("<div id=\""+self.getUuid()+"!up\" </div>");
		wh.write("<div id=\""+self.getUuid()+"!down\" </div>");
		wh.write("</div>");
		wh.write("<div id=\""+self.getUuid()+"!line\"")
			.write(" class=\""+look+"space\" ></div>");
	}
}
