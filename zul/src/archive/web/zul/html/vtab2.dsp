<%--
vtab2.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Aug 18 2008, Created by Ryanwu
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcs" value="${self.zclass}-"/>
<li id="${self.uuid}" z.type="Tab2"${self.outerAttrs}${self.innerAttrs}>
	<c:choose>
		<c:when test="${self.closable}">
			<a id="${self.uuid}!close" class="${c:cat(zcs,'close')}"  ></a>
		</c:when>
		<c:otherwise>
			<a class="${c:cat(zcs,'noclose')}" ></a>
		</c:otherwise>
	</c:choose>
	<div class="${c:cat(zcs,"hl")}" id="${self.uuid}!real">
		<div id="${self.uuid}!hr" class="${c:cat(zcs,'hr')}">
				<c:choose>
					<c:when test="${self.closable}">
						<div id="${self.uuid}!hm" class="${c:cat(zcs,'hm')} ${c:cat(zcs,'hm-close')}">
					</c:when>
					<c:otherwise>
						<div id="${self.uuid}!hm" class="${c:cat(zcs,'hm')}">
					</c:otherwise>
				</c:choose>
				<span class="${c:cat(zcs,'text')}">
					${self.imgTag}<c:out value="${self.label}"/>
				</span>
			</div>
		</div>
	</div>
</li>