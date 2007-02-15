<%--
grid.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:56:36     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.grid.Grid"${self.outerAttrs}${self.innerAttrs}>
<c:if test="${!empty self.columns}">
	<div id="${self.uuid}!head" class="grid-head">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
	${z:redraw(self.columns, null)}
	</table>
	</div>
</c:if>
	<div id="${self.uuid}!body" class="grid-body">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="grid-btable">
	${z:redraw(self.rows, null)}
	</table>
	</div>
<c:if test="${!empty self.foot}">
	<div id="${self.uuid}!foot" class="grid-foot">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
${z:redraw(self.foot, null)}
	</table>
	</div>
</c:if>
</div>
