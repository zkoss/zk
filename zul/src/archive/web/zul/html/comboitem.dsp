<%--
comboitem.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 16 09:14:15     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<tr id="${self.uuid}" zk_type="Cmit"${self.outerAttrs}${self.innerAttrs}>
<td>${self.imgTag}</td><td><c:out value="${self.label}"/><c:if test="${!empty self.description}"><br/><span><c:out value="${self.description}"/></span></c:if></td>
</tr><%-- No space between td, because cb.js depends on it --%>
