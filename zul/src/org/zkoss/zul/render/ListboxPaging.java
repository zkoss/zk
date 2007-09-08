/* ListboxPaging.java

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
import java.util.ListIterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Paging;

/**
 * {@link Listbox}'s paging mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListboxPaging implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Listbox self = (Listbox)comp;
		final String uuid = self.getUuid();
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.sel.Libox\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		
		wh.write("<div id=\"").write(uuid)
			.write("!paging\" class=\"listbox-paging\">")
			.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"listbox-btable\">");

		//header
		wh.write("<tbody>").write(self.getListhead()).write("</tbody>");

		//body
		wh.write("<tbody id=\"").write(uuid).write("!cave\">");
		int i = self.getVisibleBegin();
		if (i < self.getItems().size()) {
			ListIterator it = self.getItems().listIterator(i);
			for (int end = self.getVisibleEnd(); i <= end && it.hasNext(); i++) {
				((Component)it.next()).redraw(out);
			}
		}
		wh.write("</tbody>");

		//Footer
		wh.write("<tbody class=\"grid-foot\">").write(self.getListfoot())
			.write("</tbody></table>");

		//Paging
		wh.write("<div id=\"").write(uuid)
			.write("!pgi\" class=\"listbox-pgi\">")
			.write(self.getPaging())
			.write("</div></div></div>");

		/*
		<div id="${self.uuid}" z.type="zul.sel.Libox"${self.outerAttrs}${self.innerAttrs}>
			<div id="${self.uuid}!paging" class="listbox-paging">
			<table width="100%" border="0" cellpadding="0" cellspacing="0" class="listbox-btable">
			<tbody>
		${z:redraw(self.listhead, null)}
			</tbody>
	
			<tbody id="${self.uuid}!cave">
			<c:forEach var="item" items="${self.items}" begin="${self.visibleBegin}" end="${self.visibleEnd}">
		${z:redraw(item, null)}
			</c:forEach>
			</tbody>
	
			<tbody class="grid-foot">
		${z:redraw(self.listfoot, null)}
			</tbody>
			</table>
			<div id="${self.uuid}!pgi" class="listbox-pgi">
			${z:redraw(self.paging, null)}
			</div>
			</div>
		</div>
		*/		
	}

}
