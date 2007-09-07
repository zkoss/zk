/* TabDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 3:59:51 PM , Created by robbiecheng
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

import org.zkoss.lang.Strings;
import org.zkoss.xel.fn.StringFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Tab;

/**
 * {@link Tab}'s vertical mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 * 
 */
public class TabDefault implements ComponentRenderer {

	/**
	 * tab.dsp
	 * 
	 * <c:set var="self" value="${requestScope.arg.self}"/> <c:set var="suffix"
	 * value="-sel" if="${self.selected}"/> <c:set var="suffix" value="-uns"
	 * unless="${self.selected}"/>
	 * <td id="${self.uuid}" z.type="Tab"${self.outerAttrs}${self.innerAttrs} z.sel="${self.selected}" z.box="${self.tabbox.uuid}" z.panel="${self.linkedPanel.uuid}">
	 * 
	 * <table border="0" cellpadding="0" cellspacing="0" width="100%">
	 * <tr>
	 * <td class="${c:cat('tab-3d-tl',suffix)}"></td>
	 * <td colspan="${self.closable ?4:3}" class="${c:cat('tab-3d-tm',suffix)}"></td>
	 * <td class="${c:cat('tab-3d-tr',suffix)}"></td>
	 * </tr>
	 * <tr height="${empty self.height ? '22': self.height}">
	 * <td class="${c:cat('tab-3d-ml',suffix)}"></td>
	 * <td width="3" class="${c:cat('tab-3d-mm',suffix)}"></td>
	 * <td align="center" class="${c:cat('tab-3d-mm',suffix)}"><a
	 * href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out
	 * value="${self.label}"/></a></td>
	 * <c:if test="${self.closable}">
	 * <td width="11" align="right" class="${c:cat('tab-3d-mm',suffix)}"><img
	 * id="${self.uuid}!close"
	 * src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
	 * </c:if>
	 * <td width="3" class="${c:cat('tab-3d-mm',suffix)}"></td>
	 * <td class="${c:cat('tab-3d-mr',suffix)}"></td>
	 * </tr>
	 * <tr>
	 * <td class="${c:cat('tab-3d-bl',suffix)}"></td>
	 * <td colspan="${self.closable ?4:3}" class="${c:cat('tab-3d-bm',suffix)}"></td>
	 * <td class="${c:cat('tab-3d-br',suffix)}"></td>
	 * </tr>
	 * </table> </td>
	 *
	 *
	 * vtab.dsp
	 * 
	 * <c:set var=\"self\" value=\"${requestScope.arg.self}\"/> <c:set
	 * var=\"suffix\" value=\"-sel\" if=\"${self.selected}\"/> <c:set
	 * var=\"suffix\" value=\"-uns\" unless=\"${self.selected}\"/> <c:set
	 * var=\"wd\" value=\" width=\\"${self.width}\\"\" unless=\"${empty
	 * self.width}\"/>
	 * 
	 * <tr id=\"${self.uuid}\" z.type=\"Tab\"${self.outerAttrs} z.sel=\"${self.selected}\" z.box=\"${self.tabbox.uuid}\" z.panel=\"${self.linkedPanel.uuid}\">
	 * 
	 * <td align=\"right\"${wd}><table border=\"0\" cellpadding=\"0\"
	 * cellspacing=\"0\" width=\"100%\">
	 * <tr>
	 * <td class=\"${c:cat('tab-v3d-tl',suffix)}\"></td>
	 * <td colspan=\"3\" class=\"${c:cat('tab-v3d-tm',suffix)}\"></td>
	 * <td class=\"${c:cat('tab-v3d-tr',suffix)}\"></td>
	 * </tr>
	 * 
	 * <tr height=\"22\">
	 * <td class=\"${c:cat('tab-v3d-ml',suffix)}\"></td>
	 * <td width=\"3\" class=\"${c:cat('tab-v3d-mm',suffix)}\"></td>
	 * <td align=\"center\" class=\"${c:cat('tab-v3d-mm',suffix)}\" id=\"${self.uuid}!real\"${self.innerAttrs}><a
	 * href=\"javascript:;\" id=\"${self.uuid}!a\">${self.imgTag}<c:out
	 * value=\"${self.label}\"/></a></td>
	 * <td width=\"3\" class=\"${c:cat('tab-v3d-mm',suffix)}\"></td>
	 * <td class=\"${c:cat('tab-v3d-mr',suffix)}\"></td>
	 * </tr>
	 * 
	 * <c:if test=\"${self.closable}\">
	 * <tr height=\"8\">
	 * <td class=\"${c:cat('tab-v3d-ml',suffix)}\"></td>
	 * <td width=\"3\" class=\"${c:cat('tab-v3d-mm',suffix)}\"></td>
	 * <td align=\"center\" valign=\"bottom\" class=\"${c:cat('tab-v3d-mm',suffix)}\"><img
	 * id=\"${self.uuid}!close\"
	 * src=\"${c:encodeURL('~./zul/img/close-off.gif')}\"/></td>
	 * <td width=\"3\" class=\"${c:cat('tab-v3d-mm',suffix)}\"></td>
	 * <td class=\"${c:cat('tab-v3d-mr',suffix)}\"></td>
	 * </tr>
	 * </c:if>
	 * 
	 * <tr>
	 * <td class=\"${c:cat('tab-v3d-bl',suffix)}\"></td>
	 * <td colspan=\"3\" class=\"${c:cat('tab-v3d-bm',suffix)}\"></td>
	 * <td class=\"${c:cat('tab-v3d-br',suffix)}\"></td>
	 * </tr>
	 * 
	 * </table></td>
	 * </tr>
	 */

	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Execution exec = Executions.getCurrent();
		final Tab self = (Tab) comp;
		final String suffix = (self.isSelected()) ? "-sel" : "-uns";

		if (self.getTabbox().getOrient().equals("vertical")) {
			
			wh.write("<tr id=\"" + self.getUuid() + "\" z.type=\"Tab\"" + self.getOuterAttrs() + self.getInnerAttrs()
					+ "z.sel=\"" + self.isSelected() + "\" z.box=\"" + self.getTabbox().getUuid() + "\" z.panel=\"" 
					+ self.getLinkedPanel().getUuid() + "\">");		
			
			wh.write("<td align=\"right\"");
			if (!Strings.isBlank(self.getWidth()))
				wh.write(" width=\"" + self.getWidth()+ "\"");
			wh.write("><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");
			wh.write("<tr>");			
				wh.write("<td class=\"" + StringFns.cat("tab-v3d-tl",suffix) + "\"></td>");
				wh.write("<td colspan=\"3\" class=\"" + StringFns.cat("tab-v3d-tm",suffix) + "\"></td>");
				wh.write("<td class=\"" + StringFns.cat("tab-v3d-tr",suffix) + "\"></td>");			
			wh.write("</tr>");

			wh.write("<tr height=\"22\">");
				wh.write("<td class=\"" + StringFns.cat("tab-v3d-ml",suffix) + "\"></td>");
				wh.write("<td width=\"3\" class=\"" + StringFns.cat("tab-v3d-mm",suffix) + "\"></td>");
				wh.write("<td align=\"center\" class=\"" + StringFns.cat("tab-v3d-mm",suffix) + "\" id=\"" + self.getUuid()+"!real\"" +self.getInnerAttrs()+"><a href=\"javascript:;\"" +
					" id=\"" + self.getUuid() + "!a\">");
				if (!Strings.isBlank(self.getImgTag()))
					wh.write(self.getImgTag());
				if (!Strings.isBlank(self.getLabel()))
				RenderFns.getOut(out).setValue(self.getLabel()).render();
				wh.write( "</a></td>");		
				wh.write("<td width=\"3\" class=\"" + StringFns.cat("tab-v3d-mm",suffix) + "\"></td>");	
				wh.write("<td class=\"" + StringFns.cat("tab-v3d-mr",suffix) + "\"></td>");
			wh.write("</tr>");		
			
			if(self.isClosable()){
				wh.write("<tr height=\"8\">");
				wh.write("<td class=\"" + StringFns.cat("tab-v3d-ml",suffix) + "\"></td>");
				wh.write("<td width=\"3\" class=\"" + StringFns.cat("tab-v3d-mm",suffix) + "\"></td>");
				wh.write("<td width=\"11\" align=\"center\" valign=\"buttom\" class=\"" + StringFns.cat("tab-v3d-mm",suffix) + 
						"\"><img id=\""+ self.getUuid() + "!close\" src=\"" + exec.encodeURL("~./zul/img/close-off.gif") + "\"/></td>");
				wh.write("<td class=\"" + StringFns.cat("tab-v3d-mr",suffix) + "\"></td>");
				wh.write("</tr>");			
			}
			
			wh.write("<tr>");		
			wh.write("<td class=\"" + StringFns.cat("tab-v3d-bl",suffix) + "\"></td>");
			wh.write("<td colspan=\"3\" class=\"" + StringFns.cat("tab-v3d-bm",suffix) + "\"></td>");
			wh.write("<td class=\"" + StringFns.cat("tab-v3d-br",suffix) + "\"></td>");
			wh.write("</tr>");

			wh.write("</table></td>");
			wh.write("</tr>");

			
		}
		else{
			
			final int colspan = (self.isClosable()) ? 4 : 3;
			final String height = Strings.isBlank(self.getHeight()) ? "22"
					: self.getHeight();

			wh.write("<td id=\"" + self.getUuid() + "\" z.type=\"Tab\""
					+ self.getOuterAttrs() + self.getInnerAttrs() + "z.sel=\""
					+ self.isSelected() + "\" z.box=\""
					+ self.getTabbox().getUuid() + "\" z.panel=\""
					+ self.getLinkedPanel().getUuid() + "\">");
			wh.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");

			wh.write("<tr>");
			wh.write("<td class=\"" + StringFns.cat("tab-3d-tl", suffix)
					+ "\"></td>");
			wh.write("<td colspan=\"" + colspan + "\" class=\""
					+ StringFns.cat("tab-3d-tm", suffix) + "\"></td>");
			wh.write("<td class=\"" + StringFns.cat("tab-3d-tr", suffix)
					+ "\"></td>");
			wh.write("</tr>");

			wh.write("<tr height=\"" + height + "\">");
			wh.write("<td class=\"" + StringFns.cat("tab-3d-ml", suffix)
					+ "\"></td>");
			wh.write("<td width=\"3\" class=\""
					+ StringFns.cat("tab-3d-mm", suffix) + "\"></td>");
			wh.write("<td align=\"center\" class=\""
					+ StringFns.cat("tab-3d-mm", suffix)
					+ "\"><a href=\"javascript:;\"" + " id=\"" + self.getUuid()
					+ "!a\">");
			if (!Strings.isBlank(self.getImgTag()))
				wh.write(self.getImgTag());
			if (!Strings.isBlank(self.getLabel()))
				RenderFns.getOut(out).setValue(self.getLabel()).render();
			wh.write("</a></td>");

			// Bug 1780044: width cannot (and need not) be specified
			if (self.isClosable()) {
				wh.write("<td align=\"right\" class=\""
						+ StringFns.cat("tab-3d-mm", suffix) + "\"><img id=\""
						+ self.getUuid() + "!close\" src=\""
						+ exec.encodeURL("~./zul/img/close-off.gif")
						+ "\"/></td>");
			}
			wh.write("<td width=\"3\" class=\""
					+ StringFns.cat("tab-3d-mm", suffix) + "\"></td>");
			wh.write("<td class=\"" + StringFns.cat("tab-3d-mr", suffix)
					+ "\"></td>");
			wh.write("</tr>");

			wh.write("<tr>");
			wh.write("<td class=\"" + StringFns.cat("tab-3d-bl", suffix)
					+ "\"></td>");
			wh.write("<td colspan=\"" + colspan + "\" class=\""
					+ StringFns.cat("tab-3d-bm", suffix) + "\"></td>");
			wh.write("<td class=\"" + StringFns.cat("tab-3d-br", suffix)
					+ "\"></td>");
			wh.write("</tr>");
			wh.write("</table></td>");
			
		}
	}

}
