<%--
row.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Oct 25 17:08:44     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<tr z.type="Grw" id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" varStatus="status" items="${self.children}">
	<td z.type="Gcl" id="${child.uuid}!chdextr"${u:getColAttrs(self, status.index)}><div id="${child.uuid}!cell" class="${self.zclass}-cnt <c:if test="${self.grid.fixedLayout}">z-overflow-hidden</c:if>">${z:redraw(child, null)}</div></td>
	</c:forEach>
</tr>
