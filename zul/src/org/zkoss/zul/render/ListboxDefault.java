/* ListboxDefault.java

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
import org.zkoss.zul.Listbox;

/**
 * {@link Listbox}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListboxDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Listbox self = (Listbox)comp;
		final String uuid = self.getUuid();
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.sel.Libox\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		if(self.getListhead() != null){
			wh.write("<div id=\"").write(uuid).write("!head\" class=\"listbox-head\">");
			wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">");
			self.getListhead().redraw(out);
			wh.write("</table></div>");
		}
		wh.write("<div id=\"").write(uuid).write("!body\" class=\"listbox-body\">");
		wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"").write(uuid).write("!cave\" class=\"listbox-btable\">");
		for (Iterator it = self.getItems().iterator(); it.hasNext();) {
			final Component item = (Component)it.next();
			item.redraw(out);
		}
		wh.write("</table></div>");
		if(self.getListfoot() != null){
			wh.write("<div id=\"").write(uuid).write("!foot\" class=\"listbox-foot\">");
			wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">");
			self.getListfoot().redraw(out);
			wh.write("</table></div>");
		}
		wh.write("</div>");
		/*
		<c:set var="self" value="${requestScope.arg.self}"/>
		<div id="${self.uuid}" z.type="zul.sel.Libox"${self.outerAttrs}${self.innerAttrs}>
		<c:if test="${!empty self.listhead}">
			<div id="${self.uuid}!head" class="listbox-head">
			<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		${z:redraw(self.listhead, null)}
			</table>
			</div>
		</c:if>
			<div id="${self.uuid}!body" class="listbox-body">
			<table width="100%" border="0" cellpadding="0" cellspacing="0" id="${self.uuid}!cave" class="listbox-btable">
			<c:forEach var="item" items="${self.items}">
		${z:redraw(item, null)}
			</c:forEach>
			</table>
			</div>
		<c:if test="${!empty self.listfoot}">
			<div id="${self.uuid}!foot" class="listbox-foot">
			<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		${z:redraw(self.listfoot, null)}
			</table>
			</div>
		</c:if>
		</div>
		*/

	}

}
