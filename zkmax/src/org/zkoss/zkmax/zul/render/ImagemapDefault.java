/* ImagemapDefault.java

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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Imagemap;

/**
 * {@link Imagemap}'s default and alphafix mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ImagemapDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Imagemap self = (Imagemap)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		
		wh.write("<span id=\"").write(uuid).write("\" z.type=\"zul.widget.Map\" z.cave=\"")
			.write(uuid).write("_map\"").write(self.getOuterAttrs()).write(">");
		wh.write("<a href=\"").write(exec.encodeURL("~./zul/html/imagemap-done.dsp"));
		wh.write("?").write(uuid).write("\" target=\"zk_hfr_\">");
		wh.write("<img id=\"").write(self.getUuid()).write("!real\"")
			.write(self.getInnerAttrs()).write("/></a>");
		wh.write("<map name=\"").write(uuid).write("_map\" id=\"").write(uuid).write("_map\">");
		wh.writeChildren(self);
		wh.write("</map></span>");
	}
}
