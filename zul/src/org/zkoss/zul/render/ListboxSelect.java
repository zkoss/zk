/* ListboxSelect.java

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
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

/**
 * {@link Listbox}'s select mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListboxSelect implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Listbox self = (Listbox)comp;
		final String uuid = self.getUuid();
		wh.write("<select id=\"").write(uuid).write("\" z.type=\"zul.sel.Lisel\"").write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		for (Iterator it = self.getItems().iterator(); it.hasNext();) {
			final Listitem item = (Listitem)it.next();
			wh.write("<option id=\"").write(item.getUuid()).write("\"").write(item.getOuterAttrs()).write(item.getInnerAttrs()).writeln(">");
			new Out(item.getLabel()).setMaxlength(self.getMaxlength()).render(out);
			wh.write("</option>");
		}
		wh.write("</select>");
		/*
		<select id="${self.uuid}" z.type="zul.sel.Lisel"${self.outerAttrs}${self.innerAttrs}>
		<c:forEach var="item" items="${self.items}">
		<option id="${item.uuid}"${item.outerAttrs}${item.innerAttrs}><c:out value="${item.label}" maxlength="${self.maxlength}"/></option>
		</c:forEach><%-- for better performance, we don't use z:redraw --%>
		</select>
		*/
	}

}
