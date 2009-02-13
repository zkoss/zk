/* AuxheadDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 24 11:42:31     2007, Created by tomyeh
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
import org.zkoss.zul.Auxhead;

/**
 * {@link Auxhead}'s default mold.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuxheadDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Auxhead self = (Auxhead)comp;
		wh.write("<tr id=\"").write(self.getUuid()).write('"')
		  .write(self.getOuterAttrs()).write(self.getInnerAttrs())
		  .write(" align=\"left\">")
		  .writeChildren(self)
		  .writeln("</tr>");
	}
}
