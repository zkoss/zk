/* TextboxDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 12:06:52 PM , Created by robbiecheng
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
import org.zkoss.zul.Textbox;

/**
 * {@link Textbox}'s default mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class TextboxDefault implements ComponentRenderer{

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Textbox self = (Textbox) comp;
		final String uuid = self.getUuid();

		//textarea doesn't support maxlength
		if (self.isMultiline()){
			wh.write("<textarea id=\"").write(uuid).write("\" z.type=\"zul.vd.Txbox\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">")
			.write(self.getAreaText()).write("</textarea>");
		}
		else{
			wh.write("<input id=\"").write(uuid).write("\" z.type=\"zul.vd.Txbox\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write("/>");
		}
	
	}
}
