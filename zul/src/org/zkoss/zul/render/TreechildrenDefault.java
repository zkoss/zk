/* TreechildrenDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:23:50 AM , Created by robbiecheng
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
import java.util.ListIterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Treechildren;

/**
 * {@link Treechildren}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreechildrenDefault implements ComponentRenderer {
/**
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.tree == self.parent}">
<tbody id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.children}" begin="${self.visibleBegin}" end="${self.visibleEnd}">
	${z:redraw(child, null)}
	</c:forEach>
</tbody>
</c:when>
<c:otherwise>
	<c:forEach var="child" items="${self.children}" begin="${self.visibleBegin}" end="${self.visibleEnd}">
	${z:redraw(child, null)}
	</c:forEach>
</c:otherwise>
</c:choose>

 */
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Treechildren self = (Treechildren) comp;
		
		if(self.getTree() == self.getParent()){
			wh.write("<tbody id=\"" + self.getUuid() + "\"" + self.getOuterAttrs() 
					+ self.getInnerAttrs() +">");			
			int i = self.getVisibleBegin();
			if (i < self.getChildren().size()) 
			{
				ListIterator it = self.getChildren().listIterator(i);
				for (int end = self.getVisibleEnd(); i <= end && it.hasNext(); i++) 
				{
					((Component)it.next()).redraw(out);
				}
			}							
			wh.write("</tbody>");
			
		}
		else{
			int i = self.getVisibleBegin();
			if (i < self.getChildren().size()) 
			{
				ListIterator it = self.getChildren().listIterator(i);
				for (int end = self.getVisibleEnd(); i <= end && it.hasNext(); i++) 
				{
					((Component)it.next()).redraw(out);
				}
			}			
		}

	}

}
