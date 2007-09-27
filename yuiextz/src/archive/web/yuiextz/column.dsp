<%--
column.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 22, 2007 5:11:26 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<th id="${self.uuid}" z.type="yuiextz.grid.ExtCol"${self.outerAttrs}${self.innerAttrs}>${self.imgTag}<c:out value="${self.label}"/></th>
