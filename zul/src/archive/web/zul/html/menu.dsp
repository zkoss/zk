<%--
menu.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 14:34:10     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.topmost}">
 <td id="${self.uuid}" align="left" zk_type="zul.html.menu.Menu"${self.outerAttrs}${self.innerAttrs}><a href="javascript:;" id="${self.uuid}!a">${self.imgTag} <c:out value="${self.label}"/></a>${z:redraw(self.menupopup, null)}</td>
</c:when>
<c:otherwise>
 <tr id="${self.uuid}" zk_type="zul.html.menu.Menu"${self.outerAttrs}${self.innerAttrs}>
 <td><h:img src="~./img/spacer.gif" width="11"/></td>
 <td align="left"><a href="javascript:;" id="${self.uuid}!a">${self.imgTag} <c:out value="${self.label}"/></a>${z:redraw(self.menupopup, null)}</td>
 <td><h:img src="~./zul/img/menu/arrow.gif" width="9"/></td>
 </tr>
</c:otherwise>
</c:choose>