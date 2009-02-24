<%--
textbox.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Jun 14 17:17:17     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose trim="true">
<c:when test="${self.multiline}"><%-- textarea doesn't support maxlength --%>
<textarea id="${self.uuid}" z.type="zul.vd.Txbox"${self.outerAttrs}${self.innerAttrs}>${self.areaText}</textarea>
</c:when>
<c:otherwise>
<input id="${self.uuid}" z.type="zul.vd.Txbox"${self.outerAttrs}${self.innerAttrs}/>
</c:otherwise>
</c:choose>