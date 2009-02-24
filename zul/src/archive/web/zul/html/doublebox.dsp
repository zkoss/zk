<%--
doublebox.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Sat Oct  14 13:25:12     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<input id="${self.uuid}" z.type="zul.vd.Dbbox"${self.outerAttrs}${self.innerAttrs}/>