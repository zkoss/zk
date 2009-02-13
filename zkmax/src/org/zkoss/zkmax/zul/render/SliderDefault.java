/* SliderDefault.java

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
import org.zkoss.zul.Slider;

/**
 * {@link Slider}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class SliderDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Slider self = (Slider)comp;
		final String uuid = self.getUuid();

		String scls = self.getSclass();
		scls = scls != null && scls.length() != 0 ? scls + '-':
			"sphere".equals(self.getMold()) ? "slidersph-": "slider-";

		wh.write("<table id=\"").write(uuid).write("\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.writeln(" z.type=\"zul.sld.Sld\" cellpadding=\"0\" cellspacing=\"0\" >")
			.write("<tr><td class=\"").write(scls).writeln("bkl\"></td>")
			.write("<td class=\"").write(scls).write("bk\"><span id=\"").write(uuid)
			.write("!btn\" class=\"").write(scls).write("btn\" title=\"")
			.write(self.getCurpos()).writeln("\"></span></td>")
			.write("<td class=\"").write(scls)
			.write("bkr\"></td></tr>\n</table>");
	}

}
