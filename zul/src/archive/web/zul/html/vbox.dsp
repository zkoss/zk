<%--
vbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 21 08:48:47     2005, Created by tomyeh
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
<c:set var="scls" value="${self.sclass}"/>
<c:set var="scls" value="vbox" if="${empty scls}"/>
<c:set var="spstyle" value="height:${self.spacing}" unless="${empty self.spacing}"/>
	<c:forEach var="child" items="${self.children}">
	<tr id="${child.uuid}!chdextr"${u:getBoxChildOuterAttrs(child)}><td${u:getBoxChildInnerAttrs(child)}>${z:redraw(child, null)}</td></tr>
<c:if test="${!empty child.nextSibling}">
	<c:set var="s" value="display:none;${spstyle}"
		if="${!child.visible || '0' == self.spacing || '0px' == self.spacing || '0pt' == self.spacing || '0%' == self.spacing}"/>
	<tr id="${child.uuid}!chdextr2" class="${scls}-sp"${c:attr('style',empty s ? spstyle: s)}><td><c:if test="${c:isExplorer()}"><img style="width:0;height:0"/></c:if></td></tr>
</c:if>
	</c:forEach>
</table>