/* ListboxSelect.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

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
		wh.write("<select id=\"").write(uuid).write("\" z.type=\"zul.sel.Lisel\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");

		for (Iterator it = self.getItems().iterator(); it.hasNext();) {
			final Listitem item = (Listitem)it.next();
			wh.write("<option id=\"").write(item.getUuid()).write("\"")
				.write(item.getOuterAttrs()).write(item.getInnerAttrs()).write(">");
			new Out(item.getLabel()).setMaxlength(self.getMaxlength()).render(out);
			wh.writeln("</option>");
		}

		wh.write("</select>");
	}

}
