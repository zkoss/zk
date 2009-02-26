<%--
button.dsp

{{IS_NOTE
	Purpose:

	Description:
		In Safari, the shape of <button> is not pleasure, so we use
		<input> if no image is required
	History:
		Wed Jun  8 10:19:50     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${!self.imageAssigned && c:isSafari()}">
<input type="button" id="${self.uuid}" z.type="zul.btn.ButtonOS" value="${self.label}"${self.outerAttrs}${self.innerAttrs}/>
</c:when>
<c:otherwise>
<button type="button" id="${self.uuid}" z.type="zul.btn.ButtonOS"${self.outerAttrs}${self.innerAttrs}>
<c:choose>
<c:when test="${self.dir == 'reverse'}">
	<c:out value="${self.label}"/><c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if>${self.imgTag}
</c:when>
<c:otherwise>
	${self.imgTag}<c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if><c:out value="${self.label}"/>
</c:otherwise>
</c:choose>
</button>
</c:otherwise>
</c:choose>
