/* ImagemapDefault.java

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

import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Imagemap;

/**
 * {@link Imagemap}'s default and alphafix mold.
 * @author Jeff Liu
 * @since 3.0.0
 */
public class ImagemapDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Imagemap self = (Imagemap)comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		
		wh.write("<span id=\"").write(uuid).write("\" z.type=\"zul.widget.Map\" z.cave=\"").write(uuid).write("_map\"").write(self.getOuterAttrs()).writeln(">");
		wh.write("<a href=\"").write(exec.encodeURL("~./zul/html/imagemap-done.dsp"));
		wh.write("?").write(uuid).write("\" target=\"zk_hfr_\">");
		wh.write("<img id=\"").write(self.getUuid()).write("!real\" ismap=\"ismap\"").write(self.getInnerAttrs()).writeln("/></a>");
		wh.write("<map name=\"").write(uuid).write("_map\" id=\"").write(uuid).write("_map\">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			ZkFns.redraw(child, out);
		}
		wh.writeln("</map></span>");
		/*
		<span id="${self.uuid}" z.type="zul.widget.Map" z.cave="${self.uuid}_map"${self.outerAttrs}>
		<a href="${c:encodeURL('~./zul/html/imagemap-done.dsp')}?${self.uuid}" target="zk_hfr_">
		<img id="${self.uuid}!real" ismap="ismap"${self.innerAttrs}/></a>
		<map name="${self.uuid}_map" id="${self.uuid}_map">
			<c:forEach var="child" items="${self.children}">
			${z:redraw(child, null)}
			</c:forEach>
		</map>
		</span>
		*/
	}

}
