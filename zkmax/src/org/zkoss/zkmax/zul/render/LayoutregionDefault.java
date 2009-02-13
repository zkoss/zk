/* LayoutregionDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Oct 11, 2007 11:23:05 AM , Created by jumperchen
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
import org.zkoss.zkex.zul.LayoutRegion;

/**
 * {@link LayoutRegion}'s default mold.
 * 
 * @author jumperchen
 * @since 3.0.0
 * 
 */
public class LayoutregionDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final LayoutRegion self = (LayoutRegion) comp;
		wh.write("<div id=\"").write(self.getUuid()).write('"')
			.write(" z.type=\"zkex.zul.layout.LayoutRegion\">").write("<div id=\"")
			.write(self.getUuid()).write("!real\"").write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write(">").write("<div id=\"").write(self.getUuid())
			.write("!cave\" class=\"layout-region-body\">").writeChildren(self)
			.write("</div></div>");
		if (!self.getPosition().equals("center")) {
			wh.write("<div id=\"").write(self.getUuid()).write("!split\" class=\"layout-split ");
			if (self.getPosition().equals("north")
					|| self.getPosition().equals("south"))
				wh.write("layout-split-v");
			if (self.getPosition().equals("west")
					|| self.getPosition().equals("east"))
				wh.write("layout-split-h");
			wh.write("\"><span id=\"").write(self.getUuid()).write(
					"!splitbtn\" class=\"layout-split-btn-");
			if (self.getPosition().equals("north"))
				wh.write("t");
			else if (self.getPosition().equals("south"))
				wh.write("b");
			else if (self.getPosition().equals("west"))
				wh.write("l");
			else wh.write("r");
			wh.write("\"></span></div>");
		}
		wh.write("</div>");
	}

}
