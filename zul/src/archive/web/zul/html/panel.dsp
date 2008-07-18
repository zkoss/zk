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
<c:set var="titlesc" value="${self.titleSclass}" />
<div id="${self.uuid}" z.type="zul.panel.Panel" z.autoz="true"${self.outerAttrs}${self.innerAttrs}>
		<c:if test="${self.framable}">
<div class="${titlesc}l <c:if test="${empty self.caption and empty self.title}">${titlesc}-notitle</c:if>"><div class="${titlesc}r"><div class="${titlesc}m">
		</c:if>
		<c:if test="${!empty self.caption or !empty self.title}">
<div id="${self.uuid}!caption" class="${titlesc} title <c:if test="${!self.framable && self.border != 'normal'}">${titlesc}-${self.border}</c:if>">
			<c:choose>
				<c:when test="${empty self.caption}">
					<c:if test="${self.closable}">
<div id="${self.uuid}!close" class="${self.sclass}-tool ${self.sclass}-close"></div>
					</c:if>
					<c:if test="${self.maximizable}">
<div id="${self.uuid}!maximize" class="${self.sclass}-tool ${self.sclass}-maximize <c:if test="${self.maximized}">${self.sclass}-maximized</c:if>"></div>
					</c:if>
					<c:if test="${self.minimizable}">
<div id="${self.uuid}!minimize" class="${self.sclass}-tool ${self.sclass}-minimize"></div>
					</c:if>
					<c:if test="${self.collapsible}">
<div id="${self.uuid}!toggle" class="${self.sclass}-tool ${self.sclass}-toggle"></div>
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
<div id="${self.uuid}!bwrap" class="${self.sclass}-bwrap" <c:if test="${!self.open}">style="display:none;"</c:if>>
	<c:if test="${self.framable}"><div class="${self.sclass}-cl"><div class="${self.sclass}-cr"><div class="${self.sclass}-cm"></c:if>
		<c:if test="${!empty self.topToolbar}">
<div id="${self.uuid}!tbar" class="${self.sclass}-tbar <c:if test="${self.border != 'normal'}">${self.sclass}-tbar-${self.border}</c:if> <c:if test="${self.framable and empty self.caption and empty self.title}">${self.sclass}-notitle</c:if>">
${z:redraw(self.topToolbar, null)}
</div>
		</c:if>
		<c:if test="${!empty self.panelchildren}">
${z:redraw(self.panelchildren, null)}
		</c:if>
		<c:if test="${!empty self.bottomToolbar}">
<div id="${self.uuid}!bbar" class="${self.sclass}-bbar <c:if test="${self.border != 'normal'}">${self.sclass}-bbar-${self.border}</c:if> <c:if test="${self.framable and empty self.caption and empty self.title}">${self.sclass}-notitle</c:if>">
${z:redraw(self.bottomToolbar, null)}
</div>
		</c:if>
	<c:if test="${self.framable}">
</div></div></div>
<div class="${self.sclass}-bl <c:if test="${empty self.footToolbar}">${self.sclass}-nofbar</c:if>"><div class="${self.sclass}-br"><div class="${self.sclass}-bm">
	</c:if>
	<c:if test="${!empty self.footToolbar}">
<div id="${self.uuid}!fbar" class="${self.sclass}-fbar">
${z:redraw(self.footToolbar, null)}
</div>
	</c:if>
	<c:if test="${self.framable}"></div></div></div></c:if>
</div></div>