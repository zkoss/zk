/* Window2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 19, 2008 6:44:28 PM , Created by jumperchen
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.Out;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Window;

/**
 * {@link Window}'s default mold for new trendy.
 * @author jumperchen
 *
 */
public class Window2Default implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Window self = (Window)comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.wnd2.Wnd2\" z.autoz=\"true\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		final Caption caption = self.getCaption();
		final String title = self.getTitle();
		final boolean isFrame = !self.inEmbedded() && !self.inPopup();
		final String noBorder = !"normal".equals(self.getBorder()) ? "-noborder" : "";
		String wcExtStyle = "";
		if (caption != null || title.length() > 0) {
			wh.write("<div class=\"").write(zcls).write("-tl").write(noBorder)
				.write("\"><div class=\"").write(zcls).write("-tr").write(noBorder)
				.write("\"><div class=\"").write(zcls).write("-tm").write(noBorder)
				.write("\"><div id=\"").write(uuid).write("!caption\" class=\"")
				.write(zcls).write("-header\">");
			if (caption == null) {
				if (self.isClosable())
					wh.write("<div id=\"").write(uuid).write("!close\" class=\"").write(zcls).write("-tool ").write(zcls).write("-close\"></div>");
				if (self.isMaximizable()) {
					wh.write("<div id=\"").write(uuid).write("!maximize\" class=\"").write(zcls).write("-tool ").write(zcls).write("-maximize");
					if (self.isMaximized())
							wh.write(" ").write(zcls).write("-maximized");
					wh.write("\"></div>");
				}
				if (self.isMinimizable())
					wh.write("<div id=\"").write(uuid).write("!minimize\" class=\"").write(zcls).write("-tool ").write(zcls).write("-minimize\"></div>");
				new Out(title).render(out);
			} else {
				wh.write(caption);
			}
			wh.write("</div></div></div></div>");
			wcExtStyle = "border-top:0;";
		} else if (isFrame) {
			wh.write("<div class=\"").write(zcls).write("-tl").write(noBorder)
				.write("\"><div class=\"").write(zcls).write("-tr").write(noBorder)
				.write("\"><div class=\"").write(zcls).write("-tm").write(noBorder)
				.write("-noheader\"></div></div></div>");
		}
		wh.write("<div id=\"").write(uuid).write("!bwrap\" class=\"").write(zcls).write("-body\">");
		if (isFrame) {
			wh.write("<div class=\"").write(zcls).write("-cl").write(noBorder)
				.write("\"><div class=\"").write(zcls).write("-cr").write(noBorder)
				.write("\"><div class=\"").write(zcls).write("-cm").write(noBorder)
				.write("\">");
		}
		final String cs = self.getContentStyle();
		if(cs != null){
			wcExtStyle += cs;
		}
		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"");
		wh.write(self.getContentSclass()).write(" ").write(zcls).write("-cnt").write(noBorder);
		wh.write("\"").writeAttr("style", wcExtStyle);
		wh.write(">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child != caption)
				wh.write(child);
		}
		wh.write("</div>");
		if (isFrame) {
			wh.write("</div></div></div><div class=\"").write(zcls).write("-bl")
				.write(noBorder).write("\"><div class=\"").write(zcls).write("-br")
				.write(noBorder).write("\"><div class=\"").write(zcls).write("-bm")
				.write(noBorder).write("\"></div></div></div>");
		}
		wh.write("</div></div>");	
	}
}
