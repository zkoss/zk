/* GroupboxDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 13:18:53     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.render;

import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Groupbox;

/**
 * {@link Groupbox}'s default mold.
 *
 * @author dennis.chen
 * @since 3.0.0
 */
public class GroupboxDefault implements ComponentRenderer {
	public void render(Component comp, Writer out) throws IOException {
		final Groupbox self = (Groupbox)comp;
		final Caption caption = self.getCaption();
		out.write("<fieldset id=\"");
		out.write(self.getUuid());
		out.write("\"");
		out.write(self.getOuterAttrs());
		out.write(self.getInnerAttrs());
		out.write(">");
		if(caption!=null){
			caption.redraw(out);
		}
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child != caption)
				child.redraw(out);
		}
		out.write("</fieldset>\n");
	}
}
