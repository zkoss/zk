/* Window2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 19, 2008 6:44:28 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zul.wnd2.Wnd2\" z.autoz=\"true\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		final Caption caption = self.getCaption();
		final String title = self.getTitle(), titlesc = self.getTitleSclass();
		final boolean isEmbedded = self.inEmbedded();
		String wcExtStyle = "";
		if (caption != null || title.length() > 0) {
			wh.write("<div class=\"l").write(titlesc).write("\"><div class=\"r")
				.write(titlesc).write("\"><div class=\"m").write(titlesc).write("\"><div id=\"")
				.write(uuid).write("!caption\" class=\"").write(titlesc).write(" title\">");
			if (caption == null) {
				if (self.isClosable())
					wh.write("<div id=\"").write(uuid).write("!close\" class=\"z-window-tool z-window-close\"></div>");
				if (self.isMaximizable()) {
					wh.write("<div id=\"").write(uuid).write("!maximize\" class=\"z-window-tool z-window-maximize");
					if (self.isMaximized())
							wh.write(" z-window-maximized");
					wh.write("\"></div>");
				}
				if (self.isMinimizable())
					wh.write("<div id=\"").write(uuid).write("!minimize\" class=\"z-window-tool z-window-minimize\"></div>");
				new Out(title).render(out);
			} else {
				wh.write(caption);
			}
			wh.write("</div></div></div></div>");
			wcExtStyle = "border-top:0;";
		} else if (!isEmbedded) {
			wh.write("<div class=\"l").write(titlesc).write("\"><div class=\"r")
				.write(titlesc).write("\"><div class=\"m").write(titlesc)
				.write("-notitle\"></div></div></div>");
		}
		final String ccls = self.getContentSclass();
		wh.write("<div id=\"").write(uuid).write("!bwrap\" class=\"wc-bwrap\">");
		if (!isEmbedded)
			wh.write("<div class=\"l").write(ccls).write("\"><div class=\"r").write(ccls)
				.write("\"><div class=\"m").write(ccls).write("\">");
		final String cs = self.getContentStyle();
		if(cs != null){
			wcExtStyle += cs;
		}
		wh.write("<div id=\"").write(uuid).write("!cave\" class=\"");
		wh.write(ccls).write("\"").writeAttr("style", wcExtStyle);
		wh.write(">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child != caption)
				wh.write(child);
		}
		wh.write("</div>");
		final String mode = self.getMode();
		if (!isEmbedded)
			wh.write("</div></div></div><div class=\"lwb-").write(mode)
				.write("\"><div class=\"rwb-").write(mode).write("\"><div class=\"mwb-")
				.write(mode).write("\"></div></div></div>");
		wh.write("</div></div>");	
	}
}
