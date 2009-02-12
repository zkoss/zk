<%--
vtabs2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 18 2008, Created by Ryanwu
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcs" value="${self.zclass}-"/>
<div id="${self.uuid}" z.type="zul.tab2.Tabs2"${self.outerAttrs}${self.innerAttrs}>
	<div id="${self.uuid}!header" class="${c:cat(zcs,'header')}"> 
		<ul id="${self.uuid}!ul" class="${c:cat(zcs,'cnt')}" >
			<c:forEach var="child" items="${self.children}">
				${z:redraw(child, null)}
			</c:forEach>
			<li id="${self.uuid}!edge" class="${c:cat(zcs,'edge')}" ></li>
			<%-- bookmark for adding children --%>
		</ul>
	</div>
	<%-- Button --%>
	<div id="${self.uuid}!up" ></div>
	<div id="${self.uuid}!down" ></div>
</div>
<div id="${self.uuid}!line" class="${c:cat(zcs,'space')}" > </div><%-- Bug 2313445 we handle it in Tabs.invalidate() --%>
