/* ToolbarbuttonDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 2:41:00 PM , Created by robbiecheng
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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Toolbarbutton;

/**
 * {@link Toolbarbutton}'s default mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class ToolbarbuttonDefault implements ComponentRenderer {
	/**
<c:set var="self" value="${requestScope.arg.self}"/>
<a id="${self.uuid}" z.type="zul.widget.Tbtn" ${self.outerAttrs}${self.innerAttrs}>
<c:choose>
<c:when test="${self.dir == 'reverse'}">
	<c:out value="${self.label}"/>
	<c:if test="${self.imageAssigned and self.orient == 'vertical'}">
	  <br/>
	</c:if>
	${self.imgTag}
</c:when>
<c:otherwise>
	${self.imgTag}
	<c:if test="${self.imageAssigned and self.orient == 'vertical'}">
	  <br/>
	</c:if>
	<c:out value="${self.label}"/>
</c:otherwise>
</c:choose></a>
	 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Toolbarbutton self = (Toolbarbutton) comp;
		
		wh.write("<a id=\"" + self.getUuid() + "\" z.type=\"zul.widget.Tbtn\"" + self.getOuterAttrs() + self.getInnerAttrs() + ">");
		if (self.getDir().equals("reverse")){
			RenderFns.getOut(out).setValue(self.getLabel()).render();
			if (self.isImageAssigned() && self.getOrient().equals("vertical")){
				wh.write("<br/>");
			}
			wh.write(self.getImgTag());
		}
		else{
			wh.write(self.getImgTag());
			if (self.isImageAssigned() && self.getOrient().equals("vertical")){
				wh.write("<br/>");				
			}
			RenderFns.getOut(out).setValue(self.getLabel()).render();
		}		
		wh.write("</a>");

	}
	


}
