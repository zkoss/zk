<%--
caption.dsp

{{IS_NOTE
	Purpose:
		Used with groupbox.
	Description:
		
	History:
		Tue Oct 11 15:54:37     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" zk_type="zul.html.widget.Capt"${self.outerAttrs}${self.innerAttrs} width="100%" border="0" cellpadding="0" cellspacing="0">
<tr class="caption" valign="middle">
	<td align="left">${self.imgTag}<c:out value="${self.compoundLabel}"/></td>
	<td align="right"><c:forEach var="child" items="${self.children}">${u:redraw(child, null)}</c:forEach><span style="display:none" id="${self.uuid}!child"></span><%-- bookmark for adding child  --%></td>
</tr>
</table>
