<%--
treecell.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 16:12:15     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<td z.type="Lic" id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave"${self.labelAttrs} class="cell-inner <c:if test="${self.tree.fixedLayout}">overflow-hidden</c:if>">${self.columnHtmlPrefix}${self.imgTag}<c:out value="${self.label}" maxlength="${self.maxlength}"/><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach>${self.columnHtmlPostfix}</div></td>
