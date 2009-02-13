<%--
complete.dsp

{{IS_NOTE
	Purpose:
		Used to render a ZUL page as a complete page (aka., desktop)
	Description:
		
	History:
		Fri Jun 10 09:16:14     2005, Created by tomyeh
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
<%@ page contentType="${z:outContentType(page)}" %>
${z:outDocType(page)}
<c:set var="zk_argAction" value="${arg.action}" scope="request"/><%-- ZHTML head counts on it --%>
<c:set var="zk_argResponses" value="${arg.responses}" scope="request"/><%-- ZHTML body counts on it --%>
<c:forEach var="root" items="${page.roots}">
${z:redraw(root, null)}
</c:forEach>

<c:if test="${!empty zk_argAction && !arg.asyncUpdate}">
${z:outLangStyleSheets()}
${z:outLangJavaScripts(null)}
<script type="text/javascript">
	zkau.addDesktop("${page.desktop.id}");
</script>
</c:if>

<c:if test="${!empty zk_argResponses}">
<script type="text/javascript">
${z:outResponseJavaScripts(zk_argResponses)}
</script>
</c:if>
