<%--
row.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 22, 2007 5:13:24 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/yuiextz/core" prefix="y" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<tr id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" varStatus="status" items="${self.children}">
	<td id="${child.uuid}!chdextr" ${y:getColAttrs(self, status.index)}>${z:redraw(child, null)}</td>
	</c:forEach>
</tr>
