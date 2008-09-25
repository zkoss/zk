<%--
panel.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 11 11:44:46 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c"%>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z"%>
<c:set var="self" value="${requestScope.arg.self}" />
<c:set var="zcls" value="${self.zclass}"/>
<div id="${self.uuid}" z.type="zul.panel.Panel" z.autoz="true"${self.outerAttrs}${self.innerAttrs}>
		<c:if test="${self.framable}">
<div class="${zcls}-tl <c:if test="${empty self.caption and empty self.title}">${zcls}-header-noheader</c:if>"><div class="${zcls}-tr"><div class="${zcls}-tm">
		</c:if>
		<c:if test="${!empty self.caption or !empty self.title}">
<div id="${self.uuid}!caption" class="${zcls}-header <c:if test="${!self.framable && self.border != 'normal'}">${zcls}-header-noborder</c:if>">
			<c:choose>
				<c:when test="${empty self.caption}">
					<c:if test="${self.closable}">
<div id="${self.uuid}!close" class="${zcls}-tool ${zcls}-close"></div>
					</c:if>
					<c:if test="${self.maximizable}">
<div id="${self.uuid}!maximize" class="${zcls}-tool ${zcls}-maximize <c:if test="${self.maximized}">${zcls}-maximized</c:if>"></div>
					</c:if>
					<c:if test="${self.minimizable}">
<div id="${self.uuid}!minimize" class="${zcls}-tool ${zcls}-minimize"></div>
					</c:if>
					<c:if test="${self.collapsible}">
<div id="${self.uuid}!toggle" class="${zcls}-tool ${zcls}-toggle"></div>
					</c:if>
					<c:out value="${self.title}" />
				</c:when>
				<c:otherwise>
${z:redraw(self.caption, null)}
				</c:otherwise>
			</c:choose>
			</div>
		</c:if>
		<c:if test="${self.framable}"></div></div></div></c:if>
<div id="${self.uuid}!bwrap" class="${zcls}-body" <c:if test="${!self.open}">style="display:none;"</c:if>>
	<c:if test="${self.framable}"><div class="${zcls}-cl"><div class="${zcls}-cr"><div class="${zcls}-cm"></c:if>
		<c:if test="${!empty self.topToolbar}">
<div id="${self.uuid}!tbar" class="${zcls}-tbar <c:if test="${self.border != 'normal'}">${zcls}-tbar-noborder</c:if> <c:if test="${self.framable and empty self.caption and empty self.title}">${zcls}-noheader</c:if>">
${z:redraw(self.topToolbar, null)}
</div>
		</c:if>
		<c:if test="${!empty self.panelchildren}">
${z:redraw(self.panelchildren, null)}
		</c:if>
		<c:if test="${!empty self.bottomToolbar}">
<div id="${self.uuid}!bbar" class="${zcls}-bbar <c:if test="${self.border != 'normal'}">${zcls}-bbar-noborder</c:if> <c:if test="${self.framable and empty self.caption and empty self.title}">${zcls}-noheader</c:if>">
${z:redraw(self.bottomToolbar, null)}
</div>
		</c:if>
	<c:if test="${self.framable}">
</div></div></div>
<div class="${zcls}-bl <c:if test="${empty self.footToolbar}">${zcls}-nofbar</c:if>"><div class="${zcls}-br"><div class="${zcls}-bm">
	</c:if>
	<c:if test="${!empty self.footToolbar}">
<div id="${self.uuid}!fbar" class="${zcls}-fbar">
${z:redraw(self.footToolbar, null)}
</div>
	</c:if>
	<c:if test="${self.framable}"></div></div></div></c:if>
</div></div>