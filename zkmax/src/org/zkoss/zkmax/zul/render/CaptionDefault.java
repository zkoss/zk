/* CaptionDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed Sep  5 13:18:53     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkmax.zul.render;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;

/**
 * {@link Caption}'s default mold.
 * 
 * @author dennis.chen
 * @since 3.0.0
 */
public class CaptionDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Caption self = (Caption) comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		final String imgTag = self.getImgTag();

		if (self.isLegend()) {
			wh.write("<legend>").write(imgTag);
			new Out(self.getLabel()).render(out);
			wh.writeChildren(self);
			wh.writeln("</legend>");
		} else {
			final String pzcls = ((HtmlBasedComponent)self.getParent()).getZclass();
			final String puuid = self.getParent().getUuid();
			wh.write("<table id=\"").write(uuid).write("\" ");
			wh.write("z.type=\"zul.widget.Capt\"").write(self.getOuterAttrs())
					.write(self.getInnerAttrs());
			wh.writeln(" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.writeln("<tr valign=\"middle\">");
			wh.write("<td align=\"left\" class=\"").write(zcls).write("-l\">").write(imgTag);
			new Out(self.getCompoundLabel()).setNbsp(true).render(out);
			wh.writeln("</td>");

			wh.write("<td align=\"right\" class=\"").write(zcls).write("-r\" id=\"").write(uuid).write("!cave\">")
				.writeChildren(self)
				.writeln("</td>");
			if (self.isMinimizableVisible()) {
				wh.write("<td width=\"16\"><div id=\"")
					.write(puuid)
					.write("!minimize\" class=\"")
					.write(pzcls).write("-tool ").write(pzcls)
					.write("-minimize\"></div></td>");
			}
			if (self.isMaximizableVisible()) {
				wh.write("<td width=\"16\"><div id=\"")
					.write(puuid)
					.write("!maximize\" class=\"")
					.write(pzcls).write("-tool ").write(pzcls)
					.write("-maximize");
				if (((Window)self.getParent()).isMaximized())
					wh.write(" ").write(pzcls).write("-maximized");
				wh.write("\"></div></td>");
			}
			if (self.isClosableVisible()) {
				wh.write("<td width=\"16\"><div id=\"")
					.write(puuid)
					.write("!close\" class=\"")
					.write(pzcls).write("-tool ").write(pzcls)
					.write("-close\"></div></td>");
			}
			wh.write("</tr></table>");
		}
	}
}
