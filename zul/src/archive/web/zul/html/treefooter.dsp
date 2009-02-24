<%--
treefooter.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 19 15:35:03     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<td id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zul.zul.Ftr"><div id="${self.uuid}!cave" class="${self.zclass}-cnt">${self.imgTag}<c:out value="${self.label}"/><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></div></td>
