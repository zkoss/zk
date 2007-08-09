<%--
tab.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:58:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="suffix" value="-sel" if="${self.selected}"/>
<c:set var="suffix" value="-uns" unless="${self.selected}"/>
<td id="${self.uuid}" z.type="Tab"${self.outerAttrs}${self.innerAttrs} z.sel="${self.selected}" z.box="${self.tabbox.uuid}" z.panel="${self.linkedPanel.uuid}">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td width="5" height="5" class="${c:cat('tab-3d-tl',suffix)}"></td>
	<td colspan="${self.closable ?4:3}" height="5" class="${c:cat('tab-3d-tm',suffix)}"></td>
	<td width="5" height="5" class="${c:cat('tab-3d-tr',suffix)}"></td>
</tr>
<tr height="${empty self.height ? '22': self.height}">
	<td width="5" class="${c:cat('tab-3d-ml',suffix)}"></td>
	<td width="3" class="${c:cat('tab-3d-mm',suffix)}"></td>
	<td align="center" class="${c:cat('tab-3d-mm',suffix)}"><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
<c:if test="${self.closable}">
	<td width="11" align="right" class="${c:cat('tab-3d-mm',suffix)}"><img id="${self.uuid}!close" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
</c:if>
	<td width="3" class="${c:cat('tab-3d-mm',suffix)}"></td>
	<td width="5" class="${c:cat('tab-3d-mr',suffix)}"></td>
</tr>
<tr>
	<td width="5" height="3" class="${c:cat('tab-3d-bl',suffix)}"></td>
	<td colspan="${self.closable ?4:3}" height="3" class="${c:cat('tab-3d-bm',suffix)}"></td>
	<td width="5" height="3" class="${c:cat('tab-3d-br',suffix)}"></td>
</tr>
</table>
</td>
