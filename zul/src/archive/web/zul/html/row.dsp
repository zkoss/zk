<%--
row.dsp

{{IS_NOTE
	$Id: row.dsp,v 1.7 2006/03/17 10:06:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 17:08:44     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<tr id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" varStatus="status" items="${self.children}">
	<td id="${child.uuid}!chdextr"${self.grid.columns.children[status.index].colAttrs} class="gridev">${u:redraw(child, null)}</td>
	</c:forEach>
</tr>
