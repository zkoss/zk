/* ListboxPaging.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 14, 2007 11:05:17 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.yuiext.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.yuiext.grid.Listbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

/**
 * {@link Listbox}'s paging mold.
 * @author jumperchen
 * @since ZK 3.0.0
 */
public class ListboxPaging implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Listbox self = (Listbox)comp;
		final String uuid = self.getUuid();
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.sel.Libox\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		
		wh.write("<div id=\"").write(uuid)
			.write("!paging\" class=\"listbox-paging\">")
			.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"listbox-btable\">");

		//header
		wh.writeln("<tbody class=\"listbox-head\">").writeComponents(self.getHeads()).writeln("</tbody>");

		//body
		wh.write("<tbody id=\"").write(uuid).writeln("!cave\">")
			.writeComponents(self.getItems(), self.getVisibleBegin(), self.getVisibleEnd())
			.writeln("</tbody>");

		//Footer
		wh.writeln("<tbody class=\"listbox-foot\">").write(self.getListfoot())
			.write("</tbody>\n</table>");

		//Paging
		wh.write("<div id=\"").write(uuid)
			.write("!pgi\" class=\"listbox-pgi\">")
			.write(self.getPaging())
			.write("</div></div></div>");
	}
}
