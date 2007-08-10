<%--
listbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri June 15 12:11:13     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<li id="${self.uuid}" ${self.outerAttrs}${self.innerAttrs}>
</li>