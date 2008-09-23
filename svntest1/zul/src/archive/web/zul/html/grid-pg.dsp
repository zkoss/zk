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
<c:set var="mcls" value="${self.moldSclass}"/>
<div id="${self.uuid}" z.type="zul.grid.Grid" z.pg="t"${self.outerAttrs}${self.innerAttrs}>
	<c:if test="${!empty self.pagingChild && self.pagingPosition == 'top' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgit" class="${mcls}-pgi-t">
	${z:redraw(self.pagingChild, null)}
	</div>
	</c:if>
<c:if test="${!empty self.columns}">
	<div id="${self.uuid}!head" class="${mcls}-header">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.columns}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.columns.uuid}!hdfaker" class="${mcls}-faker">
			<c:forEach var="child" items="${self.columns.children}">
				<th id="${child.uuid}!hdfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
	<c:forEach var="head" items="${self.heads}">
${z:redraw(head, null)}
	</c:forEach>
	</table>
	</div>
</c:if>
	<div id="${self.uuid}!body" class="${mcls}-body" <c:if test="${!empty self.height}">style="height:${self.height}"</c:if>>
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" <c:if test="${self.fixedLayout}">style="table-layout:fixed"</c:if>>
		<c:if test="${!empty self.columns}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.columns.uuid}!bdfaker" class="${mcls}-faker">
			<c:forEach var="child" items="${self.columns.children}">
				<th id="${child.uuid}!bdfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
	${z:redraw(self.rows, null)}
	</table>
	</div>
<c:if test="${!empty self.foot}">
	<div id="${self.uuid}!foot" class="${mcls}-footer">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.columns}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.columns.uuid}!ftfaker" class="${mcls}-faker">
			<c:forEach var="child" items="${self.columns.children}">
				<th id="${child.uuid}!ftfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
${z:redraw(self.foot, null)}
	</table>
	</div>
</c:if>
	<c:if test="${!empty self.pagingChild && self.pagingPosition == 'bottom' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgib" class="${mcls}-pgi-b">
	${z:redraw(self.pagingChild, null)}
	</div>
	</c:if>
</div>
