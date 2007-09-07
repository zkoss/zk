/* FooterDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 10:42:39 AM , Created by jumperchen
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
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Footer;

/*
 * {@link Footer}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class FooterDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Footer self = (Footer) comp;
		final String uuid = self.getUuid();
		wh.write("<td id=\"").write(uuid).write("\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write(self.getImgTag());
		new Out(out).setValue(self.getLabel()).render();
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			((Component) it.next()).redraw(out);
		}
		wh.writeln("</td>");
	}

}
