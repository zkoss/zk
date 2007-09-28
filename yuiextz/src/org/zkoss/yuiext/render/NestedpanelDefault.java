/* NestedpanelDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 14, 2007 12:11:42 PM , Created by jumperchen
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

import org.zkoss.yuiext.layout.Nestedpanel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

/**
 * {@link Nestedpanel}'s default mold.
 * 
 * @author jumperchen
 * @since ZK 3.0.0
 *
 */
public class NestedpanelDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Nestedpanel self = (Nestedpanel) comp;
		wh.write("<span id=\"").write(self.getUuid()).write('"').write(self.getOuterAttrs())
			.write(self.getInnerAttrs()).write(" z.type=\"yuiextz.layout.ExtNestedPanel\">")
			.writeChildren(self).writeln("</span>");
	}

}
