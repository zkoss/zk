/* ListheaderDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 6 2007, Created by Jeff.Liu
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
import org.zkoss.zul.Listheader;

/**
 * {@link Listheader}'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ListheaderDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Listheader self = (Listheader)comp;
		wh.write("<th id=\"").write(self.getUuid()).write("\" z.type=\"Lhr\"").write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		wh.write(self.getImgTag());
		new Out(out, self.getLabel()).render();
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			child.redraw(out);
		}
		wh.write("</th>");
		/*
		<th id="${self.uuid}" z.type="Lhr"${self.outerAttrs}${self.innerAttrs}>
		${self.imgTag}
		<c:out value="${self.label}"/>
		</th>
		*/
	}

}
