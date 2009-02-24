<%--
tabs.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Jul 12 10:58:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcs" value="${self.zclass}-"/>
<div id="${self.uuid}" z.type="zul.tab2.Tabs2"${self.outerAttrs}${self.innerAttrs}>
	<div id="${self.uuid}!right" ></div>
	<div id="${self.uuid}!left" ></div>
	<div id="${self.uuid}!header" class="${c:cat(zcs,'header')}" >
		<ul id="${self.uuid}!cave" class="${c:cat(zcs,'cnt')}" >
			<c:forEach var="child" items="${self.children}">
			${z:redraw(child, null)}
			</c:forEach>
			<li id="${self.uuid}!edge" class="${c:cat(zcs,'edge')}" > </li>
			<div id="${self.uuid}!clear" class="z-clear"> </div>
		</ul>
	</div>
	<div id="${self.uuid}!line" class="${c:cat(zcs,'space')}" ></div>
</div>
