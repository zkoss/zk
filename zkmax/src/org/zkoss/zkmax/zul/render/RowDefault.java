/* RowDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 11:12:19 AM , Created by jumperchen
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
import org.zkoss.zul.Row;

/*
 * {@link Row}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class RowDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Row self = (Row) comp;
		final String uuid = self.getUuid();
		wh.write("<tr z.type=\"Grw\" id=\"").write(uuid).write('"')
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>');

		int i = 0;
		for (Iterator it = self.getChildren().iterator(); it.hasNext();i++) {
			final Component child = (Component) it.next();
			wh.write("<td z.type=\"Gcl\" id=\"").write(child.getUuid()).write("!chdextr\"")
				.write(self.getChildAttrs(i)).write("><div id=\"").write(child.getUuid())
				.write("!cell\" class=\"").write(self.getZclass()).write("-cnt");
			if (self.getGrid().isFixedLayout())
				wh.write(" z-overflow-hidden");
			wh.write("\">");

			child.redraw(out);

			wh.writeln("</div></td>");
		}

		wh.writeln("</tr>");
	}

}
