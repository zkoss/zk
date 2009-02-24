<%--
panelchildren.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Wed Jun 11 14:33:55 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="Panelchild" ${self.outerAttrs}${self.innerAttrs}>
<c:forEach var="child" items="${self.children}">
${z:redraw(child, null)}
</c:forEach>
</div>
