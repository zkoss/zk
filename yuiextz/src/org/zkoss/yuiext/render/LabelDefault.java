/* LabelDefault.java

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

import java.io.Writer;
import java.io.IOException;

import org.zkoss.yuiext.grid.Label;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

/**
 * {@link Label}'s default mold.
 *
 * @author jumperchen
 * @since ZK 3.0.0
 */
public class LabelDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Label self = (Label)comp;
		final boolean idRequired = self.isIdRequired();
		if (idRequired) {
			wh.write("<span id=\"").write(self.getUuid()).write("\"");
			wh.write(self.getOuterAttrs()).write(self.getInnerAttrs());
			wh.write(">");
		}

		wh.write(self.getEncodedText());

		if (idRequired)
			wh.write("</span>");
	}
}
