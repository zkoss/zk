/* TimeboxDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 12:26:24 PM , Created by robbiecheng
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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Timebox;

/**
 * {@link Timebox}'s default mold.
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TimeboxDefault implements ComponentRenderer {	
	
	/**
	 * <c:set var="self" value="${requestScope.arg.self}"/> <%-- z.combo means
	 * an input with addition buttons --%> <span
	 * id="${self.uuid}"${self.outerAttrs} z.type="zul.tb.Tmbox" z.combo="true">
	 * <input id="${self.uuid}!real" autocomplete="off"${self.innerAttrs}/>
	 * <span id="${self.uuid}!btn" class="rbtnbk"> <img
	 * src="${c:encodeURL(self.image)}"${self.buttonVisible?'':'
	 * style="display:none"'}/> </span> </span>
	 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Execution exec = Executions.getCurrent();		
		final Timebox self = (Timebox) comp;
		final String uuid = self.getUuid();
		
		wh.write("<span id=\"" + uuid + self.getOuterAttrs() + "\" z.type=\"zul.tb.Tmbox\" z.combo=\"true\">");		
		wh.write("<input id=\"" + self.getUuid() + "!real\" autocomplete=\"off\"" + self.getInnerAttrs() + "/>");		
		wh.write("<span id=\"" + self.getUuid() + "!btn\" class=\"rbtnbk\">");		
		wh.write("<img src=\"" + exec.encodeURL(self.getImage()) + "\"");
		if (!self.isButtonVisible())
			wh.write(" style=\"display:none\"");
		wh.write("/>");
		wh.writeln("</span>");
		wh.writeln("</span>");
	}



}
