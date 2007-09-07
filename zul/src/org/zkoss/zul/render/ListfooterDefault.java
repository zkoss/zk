/* ListfooterDefault.java

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

import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Listfooter;

/**
 * {@link Listfooter}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListfooterDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Listfooter self = (Listfooter)comp;
		wh.write("<td id=\"").write(self.getUuid()).write("\" ").write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		wh.write(self.getImgTag());
		RenderFns.getOut(out).setValue(self.getLabel()).render();
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			ZkFns.redraw(child, out);
		}
		wh.write("</td>");
		/*
		<td id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
		${self.imgTag}
		<c:out value="${self.label}"/>
		<c:forEach var="child" items="${self.children}">
		${z:redraw(child, null)}
		</c:forEach></td>
		*/
	}

}
