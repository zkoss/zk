/* ListgroupDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Apr 25, 2008 3:17:15 PM , Created by jumperchen
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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Listgroup;

/**
 * {@link Listgroup}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class ListgroupDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Listgroup self = (Listgroup)comp;
		
		wh.write("<tr id=\"").write(self.getUuid()).write("\" z.type=\"Litgp\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.writeChildren(self);
		wh.writeln("</tr>");
		
	}
}
