/* Menubar2Vertical.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2008 11:52:52 AM , Created by jumperchen
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Menubar;

/**
 * {@link Menubar}'s vertical oriented.
 * @author jumperchen
 * @since 3.5.0
 *
 */
public class Menubar2Vertical implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menubar self = (Menubar)comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.menu2.Menubar2\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" id=\"").write(uuid).writeln("!cave\">");

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final HtmlBasedComponent child = (HtmlBasedComponent)it.next();
			wh.write("<tr id=\"").write(child.getUuid()).write("!chdextr\"");
			wh.writeAttr("height",child.getHeight()).writeln(">");
			child.redraw(out);
			wh.writeln("</tr>");
		}
		wh.write("</table></div>");
	}
}
