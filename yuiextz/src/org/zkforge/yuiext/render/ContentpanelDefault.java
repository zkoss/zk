/* ContentpanelDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 14, 2007 12:04:32 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkforge.yuiext.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.zkforge.yuiext.layout.Contentpanel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

/**
 * {@link Contentpanel}'s default mold.
 * 
 * @author jumperchen
 * @since ZK 3.0.0
 *
 */
public class ContentpanelDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Contentpanel self = (Contentpanel) comp;
		wh.write("<span id=\"").write(self.getUuid()).write('"').write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write(" z.type=\"yuiextz.layout.ExtContentPanel\">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			wh.write("<span id=\"").write(self.getUuid()).write("!cave\" style=\"width:100%;height:100%\">");
			child.redraw(out);
			wh.write("</span>");
		}
		wh.writeln("</span>");
	}

}
