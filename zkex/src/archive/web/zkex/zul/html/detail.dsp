<%--
detail.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 25 17:50:57 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zkex.zul.detail.Detail" ${self.outerAttrs}${self.innerAttrs}>
<div id="${self.uuid}!img" class="${self.zclass}-img"></div>
<div id="${self.uuid}!cave" style="${self.contentStyle};display:none;" class="${self.contentSclass}">
	<c:forEach var="child" items="${self.children}">
  ${z:redraw(child, null)}
	</c:forEach>
</div>
</div>
