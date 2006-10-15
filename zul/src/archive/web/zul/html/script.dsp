<%--
script.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct 15 12:14:24     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<script id="${self.uuid}"${self.outerAttrs}><%-- not HtmlBasedComponent --%>
${self.content}<%-- don't escape --%>
</script>