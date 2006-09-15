<%--
page.dsp

{{IS_NOTE
	Purpose:
		Render a ZUL page if it is included
	Description:
		zk_htmlHeadRequired
			It is set by desktop.dsp to ask this page to render </head><body>
	History:
		Wed Jun  8 17:15:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<c:if test="${!arg.asyncUpdate}">
${z:outLangStyleSheets()}
${z:outLangJavaScripts(arg.action)}
</c:if>
<c:if test="${!empty zk_htmlHeadRequired}">
<c:set var="zk_htmlHeadRequired" value="" scope="request"/>
${z:outPageHeaders(page)}
</head>
<body${c:attr('style', page.style)}>
</c:if>
<div id="${page.id}" style="${empty page.style ? 'width:100%': page.style}">
<c:forEach var="root" items="${page.roots}">
${z:redraw(root, null)}
</c:forEach>
</div>
<c:if test="${!empty arg.responses}">
<script type="text/javascript">
${z:outResponseJavaScripts(arg.responses)}
</script>
</c:if>
