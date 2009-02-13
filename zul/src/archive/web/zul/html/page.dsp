<%--
page.dsp

{{IS_NOTE
	Purpose:
		The template to render an included page
	Description:
		zk_htmlHeadRequired
			It is set by desktop.dsp to ask this page to render </head><body>
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
<c:if test="${!arg.asyncUpdate}">
${z:outLangStyleSheets()}
${z:outLangJavaScripts(null)}
</c:if>
<c:if test="${!empty zk_htmlHeadRequired}">
<c:set var="zk_htmlHeadRequired" value="" scope="request"/>
${z:outHeaders(page)}
</head>
<body>
</c:if>
<div${z:outPageAttrs(page)}>
<c:forEach var="root" items="${page.roots}">
${z:redraw(root, null)}
</c:forEach>
</div>
<c:if test="${!empty arg.responses}">
<script type="text/javascript">
${z:outResponseJavaScripts(arg.responses)}
</script>
</c:if>
