/* Menupopup2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2008 12:15:33 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Menupopup;


/**
 * {@link Menupopup}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class Menupopup2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menupopup self = (Menupopup)comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write('"')
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>')
			.write("<a id=\"").write(uuid).write("!a\" tabindex=\"-1\" onclick=\"return false;\"")
			.write(" href=\"javascript:;\" style=\"position:absolute;left:0px;top:-5px;width:0px;height:0px;line-height:1px;\"></a>")
			.write("<ul class=\"").write(self.getMoldSclass()).write("-cnt\" id=\"").write(uuid).writeln("!cave\">")
			.writeChildren(self)
			.write("</ul></div>");
	}
}

