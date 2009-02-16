/* TablechildrenDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 14, 2008 5:21:54 PM , Created by robbiecheng
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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkmax.zul.Tablechildren;

/**
 * {@link Tablechildren}'s default mold.
 * @author robbiecheng
 * @since 3.5.0
 *
 */
public class TablechildrenDefault implements ComponentRenderer{
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Tablechildren self = (Tablechildren) comp;
		final String zcls = self.getZclass();
		int i = 0;
		for (Iterator it = self.getChildren().iterator(); it.hasNext();i++) {
			final Component child = (Component) it.next();
			wh.write("<td id=\"").write(self.getUuid()).write("\" class=\"")
				.write(zcls).write("\"")
				.write(self.getOuterAttrs()).write(self.getInnerAttrs())
				.write(" >");
			child.redraw(out);
			wh.writeln("</td>");
		}
	}

}
