/* TreefootDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:18:51 AM , Created by robbiecheng
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
import org.zkoss.zul.Treefoot;

/**
 * {@link Treefoot}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreefootDefault implements ComponentRenderer {
/**
<c:set var="self" value="${requestScope.arg.self}"/>
<tr id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
	</c:forEach>
</tr>
 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Treefoot self = (Treefoot) comp;
		
		wh.write("<tr id=\"" + self.getUuid() + "\"" + self.getOuterAttrs() 
				+ self.getInnerAttrs() +">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			child.redraw(out);
		}	
		wh.write("</tr>");

	}

}
