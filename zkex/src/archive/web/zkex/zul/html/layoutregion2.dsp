<%--
layoutregion2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 31 15:45:49 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zkex.zul.borderlayout.LayoutRegion2">
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}>
		<c:if test="${!empty self.title}">
			<div id="${self.uuid}!caption" class="${self.zclass}-header"><c:if test="${self.position != 'center'}"><div id="${self.uuid}!btn" class="${self.parent.zclass}-icon ${self.zclass}-collapse" <c:if test="${!self.collapsible}">style="display:none;"</c:if>></div></c:if><c:out value="${self.title}" /></div>
		</c:if>
		<div id="${self.uuid}!cave" class="${self.zclass}-body">
		<c:forEach var="child" items="${self.children}">
			${z:redraw(child, null)}
		</c:forEach>
		</div>
	</div>
	<c:if test="${self.position != 'center'}">
	<div id="${self.uuid}!split" class="${self.zclass}-split"></div>
	<c:if test="${!empty self.title}">
	<div id="${self.uuid}!collapsed" class="${self.zclass}-collapsed" style="display:none"><div id="${self.uuid}!btned" class="${self.parent.zclass}-icon ${self.zclass}-expand" <c:if test="${!self.collapsible}">style="display:none;"</c:if>></div></div>
	</c:if>
	</c:if>
</div>
