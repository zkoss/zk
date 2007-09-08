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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Listbox;

/**
 * {@link Listbox}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListboxDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Listbox self = (Listbox)comp;
		final String uuid = self.getUuid();
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.sel.Libox\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");

		if(self.getListhead() != null){
			wh.write("<div id=\"").write(uuid).write("!head\" class=\"listbox-head\">")
				.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">")
				.writeln(self.getListhead())
				.write("</table></div>");
		}

		wh.write("<div id=\"").write(uuid).write("!body\" class=\"listbox-body\">")
			.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"")
			.write(uuid).writeln("!cave\" class=\"listbox-btable\">")
			.write(self.getItems())
			.write("\n</table></div>");

		if(self.getListfoot() != null){
			wh.write("<div id=\"").write(uuid).write("!foot\" class=\"listbox-foot\">")
				.writeln("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">")
				.writeln(self.getListfoot())
				.write("</table></div>");
		}

		wh.write("</div>");
	}
}
