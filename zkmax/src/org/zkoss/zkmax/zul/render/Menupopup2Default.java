/* Menupopup2Default.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 28, 2008 12:15:33 PM , Created by jumperchen
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
import org.zkoss.zul.Menupopup;


/**
 * {@link Menupopup}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class Menupopup2Default implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Menupopup self = (Menupopup)comp;
		final Execution exec = Executions.getCurrent();
		final String tag = exec.isBrowser("ie") || exec.isBrowser("gecko") ? "a" : "button";
		final String uuid = self.getUuid();
		wh.write("<div id=\"").write(uuid).write('"')
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write('>')
			.write("<").write(tag).write(" id=\"").write(uuid).write("!a\" tabindex=\"-1\" onclick=\"return false;\"")
			.write(" href=\"javascript:;\" style=\"padding:0 !important; margin:0 !important; border:0 !important;	background: transparent !important;	font-size: 1px !important; width: 1px !important; height: 1px !important;-moz-outline: 0 none; outline: 0 none;	-moz-user-select: text; -khtml-user-select: text;\"></")
			.write(tag).write(">")
			.write("<ul class=\"").write(self.getZclass()).write("-cnt\" id=\"").write(uuid).writeln("!cave\">")
			.writeChildren(self)
			.write("</ul></div>");
	}
}

