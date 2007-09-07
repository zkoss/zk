/* ComboitemDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 5:10:32 PM , Created by jumperchen
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

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Comboitem;

/*
 * {@link  Comboitem}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class ComboitemDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Comboitem self = (Comboitem) comp;
		final String uuid = self.getUuid();
		wh.write("<tr id=\"").write(uuid).write("\" z.type=\"Cmit\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<td>").write(self.getImgTag()).write("</td><td>");
		RenderFns.getOut(out).setValue(self.getLabel()).render();
		if (!Strings.isBlank(self.getDescription())
				|| !Strings.isBlank(self.getContent())) {
			wh.write("<br/><span>");
			RenderFns.getOut(out).setValue(self.getDescription()).render();
			wh.write("</span>").write(self.getContent());
		}
		wh.write("</td></tr>");
		wh.writeln();
	}

}
