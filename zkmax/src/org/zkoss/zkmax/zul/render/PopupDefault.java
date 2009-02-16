/* PopupDefault.java

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
import org.zkoss.zul.Popup;

/** 
 * {@link Popup}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class PopupDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Popup self = (Popup)comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.widget.Pop\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">")
			.write("<div class=\"").write(zcls).write("-tl\"><div class=\"").write(zcls)
			.write("-tr\"><div class=\"").write(zcls).write("-tm\"></div></div></div>")
			.write("<div id=\"").write(uuid).write("!bwrap\" class=\"").write(zcls)
			.write("-body\"><div class=\"").write(zcls).write("-cl\">")
			.write("<div class=\"").write(zcls).write("-cr\"><div class=\"").write(zcls).write("-cm\"><div id=\"")
			.write(uuid).write("!cave\" class=\"").write(zcls).write("-cnt\">")
			.writeChildren(self)
			.write("</div></div></div></div><div class=\"").write(zcls).write("-bl\"><div class=\"").write(zcls).write("-br\">")
			.write("<div class=\"").write(zcls).write("-bm\"></div></div></div></div></div>");
	}
}
