<%--
column.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jan  9 14:50:13     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<th id="${self.uuid}" z.type="Col"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave" class="head-cell-inner">${self.imgTag}<c:out value="${self.label}"/><c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
	</c:forEach></div></th>
