/* CaptionDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Wed Sep  5 13:18:53     2007, Created by Dennis.Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
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
		final String mcls = self.getMoldSclass();
		final String imgTag = self.getImgTag();

		if (self.isLegend()) {
			wh.write("<legend>").write(imgTag);
			new Out(self.getLabel()).render(out);
			wh.writeChildren(self);
			wh.writeln("</legend>");
		} else {
			wh.write("<table id=\"").write(uuid).write("\" ");
			wh.write("z.type=\"zul.widget.Capt\"").write(self.getOuterAttrs())
					.write(self.getInnerAttrs());
			wh.writeln(" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.writeln("<tr valign=\"middle\">");
			wh.write("<td align=\"left\" class=\"").write(mcls).write("-l\">").write(imgTag);
			new Out(self.getCompoundLabel()).setNbsp(true).render(out);
			wh.writeln("</td>");

			wh.write("<td align=\"right\" class=\"").write(mcls).write("-r\" id=\"").write(uuid).write("!cave\">")
				.writeChildren(self)
				.writeln("</td>");

			if (self.isClosableVisible()) {
				final String pcls = ((HtmlBasedComponent)self.getParent()).getMoldSclass();
				wh.write("<td width=\"16\"><div id=\"")
					.write(self.getParent().getUuid())
					.write("!close\" class=\"")
					.write(pcls).write("-tool ").write(pcls).write("-close\"></div></td>");
			}

			wh.write("</tr></table>");
		}
	}
}
