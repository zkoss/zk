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
import java.util.Iterator;

import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
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
		final WriterHelper wh = new WriterHelper(out);
		final Treechildren self = (Treechildren) comp;
		
		if(self.getTree() == self.getParent()){
			wh.write("<tbody id=\"" + self.getUuid() + "\"" + self.getOuterAttrs() 
					+ self.getInnerAttrs() +">");
			Iterator it = self.getChildren().iterator();
			  final int _beg = self.getVisibleBegin();
			  final int _end = self.getVisibleEnd();
			  for( int i = 0; ++i<_beg && it.hasNext();)
			   it.next();
			  for( int i = 0, cnt = _end - _beg + 1; it.hasNext() && --cnt >= 0; ++i) {
			   final Component item = (Component)it.next();
			   ZkFns.redraw(item, out);
			  }						
			wh.write("</tbody>");
			
		}
		else{
			Iterator it = self.getChildren().iterator();
			  final int _beg = self.getVisibleBegin();
			  final int _end = self.getVisibleEnd();
			  for( int i = 0; ++i<_beg && it.hasNext();)
			   it.next();
			  for( int i = 0, cnt = _end - _beg + 1; it.hasNext() && --cnt >= 0; ++i) {
			   final Component item = (Component)it.next();
			   ZkFns.redraw(item, out);
			  }		
		}

	}

}
