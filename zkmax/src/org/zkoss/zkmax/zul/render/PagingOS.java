/* PagingOS.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2008 1:59:34 PM , Created by jumperchen
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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Paging;

/**
 * {@link Paging}'s default mold.
 * @author jumperchen
 * @since 3.5.2
 */
public class PagingOS implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Paging self = (Paging)comp;
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.pg.PgOS\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write(self.getInnerTags());
		wh.write("</div>");
	}
}
