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
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zul/core" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" z.type="zul.box.Box"${self.outerAttrs}${self.innerAttrs} cellpadding="0" cellspacing="0">
<tr id="${self.uuid}!cave" ${self.caveAttrs}>
<c:set var="spstyle" value="width:${self.spacing}" unless="${empty self.spacing}"/>
	<c:forEach var="child" items="${self.children}">
 <td id="${child.uuid}!chdextr"${u:getBoxChildOuterAttrs(child)}${u:getBoxChildInnerAttrs(child)}>${z:redraw(child, null)}</td>
<c:if test="${!empty child.nextSibling}">
	<c:set var="s" value="display:none;${spstyle}"
		if="${!child.visible || '0' == self.spacing || '0px' == self.spacing || '0pt' == self.spacing || '0%' == self.spacing}"/>
	<td id="${child.uuid}!chdextr2" class="${self.zclass}-sep"${c:attr('style',empty s ? spstyle: s)}><c:if test="${c:isExplorer()}"><img style="width:0;height:0"/></c:if></td>
</c:if>
	</c:forEach>
</tr>
</table>
