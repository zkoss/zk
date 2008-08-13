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
<div id="${self.uuid}!close" class="${self.moldSclass}-tool ${self.moldSclass}-close"></div>
					</c:if>
					<c:if test="${self.maximizable}">
<div id="${self.uuid}!maximize" class="${self.moldSclass}-tool ${self.moldSclass}-maximize <c:if test="${self.maximized}">${self.moldSclass}-maximized</c:if>"></div>
					</c:if>
					<c:if test="${self.minimizable}">
<div id="${self.uuid}!minimize" class="${self.moldSclass}-tool ${self.moldSclass}-minimize"></div>
					</c:if>
					<c:if test="${self.collapsible}">
<div id="${self.uuid}!toggle" class="${self.moldSclass}-tool ${self.moldSclass}-toggle"></div>
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
<div id="${self.uuid}!bwrap" class="${self.moldSclass}-bwrap" <c:if test="${!self.open}">style="display:none;"</c:if>>
	<c:if test="${self.framable}"><div class="${self.moldSclass}-cl"><div class="${self.moldSclass}-cr"><div class="${self.moldSclass}-cm"></c:if>
		<c:if test="${!empty self.topToolbar}">
<div id="${self.uuid}!tbar" class="${self.moldSclass}-tbar <c:if test="${self.border != 'normal'}">${self.moldSclass}-tbar-${self.border}</c:if> <c:if test="${self.framable and empty self.caption and empty self.title}">${self.moldSclass}-notitle</c:if>">
${z:redraw(self.topToolbar, null)}
</div>
		</c:if>
		<c:if test="${!empty self.panelchildren}">
${z:redraw(self.panelchildren, null)}
		</c:if>
		<c:if test="${!empty self.bottomToolbar}">
<div id="${self.uuid}!bbar" class="${self.moldSclass}-bbar <c:if test="${self.border != 'normal'}">${self.moldSclass}-bbar-${self.border}</c:if> <c:if test="${self.framable and empty self.caption and empty self.title}">${self.moldSclass}-notitle</c:if>">
${z:redraw(self.bottomToolbar, null)}
</div>
		</c:if>
	<c:if test="${self.framable}">
</div></div></div>
<div class="${self.moldSclass}-bl <c:if test="${empty self.footToolbar}">${self.moldSclass}-nofbar</c:if>"><div class="${self.moldSclass}-br"><div class="${self.moldSclass}-bm">
	</c:if>
	<c:if test="${!empty self.footToolbar}">
<div id="${self.uuid}!fbar" class="${self.moldSclass}-fbar">
${z:redraw(self.footToolbar, null)}
</div>
	</c:if>
	<c:if test="${self.framable}"></div></div></div></c:if>
</div></div>