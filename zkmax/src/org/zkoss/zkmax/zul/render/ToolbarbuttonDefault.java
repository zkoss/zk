/* ToolbarbuttonDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 2:41:00 PM , Created by robbiecheng
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

import org.zkoss.zul.Toolbarbutton;

/**
 * {@link Toolbarbutton}'s default mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class ToolbarbuttonDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Toolbarbutton self = (Toolbarbutton) comp;
		
		wh.write("\n<a id=\"").write(self.getUuid()).write("\" z.type=\"zul.widget.Tbtn\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");

		if (self.getDir().equals("reverse")){
			new Out(self.getLabel()).render(out);
			if (self.isImageAssigned() && self.getOrient().equals("vertical"))
				wh.writeln("<br/>");
			wh.write(self.getImgTag());
		} else{
			wh.write(self.getImgTag());
			if (self.isImageAssigned() && self.getOrient().equals("vertical"))
				wh.writeln("<br/>");				
			new Out(self.getLabel()).render(out);
		}		

		wh.write("</a>"); // Bug #1859568.
	}
}
