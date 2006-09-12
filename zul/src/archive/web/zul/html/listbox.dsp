<%--
listbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 15 18:20:53     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" zk_type="zul.html.sel.Libox"${self.outerAttrs}${self.innerAttrs}>
	<c:if test="${!empty self.listhead}">
	<div id="${self.uuid}!head" class="listbox-head">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
${z:redraw(self.listhead, null)}
	</table>
	</div>
	</c:if>
	<div id="${self.uuid}!body" class="listbox-body">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="${self.uuid}!cave" class="listbox-btable">
	<c:forEach var="item" items="${self.items}">
${z:redraw(item, null)}
	</c:forEach>
	</table>
	</div>
	<div id="${self.uuid}!foot" class="listbox-foot">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
${z:redraw(self.listfoot, null)}
	</table>
	</div>
</div>
