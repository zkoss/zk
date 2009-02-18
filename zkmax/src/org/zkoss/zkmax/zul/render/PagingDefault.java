/* PagingDefault.java

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
		final String zcls = self.getZclass();
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write("\" name=\"")
			.write(uuid).write("\" z.type=\"zul.pg.Pg\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		
		wh.write("<table cellspacing=\"0\"><tbody><tr>");
		wh.write("<td><table id=\"").write(uuid+"!tb_f")
			.write("\" name=\"").write(uuid+"!tb_f")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"")
			.write(zcls).write("-btn\"><tbody><tr><td class=\"").write(zcls)
			.write("-btn-l\"></td><td class=\"").write(zcls)
			.write("-btn-m\"><div><button type=\"button\" class=\"")
			.write(zcls).write("-first\"> </button></div></td><td class=\"").write(zcls).write("-btn-r\"></td></tr></tbody></table></td>");
		wh.write("<td><table id=\"").write(uuid+"!tb_p")
			.write("\" name=\"").write(uuid+"!tb_p")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"").write(zcls)
			.write("-btn\"><tbody><tr><td class=\"").write(zcls).write("-btn-l\"></td><td class=\"")
			.write(zcls).write("-btn-m\"><div><button type=\"button\" class=\"")
			.write(zcls).write("-prev\"> </button></div></td><td class=\"").write(zcls)
			.write("-btn-r\"></td></tr></tbody></table></td>");
		wh.write("<td><span class=\"").write(zcls).write("-sep\"/></td>");
		wh.write("<td><span class=\"").write(zcls).write("-text\"></span></td>");
		wh.write("<td><input type=\"text\" class=\"").write(zcls).write("-inp\" value=\"")
			.write(self.getActivePage() + 1).write("\" size=\"3\" id=\"").write(uuid + "!real")
			.write("\" name=\"").write(uuid + "!real")
			.write("\"/></td>");
		wh.write("<td><span class=\"").write(zcls).write("-text\">/ ").write(self.getPageCount())
			.write("</span></td>");
		wh.write("<td><span class=\"").write(zcls).write("-sep\"/></td>");
		wh.write("<td><table id=\"").write(uuid+"!tb_n")
			.write("\" name=\"").write(uuid+"!tb_n")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"")
			.write(zcls).write("-btn\"><tbody><tr><td class=\"").write(zcls)
			.write("-btn-l\"></td><td class=\"").write(zcls)
			.write("-btn-m\"><div><button type=\"button\" class=\"")
			.write(zcls).write("-next\"> </button></div></td><td class=\"").write(zcls)
			.write("-btn-r\"></td></tr></tbody></table></td>");
		wh.write("<td><table id=\"").write(uuid+"!tb_l")
			.write("\" name=\"").write(uuid+"!tb_l")
			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"")
			.write(zcls).write("-btn\"><tbody><tr><td class=\"").write(zcls)
			.write("-btn-l\"></td><td class=\"").write(zcls)
			.write("-btn-m\"><div><button type=\"button\" class=\"")
			.write(zcls).write("-last\"> </button></div></td><td class=\"").write(zcls)
			.write("-btn-r\"></td></tr></tbody></table></td>");
		wh.write("</tr></tbody></table>");
		
		if (self.isDetailed())
			wh.write(self.getInfoTags());
		wh.write("</div>");
	}
}
