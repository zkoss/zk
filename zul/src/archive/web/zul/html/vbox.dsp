<%--
vbox.dsp

{{IS_NOTE
	$Id: vbox.dsp,v 1.6 2006/03/17 10:06:35 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jun 21 08:48:47     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.children}">
	<div width="100%" id="${child.uuid}!chdextr"${self.childExteriorAttrs}>${u:redraw(child, null)}</div>
	</c:forEach>
</div>
