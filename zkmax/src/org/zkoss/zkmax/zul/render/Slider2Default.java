/* Slider2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 6 2008, Created by robbiecheng
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
import org.zkoss.zul.Slider;

/**
 * {@link Slider}'s default mold.
 * @author robbiecheng
 * @since 3.5.0
 */
public class Slider2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Slider self = (Slider)comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		
		wh.write("<div id=\"").write(uuid).write("\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write(" z.type=\"zul.sld.Sld\">")
			.write("<div id=\"").write(uuid).write("!inner\" class=\"").write(zcls).write("-center\">")
			.write("<div id=\"").write(uuid).write("!btn\" class=\"").write(zcls).write("-btn\"></div>")
			.write("</div></div>");
	}

}
