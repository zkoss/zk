<%--
gridpg.dsp

{{IS_NOTE
	Purpose:
		Grid for mold = paging
	Description:
		
	History:
		Mon Aug 21 14:43:31     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.grid.Grid"${self.outerAttrs}${self.innerAttrs}>
	<div id="${self.uuid}!paging" class="grid-paging">
	<c:if test="${self.pagingPosition == 'top' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgit" class="grid-pgi">
	${z:redraw(self.paging, null)}
	</div>
	</c:if>
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" class="grid-btable" <c:if test="${!empty self.columns}">style="table-layout:fixed"</c:if>>
	<tbody class="grid-head">
	<c:forEach var="head" items="${self.heads}">
${z:redraw(head, null)}
	</c:forEach>
	</tbody>
${z:redraw(self.rows, null)}
	<tbody class="grid-foot">
${z:redraw(self.foot, null)}
	</tbody>
	</table>
	<c:if test="${self.pagingPosition == 'bottom' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgib" class="grid-pgi">
	${z:redraw(self.paging, null)}
	</div>
	</c:if>
	</div>
</div>
