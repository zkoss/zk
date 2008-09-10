<%--
tab.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:58:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="look" value="${self.tabbox.tabLook}-"/>
<c:set var="hghStyle" value="height:${self.height}" unless="${empty self.height}"/>
<li id="${self.uuid}" ${c:attr('style',hghStyle)} z.type="Tab2"${self.outerAttrs}${self.innerAttrs} z.sel="${self.selected}" z.box="${self.tabbox.uuid}" z.panel="${self.linkedPanel.uuid}" z.disabled="${self.disabled}">
		<c:if test="${self.closable}">
			<a class="${c:cat(look,'close')}" id="${self.uuid}!close" onclick="return false;"/>
		</c:if>
		<a class="${c:cat(look,'a')}" id="${self.uuid}!a"  onclick="return false;" href="#">
			<em id="${self.uuid}!em" class="${c:cat(look,'em')}">
			<c:choose>
				<c:when test="${self.closable}">
					<span id="${self.uuid}!inner" class="${c:cat(look,'inner')} ${c:cat(look,'innerclose')}">
				</c:when>
				<c:otherwise>
					<span id="${self.uuid}!inner" class="${c:cat(look,'inner')}">
				</c:otherwise>
			</c:choose>
				<span class="${c:cat(look,'text')}">${self.imgTag}<c:out value="${self.label}"/></span>
			</span>
		</em>
	</a>
</li>