<%--
borderlayout2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 31 15:45:34 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zkex.zul.borderlayout.BorderLayout2">
	<c:forEach var="child" items="${self.children}">
		${z:redraw(child, null)}
	</c:forEach>
</div>
