/* DefaultTextbox.java

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
public class DefaultTextbox implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final Textbox self = (Textbox)comp;
		final boolean isMultiline = self.isMultiline();
		if(isMultiline){
			out.write("<textarea id=\"");
			out.write(self.getUuid());
			out.write("\" z.type=\"zul.widget.Txbox\"");
			out.write(self.getOuterAttrs());
			out.write(self.getOuterAttrs());
			out.write(self.getInnerAttrs());
			out.write(">");
			out.write(self.getAreaText());
			out.write("</textarea>\n");
		}else{
			out.write("<input id=\"");
			out.write(self.getUuid());
			out.write("\" z.type=\"zul.widget.Txbox\"");
			out.write(self.getOuterAttrs());
			out.write(self.getInnerAttrs());
			out.write("/>\n");
		}
	}
}
