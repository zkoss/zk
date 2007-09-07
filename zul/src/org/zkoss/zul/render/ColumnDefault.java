/* ColumnDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 10:48:59 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Column;

/*
 * {@link Column}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class ColumnDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Column self = (Column) comp;
		final String uuid = self.getUuid();
		wh.write("<th id=\"").write(uuid).write("\"").write(" z.type=\"Col\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write(self.getImgTag());
		RenderFns.getOut(out).setValue(self.getLabel()).render();
		wh.writeln("</th>");
	}

}
