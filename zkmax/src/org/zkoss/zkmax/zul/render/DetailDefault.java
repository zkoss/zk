/* DetailDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 30, 2008 11:14:28 AM , Created by jumperchen
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
import org.zkoss.zul.Detail;

/**
 * {@link Detail}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class DetailDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Detail self = (Detail) comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zkex.zul.detail.Detail\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<div id=\"").write(uuid).write("!img\" class=\"").write(self.getZclass())
			.write("-img\"></div><div id=\"").write(uuid).write("!cave\" style=\"").write(self.getContentStyle())
			.write("\" class=\"").write(self.getContentSclass()).write("\">")
			.writeChildren(self).write("</div></div>");
	}
}
