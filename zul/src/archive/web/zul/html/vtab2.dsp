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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="wd" value="width:${self.width}" unless="${empty self.width}"/>
<c:set var="look" value="${self.tabbox.tabLook}-"/>

<li id="${self.uuid}" ${c:attr('style',wd)} z.type="Tab2"${self.outerAttrs} z.sel="${self.selected}" z.box="${self.tabbox.uuid}" z.panel="${self.linkedPanel.uuid}" z.disabled="${self.disabled}">	
	<c:choose>
		<c:when test="${self.closable}">
			<a id="${self.uuid}!close" class="${c:cat(look,'close')}"  ></a>		
		</c:when>
		<c:otherwise>
			<a class="${c:cat(look,'noclose')}" ></a>		
		</c:otherwise>
	</c:choose>			
	<a class="${c:cat(look,"a")}" id="${self.uuid}!real"${self.innerAttrs}>
		<em id="${self.uuid}" class="${c:cat(look,'em')}">	 	
				<c:choose>
					<c:when test="${self.closable}">
						<span id="${self.uuid}!inner" class="${c:cat(look,'inner')} ${c:cat(look,'innerclose')}">
					</c:when>
					<c:otherwise>
						<span id="${self.uuid}!inner" class="${c:cat(look,'inner')}">
					</c:otherwise>
				</c:choose>		
				<span class="${c:cat(look,'text')}">
					${self.imgTag}<c:out value="${self.label}"/>
				</span>
			</span>
		</em>
	</a>
</li>