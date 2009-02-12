<%--
layoutregion.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 27, 2007 4:34:27 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zkex.zul.layout.LayoutRegion">
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}>
		<div id="${self.uuid}!cave" class="layout-region-body">
		<c:forEach var="child" items="${self.children}">
			${z:redraw(child, null)}
		</c:forEach>
		</div>
	</div>
	<c:if test="${self.position != 'center'}">
	<div id="${self.uuid}!split" class="layout-split <c:if test="${self.position == 'north' || self.position == 'south'}">layout-split-v</c:if><c:if test="${self.position == 'west' || self.position == 'east'}">layout-split-h</c:if>"><span id="${self.uuid}!splitbtn" class="layout-split-btn-<c:if test="${self.position == 'north'}">t</c:if><c:if test="${self.position == 'south'}">b</c:if><c:if test="${self.position == 'west'}">l</c:if><c:if test="${self.position == 'east'}">r</c:if>"></span></div>
	</c:if>
</div>
