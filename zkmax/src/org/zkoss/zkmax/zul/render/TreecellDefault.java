/* TreecellDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:17:41 AM , Created by robbiecheng
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

import org.zkoss.zul.Treecell;

/**
 * {@link Treecell}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreecellDefault implements ComponentRenderer{
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Treecell self = (Treecell) comp;
		
		wh.write("<td z.type=\"Lic\" id=\"").write(self.getUuid()).write('"')
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write("><div id=\"").write(self.getUuid()).write("!cave\"")
			.write(self.getLabelAttrs()).write(" class=\"").write(self.getZclass()).write("-cnt");
		if (self.getTree().isFixedLayout())
			wh.write(" z-overflow-hidden");
		wh.write("\">").write(self.getColumnHtmlPrefix()).write(self.getImgTag());

		new Out(self.getLabel()).setMaxlength(self.getMaxlength()).render(out);
	
		wh.writeChildren(self)
			.write(self.getColumnHtmlPostfix())
			.writeln("</div></td>");
	}
}
