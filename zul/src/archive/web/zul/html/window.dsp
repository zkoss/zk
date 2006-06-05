<%--
window.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		zk_idsp:
			An ID space (au.js)
		zk_autoz:
			Automatically adjust z-index onmousedown (au.js)
	History:
		Tue May 31 19:37:23     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" zk_type="zul.html.widget.Wnd" zk_idsp="true" zk_autoz="true"${self.outerAttrs}${self.innerAttrs}>
<c:if test="${!empty self.caption or !empty self.title}">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr id="${self.uuid}!caption" class="title">
<td><c:if test="${!empty self.caption}">${u:redraw(self.caption, null)}</c:if><c:if test="${empty self.caption}"><c:out value="${self.title}"/></c:if></td>
<c:if test="${self.closable}">
	<td width="16"><img id="${self.uuid}!img" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
</c:if>
</tr>
<tr height="5px"><td></td></tr>
</table>
</c:if><%-- !empty self... --%>
<c:forEach var="child" items="${self.children}">
<c:if test="${self.caption != child}">${u:redraw(child, null)}</c:if>
</c:forEach>
</div>
