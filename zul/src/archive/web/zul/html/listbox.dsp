<%--
listbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 15 18:20:53     2005, Created by tomyeh
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
<c:set var="zcls" value="${self.zclass}"/>
<div id="${self.uuid}" z.type="zul.sel.Libox"${self.outerAttrs}${self.innerAttrs}>
<c:if test="${!empty self.listhead}">
	<div id="${self.uuid}!head" class="${zcls}-header">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
		<c:if test="${!empty self.listhead}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.listhead.uuid}!hdfaker" class="${zcls}-faker">
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
	<div id="${self.uuid}!body" class="${zcls}-body" ${hgh}>
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" id="${self.uuid}!cave" <c:if test="${self.fixedLayout}">style="table-layout:fixed"</c:if>>
		<c:if test="${!empty self.listhead}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.listhead.uuid}!bdfaker" class="${zcls}-faker">
			<c:forEach var="child" items="${self.listhead.children}">
				<th id="${child.uuid}!bdfaker"${child.outerAttrs}>
					<div style="overflow:hidden"></div>
				</th>
			</c:forEach>
			</tr>
		</tbody>
		</c:if>
${u:resetStripeClass(self)}
	<c:forEach var="item" items="${self.items}">
${u:setStripeClass(item)}	
${z:redraw(item, null)}
	</c:forEach>
	</table><${c:browser('ie') || c:browser('gecko') ? 'a' : 'button'} z.keyevt="true" id="${self.uuid}!a" tabindex="-1" onclick="return false;" href="javascript:;" style="position: absolute;left: 0px; top: 0px;padding:0 !important; margin:0 !important; border:0 !important; background: transparent !important; font-size: 1px !important; width: 1px !important; height: 1px !important;-moz-outline: 0 none; outline: 0 none;	-moz-user-select: text; -khtml-user-select: text;"></${c:browser('ie') || c:browser('gecko') ? 'a' : 'button'}>
	</div>
<c:if test="${!empty self.listfoot}">
	<div id="${self.uuid}!foot" class="${zcls}-footer">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
	<c:if test="${!empty self.listhead}">
		<tbody style="visibility:hidden;height:0px">
			<tr id="${self.listhead.uuid}!ftfaker" class="${zcls}-faker">
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
</div>
