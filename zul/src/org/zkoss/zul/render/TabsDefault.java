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
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;

/**
 * {@link Tabs}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TabsDefault implements ComponentRenderer {
	
	/**
tabs.dsp

<thead id=\"${self.uuid}\" z.type=\"zul.tab.Tabs\"${self.outerAttrs}${self.innerAttrs}>

<tr><td>
<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">
<tr valign=\"bottom\">

<%-- prefix column  --%>
<td><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">
<tr>
	<td class=\"tab-3d-first\"></td>
</tr>
</table></td>

	<c:forEach var=\"child\" items=\"${self.children}\">
	${z:redraw(child, null)}
	</c:forEach>

<td style=\"display:none\" id=\"${self.uuid}!child\"></td><%-- bookmark for adding children --%>

<%-- postfix column  --%>
<td><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">
<tr>
	<td class=\"tab-3d-last1\" id=\"${self.uuid}!last\"></td>
	<td class=\"tab-3d-last2\"></td>
</tr>
</table></td>
	</tr>
</table>
</td></tr>

</thead>

vtabs.dsp

<td id=\"${self.uuid}\" align=\"right\" z.type=\"zul.tab.Tabs\"${self.outerAttrs}${self.innerAttrs}>
<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">

<%-- prefix row  --%>
<tr>
<td align=\"right\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">
<tr>
	<td class=\"tab-v3d-first\"></td>
</tr>
</table></td>
</tr>

	<c:forEach var=\"child\" items=\"${self.children}\">
	${z:redraw(child, null)}
	</c:forEach>

<tr style=\"display:none\" id=\"${self.uuid}!child\"><td></td></tr><%-- bookmark for adding children --%>

<%-- postfix row --%>
<tr>
<td align=\"right\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">
<tr id=\"${self.uuid}!last\">
	<td class=\"tab-v3d-last1\"></td>
</tr>
<tr>
	<td class=\"tab-v3d-last2\"></td>
</tr>
</table></td>
</tr>

</table>
</td>

	 */
	
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Tabs self = (Tabs)comp;
		
		if(((Tabbox)self.getParent()).getOrient().equals("vertical")){
			wh.write("<td id=\"" + self.getUuid() + "\" align=\"right\" z.type=\"zul.tab.Tabs\"" + self.getOuterAttrs() + self.getInnerAttrs() + ">");
			wh.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
			
			/**
			 * prefix row
			 */
			wh.write("<tr>");
			wh.write("<td align=\"right\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr>");
				wh.write("<td class=\"tab-v3d-first\"></td>");
			wh.write("</tr>");
			wh.write("</table></td>");
			wh.write("</tr>");

			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				child.redraw(out);
			}

			wh.write("<tr style=\"display:none\" id=\""+ self.getUuid() +"!child\"><td></td></tr>"); //bookmark for adding children

			/**
			 * postfix row
			 */		
			wh.write("<tr>");
			wh.write("<td align=\"right\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr id=\""+ self.getUuid() +"!last\">");
				wh.write("<td class=\"tab-v3d-last1\"></td>");
			wh.write("</tr>");
			wh.write("<tr>");
				wh.write("<td class=\"tab-v3d-last2\"></td>");
			wh.write("</tr>");
			wh.write("</table></td>");
			wh.write("</tr>");

			wh.write("</table>");
			wh.write("</td>");
			
		}
		else{
			wh.write("<thead id=\"" + self.getUuid() + "\" z.type=\"zul.tab.Tabs\"" + self.getOuterAttrs() + self.getInnerAttrs() + ">");
			wh.write("<tr><td>");
			wh.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr valign=\"bottom\">");
			
			/**
			 * prefix column
			 */
			wh.write("<td><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr><td class=\"tab-3d-first\"></td></tr>");
			wh.write("</table></td>");				

			for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				child.redraw(out);
			}
			
			wh.write("<td style=\"display:none\" id=\"" + self.getUuid() + "!child\"></td>"); //bookmark for adding children
			
			/**
			 * postfix column
			 */		
			wh.write("<td><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			wh.write("<tr>");
			wh.write("<td class=\"tab-3d-last1\" id=\"" + self.getUuid() +"!last\"></td>");
			wh.write("<td class=\"tab-3d-last2\"></td>");
			wh.write("</tr>");
			wh.write("</table></td>");
			wh.write("</tr>");
			wh.write("</table>");
			wh.write("</td></tr>");
			wh.write("</thead>");
			
		}
		
	}
}
