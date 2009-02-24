<%--
auxheader.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Wed Oct 24 11:10:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<th id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave" class="${self.zclass}-cnt">${self.imgTag}<c:out value="${self.label}"/><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></div></th>
