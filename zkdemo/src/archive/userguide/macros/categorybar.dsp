<%--
categorybar.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 13 11:27:21     2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="uuid" value="${self.uuid}"/>
<div id="${uuid}" z.type="CategoryBar" ${self.outerAttrs}${self.innerAttrs}>
<div id="${uuid}!right"></div>
<div id="${uuid}!left"></div>
<div id="${uuid}!body" class="${self.zclass}-body">
<div id="${uuid}!cave">
<c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach>
<div class="z-clear"></div>
</div></div></div>

