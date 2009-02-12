<%--
page.dsp

{{IS_NOTE
	Purpose:
		Render a ZUL page if it is included
	Description:
		zk_completeDesktop
			Defined if this page is included by desktop.dsp
	History:
		Wed Jun  8 17:15:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<c:choose>
<c:when test="${!zk_completeDesktop}">
	<c:if test="${!arg.embed}">
${z:outLangStyleSheets()}
${z:outLangJavaScripts(null)}
	</c:if>

<div${z:outPageAttrs(page)}>
	<c:forEach var="root" items="${page.roots}">
${z:redraw(root, null)}
	</c:forEach>
</div>
${z:outResponseJavaScripts(arg.responses)}
</c:when>

<c:otherwise><%-- zk_completeDesktop --%>
	<c:set var="zk_argResponses" value="${arg.responses}" scope="request"/><%-- ZHTML body counts on it --%>
	<c:forEach var="root" items="${page.roots}">
${z:redraw(root, null)}
	</c:forEach>

	<c:if test="${!arg.embed}">
${z:outZkHtmlTags()}
${z:outHtmlUnavailable(page)}
	</c:if>
${z:outResponseJavaScripts(zk_argResponses)}
</c:otherwise>
</c:choose>
