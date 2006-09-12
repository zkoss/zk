<%--
listbox-pg.dsp

{{IS_NOTE
	Purpose:
		Listbox for mold = paging
	Description:
		
	History:
		Mon Aug 28 11:55:34     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" zk_type="zul.html.sel.Libox"${self.outerAttrs}${self.innerAttrs}>
	<div id="${self.uuid}!paging" class="listbox-paging">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="${self.uuid}!cave" class="listbox-btable">
	<tbody>
${z:redraw(self.listhead, null)}
	</tbody>

	<tbody id="${self.uuid}!cave">
	<c:forEach var="item" items="${self.items}" begin="${self.visibleBegin}" end="${self.visibleEnd}">
${z:redraw(item, null)}
	</c:forEach>
	</tbody>

	<tbody>
${z:redraw(self.listfoot, null)}
	</tbody>
	</table>
	</div>
	${z:redraw(self.paging, null)}
</div>
