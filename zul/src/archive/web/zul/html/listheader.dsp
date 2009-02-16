<%--
listheader.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 09:36:00     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<th id="${self.uuid}" z.type="Lhr"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave" class="${self.zclass}-cnt">${self.columnHtmlPrefix}${self.imgTag}<c:out value="${self.label}"/><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></div></th>
