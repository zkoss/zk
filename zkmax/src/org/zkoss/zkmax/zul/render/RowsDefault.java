/* RowsDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 7, 2007 11:12:33 AM , Created by jumperchen
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
import java.util.ListIterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Rows;
import org.zkoss.zul.fn.ZulFns;

/*
 * {@link Rows}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class RowsDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Rows self = (Rows) comp;
		final String uuid = self.getUuid();
		wh.write("<tbody id=\"").write(uuid).write("\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		final int from = self.getVisibleBegin(), to = self.getVisibleEnd();
		if (from < self.getChildren().size()) {
			ListIterator it = self.getChildren().listIterator(from);
			ZulFns.resetStripeClass(self);
			for (int cnt = to - from + 1; it.hasNext() && --cnt >= 0;) {
				final Component child = (Component) it.next();
				ZulFns.setStripeClass(child);
				child.redraw(out);
			}
		}
		wh.writeln("</tbody>");
	}
}
