/* RowsDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 7, 2007 11:12:33 AM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;
import java.util.ListIterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.WriterHelper;
import org.zkoss.zul.Rows;

/*
 * {@link Rows}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class RowsDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Rows self = (Rows) comp;
		final String uuid = self.getUuid();
		wh.write("<tbody id=\"").write(uuid).write("\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");

		int i = self.getVisibleBegin();
		if (i < self.getChildren().size()) {
			ListIterator it = self.getChildren().listIterator(i);
			for (int end = self.getVisibleEnd(); i <= end && it.hasNext(); i++) {
				((Component)it.next()).redraw(out);
			}
		}
		wh.writeln("</tbody>");
	}
}
