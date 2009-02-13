<%--
vtabs.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 3 2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="look" value="${self.tabbox.tabLook}-"/>
<td id="${self.uuid}" align="right" z.type="zul.tab.Tabs"${self.outerAttrs}${self.innerAttrs}>
<table class="vtabsi" border="0" cellpadding="0" cellspacing="0">

<%-- prefix row  --%>
<tr>
<td align="right"><table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td class="${c:cat(look,'first1')}"></td>
</tr>
<tr id="${self.uuid}!first">
	<td class="${c:cat(look,'first2')}"></td>
</tr>
</table></td>
</tr>

	<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
	</c:forEach>

<tr style="display:none" id="${self.uuid}!child"><td></td></tr><%-- bookmark for adding children --%>

<%-- postfix row --%>
<tr>
<td align="right"><table border="0" cellpadding="0" cellspacing="0">
<tr id="${self.uuid}!last">
	<td class="${c:cat(look,'last1')}"></td>
</tr>
<tr>
	<td class="${c:cat(look,'last2')}"></td>
</tr>
</table></td>
</tr>

</table>
</td>
