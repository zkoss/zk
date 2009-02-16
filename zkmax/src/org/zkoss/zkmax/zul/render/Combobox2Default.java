/* Combobox2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 6, 2008 8:57:53 AM , Created by jumperchen
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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Combobox;

/**
 * {@link Combobox}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.5.0
 */
public class Combobox2Default implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Combobox self = (Combobox) comp;
		final String uuid = self.getUuid();
		final String zcls = self.getZclass();
		final Execution exec = Executions.getCurrent();

		wh.write("<span id=\"").write(uuid).write("\"")
			.write(self.getOuterAttrs())
			.write(" z.type=\"zul.cb.Cmbox\" z.combo=\"true\"><input id=\"")
			.write(uuid).write("!real\" autocomplete=\"off\"")
			.write(" class=\"").write(zcls).write("-inp\"")
			.write(self.getInnerAttrs()).write("/><span id=\"")
			.write(uuid).write("!btn\" class=\"").write(zcls).write("-btn\"");

		if (!self.isButtonVisible())
			wh.write(" style=\"display:none\"");

		wh.write("><img class=\"").write(zcls).write("-img\" onmousedown=\"return false;\"");
		wh.write(" src=\"").write(exec.encodeURL("~./img/spacer.gif")).write("\"")
			.write("\"/></span><div id=\"").write(uuid).write("!pp\" class=\"")
			.write(zcls).write("-pp\" style=\"display:none\" tabindex=\"-1\">")
			.write("<table id=\"").write(uuid)
			.write("!cave\" cellpadding=\"0\" cellspacing=\"0\">")
			.writeChildren(self)
			.write("</table></div></span>");
	}
}
