<%--
treechildren.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 16:10:03     2005, Created by tomyeh
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
<c:when test="${self.tree == self.parent}">
<tbody id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.visibleChildrenIterator}">
	${z:redraw(child, null)}
	</c:forEach>
</tbody>
</c:when>
<c:otherwise>
	<c:forEach var="child" items="${self.visibleChildrenIterator}">
	${z:redraw(child, null)}
	</c:forEach>
</c:otherwise>
</c:choose>
