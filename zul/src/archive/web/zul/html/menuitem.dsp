<%--
menuitem.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 14:41:34     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.topmost}">
 <td id="${self.uuid}" align="left" z.type="Menuit"${self.outerAttrs}${self.innerAttrs}><a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
</c:when>
<c:otherwise>
 <tr id="${self.uuid}" z.type="Menuit"${self.outerAttrs}${self.innerAttrs}>
 <td class="${self.checked ? 'menu1ck': 'menu1'}"></td>
 <td align="left"><a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a></td>
 <td width="9px"></td>
 </tr>
</c:otherwise>
</c:choose>