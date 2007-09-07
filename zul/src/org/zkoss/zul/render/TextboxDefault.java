/* TextboxDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 12:06:52 PM , Created by robbiecheng
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
import org.zkoss.zul.Textbox;

/**
 * {@link Textbox}'s default mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class TextboxDefault implements ComponentRenderer{
	/**
	<c:set var="self" value="${requestScope.arg.self}"/>
	<c:choose>
	<c:when test="${self.multiline}"><%-- textarea doesn't support maxlength --%>
	<textarea id="${self.uuid}" z.type="zul.widget.Txbox"${self.outerAttrs}${self.innerAttrs}>${self.areaText}</textarea>
	</c:when>
	<c:otherwise>
	<input id="${self.uuid}" z.type="zul.widget.Txbox"${self.outerAttrs}${self.innerAttrs}/>
	</c:otherwise>
	</c:choose>
		 */

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Textbox self = (Textbox) comp;
		final String uuid = self.getUuid();

		//textarea doesn't support maxlength
		if (self.isMultiline()){
			wh.write("<textarea id=\"").write(uuid).write("\" z.type=\"zul.widget.Txbox\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">")
			.write(self.getAreaText()).write("</textarea>");
		}
		else{
			wh.write("<input id=\"").write(uuid).write("\" z.type=\"zul.widget.Txbox\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write("/>");
		}
	
	}
}
