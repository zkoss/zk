<%--
borderlayout.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 27, 2007 4:58:31 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zkex.zul.layout.BorderLayout">
	<c:forEach var="child" items="${self.children}">
		${z:redraw(child, null)}
	</c:forEach>
</div>