<%--
caption.dsp

{{IS_NOTE
	Purpose:
		Used with groupbox.
	Description:
		
	History:
		Tue Oct 11 15:54:37     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<c:set var="pzcls" value="${self.parent.zclass}"/>
<c:set var="puuid" value="${self.parent.uuid}"/>
<table id="${self.uuid}" z.type="zul.widget.Capt"${self.outerAttrs}${self.innerAttrs} width="100%" border="0" cellpadding="0" cellspacing="0">
<tr valign="middle">
	<td align="left" class="${zcls}-l">${self.imgTag}<c:out value="${self.compoundLabel}" nbsp="true"/></td><%-- bug 1688261: nbsp is required --%>
	<td align="right" class="${zcls}-r" id="${self.uuid}!cave"><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></td>
<c:if test="${self.minimizableVisible}">
	<td width="16"><div id="${puuid}!minimize" class="${pzcls}-tool ${pzcls}-minimize"></div></td>
</c:if>
<c:if test="${self.maximizableVisible}">
	<td width="16"><div id="${puuid}!maximize" class="${pzcls}-tool ${pzcls}-maximize <c:if test="${self.parent.maximized}">${pzcls}-maximized</c:if>"></div></td>
</c:if>
<c:if test="${self.closableVisible}">
	<td width="16"><div id="${puuid}!close" class="${pzcls}-tool ${pzcls}-close"></div></td>
</c:if>
</tr>
</table>
