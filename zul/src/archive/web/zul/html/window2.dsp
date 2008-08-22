<%--
window2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		New trendy mold for Window component
	History:
		Thu May 15 10:26:52 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="titlesc" value="${self.titleSclass}"/>
<div id="${self.uuid}" z.type="zul.wnd2.Wnd2" z.autoz="true"${self.outerAttrs}${self.innerAttrs}>
<c:choose>
<c:when test="${!empty self.caption or !empty self.title}">
<div class="${self.moldSclass}-tl"><div class="${self.moldSclass}-tr"><div class="${self.moldSclass}-tm">
<div id="${self.uuid}!caption" class="${self.moldSclass}-header">
<c:choose>
<c:when test="${empty self.caption}">
<c:if test="${self.closable}"><div id="${self.uuid}!close" class="${self.moldSclass}-tool ${self.moldSclass}-close"></div></c:if>
<c:if test="${self.maximizable}">
<div id="${self.uuid}!maximize" class="${self.moldSclass}-tool ${self.moldSclass}-maximize <c:if test="${self.maximized}">${self.moldSclass}-maximized</c:if>"></div>
</c:if>
<c:if test="${self.minimizable}">
<div id="${self.uuid}!minimize" class="${self.moldSclass}-tool ${self.moldSclass}-minimize"></div>
</c:if>
<c:out value="${self.title}"/>
</c:when>
<c:otherwise>
${z:redraw(self.caption, null)}
</c:otherwise>
</c:choose>
</div></div></div></div>
<c:set var="wcExtStyle" value="border-top:0;"/><%-- used below --%>
</c:when>
<c:when test="${self.mode != 'embedded' and self.mode != 'popup'}">
<div class="${self.moldSclass}-tl"><div class="${self.moldSclass}-tr"><div class="${self.moldSclass}-tm-noheader"></div></div></div>
</c:when>
</c:choose>
<div id="${self.uuid}!bwrap" class="${self.moldSclass}-body">
<c:if test="${self.mode != 'embedded' and self.mode != 'popup'}">
<div class="${self.moldSclass}-cl"><div class="${self.moldSclass}-cr"><div class="${self.moldSclass}-cm<c:if test="${'normal' != self.border}">-noborder</c:if>">
</c:if>
<c:set var="wcExtStyle" value="${c:cat(wcExtStyle, self.contentStyle)}"/>
<div id="${self.uuid}!cave" class="${self.contentSclass} ${self.moldSclass}-content<c:if test="${'normal' != self.border}">-noborder</c:if>"${c:attr('style',wcExtStyle)}>
	<c:forEach var="child" items="${self.children}"><c:if test="${self.caption != child}">${z:redraw(child, null)}</c:if></c:forEach>
</div><%-- we don't generate shadow here since it looks odd when on top of modal mask --%>
<c:if test="${self.mode != 'embedded' and self.mode != 'popup'}">
</div></div></div>
<div class="${self.moldSclass}-bl"><div class="${self.moldSclass}-br"><div class="${self.moldSclass}-bm"></div></div></div>
</c:if>
</div>
</div>

