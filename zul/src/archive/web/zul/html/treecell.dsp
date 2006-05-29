<%--
treecell.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 16:12:15     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<td id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>${self.columnHtmlPrefix}${self.imgTag}<c:out value="${self.label}" maxlength="${self.maxlength}"/><c:forEach var="child" items="${self.children}">${u:redraw(child, null)}</c:forEach>${self.columnHtmlPostfix}</td>
