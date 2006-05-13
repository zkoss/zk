<%--
toolbar.dsp

{{IS_NOTE
	$Id: toolbar.dsp,v 1.4 2006/03/17 10:06:35 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 16:34:06     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="break" value=""/>
<c:set var="verticalBreak" value="${self.orient == 'vertical' ? '<br/>': ''}"/>
<div id="${self.uuid}" ${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.children}">
	${break}${u:redraw(child, null)}
	<c:set var="break" value="${verticalBreak}"/>
	</c:forEach>
</div>
