/* ProgressmeterDefault.java

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Progressmeter;

/**
 * {@link Progressmeter }'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ProgressmeterDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Progressmeter self = (Progressmeter)comp;
	
		final Execution exec = Executions.getCurrent();
		
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.widget.PMeter\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln(">");
		wh.write("<img id=\"").write(self.getUuid()).write("!img\" style=\"height:10px\" src=\"");
		wh.write(exec.encodeURL("~./zk/img/prgmeter.gif")).write("\" />");
		wh.write("</div>");
		// <div id="${self.uuid}" z.type="zul.widget.PMeter"${self.outerAttrs}${self.innerAttrs}><img id="${self.uuid}!img" style="height:10px" src="${c:encodeURL('~./zk/img/prgmeter.gif')}"/></div>

	}

}
