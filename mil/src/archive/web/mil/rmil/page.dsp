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
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<%@ taglib uri="/WEB-INF/tld/mil/core.dsp.tld" prefix="m" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<zk id="${page.uuid}" di="${page.desktop.id}" ${m:desktopAttrs(arg.action)}>
<c:forEach var="root" items="${page.roots}">
${z:redraw(root, null)}
</c:forEach>
</zk>
