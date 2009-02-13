/* BorderlayoutDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Oct 11, 2007 11:04:45 AM , Created by jumperchen
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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkex.zul.Borderlayout;


/**
 * {@link Borderlayout}'s default mold.
 * 
 * @author jumperchen
 * @since 3.0.0
 *
 */
public class BorderlayoutDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Borderlayout self = (Borderlayout) comp;
		wh.write("<div id=\"").write(self.getUuid()).write('"').write(self.getOuterAttrs())
		.write(self.getInnerAttrs()).write(" z.type=\"zkex.zul.layout.BorderLayout\">")
		.writeChildren(self).write("</div>");
	}
}
