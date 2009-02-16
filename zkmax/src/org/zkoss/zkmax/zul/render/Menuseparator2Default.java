/* Menuseparator2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2008 12:21:01 PM , Created by jumperchen
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
import org.zkoss.zul.Menuseparator;

/**
 * {@link Menuseparator}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class Menuseparator2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menuseparator self = (Menuseparator)comp;
		final String uuid = self.getUuid();
		wh.write("<li id=\"").write(uuid).write("\" z.type=\"Menusp2\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		wh.writeln("<span class=\"").write(self.getZclass()).write("-inner\">&nbsp;</span>");
		wh.writeln("</li>");
	}
}
