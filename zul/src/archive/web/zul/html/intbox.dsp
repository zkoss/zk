<%--
intbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 09:12:37     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<input id="${self.uuid}" z.type="zul.vd.Inbox"${self.outerAttrs}${self.innerAttrs}/>