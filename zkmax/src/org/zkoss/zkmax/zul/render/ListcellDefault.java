/* ListcellDefault.java

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Listcell;


/**
 * {@link Listcell}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListcellDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Listcell self = (Listcell)comp;
		final String uuid = self.getUuid();
		wh.write("<td z.type=\"Lic\" id=\"").write(uuid).write("\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write("><div id=\"").write(self.getUuid()).write("!cave\"")
			.write(self.getLabelAttrs()).write(" class=\"cell-inner ");
			if (self.getListbox().isFixedLayout())
				wh.write("overflow-hidden");
			wh.write("\">");
		wh.write(self.getColumnHtmlPrefix()).write(self.getImgTag());
		new Out(self.getLabel()).setMaxlength(self.getMaxlength()).render(out);
		wh.writeChildren(self);
		wh.write(self.getColumnHtmlPostfix()).writeln("</div></td>");
	}

}
