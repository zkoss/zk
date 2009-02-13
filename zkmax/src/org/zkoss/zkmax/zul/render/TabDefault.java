/* TabDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 3:59:51 PM , Created by robbiecheng
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

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tab}'s default mold.
 * It forward to {@link TabDefaultV} if the orient is vertical.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 * 
 */
public class TabDefault implements ComponentRenderer {
	private final TabDefaultV _vtab = new TabDefaultV();

	public void render(Component comp, Writer out) throws IOException {
		final Tab self = (Tab) comp;
		final Tabbox tabbox = self.getTabbox();
		if ("vertical".equals(tabbox.getOrient())) {
			_vtab.render(comp, out);
			return; //done
		}

		final SmartWriter wh = new SmartWriter(out);
		final Execution exec = Executions.getCurrent();
		final String look = tabbox.getTabLook() + '-';
		final String suffix = self.isSelected() ? "-sel" : "-uns";
		final Tabpanel panel = self.getLinkedPanel();

		final int colspan = self.isClosable() ? 4 : 3;
		wh.write("<td id=\"").write(self.getUuid()).write("\" z.type=\"Tab\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write(" z.sel=\"").write(self.isSelected()).write("\" z.box=\"")
			.write(tabbox.getUuid()).write("\" z.panel=\"")
			.write(panel==null?"":panel.getUuid()).write("\" ")
			.write("z.disabled=\"").write(self.isDisabled())
			.write("\">");

		wh.writeln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">")
			.write("<tr><td class=\"").write(look).write("tl").write(suffix).writeln("\"></td>")
			.write("<td colspan=\"").write(colspan)
			.write("\" class=\"").write(look).write("tm").write(suffix).writeln("\"></td>")
			.write("<td class=\"").write(look).write("tr").write(suffix).writeln("\"></td></tr>");

		wh.write("<tr class=\"").write(look).write('m');
		if (!Strings.isBlank(self.getHeight()))
			wh.write("\" style=\"height:").write(self.getHeight());
		wh.writeln("\"><td class=\"")
			.write(look).write("ml").write(suffix).writeln("\"></td>")
			.write("<td width=\"3\" class=\"").write(look).write("mm").write(suffix).writeln("\"></td>")
			.write("<td align=\"center\" class=\"").write(look).write("mm").write(suffix)
			.write("\"><a href=\"javascript:;\"").write(" id=\"").write(self.getUuid()).write("!a\">")
			.write(self.getImgTag());
		new Out(self.getLabel()).render(out);
		wh.writeln("</a></td>");

		if (self.isClosable()) {
			// Bug 1780044: width cannot (and need not) be specified
			wh.write("<td align=\"right\" class=\"").write(look).write("mm").write(suffix)
				.write("\"><img id=\"")
				.write(self.getUuid()).write("!close\" src=\"")
				.write(exec.encodeURL("~./zul/img/close-off.gif"))
				.writeln("\"/></td>");
		}

		wh.write("<td width=\"3\" class=\"").write(look).write("mm").write(suffix).writeln("\"></td>")
			.write("<td class=\"").write(look).write("mr").write(suffix).writeln("\"></td></tr>");

		wh.write("<tr><td class=\"").write(look).write("bl").write(suffix).writeln("\"></td>")
			.write("<td colspan=\"").write(colspan).write("\" class=\"").write(look).write("bm").write(suffix).writeln("\"></td>")
			.write("<td class=\"").write(look).write("br").write(suffix).writeln("\"></td></tr></table></td>");
	}
}
