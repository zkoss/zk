<%--
caption.dsp

{{IS_NOTE
	Purpose:
		Used with groupbox.
	Description:
		
	History:
		Tue Oct 11 15:54:37     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" z.type="zul.widget.Capt"${self.outerAttrs}${self.innerAttrs} width="100%" border="0" cellpadding="0" cellspacing="0">
<tr valign="middle">
	<td align="left" class="caption">${self.imgTag}<c:out value="${self.compoundLabel}" nbsp="true"/></td><%-- bug 1688261: nbsp is required --%>
	<td align="right" class="caption" id="${self.uuid}!cave"><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></td>
<c:if test="${self.closableVisible}">
	<td width="16"><img id="${self.parent.uuid}!close" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
</c:if>
</tr>
</table>
