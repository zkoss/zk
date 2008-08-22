/* TabsDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 6:21:35 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;

/**
 * {@link Tabs}'s default mold.
 * It forwards to {@link TabsDefaultV} if the orient is vertical.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class Tabs2Default implements ComponentRenderer {
	private final Tabs2DefaultV _vtabs = new Tabs2DefaultV();

	public void render(Component comp, Writer out) throws IOException {
		final Tabs self = (Tabs)comp;
		final Tabbox tabbox = self.getTabbox();

		if("vertical".equals(tabbox.getOrient())) {
			_vtabs.render(comp, out);
			return;
		}

		final SmartWriter wh = new SmartWriter(out);		
		final String look = tabbox.getTabLook() + '-';		
		final String scroll = tabbox.isTabscroll() ? "scrolltabs" : "tabs" ;	
			String uuid = self.getUuid();
			wh.write("<div id=\""+uuid+"\" class=\"").write(look+scroll).write("\" z.type=\"zul.tab2.Tabs2\"")
			    .write(self.getOuterAttrs()).write(self.getInnerAttrs()).writeln('>');
			    wh.write("<div id=\""+uuid+"!right").writeln("\" class=\"" + look + "scrollright\" ></div>");
				wh.write("<div id=\""+uuid+"!left").writeln("\" class=\"" + look + "scrollleft\" ></div>");
			    wh.write("<div id=\""+uuid+"!header\"").writeln(" class=\""+look+"head\" >");
				    wh.writeln("<ul id=\""+uuid+"!cave\" class=\""+look+"ul\" >");							    	
				    	wh.writeChildren(self);			    	
				    	wh.writeln("<li id=\""+uuid+"!edge\" class=\""+look+"edge\" ></li>");
				    	wh.writeln("<div id=\""+uuid+"!clear\" class=\""+look+"clear\"></div>");
				    wh.writeln("</ul>");
			    wh.writeln("</div>");
			    wh.writeln("<div id=\""+uuid+"!line\" class=\""+look+"space\" ></div>");	
			wh.writeln("</div>");
	}
}
