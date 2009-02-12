/* CheckboxDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 4:03:23 PM , Created by jumperchen
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
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Checkbox;

/*
 * {@link Checkbox}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class CheckboxDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Checkbox self = (Checkbox) comp;
		final String uuid = self.getUuid();
		wh.write("<span id=\"").write(uuid).write("\"");
		wh.write(" z.type=\"zul.btn.Ckbox\"");
		wh.write(self.getOuterAttrs()).write("><input type=\"checkbox\" id=\"");
		wh.write(uuid).write("!real\"").write(self.getInnerAttrs());
		wh.write("/><label for=\"").write(uuid).write("!real\"");
		wh.write(self.getLabelAttrs()).write(" class=\""+self.getZclass()+"-cnt\"")
		.write(">").write(self.getImgTag());
		new Out(self.getLabel()).render(out);
		wh.write("</label></span>");
	}
}
