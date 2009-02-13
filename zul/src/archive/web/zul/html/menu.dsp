<%--
menu.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 14:34:10     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.topmost}">
 <td id="${self.uuid}" align="left" z.type="zul.menu.Menu"${self.outerAttrs}${self.innerAttrs}><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a>${z:redraw(self.menupopup, null)}</td>
</c:when>
<c:otherwise>
 <tr id="${self.uuid}" z.type="zul.menu.Menu"${self.outerAttrs}${self.innerAttrs}>
 <td class="menu1"></td>
 <td align="left"><a href="javascript:;" id="${self.uuid}!a">${self.imgTag}<c:out value="${self.label}"/></a>${z:redraw(self.menupopup, null)}</td>
 <td class="menu3ar"></td>
 </tr>
</c:otherwise>
</c:choose>