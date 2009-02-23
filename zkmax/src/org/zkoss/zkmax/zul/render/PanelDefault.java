/* PanelDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 26, 2008 11:27:53 AM , Created by jumperchen
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

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Panel;

/**
 * {@link Panel}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class PanelDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Panel self = (Panel)comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		final String title = self.getTitle();
		final String border = self.getBorder();
		final boolean hasBorder = "normal".equals(border);
		final boolean noTitle = Strings.isBlank(title);
		final Caption caption = self.getCaption();
		final boolean noHeader = noTitle && caption == null;
		
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.panel.Panel\" z.autoz=\"true\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		
		if (caption != null || !noTitle) {
			if (self.isFramable()) {
				wh.write("<div class=\"").write(zcls).write("-tl").write("\"><div class=\"")
					.write(zcls).write("-tr\"></div></div><div class=\"").write(zcls)
					.write("-hl").write("\"><div class=\"").write(zcls).write("-hr\"><div class=\"")
					.write(zcls).write("-hm\">");
			}
			wh.write("<div id=\"").write(uuid).write("!caption\" class=\"").write(zcls)
				.write("-header");
			if (!self.isFramable() && !hasBorder)
				wh.write(' ').write(zcls).write("-header-noborder");
			wh.write("\">");
			if (caption == null) {
				if (self.isClosable())
					wh.write("<div id=\"").write(uuid).write("!close\" class=\"")
						.write(zcls).write("-icon ").write(zcls).write("-close\"></div>");
				if (self.isMaximizable()) {
					wh.write("<div id=\"").write(uuid).write("!maximize\" class=\"")
						.write(zcls).write("-icon ").write(zcls).write("-max");
					if (self.isMaximized())
							wh.write(" ").write(zcls).write("-maxd");
					wh.write("\"></div>");
				}
				if (self.isMinimizable())
					wh.write("<div id=\"").write(uuid).write("!minimize\" class=\"")
						.write(zcls).write("-icon ").write(zcls).write("-min\"></div>");
				if (self.isCollapsible())
					wh.write("<div id=\"").write(uuid).write("!toggle\" class=\"")
						.write(zcls).write("-icon ").write(zcls).write("-toggle\"></div>");
				new Out(title).render(out);				
			} else wh.write(caption);
			
			wh.write("</div>");
			if (self.isFramable()) {
				wh.write("</div></div></div>");
			}
		} else if (self.isFramable()) {
			wh.write("<div class=\"").write(zcls).write("-tl").write("\"><div class=\"")
			.write(zcls).write("-tr\"></div></div>");
		}
		
		wh.write("<div id=\"").write(uuid).write("!body\" class=\"").write(zcls).write("-body\"");
		if (!self.isOpen()) wh.write("style=\"display:none;\"");
		wh.write('>');
		
		if (self.isFramable()) {
			wh.write("<div class=\"").write(zcls).write("-cl\"><div class=\"").write(zcls)
				.write("-cr\"><div class=\"").write(zcls).write("-cm");
			if (noHeader)
				wh.write(' ').write(zcls).write("-noheader");
			wh.write("\">");
		}
		
		if (self.getTopToolbar() != null) {
			wh.write("<div id=\"").write(uuid).write("!tb\" class=\"").write(zcls).write("-tbar");
			if (!hasBorder)
				wh.write(' ').write(zcls).write("-tbar-noborder");
			if (self.isFramable() && caption == null && noTitle)
				wh.write(' ').write(zcls).write("-noheader");
			wh.write("\">").write(self.getTopToolbar()).write("</div>");
		}
		wh.write(self.getPanelchildren());

		if (self.getBottomToolbar() != null) {
			wh.write("<div id=\"").write(uuid).write("!bb\" class=\"").write(zcls).write("-bbar");
			if (!hasBorder)
				wh.write(' ').write(zcls).write("-bbar-noborder");
			if (self.isFramable() && caption == null && noTitle)
				wh.write(' ').write(zcls).write("-noheader");
			wh.write("\">").write(self.getBottomToolbar()).write("</div>");
		}
		
		if (self.isFramable()) {
			wh.write("</div></div></div><div class=\"").write(zcls).write("-fl");
			if (self.getFootToolbar() == null)
				wh.write(' ').write(zcls).write("-nofbar");
			wh.write("\"><div class=\"").write(zcls).write("-fr\"><div class=\"").write(zcls).write("-fm\">");
		}
		if (self.getFootToolbar() != null) {
			wh.write("<div id=\"").write(uuid).write("!fb\" class=\"").write(zcls)
				.write("-fbar\">").write(self.getFootToolbar()).write("</div>");
		}
		if (self.isFramable())
			wh.write("</div></div></div><div class=\"").write(zcls).write("-bl\"><div class=\"")
				.write(zcls).write("-br\"></div></div>");
		wh.write("</div></div>");
	}
}
