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
<table border="0" cellpadding="0" cellspacing="0" width="${arg.width}">
<c:choose>
<c:when test="${empty arg.caption}">
<tr>
 <td style="background-color:${arg.color}" colspan="3"><img height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
</tr>
<tr>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td class="gamma"><img height="4" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
</tr>
</c:when>
<c:otherwise>
<tr>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td style="background-color:${arg.color};color: white">&nbsp;<c:out value="${arg.caption}"/>&nbsp;</td>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
</tr>
<tr>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td class="gamma"><img height="4" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
</tr>
</c:otherwise>
</c:choose>
</table>

<%-- content --%>
<table border="0" cellpadding="0" cellspacing="0" width="${arg.width}">
<tr>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <c:if test="${!empty arg.spacing}"><td class="gamma" width="${arg.spacing}"><img width="${arg.spacing}" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td></c:if>
 <td class="gamma" width="100%"${c:attr('align', arg.align)}>${c:render(arg.actionContext)}</td>
 <c:if test="${!empty arg.spacing}"><td class="gamma" width="${arg.spacing}"><img width="${arg.spacing}" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td></c:if>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
</tr>
</table>

<%-- bottom border --%>
<table border="0" cellpadding="0" cellspacing="0" width="${arg.width}">
<tr>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td class="gamma"><img height="4" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
 <td style="background-color:${arg.color}" width="1"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
</tr>
<tr>
 <td style="background-color:${arg.color}" colspan="3"><img height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td>
</tr>
</table>
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
