<%--
treeitem.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Thu Jul  7 16:11:48     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}" />
<c:set var="tree" value="${self.tree}" />
<c:choose>
	<c:when test="${tree.mold == 'paging'}">
		<c:if test="${self.visible and u:shallVisitTree(tree, self)}">
			<c:if test="${u:shallRenderTree(tree)}">
			${z:redraw(self.treerow, null)}
			</c:if>
			<c:if test="${self.open}">
			${z:redraw(self.treechildren, null)}
			</c:if>
		</c:if>
	</c:when>
	<c:otherwise>
	${z:redraw(self.treerow, null)}
	${z:redraw(self.treechildren, null)}
	</c:otherwise>
</c:choose>