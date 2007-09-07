/* TabboxVertical.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 7:50:10 PM , Created by robbiecheng
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

import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Tabbox;

/**
 * {@link Tabbox}'s vertical mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */

public class TabboxVertical implements ComponentRenderer {

	/**
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.tabs="${self.tabs.uuid}" z.type="zul.tab.Tabbox" border="0" cellpadding="0" cellspacing="0">
<tr valign="top">
${z:redraw(self.tabs, null)}
${z:redraw(self.tabpanels, null)}
</tr>
</table>
	 */

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Tabbox self = (Tabbox) comp;	
		
		wh.write("<table id=\"" + self.getUuid() + "\"" + self.getOuterAttrs() + self.getInnerAttrs() + " z.tabs=\"" 
				+ self.getTabs().getUuid() + "\" z.type=\"zul.tab.Tabbox\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");		
		wh.write("<tr valign=\"top\">");
		ZkFns.redraw(self.getTabs(), null);
	    ZkFns.redraw(self.getTabpanels(), null);		
		wh.write("</tr>");
		wh.write("</table>");

	}

}
