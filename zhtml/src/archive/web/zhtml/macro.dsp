<%--
macro.dsp

{{IS_NOTE
	$Id: macro.dsp,v 1.2 2006/04/19 07:59:46 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Apr 17 11:15:17     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.children}">
${u:redraw(child, null)}
	</c:forEach>
</span>