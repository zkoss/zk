<%--
menupopup.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 14:40:59     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
 <table cellpadding="0" cellspacing="0">
	<c:forEach var="child" items="${self.children}">
  ${u:redraw(child, null)}
	</c:forEach>
 <tr id="${self.uuid}!child" style="display:none"><td colspan="3"></td></tr><%-- bookmark for adding children --%>
 </table>
</div>
