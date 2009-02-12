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
	<a class="${c:cat(zcs,"body")}" id="${self.uuid}!real">
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
					${self.imgTag}<c:out value="${self.label}"/>
				</span>
			</span>
		</em>
	</a>
</li>