/* ListcellDefault.java

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
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Listcell;


/**
 * {@link Listcell}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListcellDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Listcell self = (Listcell)comp;
		final String uuid = self.getUuid();
		wh.write("<td id=\"").write(uuid).write("\"").write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write(self.getColumnHtmlPrefix()).write(self.getImgTag());
		new Out(out).setMaxlength(self.getMaxlength()).setValue(self.getLabel()).render();
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			child.redraw(out);
		}
		wh.write(self.getColumnHtmlPostfix()).write("</td>");
		
		/*
		<td id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
		${self.columnHtmlPrefix}${self.imgTag}
		<c:out value="${self.label}" maxlength="${self.maxlength}"/>
		<c:forEach var="child" items="${self.children}">
		${z:redraw(child, null)}
		</c:forEach>
		${self.columnHtmlPostfix}</td>
		*/
	}

}
