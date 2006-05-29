<%--
hmenubar.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 13:42:13     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" zk_type="zul.html.menu.Menubar"${self.outerAttrs}${self.innerAttrs}>
<table cellpadding="0" cellspacing="0">
<tr valign="bottom">
	<c:forEach var="child" items="${self.children}">
	${u:redraw(child, null)}
	</c:forEach>
 <td style="display:none" id="${self.uuid}!child"></td><%-- bookmark for adding children. --%>
</tr>
</table>
</div>