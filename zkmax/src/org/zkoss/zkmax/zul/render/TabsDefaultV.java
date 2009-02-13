/* TabsDefaultV.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 6:21:35 PM , Created by robbiecheng
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
import org.zkoss.zul.Tabs;

/**
 * {@link Tabs}'s default mold for vertial orient only.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TabsDefaultV implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Tabs self = (Tabs)comp;
		final String look = self.getTabbox().getTabLook() + '-';

		wh.write("<td id=\"").write(self.getUuid())
			.write("\" align=\"right\" z.type=\"zul.tab.Tabs\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>')
			.writeln("<table class=\"vtabsi\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		
		/* prefix row */
		wh.writeln("<tr><td align=\"right\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
			.write("<tr><td class=\"").write(look).writeln("first1\"></td></tr>")
			.write("<tr id=\"").write(self.getUuid()).write("!first\"><td class=\"")
			.write(look).writeln("first2\"></td></tr></table></td></tr>");

		wh.writeChildren(self);

		wh.write("<tr style=\"display:none\" id=\"")
			.write(self.getUuid()).writeln("!child\"><td></td></tr>"); //bookmark for adding children

		/* postfix row */		
		wh.writeln("<tr><td align=\"right\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
			.write("<tr id=\"").write(self.getUuid()).write("!last\">")
			.write("<td class=\"").write(look).writeln("last1\"></td></tr>")
			.write("<tr><td class=\"").write(look).writeln("last2\"></td></tr>")
			.writeln("</table></td></tr>");

		wh.writeln("</table></td>");
	}
}
