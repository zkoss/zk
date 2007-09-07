/* TabpanelAccordion.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2007 8:07:46 PM , Created by robbiecheng
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

import org.zkoss.lang.Strings;
import org.zkoss.xel.fn.StringFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;

/**
 * {@link Tabpanel}'s accordion mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */

public class TabpanelAccordion implements ComponentRenderer {
	/**
<c:set var=\"self\" value=\"${requestScope.arg.self}\"/>
<c:set var=\"tab\" value=\"${self.linkedTab}\"/>
<c:set var=\"suffix\" value=\"-sel\" if=\"${tab.selected}\"/>
<c:set var=\"suffix\" value=\"-uns\" unless=\"${tab.selected}\"/>
<tr id=\"${self.uuid}\"><%-- no exteriorAttribute here because tab.js controls it diff --%>
<td>
<table id=\"${tab.uuid}\"${tab.outerAttrs}${tab.innerAttrs} z.sel=\"${tab.selected}\" z.type=\"zul.tab.Tab\" z.box=\"${tab.tabbox.uuid}\" z.panel=\"${self.uuid}\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">
<c:if test=\"${!empty self.tabbox.panelSpacing and self.index!=0}\"><tr height=\"${self.tabbox.panelSpacing}\"><td></td></tr>
</c:if>
<tr>
	<td class=\"${c:cat('tabaccd-3d-tl',suffix)}\"></td>
	<td colspan=\"${tab.closable?4:3}\" class=\"${c:cat('tabaccd-3d-tm',suffix)}\"></td>
	<td class=\"${c:cat('tabaccd-3d-tr',suffix)}\"></td>
</tr>
<tr height=\"${empty tab.height ? '22':tab.height}\">
	<td class=\"${c:cat('tabaccd-3d-ml',suffix)}\"></td>
	<td width=\"3\" class=\"${c:cat('tabaccd-3d-mm',suffix)}\"></td>
	<td align=\"left\" class=\"${c:cat('tabaccd-3d-mm',suffix)}\"><a href=\"javascript:;\" id=\"${tab.uuid}!a\">${tab.imgTag}<c:out value=\"${tab.label}\"/></a></td>
<c:if test=\"${tab.closable}\">
	<td width=\"11\" align=\"right\" class=\"${c:cat('tabaccd-3d-mm',suffix)}\"><img id=\"${tab.uuid}!close\" src=\"${c:encodeURL('~./zul/img/close-off.gif')}\"/></td>
</c:if>
	<td width=\"3\" class=\"${c:cat('tabaccd-3d-mm',suffix)}\"></td>
	<td class=\"${c:cat('tabaccd-3d-mr',suffix)}\"></td>
</tr>
<tr>
	<td colspan=\"${tab.closable?6:5}\" class=\"tabaccd-3d-b\"></td>
</tr>
</table>
	<div id=\"${self.uuid}!real\"${self.outerAttrs}${self.innerAttrs}><div id=\"${self.uuid}!cave\">
<c:forEach var=\"child\" items=\"${self.children}\">
	${z:redraw(child, null)}
</c:forEach>
	</div></div>
</td>
</tr>
	 */

	public void render(Component comp, Writer out) throws IOException {	
		final WriterHelper wh = new WriterHelper(out);
		final Execution exec = Executions.getCurrent();
		final Tabpanel self = (Tabpanel) comp;
		final Tab tab = self.getLinkedTab();
		final String suffix = (self.isSelected()) ? "-sel" : "-uns";
		final int colspan1 = (tab.isClosable()) ? 4:3;
		final int colspan2 = (tab.isClosable()) ? 6:5;
		final String height = Strings.isBlank(self.getHeight()) ? "22" : self.getHeight(); 		
		
		wh.write("<tr id=\"" + self.getUuid() +"\">"); //no exteriorAttribute here because tab.js controls it diff
		wh.write("<td>");
		wh.write("<table id=\"" + tab.getUuid()+"\""+ tab.getOuterAttrs() + tab.getInnerAttrs() 
				+ " z.sel=\""+tab.isSelected()+"\" z.type=\"zul.tab.Tab\" z.box=\"" 
				+ tab.getTabbox().getUuid()+ "\" z.panel=\"" + self.getUuid() 
				+"\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");		
		if(!Strings.isBlank(self.getTabbox().getPanelSpacing()) && self.getIndex() != 0)
			wh.write("<tr height=\""+ self.getTabbox().getPanelSpacing()+ "\"><td></td></tr>");
		
		wh.write("<tr>");
			wh.write("<td class=\"").write("tabaccd-3d-tl").write(suffix).write("\"></td>");			
			wh.write("<td colspan=\"" + colspan1 + "\" class=\"").write("tabaccd-3d-tm").write(suffix).write("\"></td>");
			wh.write("<td class=\"").write("tabaccd-3d-tr").write(suffix).write("\"></td>");			
		wh.write("</tr>");	
	    
		wh.write("<tr height=\"" + height + "\">");		
			wh.write("<td class=\"").write("tabaccd-3d-ml").write(suffix).write("\"></td>");
			wh.write("<td width=\"3\" class=\"").write("tabaccd-3d-mm").write(suffix).write("\"></td>");
			wh.write("<td align=\"left\" class=\"").write("tabaccd-3d-mm").write(suffix).write("\"><a href=\"javascript:;\"")
			.write(" id=\"").write(tab.getUuid()).write("!a\">");			
			if (!Strings.isBlank(tab.getImgTag()))
				wh.write(tab.getImgTag());
			if (!Strings.isBlank(tab.getLabel()))
				new Out(out).setValue(tab.getLabel()).render();
			wh.write( "</a></td>");			

		if(tab.isClosable()){
			wh.write("<td width=\"11\" align=\"right\" class=\"").write("tabaccd-3d-mm").write(suffix) 
			.write("\"><img id=\"").write(self.getUuid()).write("!close\" src=\"")
			.write(exec.encodeURL("~./zul/img/close-off.gif")).write("\"/></td>");
		}
			wh.write("<td width=\"3\" class=\"").write("tabaccd-3d-mm").write(suffix).write("\"></td>");	
			wh.write("<td class=\"").write("tabaccd-3d-mr").write(suffix).write("\"></td>");
		wh.write("</tr>");	
		wh.write("<tr>");
			wh.write("<td colspan=\"" + colspan2 + "\" class=\"tabaccd-3d-b\"></td>");			
		wh.write("</tr>");
		wh.write("</table>");
			wh.write("<div id=\"" + self.getUuid() +"!real\""+ self.getOuterAttrs()+ self.getInnerAttrs() + "><div id=\"" + self.getUuid() +"!cave\">");
		for (Iterator it = self.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			child.redraw(out);
		}
		wh.write("</div></div>");
		wh.write("</td>");
		wh.write("</tr>");

	}

}
