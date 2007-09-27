<%--
grid.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 22, 2007 5:00:13 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="yuiextz.grid.ExtGrid"${self.outerAttrs}${self.innerAttrs} z.rows="${self.rows.uuid}">
	<table width="100%" border="0">
	<c:if test="${!empty self.columns}">
		<thead>
		${z:redraw(self.columns, null)}		
		</thead>
	</c:if>
	${z:redraw(self.rows, null)}	
	</table>
</div>
