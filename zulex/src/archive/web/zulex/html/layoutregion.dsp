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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zulex.layout.LayoutRegion">
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}>
		<div id="${self.uuid}!cave" class="layout-region-body">
		<c:forEach var="child" items="${self.children}">
			${z:redraw(child, null)}
		</c:forEach>
		</div>
	</div>
	<c:if test="${self.position != 'center'}">
	<div id="${self.uuid}!split" class="layout-split <c:if test="${self.position == 'north' || self.position == 'south'}">layout-split-v</c:if><c:if test="${self.position == 'west' || self.position == 'east'}">layout-split-h</c:if>"><img id="${self.uuid}!splitbtn" class="layout-split-button" src="<c:if test="${self.position == 'north'}">${c:encodeURL('~./zulex/img/layout/colps-t.png')}</c:if><c:if test="${self.position == 'south'}">${c:encodeURL('~./zulex/img/layout/colps-b.png')}</c:if><c:if test="${self.position == 'west'}">${c:encodeURL('~./zulex/img/layout/colps-l.png')}</c:if><c:if test="${self.position == 'east'}">${c:encodeURL('~./zulex/img/layout/colps-r.png')}</c:if>"/></div>
	</c:if>
</div>
