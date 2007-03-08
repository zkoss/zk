<%--
vtab.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 3 2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="suffix" value="-sel.gif" if="${self.selected}"/>
<c:set var="suffix" value="-uns.gif" unless="${self.selected}"/>
<tr id="${self.uuid}" z.type="Tab"${self.outerAttrs} z.sel="${self.selected}" z.box="${self.tabbox.uuid}" z.panel="${self.linkedPanel.uuid}">
<td align="right"><table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td width="5" height="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-tl',suffix))})"></td>
	<td colspan="3" height="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-tm',suffix))})"></td>
	<td width="3" height="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-tr',suffix))})"></td>
</tr>
<tr height="22">
	<td width="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-ml',suffix))})"></td>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mm',suffix))})"></td>
	<td align="center" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mm',suffix))})" id="${self.uuid}!real"${self.innerAttrs}><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mm',suffix))})"></td>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mr',suffix))})"></td>
</tr>
<c:if test="${self.closable}">
<tr height="8">
	<td width="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-ml',suffix))})"></td>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mm',suffix))})"></td>
	<td align="center" valign="bottom" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mm',suffix))})"><img id="${self.uuid}!close" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mm',suffix))})"></td>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-mr',suffix))})"></td>
</tr>
</c:if>
<tr>
	<td width="5" height="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-bl',suffix))})"></td>
	<td colspan="3" height="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-bm',suffix))})"></td>
	<td width="3" height="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/v3d-br',suffix))})"></td>
</tr>
</table></td>
</tr>