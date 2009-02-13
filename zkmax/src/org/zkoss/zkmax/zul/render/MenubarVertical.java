/* MenubarVertical.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * {@link Menubar}'s vertical mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class MenubarVertical implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menubar self = (Menubar)comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.menu.Menubar\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<table cellpadding=\"0\" cellspacing=\"0\" id=\"").write(uuid).writeln("!cave\">");

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final LabelImageElement child = (LabelImageElement)it.next();
			wh.write("<tr id=\"").write(child.getUuid()).write("!chdextr\"");
			wh.writeAttr("height",child.getHeight()).writeln(">");
			child.redraw(out);
			wh.writeln("</tr>");
		}
		wh.write("</table></div>");
	}

}
