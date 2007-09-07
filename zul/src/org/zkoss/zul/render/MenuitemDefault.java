/* MenuitemDefault.java

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
import org.zkoss.zul.Menuitem;
import org.zkoss.lang.Strings;

/**
 * {@link Menuitem}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class MenuitemDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Menuitem self = (Menuitem)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		if(self.isTopmost()){
			wh.write("<td id=\"").write(uuid).write("\" align=\"left\" z.type=\"Menuit\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
			wh.write("<a href=\"");
			if(Strings.isBlank(self.getHref()))
				wh.write("javascript:;");
			else
				wh.write(exec.encodeURL(self.getHref()));
			wh.write("\"").writeAttr("target",self.getTarget());
			wh.write(" id=\"").write(uuid).write("!a\">").write(self.getImgTag());
			new Out(out, self.getLabel()).render();
			wh.write("</a>");
			wh.writeln("</td>");
		}else{
			wh.write("<tr id=\"").write(uuid).write("\" z.type=\"Menuit\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
			wh.write("<td><img src=\"");
			if(self.isChecked())
				wh.write(exec.encodeURL("~./zul/img/menu/checked.gif"));
			else
				wh.write(exec.encodeURL("~./img/spacer.gif"));
			wh.writeln("\" width=\"11\"/></td>");
			wh.write("<td align=\"left\"><a href=\"");
			if(Strings.isBlank(self.getHref()))
				wh.write("javascript:;");
			else
				wh.write(exec.encodeURL(self.getHref()));
			wh.write("\"").writeAttr("target",self.getTarget());
			wh.write(" id=\"").write(uuid).write("!a\">").write(self.getImgTag());
			new Out(out, self.getLabel()).render();
			wh.write("</a>");
			wh.writeln("</td>");
			wh.write("<td><img src=\"").write(exec.encodeURL("~./img/spacer.gif")).writeln("\" width=\"9\"/></td>");
			wh.write("</tr>");
		}
		/*
		<c:choose>
		<c:when test="${self.topmost}">
		 <td id="${self.uuid}" align="left" z.type="Menuit"${self.outerAttrs}${self.innerAttrs}><a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
		</c:when>
		<c:otherwise>
		 <tr id="${self.uuid}" z.type="Menuit"${self.outerAttrs}${self.innerAttrs}>
		 <td><img src="${c:encodeURL(self.checked?'~./zul/img/menu/checked.gif':'~./img/spacer.gif')}" width="11"/></td>
		 <td align="left"><a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
		 <td><img src="${c:encodeURL('~./img/spacer.gif')}" width="9"/></td>
		 </tr>
		</c:otherwise>
		</c:choose>
		*/
	}

}
