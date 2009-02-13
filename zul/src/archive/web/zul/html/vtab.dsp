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
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="suffix" value="-sel" if="${self.selected}"/>
<c:set var="suffix" value="-uns" unless="${self.selected}"/>
<c:set var="wd" value=" width=\"${self.width}\"" unless="${empty self.width}"/>
<c:set var="look" value="${self.tabbox.tabLook}-"/>
<tr id="${self.uuid}" z.type="Tab"${self.outerAttrs} z.sel="${self.selected}" z.box="${self.tabbox.uuid}" z.panel="${self.linkedPanel.uuid}" z.disabled="${self.disabled}">
<td align="right"${wd}><table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td class="${c:cat3(look,'tl',suffix)}"></td>
	<td colspan="3" class="${c:cat3(look,'tm',suffix)}"></td>
	<td class="${c:cat3(look,'tr',suffix)}"></td>
</tr>
<tr class="${c:cat(look,'m')}">
	<td class="${c:cat3(look,'ml',suffix)}"></td>
	<td width="3" class="${c:cat3(look,'mm',suffix)}"></td>
	<td align="center" class="${c:cat3(look,'mm',suffix)}" id="${self.uuid}!real"${self.innerAttrs}><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
	<td width="3" class="${c:cat3(look,'mm',suffix)}"></td>
	<td class="${c:cat3(look,'mr',suffix)}"></td>
</tr>
<c:if test="${self.closable}">
<tr height="8">
	<td class="${c:cat3(look,'ml',suffix)}"></td>
	<td width="3" class="${c:cat3(look,'mm',suffix)}"></td>
	<td align="center" valign="bottom" class="${c:cat3(look,'mm',suffix)}"><img id="${self.uuid}!close" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
	<td width="3" class="${c:cat3(look,'mm',suffix)}"></td>
	<td class="${c:cat3(look,'mr',suffix)}"></td>
</tr>
</c:if>
<tr>
	<td class="${c:cat3(look,'bl',suffix)}"></td>
	<td colspan="3" class="${c:cat3(look,'bm',suffix)}"></td>
	<td class="${c:cat3(look,'br',suffix)}"></td>
</tr>
</table></td>
</tr>