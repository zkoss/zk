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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<td id="${self.uuid}" align="right" z:type="zul.tab.Tabs"${self.outerAttrs}${self.innerAttrs}>
<table border="0" cellpadding="0" cellspacing="0">

<%-- prefix row  --%>
<tr>
<td align="right"><table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td height="3" width="3" style="background-image:url(${c:encodeURL('~./zul/img/tab/v3d-first.gif')})"></td>
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
	<td height="3" width="3" style="background-image:url(${c:encodeURL('~./zul/img/tab/v3d-last1.gif')})"></td>
</tr>
<tr>
	<td height="1" width="3" style="background-image:url(${c:encodeURL('~./zul/img/tab/v3d-last2.gif')})"></td>
</tr>
</table></td>
</tr>

</table>
</td>
