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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="u" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<c:choose>
<c:when test="${!zk_completeDesktop}">
	<c:if test="${!arg.asyncUpdate}">
${u:outLangStyleSheets()}
${u:outLangJavaScripts(null)}
	</c:if>

<div id="${page.uuid}" z.dtid="${page.desktop.id}" class="zk" style="${empty page.style ? 'width:100%': page.style}" z.zidsp="page"${c:attr('z.owner', page.owner.uuid)}>
	<c:forEach var="root" items="${page.roots}">
${u:redraw(root, null)}
	</c:forEach>
</div>

	<c:if test="${!empty arg.responses}">
<script type="text/javascript">
${u:outResponseJavaScripts(arg.responses)}
</script>
	</c:if>
</c:when>

<c:otherwise><%-- zk_completeDesktop --%>
	<c:set var="zk_argAction" value="${arg.action}" scope="request"/><%-- ZHTML head counts on it --%>
	<c:set var="zk_argResponses" value="${arg.responses}" scope="request"/><%-- ZHTML body counts on it --%>
	<c:forEach var="root" items="${page.roots}">
${u:redraw(root, null)}
	</c:forEach>

	<c:if test="${!empty zk_argAction && !arg.asyncUpdate}">
${u:outLangStyleSheets()}
${u:outLangJavaScripts(null)}
<script type="text/javascript">
	zkau.addDesktop("${page.desktop.id}");
</script>
	</c:if>

	<c:if test="${!empty zk_argResponses}">
<script type="text/javascript">
${u:outResponseJavaScripts(zk_argResponses)}
</script>
	</c:if>
</c:otherwise>
</c:choose>
