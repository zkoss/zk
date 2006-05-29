<%--
menuitem.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 14:41:34     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.topmost}">
 <td id="${self.uuid}" align="left" zk_type="Menuit"${self.outerAttrs}${self.innerAttrs}><a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a">${self.imgTag} <c:out value="${self.label}"/></a></td>
</c:when>
<c:otherwise>
 <tr id="${self.uuid}" zk_type="Menuit"${self.outerAttrs}${self.innerAttrs}>
 <td><h:img src="${self.checked?'~./zul/img/menu/checked.gif':'~./img/spacer.gif'}" width="11"/></td>
 <td align="left"><a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a">${self.imgTag} <c:out value="${self.label}"/></a></td>
 <td><h:img src="~./img/spacer.gif" width="9"/></td>
 </tr>
</c:otherwise>
</c:choose>