<%--
page.dsp

{{IS_NOTE
	$Id: page.dsp,v 1.20 2006/05/15 16:43:54 tomyeh Exp $
	Purpose:
		Render a ZUL page if it is included
	Description:
		complete_desktop
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
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<c:if test="${!arg.asyncUpdate}">
${u:outLangStyleSheets()}
${u:outLangJavaScripts(page, arg.action)}
</c:if>
<c:if test="${!complete_desktop}"><div id="${page.id}" style="${empty page.style ? 'width:100%': page.style}"></c:if>
<c:forEach var="root" items="${page.roots}">
${u:redraw(root, null)}
</c:forEach>
<c:if test="${!complete_desktop}"></div></c:if>
<c:if test="${!empty arg.responses}">
<script type="text/javascript">
${u:outResponseJavaScripts(arg.responses)}
</script>
</c:if>
