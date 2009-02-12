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
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcs" value="${self.zclass}-"/>
<li id="${self.uuid}" z.type="Tab2"${self.outerAttrs}${self.innerAttrs}>
		<c:if test="${self.closable}">
			<a class="${c:cat(zcs,'close')}" id="${self.uuid}!close" onclick="return false;"/>
		</c:if>
		<a class="${c:cat(zcs,'body')}" id="${self.uuid}!a"  onclick="return false;" href="#">
			<em id="${self.uuid}!em">
			<c:choose>
				<c:when test="${self.closable}">
					<span id="${self.uuid}!inner" class="${c:cat(zcs,'inner')} ${c:cat(zcs,'close-inner')}">
				</c:when>
				<c:otherwise>
					<span id="${self.uuid}!inner" class="${c:cat(zcs,'inner')}">
				</c:otherwise>
			</c:choose>
				<span class="${c:cat(zcs,'text')}">
			<c:choose>
				<c:when test="${empty self.label and empty self.imgTag}" >
					&#160;
				</c:when>
				<c:otherwise>
					${self.imgTag}<c:out value="${self.label}"/>
				</c:otherwise>
			</c:choose>
				</span>
			</span>
		</em>
	</a>
</li>
