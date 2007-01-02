<%--
hbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 21 09:01:33     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" z.type="zul.box.Box"${self.outerAttrs}${self.innerAttrs} cellpadding="0" cellspacing="0">
<tr valign="${self.valign}" id="${self.uuid}!cave">
	<c:forEach var="child" items="${self.children}">
 <td id="${child.uuid}!chdextr"${c:attr('width',c:isInstance('org.zkoss.zk.ui.HtmlBasedComponent', child) ? child.width: null)}${c:isInstance('org.zkoss.zul.Splitter',child)?self.splitterExteriorAttrs:self.childExteriorAttrs}>${z:redraw(child, null)}</td>
	</c:forEach>
</tr>
</table>
