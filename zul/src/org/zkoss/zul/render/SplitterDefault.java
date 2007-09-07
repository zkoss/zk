/* SplitterDefault.java

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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.WriterHelper;
import org.zkoss.zul.Splitter;
/**
 * {@link Splitter }'s default mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class SplitterDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Splitter self = (Splitter)comp;
		final Execution exec = Executions.getCurrent();
		wh.write("<div id=\"").write(self.getUuid()).write("\" z.type=\"zul.box.Splt\"");
		wh.write(" z.bgi=\"").write(exec.encodeURL("~./zul/img/splt/splt-v.gif")).write("\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		wh.write("<img id=\"").write(self.getUuid()).write("!btn\" style=\"display:none\" ");
		wh.write("src=\"").write(exec.encodeURL("~./zul/img/splt/colps-l.gif")).write("\" "); 
		wh.write("/>");
		wh.write("</div>");
		//<div id="${self.uuid}" z.type="zul.box.Splt" z.bgi="${c:encodeURL('~./zul/img/splt/splt-v.gif')}"${self.outerAttrs}${self.innerAttrs}><img id="${self.uuid}!btn" style="display:none" src="${c:encodeURL('~./zul/img/splt/colps-l.gif')}"/></div>
	}

}
