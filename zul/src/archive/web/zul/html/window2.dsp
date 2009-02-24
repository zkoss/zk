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
<c:set var="zcls" value="${self.zclass}"/>
<c:set var="noborder" value="${'normal' != self.border ? '-noborder' : ''}"/>
<div id="${self.uuid}" z.type="zul.wnd2.Wnd2" z.autoz="true"${self.outerAttrs}${self.innerAttrs}>
<c:choose>
<c:when test="${!empty self.caption or !empty self.title}">
<div class="${zcls}-tl"><div class="${zcls}-tr"></div></div>
<div class="${zcls}-hl"><div class="${zcls}-hr"><div class="${zcls}-hm">
<div id="${self.uuid}!caption" class="${zcls}-header">
<c:choose>
<c:when test="${empty self.caption}">
<c:if test="${self.closable}"><div id="${self.uuid}!close" class="${zcls}-icon ${zcls}-close"></div></c:if>
<c:if test="${self.maximizable}">
<div id="${self.uuid}!maximize" class="${zcls}-icon ${zcls}-max <c:if test="${self.maximized}">${zcls}-maxd</c:if>"></div>
</c:if>
<c:if test="${self.minimizable}">
<div id="${self.uuid}!minimize" class="${zcls}-icon ${zcls}-min"></div>
</c:if>
<c:out value="${self.title}"/>
</c:when>
<c:otherwise>
${z:redraw(self.caption, null)}
</c:otherwise>
</c:choose>
</div></div></div></div>
</c:when>
<c:when test="${(self.mode != 'embedded' and self.mode != 'popup')}">
<div class="${zcls}-tl-${noborder}"><div class="${zcls}-tr${noborder}"></div></div>
</c:when>
</c:choose>

<c:if test="${self.mode != 'embedded' and self.mode != 'popup'}">
<div class="${zcls}-cl${noborder}"><div class="${zcls}-cr${noborder}"><div class="${zcls}-cm${noborder}">
</c:if>
<div id="${self.uuid}!cave" class="${self.contentSclass} ${zcls}-cnt${noborder}"${c:attr('style',self.contentStyle)}>
	<c:forEach var="child" items="${self.children}"><c:if test="${self.caption != child}">${z:redraw(child, null)}</c:if></c:forEach>
</div><%-- we don't generate shadow here since it looks odd when on top of modal mask --%>
<c:if test="${self.mode != 'embedded' and self.mode != 'popup'}">
</div></div></div>
<div class="${zcls}-bl${noborder}"><div class="${zcls}-br${noborder}"></div></div>
</c:if>
</div>

