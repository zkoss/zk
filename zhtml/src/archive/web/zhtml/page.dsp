<%--
page.dsp

{{IS_NOTE
	Purpose:
		Render a ZUL page if it is included
	Description:
		zk_completeDesktop
			Defined if this page is included by desktop.dsp
	History:
		Wed Jun  8 17:15:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="u" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<c:choose>
<c:when test="${!zk_completeDesktop}">
	<c:if test="${!arg.asyncUpdate}">
${u:outLangStyleSheets()}
${u:outLangJavaScripts(arg.action)}
	</c:if>

<div id="${page.id}" style="${empty page.style ? 'width:100%': page.style}" xmlns:z="http://www.zkoss.org/2005/zk" z:zidsp="true">
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
${u:outLangJavaScripts(zk_argAction)}
	</c:if>
	<c:if test="${!empty zk_argResponses}">
<script type="text/javascript">
${u:outResponseJavaScripts(zk_argResponses)}
</script>
	</c:if>
</c:otherwise>
</c:choose>
