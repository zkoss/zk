/* FlashDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 7, 2007 9:04:13 AM , Created by jumperchen
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
import org.zkoss.zul.Flash;

/*
 * {@link Flash}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class FlashDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Flash self = (Flash) comp;
		final String uuid = self.getUuid();
		final String encsrc = self.getEncodedSrc(), bgcolor = self.getBgcolor();
		wh.write("<div id=\"").write(uuid).write("\" ");
		wh.write(self.getOuterAttrs()).write(" z.type=\"zul.flash.Flash\">");
		wh.write("<object id=\"").write(uuid).write("!obj\" classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"");
		wh.write(self.getWidth()).write("\" height=\"").write(self.getHeight()).write("\">");
		wh.write("<param name=\"movie\" value=\"").write(encsrc).write("\"></param>");
		wh.write("<param name=\"wmode\" value=\"").write(self.getWmode()).write("\"></param>");
		wh.write("<param name=\"autoplay\" value=\"").write(self.isAutoplay()).write("\"></param>");
		wh.write("<param name=\"loop\" value=\"").write(self.isLoop()).write("\"></param>");

		if (bgcolor != null && bgcolor.length() > 0)
			wh.write("<param name=\"bgcolor\" value=\"").write(bgcolor).write("\"></param>");

		wh.write("<embed id=\"").write(uuid).write("!emb\" src=\"");
		wh.write(encsrc).write("\" type=\"application/x-shockwave-flash\" wmode=\"").write(self.getWmode())
			.write("\" autoplay=\"").write(self.isAutoplay())
			.write("\" loop=\"").write(self.isLoop()).write('"');

		if (bgcolor != null && bgcolor.length() > 0)
			wh.write(" bgcolor=\"").write(bgcolor).write('"');
		
		wh.write(" width=\"").write(self.getWidth()).write("\" height=\"").write(self.getHeight()).write("\">");
		wh.write("</embed></object></div>");
	}
}
