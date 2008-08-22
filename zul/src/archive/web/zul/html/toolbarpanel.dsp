<%--
toolbarpanel.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 17 14:59:22 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
<div class="${self.moldSclass}-body ${self.moldSclass}-${self.align}">
<table class="${self.moldSclass}-content" cellspacing="0"><tbody>
<c:if test="${self.orient != 'vertical'}">
<tr>
	<c:forEach var="child" items="${self.children}">
	<td class="${self.moldSclass}-hor">${z:redraw(child, null)}</td>
	</c:forEach>
</tr>
</c:if>
<c:if test="${self.orient == 'vertical'}">
	<c:forEach var="child" items="${self.children}">
<tr>
	<td class="${self.moldSclass}-ver">${z:redraw(child, null)}</td>
</tr>
	</c:forEach>
</c:if>
</tbody></table>
<div class="z-clear"></div>
</div></div>