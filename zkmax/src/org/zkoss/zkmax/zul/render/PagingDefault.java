/* PagingDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Paging;

/**
 * {@link Paging}'s default mold.
 * 
 * @author Jeff Liu
 * @since 3.0.0
 */
public class PagingDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Paging self = (Paging) comp;
		final String sclass = self.getSclass();
		wh.write("<div id=\"").write(self.getUuid()).write("\" name=\"")
			.write(self.getUuid()).write("\" z.type=\"zul.pg.Pg\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		
		wh.write("<table cellspacing=\"0\"><tbody><tr>");
		wh.write("<td><table id=\"").write(self.getUuid()+"!tb_f")
			.write("\" name=\"").write(self.getUuid()+"!tb_f")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"z-btn z-btn-icon\" style=\"width: auto;\"><tbody><tr><td class=\"z-btn-l\"><i>&#160;</i></td><td class=\"z-btn-m\"><em unselectable=\"on\"><button type=\"button\" class=\"z-btn-text ")
			.write(sclass).write("-first\"> </button></em></td><td class=\"z-btn-r\"><i>&#160;</i></td></tr></tbody></table></td>");
		wh.write("<td><table id=\"").write(self.getUuid()+"!tb_p")
			.write("\" name=\"").write(self.getUuid()+"!tb_p")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"z-btn z-btn-icon\" style=\"width: auto;\"><tbody><tr><td class=\"z-btn-l\"><i>&#160;</i></td><td class=\"z-btn-m\"><em unselectable=\"on\"><button type=\"button\" class=\"z-btn-text ")
			.write(sclass).write("-prev\"> </button></em></td><td class=\"z-btn-r\"><i>&#160;</i></td></tr></tbody></table></td>");
		wh.write("<td><span class=\"").write(sclass).write("-sep\"/></td>");
		wh.write("<td><span class=\"").write(sclass).write("-text\"></span></td>");
		wh.write("<td><input type=\"text\" class=\"").write(sclass).write("-number\" value=\"")
			.write(self.getActivePage() + 1).write("\" size=\"3\" id=\"").write(self.getUuid() + "!real")
			.write("\" name=\"").write(self.getUuid() + "!real")
			.write("\" style=\"height: 14px;\"/></td>");
		wh.write("<td><span class=\"").write(sclass).write("-text\">/ ").write(self.getPageCount())
			.write("</span></td>");
		wh.write("<td><span class=\"").write(sclass).write("-sep\"/></td>");
		wh.write("<td><table id=\"").write(self.getUuid()+"!tb_n")
			.write("\" name=\"").write(self.getUuid()+"!tb_n")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"z-btn z-btn-icon\" style=\"width: auto;\"><tbody><tr><td class=\"z-btn-l\"><i>&#160;</i></td><td class=\"z-btn-m\"><em unselectable=\"on\"><button type=\"button\" class=\"z-btn-text ")
			.write(sclass).write("-next\"> </button></em></td><td class=\"z-btn-r\"><i>&#160;</i></td></tr></tbody></table></td>");
		wh.write("<td><table id=\"").write(self.getUuid()+"!tb_l")
			.write("\" name=\"").write(self.getUuid()+"!tb_l")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"z-btn z-btn-icon\" style=\"width: auto;\"><tbody><tr><td class=\"z-btn-l\"><i>&#160;</i></td><td class=\"z-btn-m\"><em unselectable=\"on\"><button type=\"button\" class=\"z-btn-text ")
			.write(sclass).write("-last\"> </button></em></td><td class=\"z-btn-r\"><i>&#160;</i></td></tr></tbody></table></td>");
		wh.write("</tr></tbody></table>");
		
		if (self.isDetailed())
			wh.write(self.getInfoTags());
		wh.write("</div>");
	}
}
