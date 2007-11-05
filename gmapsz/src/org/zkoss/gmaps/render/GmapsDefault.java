/* GmapsDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 7, 2007 23:02:21 , Created by henrichen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.gmaps.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;

import org.zkoss.gmaps.Gmaps;

/*
 * {@link Gmaps}'s default mold.
 * 
 * @author henrichen
 * 
 * @since 3.0.0
 */
public class GmapsDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Gmaps self = (Gmaps) comp;
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write("\"");
		wh.write(self.getOuterAttrs()).write(" z.type=\"gmapsz.gmaps.Gmaps\">");
		wh.write("<div id=\"").write(uuid).write("!real\" style=\"width:100%;height:100%\">");
		wh.write("</div>");
		wh.write("<div id=\"").write(uuid).write("!cave\" style=\"display:none\">");
		wh.writeChildren(self);
		wh.write("</div>");
		wh.write("</div>");
	}

}
