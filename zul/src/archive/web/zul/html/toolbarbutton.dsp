<%--
toolbarbutton.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 16:39:54     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<a id="${self.uuid}" z.type="zul.btn.Tbtn" ${self.outerAttrs}${self.innerAttrs}><c:choose trim="true">
<c:when test="${self.dir == 'reverse'}">
	<c:out value="${self.label}"/><c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if>${self.imgTag}
</c:when>
<c:otherwise>
	${self.imgTag}<c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if><c:out value="${self.label}"/>
</c:otherwise>
</c:choose></a>