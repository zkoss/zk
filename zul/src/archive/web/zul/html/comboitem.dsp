<%--
comboitem.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Dec 16 09:14:15     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<tr id="${self.uuid}" z.type="Cmit"${self.outerAttrs}${self.innerAttrs}>
<td class="${zcls}-img">${self.imgTag}</td><td class="${zcls}-text"><c:out pre="true" value="${self.label}"/><c:if test="${!empty self.description}"><br/><span class="${zcls}-inner"><c:out value="${self.description}"/></span></c:if><c:if test="${!empty self.content}"><span class="${zcls}-cnt">${self.content}</span></c:if></td>
</tr><%-- No space between td, because cb.js depends on it --%>
