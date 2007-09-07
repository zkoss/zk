/* PopupDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.WriterHelper;
import org.zkoss.zul.Popup;

public class PopupDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Popup self = (Popup)comp;
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.widget.Pop\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			child.redraw(out);
		}
		wh.write("</div>");
		/*
			<div id="${self.uuid}" z.type="zul.widget.Pop"${self.outerAttrs}${self.innerAttrs}>
			<c:forEach var="child" items="${self.children}">
			  ${z:redraw(child, null)}
			</c:forEach>
			</div>
		 */
	
	}
	
}
