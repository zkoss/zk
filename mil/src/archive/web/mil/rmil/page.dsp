<%--
page.dsp

{{IS_NOTE
	Purpose:
		Render a MIL page if it is included
	Description:

	History:
		Mon May  28 14:33:41     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/mil/core" prefix="m" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<zk id="${page.desktop.id}" ${m:desktopAttrs(arg.action)}>
<c:forEach var="root" items="${page.roots}">
${z:redraw(root, null)}
</c:forEach>
</zk>
