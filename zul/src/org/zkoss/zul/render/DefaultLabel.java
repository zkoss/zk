/* DefaultLabel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 11:08:53     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Label;

/**
 * The label's default mold.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class DefaultLabel implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final Label self = (Label)comp;
		final boolean idRequired = self.isIdRequired();
		if (idRequired) {
			out.write("<span id=\"");
			out.write(self.getUuid());
			out.write("\"");
			out.write(self.getOuterAttrs());
			out.write(self.getInnerAttrs());
			out.write('>');
		}

		out.write(self.getEncodedText());

		if (idRequired)
			out.write("</span>");
	}
}
