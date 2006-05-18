<%--
grid.dsp

{{IS_NOTE
	$Id: grid.dsp,v 1.10 2006/03/27 10:59:57 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:56:36     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" zk_type="zul.html.grid.Grid"${self.outerAttrs}${self.innerAttrs}>
<c:if test="${!empty self.columns}">
	<div id="${self.uuid}!head" class="grid-head">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
	${u:redraw(self.columns, null)}
	</table>
	</div>
</c:if>
	<div id="${self.uuid}!body" class="grid-body">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
	${u:redraw(self.rows, null)}
	</table>
	</div>
</div>
