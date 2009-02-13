/* MenubarHorizontal.java

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Menubar;

/**
 * {@link Menubar}'s horizontal mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class MenubarHorizontal implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menubar self = (Menubar)comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.menu.Menubar\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>');
		wh.writeln("<table cellpadding=\"0\" cellspacing=\"0\">");
		wh.write("<tr valign=\"bottom\" id=\"").write(uuid).writeln("!cave\">");
		wh.writeChildren(self);
		wh.write("</tr>\n</table></div>");
	}
}
