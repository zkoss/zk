/* BorderlayoutDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 14, 2007 11:59:19 AM , Created by jumperchen
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

import org.zkoss.yuiext.layout.Borderlayout;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

/**
 * 
 * {@link Borderlayout}'s default mold.
 * 
 * @author jumperchen
 * @since ZK 3.0.0
 * 
 */
public class BorderlayoutDefault implements ComponentRenderer {
	
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Borderlayout self = (Borderlayout) comp;
		wh.write("<div id=\"").write(self.getUuid()).write('"').write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write(" style=\"width:100%;height:100%\" z.type=\"yuiextz.layout.ExtBorderLayout\">");
		wh.write("<span id=\"").write(self.getUuid()).write("!cave\" style=\"display:none\">")
			.writeChildren(self).write("</span><div id=\"").write(self.getUuid())
			.writeln("!real\" style=\"width:100%;height:100%\"></div></div>");
	}

}
