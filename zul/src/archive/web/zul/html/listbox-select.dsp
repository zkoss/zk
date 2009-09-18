<%--
listbox-select.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Wed Sep 28 14:01:24     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<select id="${self.uuid}" z.type="zul.sel.Lisel"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="item" items="${self.items}">
		<c:if test="${item.visible}">
			<option id="${item.uuid}"${item.outerAttrs}${item.innerAttrs}><c:out value="${item.label}" maxlength="${self.maxlength}"/></option>
		</c:if>
	</c:forEach><%-- for better performance, we don't use z:redraw --%>
</select>
