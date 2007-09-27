<%--
borderlayout.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 9, 2007 10:35:53 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} style="width:100%;height:100%" z.type="yuiextz.layout.ExtBorderLayout">
<span id="${self.uuid}!cave" style="display:none">
	<c:forEach var="child" items="${self.children}">
		${z:redraw(child, null)}
	</c:forEach>
</span>
<div id="${self.uuid}!real" style="width:100%;height:100%"></div>
</div>