/* StyleDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Dec 26 15:52:40     2007, Created by tomyeh
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
import org.zkoss.zul.Style;

/**
 * The renderer for the style component ({@link Style}).
 *
 * @author tomyeh
 * @since 3.0.2
 */
public class StyleDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Style self = (Style)comp;
		final String uuid = self.getUuid();
		wh.write("<span id=\"").write(uuid).write('"');
		wh.write(self.getOuterAttrs()).write(" z.type=\"zul.widget.Style\"></span>");
	}
}
