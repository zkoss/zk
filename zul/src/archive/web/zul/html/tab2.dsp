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
		<a class="${c:cat(zcs,'close')}" id="${self.uuid}!close" onclick="return false;" ></a>
	</c:if>
	<div class="${c:cat(zcs,'tl')}">
		<div class="${c:cat(zcs,'tr')}" ></div>
	</div>
	<div class="${c:cat(zcs,'hl')}" id="${self.uuid}!hl"  onclick="return false;" href="#">
	<div id="${self.uuid}!hr" class="${c:cat(zcs,'hr')}" >
	<c:choose><c:when test="${self.closable}">
		<div id="${self.uuid}!hm" class="${c:cat(zcs,'hm')} ${c:cat(zcs,'hm-close')}">
	</c:when><c:otherwise>
		<div id="${self.uuid}!hm" class="${c:cat(zcs,'hm')}">
	</c:otherwise></c:choose>
	<span class="${c:cat(zcs,'text')}">
	<c:choose><c:when test="${empty self.label and empty self.imgTag}" >
		&#160;
	</c:when><c:otherwise>
		${self.imgTag}<c:out value="${self.label}"/>
	</c:otherwise></c:choose>
	</span>
	</div>
	</div>
	</div>
</li>
