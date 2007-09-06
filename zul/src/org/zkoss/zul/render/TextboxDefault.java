/* TextboxDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 13:18:53     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Textbox;

/**
 * {@link Textbox}'s default mold.
 *
 * @author dennis.chen
 * @since 3.0.0
 */
public class TextboxDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Textbox self = (Textbox)comp;
		final boolean isMultiline = self.isMultiline();
		if(isMultiline){
			wh.write("<textarea id=\"").write(self.getUuid()).write("\" z.type=\"zul.widget.Txbox\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
			wh.write(self.getAreaText());
			wh.writeln("</textarea>");
		}else{
			wh.write("<input id=\"").write(self.getUuid()).write("\" z.type=\"zul.widget.Txbox\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs());
			wh.writeln("/>");
		}
	}
}
