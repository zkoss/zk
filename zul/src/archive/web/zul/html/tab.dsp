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
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="suffix" value="-sel" if="${self.selected}"/>
<c:set var="suffix" value="-uns" unless="${self.selected}"/>
<c:set var="tscls" value="${self.tabbox.tabSclass}-"/>
<td id="${self.uuid}" z.type="Tab"${self.outerAttrs}${self.innerAttrs} z.sel="${self.selected}" z.box="${self.tabbox.uuid}" z.panel="${self.linkedPanel.uuid}">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td class="${c:cat3(tscls,'tl',suffix)}"></td>
	<td colspan="${self.closable ?4:3}" class="${c:cat3(tscls,'tm',suffix)}"></td>
	<td class="${c:cat3(tscls,'tr',suffix)}"></td>
</tr>
<tr height="${empty self.height ? '22': self.height}">
	<td class="${c:cat3(tscls,'ml',suffix)}"></td>
	<td width="3" class="${c:cat3(tscls,'mm',suffix)}"></td>
	<td align="center" class="${c:cat3(tscls,'mm',suffix)}"><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
<c:if test="${self.closable}"><%-- Bug 1780044: width cannot (and need not) be specified --%>
	<td align="right" class="${c:cat3(tscls,'mm',suffix)}"><img id="${self.uuid}!close" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
</c:if>
	<td width="3" class="${c:cat3(tscls,'mm',suffix)}"></td>
	<td class="${c:cat3(tscls,'mr',suffix)}"></td>
</tr>
<tr>
	<td class="${c:cat3(tscls,'bl',suffix)}"></td>
	<td colspan="${self.closable ?4:3}" class="${c:cat3(tscls,'bm',suffix)}"></td>
	<td class="${c:cat3(tscls,'br',suffix)}"></td>
</tr>
</table>
</td>
