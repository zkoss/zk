/* ToolbarDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 3:06:14 PM , Created by robbiecheng
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
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Toolbar;

/**
 * {@link Toolbar}'s default mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class ToolbarDefault implements ComponentRenderer {
	/**
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="break" value=""/>
<c:set var="verticalBreak" value="${self.orient == 'vertical' ? '<br/>': ''}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.children}">
	${break}${z:redraw(child, null)}
	<c:set var="break" value="${verticalBreak}"/>
	</c:forEach>
</div>
	 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Toolbar self = (Toolbar) comp;
		String brk = "";
		final String verticalbrk = (self.getOrient().equals("vertical"))? "<br/>" : "";
		
		wh.write("<div id=\"" + self.getUuid() + "\"" + self.getOuterAttrs() + self.getInnerAttrs() + ">");		
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			wh.write(brk);
			child.redraw(out);
			brk = verticalbrk;
		}
		wh.write("</div>");
	}
	


}
