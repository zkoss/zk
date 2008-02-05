<%--
listbox-pg.dsp

{{IS_NOTE
	Purpose:
		Listbox for mold = paging
	Description:
		
	History:
		Mon Aug 28 11:55:34     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="zu" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.sel.Libox"${self.outerAttrs}${self.innerAttrs}>
	<div id="${self.uuid}!paging" class="listbox-paging">
	<table width="${self.innerWidth}" border="0" cellpadding="0" cellspacing="0" class="listbox-btable">
	<tbody class="listbox-head">
	<c:forEach var="head" items="${self.heads}">
${z:redraw(head, null)}
	</c:forEach>
	</tbody>

	<tbody id="${self.uuid}!cave">
${zu:resetStripeClass(self)}
	<c:forEach var="item" items="${self.items}" begin="${self.visibleBegin}" end="${self.visibleEnd}">
${zu:setStripeClass(item)}	
${z:redraw(item, null)}
	</c:forEach>
	</tbody>

	<tbody class="listbox-foot">
${z:redraw(self.listfoot, null)}
	</tbody>
	</table>
	<div id="${self.uuid}!pgi" class="listbox-pgi">
	${z:redraw(self.paging, null)}
	</div>
	</div>
</div>
