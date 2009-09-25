<%--
normal.dsp

	Purpose:
		The head of each cell
	Description:
	History:
		Thu Mar 31 22:00:59     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="arg" value="${requestScope.arg}"/>
<%-- caption and border --%>
<div style="width:${arg.width};background-color:${arg.color};border:1px solid #666;padding:7px">
<c:if test="${!empty arg.caption}">
<div style="width:100%;margin-bottom:10px"><c:out value="${arg.caption}"/></div>
</c:if>
${c:render(arg.actionContext)}

<%-- shadow --%>
<c:if test="${arg.shadow}">
<table border="0" cellpadding="0" cellspacing="0" width="${arg.width}">
<tr>
 <td width="6"><img width="6" height="6" src="${c:encodeURL('~./img/shdlf.gif')}"/></td>
 <td background="${c:encodeURL('~./img/shdmd.gif')}"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td width="6"><img width="6" height="6" src="${c:encodeURL('~./img/shdrg.gif')}"/></td>
</tr>
</table>
</c:if>
</div>