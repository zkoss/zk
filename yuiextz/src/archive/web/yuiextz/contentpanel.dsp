<%--
contentpanel.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 9, 2007 10:25:50 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="yuiextz.layout.ExtContentPanel">
	<c:forEach var="child" items="${self.children}">
	<span id="${self.uuid}!cave" style="width:100%;height:100%">${z:redraw(child, null)}</span>
	</c:forEach>
</span>