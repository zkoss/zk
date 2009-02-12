/* SpanDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  2 17:14:25     2008, Created by tomyeh
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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Span;

/*
 * {@link Span}'s default mold.
 * 
 * @author tomyeh
 * @since 3.5.0
 */
public class SpanDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Span self = (Span) comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write('"');
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.writeChildren(self);
		wh.write("</div>");
	}

}
