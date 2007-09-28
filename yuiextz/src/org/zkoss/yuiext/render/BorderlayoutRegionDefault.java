/* BorderlayoutRegionDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 14, 2007 12:15:11 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.yuiext.layout.LayoutRegion;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

/**
 * {@link LayoutRegion}'s default mold.
 * 
 * @author jumperchen
 * @since ZK 3.0.0
 * 
 */
public class BorderlayoutRegionDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final LayoutRegion self = (LayoutRegion) comp;
		wh.write("<span id=\"").write(self.getUuid()).write('"').write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write(" z.pos=\"").write(self.getPosition())
			.write("\" z.type=\"yuiextz.layout.ExtBorderLayoutRegion\">").writeChildren(self)
			.writeln("</span>");
	}

}
