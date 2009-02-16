/* ButtonOS.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2008 2:28:00 PM , Created by jumperchen
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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Button;

/**
 * {@link Button}'s OS mold.
 * @author jumperchen
 * @since 3.5.2
 */
public class ButtonOS implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Button self = (Button) comp;
		final String uuid = self.getUuid();
		if (!self.isImageAssigned() && Executions.getCurrent().isSafari()) {
			wh.write("<input type=\"button\" id=\"").write(uuid).write("\"");
			wh.write(" z.type=\"zul.btn.ButtonOS\" value=\"").write(
					self.getLabel()).write("\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(
					"/>");
		} else {
			wh.write("<button type=\"button\" id=\"").write(uuid).write("\"");
			wh.write(" z.type=\"zul.btn.ButtonOS\"").write(
					self.getOuterAttrs());
			wh.write(self.getInnerAttrs()).write(">");

			if (self.getDir().equals("reverse")) {
				new Out(self.getLabel()).render(out);
				if (self.isImageAssigned()
						&& self.getOrient().equals("vertical"))
					wh.writeln("<br/>");
				wh.write(self.getImgTag());
			} else {
				wh.write(self.getImgTag());
				if (self.isImageAssigned()
						&& self.getOrient().equals("vertical"))
					wh.writeln("<br/>");
				new Out(self.getLabel()).render(out);
			}
			wh.write("</button>");
		}
	}

}
