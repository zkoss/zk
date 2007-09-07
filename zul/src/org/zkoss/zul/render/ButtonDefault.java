/* ButtonDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 2:51:53 PM , Created by jumperchen
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

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Button;

/**
 * 
 * {@link Button}'s default mold.
 * 
 * @author jumperchen
 * @since 3.0.0
 */
public class ButtonDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Button self = (Button) comp;
		final String uuid = self.getUuid();
		if (!self.isImageAssigned() && Executions.getCurrent().isSafari()) {
			wh.write("<input type=\"button\" id=\"").write(uuid).write("\"");
			wh.write(" z.type=\"zul.widget.Button\" value=\"").write(
					self.getLabel()).write("\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(
					"/>");
			wh.writeln();
		} else {
			wh.write("<button type=\"button\" id=\"").write(uuid).write("\"");
			wh.write(" z.type=\"zul.widget.Button\"").write(
					self.getOuterAttrs());
			wh.write(self.getInnerAttrs()).write(">");

			if (self.getDir().equals("reverse")) {
				new Out(out).setValue(self.getLabel()).render();
				if (self.isImageAssigned()
						&& self.getOrient().equals("vertical"))
					wh.write("<br/>");
				wh.write(self.getImgTag());
			} else {
				wh.write(self.getImgTag());
				if (self.isImageAssigned()
						&& self.getOrient().equals("vertical"))
					wh.write("<br/>");
				new Out(out).setValue(self.getLabel()).render();
			}
			wh.write("</button>");
			wh.writeln();
		}
	}
}
