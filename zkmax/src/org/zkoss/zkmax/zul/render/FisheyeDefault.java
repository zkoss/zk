/* FisheyeDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 10, 2008 4:07:32 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

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
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkex.zul.Fisheye;

/**
 * {@link Fisheye}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.5.0
 */
public class FisheyeDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Fisheye self = (Fisheye) comp;
		final String uuid = self.getUuid();
		final String sclass = self.getZclass();
		final Execution exe = Executions.getCurrent();
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zkex.zul.fisheye.Fisheye\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write("><img id=\"")
			.write(uuid).write("!img\" src=\"").write(exe.encodeURL(self.getImage()))
			.write("\" class=\"").write(sclass).write("-img\"/><div id=\"")
			.write(uuid).write("!label\" style=\"display:none;\" class=\"").write(sclass).write("-text\">");
		new Out(self.getLabel()).render(out);
		wh.write("</div></div>");
	}
}
