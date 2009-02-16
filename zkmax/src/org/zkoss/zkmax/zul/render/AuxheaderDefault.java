/* AuxheaderDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 24 11:49:32     2007, Created by tomyeh
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
import org.zkoss.zk.ui.render.Out;

import org.zkoss.zul.Auxheader;

/**
 * {@link Auxheader}'s default mold.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuxheaderDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Auxheader self = (Auxheader)comp;
		wh.write("<th id=\"").write(self.getUuid()).write('"')
		  .write(self.getOuterAttrs()).write(self.getInnerAttrs())
		  .write("><div id=\"").write(self.getUuid())
		  .write("!cave\" class=\"").write(self.getZclass()).write("-cnt\">")
		  .write(self.getImgTag());
		new Out(self.getLabel()).render(out);
		wh.writeChildren(self).writeln("</div></th>");
	}
}
