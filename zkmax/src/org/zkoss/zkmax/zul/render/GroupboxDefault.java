/* GroupboxDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 13:18:53     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Groupbox;

/**
 * {@link Groupbox}'s default mold.
 *
 * @author dennis.chen
 * @since 3.0.0
 */
public class GroupboxDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Groupbox self = (Groupbox) comp;
		wh.write("<fieldset id=\"").write(self.getUuid()).write("\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		wh.write(self.getCaption());

		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			if(self.getCaption() != child)
				child.redraw(out);
		}

		wh.writeln("</fieldset>");
	}
}
