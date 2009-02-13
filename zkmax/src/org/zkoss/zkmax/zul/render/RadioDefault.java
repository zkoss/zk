/* RadioDefault.java

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
import org.zkoss.zul.Radio;

/**
 * {@link Radio}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class RadioDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Radio self = (Radio)comp;
		final String uuid = self.getUuid();
		wh.write("<span id=\"").write(uuid).write("\" z.type=\"zul.widget.Radio\"")
			.write(self.getOuterAttrs()).write(">").write("<input type=\"radio\" id=\"")
			.write(uuid).write("!real\"").write(self.getInnerAttrs())
			.write("/><label for=\"").write(uuid).write("!real\"")
			.write(self.getLabelAttrs()).write(">")
			.write(self.getImgTag()).write(self.getLabel())
			.write("</label>");

		if(self.getRadiogroup() != null
		&& self.getRadiogroup().getOrient().equals("vertical"))
			wh.writeln("<br/>");

		wh.write("</span>");
	}
}
