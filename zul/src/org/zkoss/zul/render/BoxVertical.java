/* BoxVertical.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 11:51:57 AM , Created by robbiecheng
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
import org.zkoss.zul.Box;
import org.zkoss.zul.fn.ZulFns;

/**
 * {@link Box}'s vertical mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class BoxVertical implements ComponentRenderer {
	
	/**
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" z.type="zul.box.Box"${self.outerAttrs}${self.innerAttrs} cellpadding="0" cellspacing="0">
	<c:forEach var="child" items="${self.children}">
	<tr id="${child.uuid}!chdextr"${u:getBoxChildOuterAttrs(child)}><td${u:getBoxChildInnerAttrs(child)}>${z:redraw(child, null)}</td></tr>
	</c:forEach>
</table>
	 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);

		final Box self = (Box) comp;
		final String uuid = self.getUuid();
		
		wh.write("<table id=\"").write(uuid).write("\" ");
		wh.write("z.type=\"zul.box.Box\"").write(self.getOuterAttrs()).write(self.getInnerAttrs());
		wh.write(" cellpadding=\"0\" cellspacing=\"0\">");
		wh.write("<tr valign=\"").write(self.getValign()).write("\" id=\"").write(uuid).write("!cave\">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			wh.write("<tr valign=\"").write("\" id=\"").write(uuid).write("!chdextr\">");
			wh.write("<td id=\"").write(child.getUuid()).write("!chdextr\"");
			wh.write(ZulFns.getBoxChildOuterAttrs(child));
			wh.write(ZulFns.getBoxChildInnerAttrs(child));
			wh.write(">");
			child.redraw(out);
			wh.write("</td></tr>");
		}		
		wh.writeln("</table>");
	}

	
	

}
