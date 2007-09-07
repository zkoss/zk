/* TreecolDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:18:28 AM , Created by robbiecheng
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
import org.zkoss.zul.Treecol;

/**
 * {@link Treecol}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreecolDefault implements ComponentRenderer {
/**
<c:set var="self" value="${requestScope.arg.self}"/>
<th id="${self.uuid}" z.type="Tcol"${self.outerAttrs}${self.innerAttrs}>${self.imgTag}<c:out value="${self.label}"/>
</th>

 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Treecol self = (Treecol) comp;
		
		wh.write("<th id=\"" + self.getUuid() + "\" z.type=\"Tcol\"" 
				+ self.getOuterAttrs() + self.getInnerAttrs());
		wh.write(self.getImgTag());
		wh.write(">");
		new Out(out, self.getLabel()).render();		
		wh.write("</th>");
		

	}

}
