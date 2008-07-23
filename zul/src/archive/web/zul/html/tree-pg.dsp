<%--
tree-pg.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 22 09:25:16 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="zu" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.tree.Tree"${self.outerAttrs}${self.innerAttrs}>
	<c:if test="${!empty self.paging && self.pagingPosition == 'top' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgit" class="tree-pgi-t">
	${z:redraw(self.paging, null)}
	</div>
	</c:if>
<c:if test="${!empty self.treecols}">
	<div id="${self.uuid}!head" class="tree-head">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.treecols}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.treecols.uuid}!hdfaker" class="tree-fake">
			<c:forEach var="child" items="${self.treecols.children}">
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
	<div id="${self.uuid}!body" class="tree-body">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" <c:if test="${self.fixedLayout}">style="table-layout:fixed"</c:if>>
		<c:if test="${!empty self.treecols}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.treecols.uuid}!bdfaker" class="tree-fake">
			<c:forEach var="child" items="${self.treecols.children}">
				<th id="${child.uuid}!bdfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
${z:redraw(self.treechildren, null)}
	</table>
	</div>
<c:if test="${!empty self.treefoot}">
	<div id="${self.uuid}!foot" class="tree-foot">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.treecols}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.treecols.uuid}!ftfaker" class="tree-fake">
			<c:forEach var="child" items="${self.treecols.children}">
				<th id="${child.uuid}!ftfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
${z:redraw(self.treefoot, null)}
	</table>
	</div>
</c:if>
	<c:if test="${!empty self.paging && self.pagingPosition == 'bottom' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgib" class="tree-pgi">
	${z:redraw(self.paging, null)}
	</div>
	</c:if>
${zu:clearRenderedItem(self)}
</div>
