<%--
tabpanel2.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Jul 12 10:58:38     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.tab2.Tabpanel2"${self.outerAttrs}>
	<div id="${self.uuid}!real" ${self.innerAttrs}><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></div>
</div>
