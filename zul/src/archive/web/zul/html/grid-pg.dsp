<%--
gridpg.dsp

{{IS_NOTE
	Purpose:
		Grid for mold = paging
	Description:
		
	History:
		Mon Aug 21 14:43:31     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.grid.Grid"${self.outerAttrs}${self.innerAttrs}>
	<div id="${self.uuid}!paging" class="grid-paging">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" class="grid-btable">
	<tbody class="grid-head">
	<c:forEach var="head" items="${self.heads}">
${z:redraw(head, null)}
	</c:forEach>
	</tbody>
${z:redraw(self.rows, null)}
	<tbody class="grid-foot">
${z:redraw(self.foot, null)}
	</tbody>
	</table>
	<div id="${self.uuid}!pgi" class="grid-pgi">
	${z:redraw(self.paging, null)}
	</div>
	</div>
</div>
