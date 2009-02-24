<%--
imagemap.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Mar 28 14:54:56     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}" z.type="zul.widget.Map" z.cave="${self.uuid}_map"${self.outerAttrs}>
<a href="${c:encodeURL('~./zul/html/imagemap-done.dsp')}?${self.uuid}" target="zk_hfr_"><img id="${self.uuid}!real"${self.innerAttrs}/></a>
<map name="${self.uuid}_map" id="${self.uuid}_map">
	<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
	</c:forEach>
</map>
</span>
