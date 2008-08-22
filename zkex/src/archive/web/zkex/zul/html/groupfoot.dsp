<%--
groupfoot.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 18 14:04:07 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<tr z.type="Grwgpft" id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" varStatus="status" items="${self.children}">
	<td z.type="Gcl" id="${child.uuid}!chdextr"${u:getColAttrs(self, status.index)}><div id="${child.uuid}!cell" class="${self.moldSclass}-content <c:if test="${self.grid.fixedLayout}">z-overflow-hidden</c:if>">${z:redraw(child, null)}</div></td>
	</c:forEach>
</tr>
