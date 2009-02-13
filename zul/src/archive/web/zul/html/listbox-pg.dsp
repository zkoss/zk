<%--
listbox-pg.dsp

{{IS_NOTE
	Purpose:
		Listbox for mold = paging
	Description:
		
	History:
		Mon Aug 28 11:55:34     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="zu" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.sel.Libox" z.pg="t"${self.outerAttrs}${self.innerAttrs}>
	<c:if test="${!empty self.pagingChild && self.pagingPosition == 'top' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgit" class="listbox-pgi-t">
	${z:redraw(self.pagingChild, null)}
	</div>
	</c:if>
<c:if test="${!empty self.listhead}">
	<div id="${self.uuid}!head" class="listbox-head">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.listhead}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.listhead.uuid}!hdfaker" class="listbox-fake">
			<c:forEach var="child" items="${self.listhead.children}">
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
<c:set var="hgh" if="${self.rows > 1}" value="style=\"overflow:hidden;height:${self.rows * 15}px\""/>
<c:set var="hgh" if="${!empty self.height}" value="style=\"overflow:hidden;height:${self.height}\""/>
	<div id="${self.uuid}!body" class="listbox-body" ${hgh}>
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" id="${self.uuid}!cave" class="listbox-btable" <c:if test="${self.fixedLayout}">style="table-layout:fixed"</c:if>>
		<c:if test="${!empty self.listhead}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.listhead.uuid}!bdfaker" class="listbox-fake">
			<c:forEach var="child" items="${self.listhead.children}">
				<th id="${child.uuid}!bdfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
${zu:resetStripeClass(self)}
	<c:forEach var="item" items="${self.items}" begin="${self.visibleBegin}" end="${self.visibleEnd}">
${zu:setStripeClass(item)}	
${z:redraw(item, null)}
	</c:forEach>
	</table>
	</div>
<c:if test="${!empty self.listfoot}">
	<div id="${self.uuid}!foot" class="listbox-foot">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
	<c:if test="${!empty self.listhead}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.listhead.uuid}!ftfaker" class="listbox-fake">
			<c:forEach var="child" items="${self.listhead.children}">
				<th id="${child.uuid}!ftfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
	</c:if>
${z:redraw(self.listfoot, null)}
	</table>
	</div>
</c:if>
	<c:if test="${!empty self.pagingChild && self.pagingPosition == 'bottom' || self.pagingPosition == 'both'}">
	<div id="${self.uuid}!pgib" class="listbox-pgi">
	${z:redraw(self.pagingChild, null)}
	</div>
	</c:if>
</div>
