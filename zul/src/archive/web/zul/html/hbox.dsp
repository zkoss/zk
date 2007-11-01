<%--
hbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 21 09:01:33     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" z.type="zul.box.Box"${self.outerAttrs}${self.innerAttrs} cellpadding="0" cellspacing="0">
<tr id="${self.uuid}!cave" ${self.caveAttrs}>
	<c:forEach var="child" items="${self.children}">
 <td id="${child.uuid}!chdextr"${u:getBoxChildOuterAttrs(child)}${u:getBoxChildInnerAttrs(child)}>${z:redraw(child, null)}</td>
	</c:forEach>
</tr>
</table>
