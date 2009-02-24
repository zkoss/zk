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
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<div id="${self.uuid}" z.type="zul.tree.Tree" z.pg="t"${self.outerAttrs}${self.innerAttrs}>
	<c:if test="${!empty self.pagingChild && self.pagingPosition == 'top' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgit" class="${zcls}-pgi-t">
	${z:redraw(self.pagingChild, null)}
	</div>
	</c:if>
<c:if test="${!empty self.treecols}">
	<div id="${self.uuid}!head" class="${zcls}-header">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.treecols}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.treecols.uuid}!hdfaker" class="${zcls}-faker">
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
	<div id="${self.uuid}!body" class="${zcls}-body">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" <c:if test="${self.fixedLayout}">style="table-layout:fixed"</c:if>>
		<c:if test="${!empty self.treecols}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.treecols.uuid}!bdfaker" class="${zcls}-faker">
			<c:forEach var="child" items="${self.treecols.children}">
				<th id="${child.uuid}!bdfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
${z:redraw(self.treechildren, null)}
	</table><${c:browser('ie') || c:browser('gecko') ? 'a' : 'button'} z.keyevt="true" id="${self.uuid}!a" tabindex="-1" onclick="return false;" href="javascript:;" style="position: absolute;left: 0px; top: 0px;padding:0 !important; margin:0 !important; border:0 !important; background: transparent !important; font-size: 1px !important; width: 1px !important; height: 1px !important;-moz-outline: 0 none; outline: 0 none;	-moz-user-select: text; -khtml-user-select: text;"></${c:browser('ie') || c:browser('gecko') ? 'a' : 'button'}>
	</div>
<c:if test="${!empty self.treefoot}">
	<div id="${self.uuid}!foot" class="${zcls}-footer">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.treecols}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.treecols.uuid}!ftfaker" class="${zcls}-faker">
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
	<c:if test="${!empty self.pagingChild && self.pagingPosition == 'bottom' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgib" class="${zcls}-pgi-b">
	${z:redraw(self.pagingChild, null)}
	</div>
	</c:if>
${u:clearTreeRenderInfo(self)}
</div>
