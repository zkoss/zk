<%--
hbox.dsp

{{IS_NOTE
	$Id: hbox.dsp,v 1.7 2006/04/18 02:25:07 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jun 21 09:01:33     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} cellpadding="0" cellspacing="0">
<tr valign="top">
	<c:forEach var="child" items="${self.children}">
 <td id="${child.uuid}!chdextr"${c:attr('width',c:isInstance('com.potix.zk.ui.HtmlBasedComponent', child) ? child.width: null)}${self.childExteriorAttrs}>${u:redraw(child, null)}</td>
	</c:forEach>
 <td style="display:none" id="${self.uuid}!child"></td><%-- bookmark for adding child (when insertHTMLBeforeEnd not appliable) --%>
</tr>
</table>
