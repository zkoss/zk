/* MenuDefault.java

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menupopup;

/**
 * {@link Menu}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class MenuDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Menu self = (Menu)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		
		if(self.isTopmost()){
			wh.write("<td id=\"").write(uuid).write("\" align=\"left\" z.type=\"zul.menu.Menu\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
			wh.write("<a href=\"javascript:;\" id=\"").write(uuid).write("!a\">").write(self.getImgTag());
			RenderFns.getOut(out).setValue(self.getLabel()).render();
			wh.write("</a>");
			Menupopup menupopup = (Menupopup)self.getMenupopup();
			if(menupopup != null) 
				menupopup.redraw(out);
			wh.writeln("</td>");
		}else{
			wh.write("<tr id=\"").write(uuid).write("\" z.type=\"zul.menu.Menu\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
			wh.write("<td><img src=\"").write(exec.encodeURL("~./img/spacer.gif")).writeln("\" width=\"11\"/></td>");
			wh.write("<td align=\"left\"><a href=\"javascript:;\" id=\"").write(uuid).write("!a\">").write(self.getImgTag());
			RenderFns.getOut(out).setValue(self.getLabel()).render();
			wh.write("</a>");
			Menupopup menupopup = (Menupopup)self.getMenupopup();
			if(menupopup != null) 
				menupopup.redraw(out);
			wh.writeln("</td>");
			wh.write("<td><img src=\"").write(exec.encodeURL("~./zul/img/menu/arrow.gif")).writeln("\" width=\"9\"/></td>");
			wh.write("</tr>");
		}

	}
	/*
	<c:choose>
	<c:when test="${self.topmost}">
	 <td id="${self.uuid}" align="left" z.type="zul.menu.Menu"${self.outerAttrs}${self.innerAttrs}><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a>${z:redraw(self.menupopup, null)}</td>
	</c:when>
	<c:otherwise>
	 <tr id="${self.uuid}" z.type="zul.menu.Menu"${self.outerAttrs}${self.innerAttrs}>
	 <td><img src="${c:encodeURL('~./img/spacer.gif')}" width="11"/></td>
	 <td align="left"><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a>${z:redraw(self.menupopup, null)}</td>
	 <td><img src="${c:encodeURL('~./zul/img/menu/arrow.gif')}" width="9"/></td>
	 </tr>
	</c:otherwise>
	</c:choose>
	*/
}
