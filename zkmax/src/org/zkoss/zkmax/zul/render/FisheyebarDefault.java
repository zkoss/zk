/* FisheyebarDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 10, 2008 4:19:05 PM , Created by jumperchen
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
import org.zkoss.zkex.zul.Fisheyebar;

/**
 * {@link Fisheyebar}'s default mold.
 * @author jumperchen
 * @since 3.5.0
 */
public class FisheyebarDefault  implements ComponentRenderer {

	public void render(Component cmp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Fisheyebar self = (Fisheyebar)cmp;
		final String uuid = self.getUuid();
		
		wh.write("<div id=\"").write(uuid).write("\" z.type=\"zkex.zul.fisheye.Fisheyebar\"")
		  .write(self.getOuterAttrs()).write(self.getInnerAttrs()).write("><div id=\"")
		  .write(uuid).write("!cave\" class=\"").write(self.getZclass()).write("-inner\">")
		  .writeChildren(self)
		  .writeln("</div></div>");
	}

}
